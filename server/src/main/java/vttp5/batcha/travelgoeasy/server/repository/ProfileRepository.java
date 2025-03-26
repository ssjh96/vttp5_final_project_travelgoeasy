package vttp5.batcha.travelgoeasy.server.repository;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import vttp5.batcha.travelgoeasy.server.model.Profile;

@Repository
public class ProfileRepository 
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static final String SQL_CREATE_PROFILE = "INSERT INTO profile (profile_id, user_id) VALUES (?, ?)";

    public boolean createProfile(Integer userId)
    {
        String profileId = UUID.randomUUID()
                                .toString()
                                .replace("-", "")
                                .substring(0, 8);
    
        int rowsCreated = jdbcTemplate.update(SQL_CREATE_PROFILE, profileId, userId);
        return rowsCreated > 0;
    }



    public static final String SQL_FIND_PROFILE_BY_USER_ID = 
        "SELECT * FROM profile WHERE user_id = ?";

    public Optional<Profile> getProfileByUserId(Integer userId) 
    {

        return jdbcTemplate.query(SQL_FIND_PROFILE_BY_USER_ID, 
            (ResultSet rs) -> {
                if(!rs.next())
                {
                    return Optional.empty();
                }

                Profile profile = Profile.toProfile(rs);
                return Optional.of(profile);
                
                }, userId);
    }



    public static final String SQL_UPDATE_PROFILE = 
        "UPDATE profile SET first_name = ?, last_name = ?, profile_picture = ? WHERE user_id = ?";

    // Update profile with new profile pic
    public boolean updateProfile(Integer userId, String firstName, String lastName, String profilePicUrl) throws IOException 
    {
        // byte[] profilePicBytes = profilePic.getBytes();
        int rowsUpdated = jdbcTemplate.update(SQL_UPDATE_PROFILE, firstName, lastName, profilePicUrl, userId);
        return rowsUpdated > 0;
    }

    public static final String SQL_GET_PROFILE_PIC_URL = 
        "SELECT profile_picture FROM profile WHERE user_id = ?";
    
    public Optional<String> getProfilePicUrl (Integer userId)
    {
        return jdbcTemplate.query(SQL_GET_PROFILE_PIC_URL, 
        (ResultSet rs) -> {
                if(!rs.next())
                {
                    return Optional.empty();
                }

                return Optional.of(rs.getString("profile_picture"));
                
                }, userId);
    }



    public static final String SQL_UPDATE_PROFILE_WITHOUT_PIC = 
    "UPDATE profile SET first_name = ?, last_name = ? WHERE user_id = ?";

    // Update profile without profile pic
    public boolean updateProfileWithoutPic(Integer userId, String firstName, String lastName) 
    {
        int rowsAffected = jdbcTemplate.update(SQL_UPDATE_PROFILE_WITHOUT_PIC, 
            firstName, 
            lastName, 
            userId);
        
        return rowsAffected > 0;
    }



    public static final String SQL_REMOVE_PROFILE_PIC = 
    "UPDATE profile SET profile_picture = NULL WHERE user_id = ?";

    // Remove profile picture 
    public boolean removeProfilePic(Integer userId) 
    {
        int rowsAffected = jdbcTemplate.update(SQL_REMOVE_PROFILE_PIC, userId);
        return rowsAffected > 0;
    }


}
