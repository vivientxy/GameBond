package tfip.project.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tfip.project.model.User;
import tfip.project.model.UserMembership;
import tfip.project.repo.UserRepository;

@Service
public class MembershipService {

    @Autowired
    UserService userSvc;

    @Autowired
    UserRepository userRepo;

    public UserMembership getUserMembership(String email) {
        User user = userSvc.getUserByEmail(email);
        System.out.println(">>> user: " + user.toString());
        String username = user.getUsername();
        return userRepo.getMembership(username);
    }

    public UserMembership updateUserMembership(String email, Integer tier, Long epochDate) {
        String username = userSvc.getUserByEmail(email).getUsername();
        Date date = new Date(epochDate * 1000);
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
