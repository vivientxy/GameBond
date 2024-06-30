package tfip.project.model;

import java.util.Date;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMembership {
    private String username;
    private int membership;
    private Date membershipDate;

    public JsonObject toJson() {
        return Json.createObjectBuilder()
            .add("username", username)
            .add("tier", membership)
            .add("membershipDate", membershipDate.toString())
            .build();
    }
}
