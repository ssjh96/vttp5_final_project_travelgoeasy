package vttp5.batcha.travelgoeasy.server.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import vttp5.batcha.travelgoeasy.server.model.UserModel;
import vttp5.batcha.travelgoeasy.server.repository.UserRepository;

@Service
public class JwtService
{
    @Value("${jwt.secret.key}")
    private String secretKey;


    @Value("${jwt.token.expiration}")
    private long tokenExpiration;

    public SecretKey getSigningKey()
    {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Autowired
    private UserRepository userRepository;

    private String createToken(Map<String, Object> claims, String subject) 
    { 
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration)) // 10 hours or 1000 * 60 * 60 * 10
                .signWith(getSigningKey())
                .compact();
        
    }

    /* 
    {
        "sub": "shamus",        // Subject (Username or User ID)
        "role": "ADMIN",        // User's role
        "iat": 1708897600,      // Issued At (timestamp)
        "exp": 1708901200       // Expiration Time 
    }
     */

    public String generateToken(UserDetails userDetails) {
        // Add role as a claim, can put others like dept etc
        Map<String, Object> claims = new HashMap<>(); 
        claims.put("username", userDetails.getUsername());

        
        UserModel user = userRepository.findByUsername(userDetails.getUsername()).get();

        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role",  user.getRole());
        claims.put("isPro", user.getIsPro());

        return createToken(claims, userDetails.getUsername());
    }
    

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims); 
        // take in claims (map), .apply applies the function to get a specific claim (find key T and return)
    }

    private Claims extractAllClaims(String token) 
    {   
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload(); // returns the Claims (map) obj
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
        // equivalent 
        // String username = extractClaim(token, claims -> claims.getSubject());
    }

    /*  - this method looks for a claim named "username" in jwt payload
        - if claim x exist, will throw a nullpointerexception 
        - use getSubject (which is usually username ) instead because it will always work if properly set the subject when generating the token
        - if subject was not set, createToken method will return null */
        
    // public String extractUsername(String token) {
    //     return extractClaim(token, claims -> claims.get("username").toString()); 
    // }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractRole(String token) {
        // Lambda: take claims as input and returns claims.get("role").toString().
        return extractClaim(token, claims -> claims.get("role").toString());
    }


    
}
