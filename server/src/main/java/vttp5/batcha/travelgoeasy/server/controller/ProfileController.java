package vttp5.batcha.travelgoeasy.server.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import vttp5.batcha.travelgoeasy.server.model.Profile;
import vttp5.batcha.travelgoeasy.server.service.RankService;
import vttp5.batcha.travelgoeasy.server.service.UserProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/api/profile")
public class ProfileController 
{
    @Autowired
    private UserProfileService userProfileService;

    @Autowired RankService rankService;

    public final static String BASE64_PREFIX = "data:image/jpeg;base64,";

    @GetMapping("/get/{userId}")
    public ResponseEntity<String> getProfile(@PathVariable String userId) 
    {
        try {
            Optional<Profile> profileOpt = userProfileService.getProfileByUserId(Integer.parseInt(userId));

            if (profileOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Json.createObjectBuilder().add("error", "user profile not found").build().toString());
            }

            Profile profile = profileOpt.get();
        
            JsonObjectBuilder job = Json.createObjectBuilder();

            // initial new profile values are null
            if(profile.getFirstName() == null) {
                job.add("firstName", "");
            } else {
                job.add("firstName", profile.getFirstName());
            }

            if(profile.getLastName() == null) {
                job.add("lastName", "");
            } else {
                job.add("lastName", profile.getLastName());
            }

            if (profile.getProfilePicUrl() != null)
            {
                job.add("profilePicture", profile.getProfilePicUrl());
            }
            else
            {
                job.addNull("profilePicture");
            }

            JsonObject payload = job.build();
            return ResponseEntity.ok(payload.toString());
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
    
    @PutMapping(path = "/update/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                                            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateProfile(@PathVariable String userId, 
                                                @RequestPart String firstName,
                                                @RequestPart String lastName,
                                                @RequestPart (required = false) MultipartFile profilePicFile) 
    {
        try {
            boolean bUpdated;
            if(profilePicFile != null && !profilePicFile.isEmpty()) // check if exist and not 0 bytes
            {
                bUpdated = userProfileService.updateProfile(Integer.parseInt(userId), firstName, lastName, profilePicFile);
            }

            else
            {
                bUpdated = userProfileService.updateProfileWithoutPic(Integer.parseInt(userId), firstName, lastName);
            }

            if (!bUpdated)
            {
                return ResponseEntity.badRequest().body(Json.createObjectBuilder()
                                        .add("error","Cannot update user profile")
                                        .build().toString());
            }

            return ResponseEntity.ok(Json.createObjectBuilder()
                                    .add("success","profile updated successfully")
                                    .build().toString());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Json.createObjectBuilder()
                                                        .add("error", e.getMessage())
                                                        .build().toString());
        }
    }

    

    @PutMapping("/removePic/{userId}")
    public ResponseEntity<String> removeProfilePic(@PathVariable String userId) 
    {
        try {
            boolean bRemoved = userProfileService.removeProfilePic(Integer.parseInt(userId));
            System.out.println("removing pic");

            if(!bRemoved)
            {
                return ResponseEntity.badRequest().body(Json.createObjectBuilder()
                                        .add("error","Cannot remove profile pic")
                                        .build().toString());
            }

            return ResponseEntity.ok(Json.createObjectBuilder()
                                    .add("success","profile picture removed successfully")
                                    .build().toString());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Json.createObjectBuilder()
                                                        .add("error", e.getMessage())
                                                        .build().toString());
        }
    }
    
}
