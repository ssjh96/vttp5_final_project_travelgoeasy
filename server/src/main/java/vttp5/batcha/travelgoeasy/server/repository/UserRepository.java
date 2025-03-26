package vttp5.batcha.travelgoeasy.server.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import vttp5.batcha.travelgoeasy.server.model.UserModel;

@Repository
public class UserRepository 
{
    @Autowired 
    private JdbcTemplate jdbcTemplate;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static final String SQL_REGISTER_USER = """
                                                    INSERT INTO users 
                                                        (username, password, email)
                                                    VALUES 
                                                        (?, ?, ?)
                                                    """;
    // Register new user
    public Integer registerNewUser (UserModel user)
    {
        if(user.getRole() == null)
        {
            user.setRole("USER"); // assign default role to new user
        }

        Integer userRegister = jdbcTemplate.update(SQL_REGISTER_USER, 
                                                user.getUsername(), 
                                                passwordEncoder.encode(user.getPassword()),
                                                user.getEmail()                           
                                                );

        return userRegister;

    }

    public static final String SQL_FIND_BY_USERNAME = "SELECT * FROM users WHERE username = ?";
    public Optional<UserModel> findByUsername(String username)
    {
        System.out.println("Query: " + SQL_FIND_BY_USERNAME + " | username: " + username);
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_FIND_BY_USERNAME, username);

        if(!rs.next())
        {
            System.out.println("User not found for username: " + username);
            return Optional.empty();
        }

        UserModel user = UserModel.toUser(rs);
        System.out.println("User found: " + user.getUsername());

        return Optional.of(user);
    }


    public static final String SQL_FIND_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
    public Optional<UserModel> findByEmail(String email)
    {
        System.out.println("Query: " + SQL_FIND_BY_EMAIL + " | email: " + email);
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_FIND_BY_EMAIL, email);

        if(!rs.next())
        {
            return Optional.empty();
        }

        UserModel user = UserModel.toUser(rs);
        System.out.println("User found: " + user.getEmail());

        return Optional.of(user);
    }

    // delete user
    public static final String SQL_DELETE_USER_BY_ID= "DELETE FROM users WHERE id = ?";
    public boolean deleteUserById(Integer userId)
    {
        int rowsDeleted = jdbcTemplate.update(SQL_DELETE_USER_BY_ID, userId);
        return rowsDeleted > 0;

    }

    // update pro status
    public static final String SQL_UPDATE_PRO_STATUS = "UPDATE users SET is_pro = ? WHERE id = ?";
    public boolean updateProStatus(Integer userId, boolean updatedProStatus)
    {
        int rowsUpdated = jdbcTemplate.update(SQL_UPDATE_PRO_STATUS, updatedProStatus, userId);
        return rowsUpdated > 0;
    }
}
