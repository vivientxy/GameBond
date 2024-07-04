package tfip.project.repo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import tfip.project.model.User;
import tfip.project.model.UserMembership;

@Repository
public class UserRepository implements SqlQueries {

    @Autowired
    JdbcTemplate template;

    // USERS

    public boolean createUser(User user) {
        try {
            this.template.update(SQL_ADD_USER, user.getUsername(), user.getPassword(), user.getEmail(),
                    user.getFirstName(), user.getLastName());
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    public User getUserByUsername(String username) {
        try {
            return template.queryForObject(SQL_GET_USER_BY_USERNAME, new UserRowMapper(), username);
        } catch (Exception e) {
            return null;
        }
    }

    public User getUserByEmail(String email) {
        try {
            return template.queryForObject(SQL_GET_USER_BY_EMAIL, new UserRowMapper(), email);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean updateUser(User user) {
        return template.update(SQL_UPDATE_USER, user.getPassword(), user.getEmail(), user.getFirstName(),
                user.getLastName(), user.isActive(), user.getUsername()) > 0 ? true : false;
    }

    public boolean deleteUser(String username) {
        return template.update(SQL_DELETE_USER, username) > 0 ? true : false;
    }

    public boolean reactivateUser(String username) {
        return template.update(SQL_REACTIVATE_USER, username) > 0 ? true : false;
    }

    // MEMBERSHIP

    public boolean createNewUserMembership(String username) {
        try {
            this.template.update(SQL_ADD_NEW_USER_MEMBERSHIP, username);
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    public boolean createNewUserMembership(UserMembership membership) {
        try {
            this.template.update(SQL_ADD_NEW_USER_MEMBERSHIP, membership.getUsername());
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    public UserMembership getMembership(String username) {
        try {
            SqlRowSet rs = template.queryForRowSet(SQL_GET_MEMBERSHIP, username);
            UserMembership membership = new UserMembership();
            if (rs.first()) {
                membership.setUsername(rs.getString("username"));
                membership.setTier(rs.getInt("tier"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date date = sdf.parse(rs.getString("membership_date"));
                membership.setMembershipDate(date);
                membership.setMonthlyGamesEntitlement(rs.getInt("monthly_games_entitlement"));
                membership.setRomEntitlement(rs.getInt("rom_entitlement"));
            }
            return membership;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateMembership(UserMembership membership) {
        return template.update(SQL_UPDATE_MEMBERSHIP, membership.getTier(), membership.getMembershipDate(),
                membership.getMonthlyGamesEntitlementByTier(), membership.getRomEntitlementByTier(),
                membership.getUsername()) > 0 ? true : false;
    }

    public boolean deleteMembership(String username) {
        return template.update(SQL_UPDATE_MEMBERSHIP, 0, new Date(), 5, 5, username) > 0 ? true : false;
    }

    // ROM COUNT
    public Integer checkRomByUser(String username) {
        try {
            return template.queryForObject(SQL_ROM_COUNT_BY_USER, Integer.class, username);
        } catch (Exception e) {
            return null;
        }
    }

    // HOSTED GAMES COUNT
    public Integer checkHostedGamesByUser(String username) {
        try {
            return template.queryForObject(SQL_HOSTED_GAMES_COUNT_BY_USER, Integer.class, username);
        } catch (Exception e) {
            return null;
        }
    }

    /* HELPER METHODS */

    private class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setEmail(rs.getString("email"));
            user.setFirstName(rs.getString("firstname"));
            user.setLastName(rs.getString("lastname"));
            user.setActive(rs.getBoolean("active"));
            return user;
        }
    }

}
