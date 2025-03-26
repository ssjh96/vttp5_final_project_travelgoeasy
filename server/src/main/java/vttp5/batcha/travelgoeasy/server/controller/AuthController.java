package vttp5.batcha.travelgoeasy.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import vttp5.batcha.travelgoeasy.server.model.JwtRequest;
import vttp5.batcha.travelgoeasy.server.model.JwtResponse;
import vttp5.batcha.travelgoeasy.server.model.UserModel;
import vttp5.batcha.travelgoeasy.server.service.AuthService;
import vttp5.batcha.travelgoeasy.server.service.JwtService;
import vttp5.batcha.travelgoeasy.server.service.UserProfileService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Allow frontend to call this API 
public class AuthController 
{

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtUtil;

    /*  CLIENT: whenever user login, 
        SERVER send jwt to client,
        CLIENT: client send some other request (/smth) with the JWT token to the server
        SERVER: validates the JWT and sends the response */

    // LOGIN
    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest authRequest) 
    {
        System.out.println(">>> AuthRequest: " + authRequest);
        
        try {
            authService.verify(authRequest);

            final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
            
            final String jwt = jwtUtil.generateToken(userDetails);
            
            return ResponseEntity.ok(new JwtResponse(jwt));
            
        } catch (BadCredentialsException e) {
            // status unauthorised
            return ResponseEntity.status(401).body(new JwtResponse(e.getMessage()));
        }
       
    }
       

    // REGISTRATION
    @PostMapping(path = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> registerUser(@RequestBody UserModel userModel) 
    {
        System.out.println("registering");
        try {
            JsonObject jResponse = userProfileService.registerUserCreateProfile(userModel);
            System.out.println("jReponse: " + jResponse.toString());

            if(jResponse.containsKey("error"))
            {
                return ResponseEntity.badRequest().body(jResponse.toString());
            }
            return ResponseEntity.ok().body(jResponse.toString());
        
        } catch (RuntimeException e) {
            JsonObject errorResponse = Json.createObjectBuilder()
            .add("error", e.getMessage())
            .build();
            
            return ResponseEntity.badRequest().body(errorResponse.toString());
        }
        
    }  
    
}
