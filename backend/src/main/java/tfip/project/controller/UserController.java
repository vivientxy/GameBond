package tfip.project.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.StringReader;

import tfip.project.model.User;
import tfip.project.service.MailService;
import tfip.project.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userSvc;

    @Autowired
	MailService mailSvc;

    @PostMapping(path = {"/register"})
    public ResponseEntity<String> registerNewUser(@RequestBody String json) {
        User user = jsonToUser(json);
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
        User retrievedUser = userSvc.getUserByUsername(formUser.getUsername());
        try {
            if (formUser.getPassword().equals(retrievedUser.getPassword()))
                return new ResponseEntity<String>(HttpStatus.OK);
        } catch (NullPointerException e) {
            return new ResponseEntity<String>("Username not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>("Wrong password", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping(path = {"/reset"})
    public ResponseEntity<String> resetPassword(@RequestBody String json) {
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
