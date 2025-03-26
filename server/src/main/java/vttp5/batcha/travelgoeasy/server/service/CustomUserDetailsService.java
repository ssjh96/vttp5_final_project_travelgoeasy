package vttp5.batcha.travelgoeasy.server.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vttp5.batcha.travelgoeasy.server.model.UserModel;
import vttp5.batcha.travelgoeasy.server.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService
{
    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
    {
        System.out.println("Authenticating user: " + username); // Debugging line

        Optional<UserModel> userOpt = userRepo.findByUsername(username);

        if (userOpt.isEmpty())
        {
            throw new UsernameNotFoundException("username not found: " + username);
        }

        UserModel currentUser = userOpt.get();

        return User.builder()
                    .username(currentUser.getUsername())
                    .password(currentUser.getPassword()) 
                    // Password in mysql db must be encrypted for login to work
                    .build(); // converts into UserDetails Obj
    }   

}
