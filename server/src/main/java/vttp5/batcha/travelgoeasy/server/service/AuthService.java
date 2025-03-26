package vttp5.batcha.travelgoeasy.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import vttp5.batcha.travelgoeasy.server.model.JwtRequest;

@Service
public class AuthService 
{
    @Autowired
    private AuthenticationManager authenticationManager;

    public boolean verify(JwtRequest jwtRequest)
    {
        System.out.println("IN VERIFICATION");

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    jwtRequest.getUsername(), jwtRequest.getPassword()));

            return authentication.isAuthenticated();
            
        } catch (Exception e) {
            System.out.println(">>> Exception: " + e.getMessage());
            // bad credentials exceptiom
            throw new BadCredentialsException("Incorrect username or password");
        }
        
    }
}
