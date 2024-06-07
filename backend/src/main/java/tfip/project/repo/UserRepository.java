package tfip.project.repo;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tfip.project.model.User;
import tfip.project.model.UserMembership;

@Repository
public class UserRepository implements SqlQueries {

    @Autowired
    JdbcTemplate template;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

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
            return template.queryForObject(SQL_GET_USER_BY_USERNAME, BeanPropertyRowMapper.newInstance(User.class),
                    username);
        } catch (Exception e) {
            return null;
        }
    }

    public User getUserByEmail(String email) {
        try {
            return template.queryForObject(SQL_GET_USER_BY_EMAIL, BeanPropertyRowMapper.newInstance(User.class), email);
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

    public boolean createUserMembership(String username, int membershipLevel) {
        try {
            this.template.update(SQL_ADD_USER_MEMBERSHIP, username, membershipLevel);
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    public boolean createUserMembership(UserMembership membership) {
        try {
            this.template.update(SQL_ADD_USER_MEMBERSHIP, membership.getUsername(), membership.getMembership());
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    public UserMembership getMembership(String username) {
        try {
            return template.queryForObject(SQL_GET_USER_BY_USERNAME,
                    BeanPropertyRowMapper.newInstance(UserMembership.class), username);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean updateMembership(UserMembership membership) {
        return template.update(SQL_UPDATE_MEMBERSHIP, membership.getMembership(), new Date(),
                membership.getUsername()) > 0 ? true : false;
    }

    public boolean deleteMembership(String username) {
        return template.update(SQL_UPDATE_MEMBERSHIP, 0, new Date(), username) > 0 ? true : false;
    }

    // REDIS RESET LINK

    public void saveResetLink(String resetId, String username) {
        ValueOperations<String,String> valueOps = redisTemplate.opsForValue();
        valueOps.set(resetId, username, 30, TimeUnit.MINUTES);
    }

    public String validateResetLink(String resetId) {
        ValueOperations<String,String> valueOps = redisTemplate.opsForValue();
        return valueOps.get(resetId).toString();
    }

}
