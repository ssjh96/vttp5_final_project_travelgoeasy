package vttp5.batcha.travelgoeasy.server.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public class UserModel 
{
    private Integer id;
    private String username;   
    private String password;    
    private String email;
    private String role;
    private Boolean isPro;
    private Date created_at;
    
    public UserModel() {
    }

    // public UserModel(Integer id, String username, String password, String email, Date created_at) {
    //     this.id = id;
    //     this.username = username;
    //     this.password = password;
    //     this.email = email;
    //     this.role = "USER";
    //     this.isPro = false;
    //     this.created_at = created_at;
    // }
    

    // User defaults: id = auto_incr | role = USER | is_pro = false | created_at = current time
    public UserModel(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        
    }

    public UserModel(Integer id, String username, String password, String email, String role, Boolean isPro, Date created_at) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.isPro = isPro;
        this.created_at = created_at;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getIsPro() {
        return isPro;
    }

    public void setIsPro(Boolean isPro) {
        this.isPro = isPro;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }



    public static UserModel toUser(SqlRowSet rs)
    {
        
        UserModel user = new UserModel();

        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        user.setIsPro(rs.getBoolean("is_pro"));
        // user.setCreated_at(rs.getDate("null") // timestamp format YYYY-MM-DD HH:MM:SS
        user.setCreated_at(rs.getTimestamp("created_at"));

        return user;
    }


    
    public String getFormattedCreatedAt() 
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(created_at);
    }
    
}
