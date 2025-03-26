package vttp5.batcha.travelgoeasy.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.stripe.model.checkout.Session;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import vttp5.batcha.travelgoeasy.server.service.StripeService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/payment")
public class StripeController 
{
    @Autowired
    private StripeService stripeService;

    @PostMapping("/create-checkout-session/{username}")
    public ResponseEntity<String> createCheckoutSession(@PathVariable String username,
                                                        @RequestParam String successUrl,
                                                        @RequestParam String cancelUrl,
                                                        @RequestParam(required = false, defaultValue = "false") boolean visaDiscount) 
    {
        try {
            Session session = stripeService.createCheckOutSession(username, successUrl, cancelUrl, visaDiscount);

            JsonObject jResponse = Json.createObjectBuilder().add("sessionId", session.getId())
                                                            .add("url", session.getUrl())
                                                            .build();

            return ResponseEntity.ok(jResponse.toString());
        } catch (Exception e) {
            JsonObject jError = Json.createObjectBuilder().add("error", e.getMessage()).build();

            return  ResponseEntity.badRequest().body(jError.toString());
        }
    }

    @PostMapping("/upgrade/{username}")
    public ResponseEntity<String> upgradeUserToPro(@PathVariable String username) 
    {
        boolean bUpgraded = stripeService.upgradeUserToPro(username);

        if(!bUpgraded)
        {
            JsonObject jError = Json.createObjectBuilder()
                                    .add("error", ("%s failed to upgrade to PRO")
                                    .formatted(username))
                                    .build();

            return  ResponseEntity.badRequest().body(jError.toString());
        }
        
        else
        {
            JsonObject jResponse = Json.createObjectBuilder()
                                        .add("success", ("%s successfully upgraded to PRO")
                                        .formatted(username))
                                        .build();

            return ResponseEntity.ok(jResponse.toString());
        }
    }
/* 
    @PostMapping("/get-publish-key")
    public ResponseEntity<String> getPublishKey() 
    {
        try {
            JsonObject jPublishKey = Json.createObjectBuilder()
                                    .add("publishableKey", stripeService.getPublishableKey())
                                    .build();

            return ResponseEntity.ok(jPublishKey.toString());    
        } catch (Exception e) {

            JsonObject jError = Json.createObjectBuilder()
                                    .add("error", "Error getting stripe publishable key")
                                    .build();

            return  ResponseEntity.badRequest().body(jError.toString());
        }
        
    }
     */
}
