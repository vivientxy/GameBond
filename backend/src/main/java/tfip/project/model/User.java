package tfip.project.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private boolean active;

    public JsonObject toJson() {
        if (username == null) username = "";
        if (password == null) password = "";
        if (email == null) email = "";
        if (firstName == null) firstName = "";
        if (lastName == null) lastName = "";
        return Json.createObjectBuilder()
            .add("username", username)
            .add("password", password)
            .add("email", email)
            .add("firstName", firstName)
            .add("lastName", lastName)
            .build();
    }
}