package vttp5.batcha.travelgoeasy.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import vttp5.batcha.travelgoeasy.server.util.JwtFilter;

@Configuration
@EnableWebSecurity // declaring to go w the configuration flow here
public class SecurityConfig 
{
    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private UserDetailsService userDetailsService; // uses customUserDetailsService override
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        /* by default, csrf tokens are tied to a session in SS.
         * When a session is created (e.g., user logs in or first visits the site), a new CSRF token is generated.
         * token is stored in the session and must be included in each subsequent state-changing request (e.g., POST, PUT, DELETE).
         * httpservletrequest request.getattribute("_csrf") cast to CsrfToken
         * it can be passed via an HTTP header (e.g., X-CSRF-TOKEN), to perform post/delete/put
         */
        http.csrf(csrf -> csrf.disable()) // Disable CSRF since Angular send JWT tokens     

            .authorizeHttpRequests(request -> request
                // Static fodler
                .requestMatchers("/", 
                                                "/index.html", 
                                                "/*.js",
                                                "/*.css", 
                                                "/public/**", 
                                                "/*.ico", 
                                                "/images/**", 
                                                "/manifest.json", 
                                                "/icons/**")
                                                .permitAll()
                // API endpoints
                .requestMatchers("/api/auth/login", 
                                                "/api/auth/register", 
                                                "/api/countries/all", 
                                                "/api/email/send", 
                                                "/api/trips/trip-id/*", 
                                                "/api/places/get-all/*", 
                                                "/api/gemini/prompt")
                                                .permitAll()
                // Angular routes
                .requestMatchers("/",
                                                "/Login/**",
                                                "/Register/**",
                                                "/shared-itinerary/**",
                                                "/Home/**",
                                                "/Profile/**",
                                                "/checkout/**",
                                                "/payment-success/**",
                                                "/payment-cancel/**",
                                                "/create-plan/**",
                                                "/all-plans/**",
                                                "/view-plan/**")
                                                .permitAll()
                .anyRequest().authenticated()) // all other endpoints must authenticate
                
            .httpBasic(Customizer.withDefaults()) // Enables HTTP Basic authentication
            
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
            // make sure no sessions created, JWT does not use HTTP Sessions
            // if sessions are stateless, we don't need to worry abt session id, x need csrf
            
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); 
                
        return http.build();        
    }    

    @Bean
    public AuthenticationManager authenticationManager()
    {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());

        return new ProviderManager(authenticationProvider);
    }
}
