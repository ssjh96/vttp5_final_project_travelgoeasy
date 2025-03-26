package vttp5.batcha.travelgoeasy.server.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Profile implements Serializable
{
    private String profileId;
    private Integer userId;
    private String firstName;
    private String lastName;
    // private byte[] profilePic;
    private String profilePicUrl;

    public Profile() {
    }

    public Profile(String profileId, Integer userId, String firstName, String lastName, String profilePicUrl) {
        this.profileId = profileId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicUrl = profilePicUrl;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    // HELPER METHOD
    public static Profile toProfile(ResultSet rs) throws SQLException
    {
        Profile profile = new Profile();

        profile.setProfileId(rs.getString("profile_id"));
        profile.setUserId(rs.getInt("user_id"));
        profile.setFirstName(rs.getString("first_name"));
        profile.setLastName(rs.getString("last_name"));
        profile.setProfilePicUrl(rs.getString("profile_picture"));

        return profile;
    }



    
    
}
