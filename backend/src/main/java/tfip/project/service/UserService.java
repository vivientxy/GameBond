package tfip.project.service;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tfip.project.model.User;
import tfip.project.model.UserMembership;
import tfip.project.repo.GameRepository;
import tfip.project.repo.RedisRepository;
import tfip.project.repo.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RedisRepository redisRepo;

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${project.url}")
    private String projectUrl;

    public User getUserByUsername(String username) {
        return userRepo.getUserByUsername(username);
    }

    public User getUserByEmail(String email) {
        return userRepo.getUserByEmail(email);
    }

    public boolean doesUserExistByUsername(String username) {
        return userRepo.getUserByUsername(username) != null;
    }

    public boolean doesUserExistByEmail(String email) {
        return userRepo.getUserByEmail(email) != null;
    }

    public boolean updateUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepo.updateUser(user);
    }

    @Transactional
    public boolean registerUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        System.out.println(">>> service: register user:" + user);
        boolean userCreated = userRepo.createUser(user);
        boolean membershipCreated = userRepo.createNewUserMembership(user.getUsername());
        boolean defaultGamesAdded = gameRepo.saveDefaultGamesToNewUser(user.getUsername());
        if (!userCreated || !membershipCreated || !defaultGamesAdded)
            throw new RuntimeException("Rolling back transaction - User/Membership creation failed");
        return true;
    }

    public Boolean validateLogin(String username, String rawPassword) {
        User user = userRepo.getUserByUsername(username);
        if (user == null)
            return null;
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public String generateResetLink(User user) {
        String resetId = UUID.randomUUID().toString().substring(0,18);
        redisRepo.saveResetLink(resetId, user.getUsername());
        return projectUrl + "/#/reset/" + resetId;
    }

    public User validateResetLink(String resetId) {
        String username = redisRepo.validateResetLink(resetId);
        if (username == null)
            return null;
        return getUserByUsername(username);
    }

    public UserMembership updateUserMembership(String email, Integer tier, Long epochDate) {
        String username = getUserByEmail(email).getUsername();
        Date date = new Date(epochDate * 1000);
        System.out.println(">>> epochDate: " + epochDate);
        System.out.println(">>> date: " + date);
        UserMembership membership = new UserMembership();
        membership.setUsername(username);
        membership.setTier(tier);
        membership.setMembershipDate(date);
        membership.setMonthlyGamesEntitlement(membership.getMonthlyGamesEntitlementByTier());
        membership.setRomEntitlement(membership.getRomEntitlementByTier());
        boolean isUpdated = userRepo.updateMembership(membership);
        if (isUpdated) 
            return membership;
        return null;
    }

}
