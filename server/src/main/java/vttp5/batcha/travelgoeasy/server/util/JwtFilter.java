package vttp5.batcha.travelgoeasy.server.util;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vttp5.batcha.travelgoeasy.server.service.CustomUserDetailsService;
import vttp5.batcha.travelgoeasy.server.service.JwtService;



@Component
public class JwtFilter extends OncePerRequestFilter
{
    @Autowired
    private JwtService jwtUtil;

    // Creates circular refrence 
    // @Autowired
    // private UserDetailsService userDetailsService;
    
    @Autowired
    ApplicationContext context;

    /* Server will receive from client side
         * Bearer [token]
         * this will be in header
         */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException 
    {

        System.out.println("IN JWT FILTER");
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if(authHeader != null && authHeader.startsWith("Bearer "))
        {
            token = authHeader.substring("Bearer ".length());
            // token = authHeader.substring(7);
            username = jwtUtil.extractUsername(token);
        }

        if(token != null && SecurityContextHolder.getContext().getAuthentication() == null) // if null for securitycontext = null, it means that no authentication set for current request yet
        {
            UserDetails userDetails = context.getBean(CustomUserDetailsService.class).loadUserByUsername(username);
            if(jwtUtil.validateToken(token, userDetails))
            {
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // after verification, set authentication
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        System.out.println("TOKEN: " + token + ", username: " + username + ", authheader: " + authHeader);

        filterChain.doFilter(request, response);
    }
    
}
