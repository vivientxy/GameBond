package tfip.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tfip.project.model.User;
import tfip.project.repo.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepo;

    public boolean doesUserExist(String username) {
        return userRepo.getUserByUsername(username) != null;
    }

    @Transactional
    public boolean registerUser(User user) {
        boolean userCreated = userRepo.createUser(user);
        boolean membershipCreated = userRepo.createUserMembership(user.getUsername(), 0);
        if (!userCreated || !membershipCreated)
            throw new RuntimeException("Rolling back transaction - User/Membership creation failed");
        return true;
    }

    


}
