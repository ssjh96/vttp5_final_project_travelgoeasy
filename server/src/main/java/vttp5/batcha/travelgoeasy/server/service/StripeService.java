package vttp5.batcha.travelgoeasy.server.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.annotation.PostConstruct;
import vttp5.batcha.travelgoeasy.server.model.UserModel;
import vttp5.batcha.travelgoeasy.server.repository.UserRepository;

@Service
public class StripeService 
{
    @Value("${stripe.secret.key}")
    private String secretKey;

    // @Value("${stripe.publishable.key}")
    // private String publishableKey;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey; // init Stripe w secret key
    }

    public Session createCheckOutSession(String username, String successUrl, String cancelUrl, boolean bVisaDiscount) throws StripeException
    {
        // Check user exist
        Optional<UserModel> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty())
        {
            throw new RuntimeException("User not found");
        }

        UserModel user = userOpt.get();

        // Check if user is already PRO member
        if(user.getIsPro())
        {
            throw new RuntimeException("User is already a PRO member");
        }
        
        long proPrice = 999L;
        if(bVisaDiscount)
        {
            proPrice = (long) (999L * 0.75);
        }


        // Create lineItem for checkout
        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
            .setQuantity(1L)
            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("sgd")
                .setUnitAmount(proPrice)
                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName(bVisaDiscount ? "TravelGoEasy PRO (Visa 25% Off)" : "TravelGoEasy PRO")
                    .build())
                .build())
            .build();
        
        // Store username for identification
        Map<String, String> metadata = new HashMap<>();
        metadata.put("username", username);

        // Create the checkout session
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addLineItem(lineItem)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .setClientReferenceId(username)
                .putAllMetadata(metadata)
                .build();
            
        Session session = Session.create(params);
        System.out.println("session: " + session);

        return session;
    }

    public boolean upgradeUserToPro(String username)
    {
        try {
            Optional<UserModel> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty())
            {
                return false;
            }
            return userRepository.updateProStatus(userOpt.get().getId(), true);
        } catch (Exception e) {
            System.out.println("Error updating prostatus: " + e.getMessage());
            return false;
        }
    }

    /* public String getPublishableKey() 
    {
        return publishableKey;
    } */
}
