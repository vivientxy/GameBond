package tfip.project.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.StringReader;

import tfip.project.model.User;
import tfip.project.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userSvc;

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
        if (retrievedUser.getPassword() == null)
            return new ResponseEntity<String>("Username not found", HttpStatus.NOT_FOUND);
        if (formUser.getPassword().equals(retrievedUser.getPassword()))
            return new ResponseEntity<String>(HttpStatus.OK);
        return new ResponseEntity<String>("Wrong password", HttpStatus.UNAUTHORIZED);
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
