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


@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userSvc;

    @PostMapping(path = {"/register"})
    public ResponseEntity<Boolean> registerNewUser(@RequestBody String json) {
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();

        User user = new User();
        user.setUsername(object.getString("username"));
        user.setPassword(object.getString("password"));
        user.setEmail(object.getString("email"));
        user.setActive(true);
        if (!object.isNull("firstName"))
            user.setFirstName(object.getString("firstName"));
        if (!object.isNull("lastName"))
            user.setLastName(object.getString("lastName"));

        try {
            boolean registered = userSvc.registerUser(user);
            return new ResponseEntity<Boolean>(registered, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<Boolean>(false, HttpStatus.CONFLICT);
        }
    }
    
    
}
