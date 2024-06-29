package tfip.project.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import tfip.project.model.User;
import tfip.project.service.MailService;
import tfip.project.service.StripeService;
import tfip.project.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userSvc;

    @Autowired
	MailService mailSvc;

    @Autowired
    private StripeService stripeSvc;

    @PostMapping(path = {"/register"})
    public ResponseEntity<String> registerNewUser(@RequestBody String json) {
        User user = jsonToUser(json);
        System.out.println(">>> register user:" + user);
        try {
            userSvc.registerUser(user);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (RuntimeException e) {
            if (userSvc.doesUserExistByUsername(user.getUsername()))
                return new ResponseEntity<String>("Username already in use", HttpStatus.CONFLICT);
            if (userSvc.doesUserExistByEmail(user.getEmail()))
                return new ResponseEntity<String>("Email already in use", HttpStatus.CONFLICT);
            return new ResponseEntity<String>("Error unrelated to username and email", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = {"/login"})
    public ResponseEntity<String> verifyLogin(@RequestBody String json) {
        User formUser = jsonToUser(json);
        Boolean isLoginSuccess = userSvc.validateLogin(formUser.getUsername(), formUser.getPassword());
        System.out.println(">>> isLoginSuccess:" + isLoginSuccess);

        if (isLoginSuccess == null)
            return new ResponseEntity<String>("Username not found", HttpStatus.NOT_FOUND);
        if (!isLoginSuccess)
            return new ResponseEntity<String>("Wrong password", HttpStatus.UNAUTHORIZED);

        User retrievedUser = userSvc.getUserByUsername(formUser.getUsername());
        retrievedUser.setPassword("");
        System.out.println(">>> retrievedUser:" + retrievedUser.toJson().toString());
        
        return new ResponseEntity<String>(retrievedUser.toJson().toString(), HttpStatus.OK);
    }

    @PostMapping(path = {"/reset"})
    public ResponseEntity<String> generateResetPasswordEmail(@RequestBody String json) {
        User formUser = jsonToUser(json);
        User retrievedUser = null;

        if (!formUser.getUsername().isBlank())
            retrievedUser = userSvc.getUserByUsername(formUser.getUsername());
        if (!formUser.getEmail().isBlank())
            retrievedUser = userSvc.getUserByEmail(formUser.getEmail());

        if (retrievedUser != null) {
            String resetLink = userSvc.generateResetLink(retrievedUser);
            mailSvc.sendPasswordResetMail(retrievedUser.getEmail(), retrievedUser.getFirstName(), resetLink);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @GetMapping(path = {"/reset/{resetId}"})
    public ResponseEntity<String> validateResetId(@PathVariable String resetId) {
        User user = userSvc.validateResetLink(resetId);
        if (user == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<String>(user.toJson().toString(), HttpStatus.OK);
    }

    @PutMapping(path = {"/reset"})
    public ResponseEntity<String> setNewPassword(@RequestBody String json) {
        User formUser = jsonToUser(json);
        boolean updateSuccess = userSvc.updateUser(formUser);
        if (!updateSuccess)
            return new ResponseEntity<String>(HttpStatus.NOT_MODIFIED);
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @PostMapping("/payment/create-session")
    public Session createCheckoutSession() throws Exception {
        return stripeSvc.createCheckoutSession("Lite", 299L);
    }

    @PostMapping("/create-payment-intent")
    public Map<String, String> createPaymentIntent(@RequestBody Map<String, Object> data) {
        try {
            long amount = (Long) data.get("amount");
            PaymentIntent paymentIntent = stripeSvc.createPaymentIntent(amount);
            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    private User jsonToUser(String jsonString) {
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();
        User user = new User();
        if (!object.isNull("username"))
            user.setUsername(object.getString("username"));
        if (!object.isNull("password"))
            user.setPassword(object.getString("password"));
        if (!object.isNull("email"))
            user.setEmail(object.getString("email"));
        if (!object.isNull("firstName"))
            user.setFirstName(object.getString("firstName"));
        if (!object.isNull("lastName"))
            user.setLastName(object.getString("lastName"));
        user.setActive(true);
        return user;
    }
    
}
