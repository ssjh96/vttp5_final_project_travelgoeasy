package vttp5.batcha.travelgoeasy.server.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import vttp5.batcha.travelgoeasy.server.model.Profile;
import vttp5.batcha.travelgoeasy.server.model.UserModel;
import vttp5.batcha.travelgoeasy.server.repository.ProfileRepository;
import vttp5.batcha.travelgoeasy.server.repository.UserRepository;

@Service
public class UserProfileService 
{
    @Autowired
    private UserRepository userRepository;

    @Autowired ProfileRepository profileRepository;

    @Autowired S3Service s3Service;

    // REGISTER AND CREATE PROFILE
    @Transactional
    public JsonObject registerUserCreateProfile(UserModel user)
    {
        // Try find username
        System.out.println("username is >>> " + user.getUsername());

        Optional<UserModel> userNameOpt = userRepository.findByUsername(user.getUsername());
        if (!userNameOpt.isEmpty())
        {
            return Json.createObjectBuilder().add("error", "username already exist..").build();
        }

        // Try find user email
        Optional<UserModel> userEmailOpt = userRepository.findByEmail(user.getEmail());
        if (!userEmailOpt.isEmpty())
        {
            return Json.createObjectBuilder().add("error", "email already exist..").build();
        }

        // REGISTER NEW USER
        int usersInserted = userRepository.registerNewUser(user);

        if (usersInserted < 1)
        {
            // rollback tx
            throw new RuntimeException("Unexpected error.. Failed to register new user..");
        }


        // Try get user ID from new user 
        Optional<UserModel> newUserOpt = userRepository.findByUsername(user.getUsername());
        if (newUserOpt.isEmpty())
        {
            return Json.createObjectBuilder().add("error", "Cannot find new user..").build();
        }

        Integer userId = newUserOpt.get().getId();

        // CREATE PROFILE

        boolean bCreated = profileRepository.createProfile(userId);
        if (!bCreated)
        {
            // rollback tx
            throw new RuntimeException("Unexpected error.. Failed to create new profile..");
        }
        
        return Json.createObjectBuilder().add("success", "username: " + user.getUsername()).build();
    }



    public Optional<Profile> getProfileByUserId (Integer userId) throws Exception
    {
        try {
            return profileRepository.getProfileByUserId(userId);
        } catch (Exception e) {
            throw new Exception("failed to create profile for user id: " + userId + ". Error: " + e.getMessage());
        }
        
    }

    @Transactional
    public boolean updateProfile(Integer userId, String firstName, String lastName, MultipartFile profilePicFile) throws Exception
    {
        try {
            // Upload to S3 get url
            String profilePicUrl = s3Service.uploadProfilePic(profilePicFile, userId);

            // Update mysql
            return profileRepository.updateProfile(userId, firstName, lastName, profilePicUrl);
        } catch (Exception e) {
            throw new Exception("failed to update profile for user id: " + userId + ". Error: " + e.getMessage());
        }
    }

    public boolean updateProfileWithoutPic(Integer userId, String firstName, String lastName) throws Exception
    {
        try {
            return profileRepository.updateProfileWithoutPic(userId, firstName, lastName);
        } catch (Exception e) {
            throw new Exception("failed to update profile for user id: " + userId + ". Error: " + e.getMessage());
        }
    }

    @Transactional
    public boolean removeProfilePic(Integer userId) throws Exception
    {
        try {
            // Find profile url in mysql
            Optional<String> existingPicUrlOpt = profileRepository.getProfilePicUrl(userId);
            
            // delete from s3
            s3Service.deleteFile(existingPicUrlOpt.get());

            return profileRepository.removeProfilePic(userId);
        } catch (Exception e) {
            throw new Exception("failed to delete profile pic for user id: " + userId + ". Error: " + e.getMessage());
        }
    }    
    
}

