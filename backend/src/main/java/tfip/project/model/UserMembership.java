package tfip.project.model;

import java.util.Date;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMembership {
    private String username;
    private int tier;
    private Date membershipDate;
    private int monthlyGamesEntitlement;
    private int romEntitlement;

    public int getMonthlyGamesEntitlementByTier() {
        switch (tier) {
            case 0:
                return 5;
            case 1:
                return 20;
            case 2:
                return 50;
            case 3:
                return 100;
            default:
                return 0;
        }
    }

    public int getRomEntitlementByTier() {
        switch (tier) {
            case 0:
                return 5;
            case 1:
                return 8;
            case 2:
                return 10;
            case 3:
                return 15;
            default:
                return 0;
        }
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
            .add("username", username)
            .add("tier", tier)
            .add("membershipDate", membershipDate.toString())
            .add("monthlyGamesEntitlement", getMonthlyGamesEntitlementByTier())
            .add("romEntitlement", getRomEntitlementByTier())
            .build();
    }
}
