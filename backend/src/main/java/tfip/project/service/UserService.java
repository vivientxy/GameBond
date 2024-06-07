package tfip.project.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tfip.project.model.User;
import tfip.project.repo.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepo;

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
        return userRepo.updateUser(user);
    }

    @Transactional
    public boolean registerUser(User user) {
        boolean userCreated = userRepo.createUser(user);
        boolean membershipCreated = userRepo.createUserMembership(user.getUsername(), 0);
        if (!userCreated || !membershipCreated)
            throw new RuntimeException("Rolling back transaction - User/Membership creation failed");
        return true;
    }

    public String generateResetLink(User user) {
        String resetId = UUID.randomUUID().toString().substring(0,18);
        userRepo.saveResetLink(resetId, user.getUsername());
        return projectUrl + "reset/" + resetId;
    }

    public User validateResetLink(String resetId) {
        String username = userRepo.validateResetLink(resetId);
        if (username == null)
            return null;
        return getUserByUsername(username);
    }

}
