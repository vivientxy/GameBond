package tfip.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDetails {
    private String gameId;
    private String gameTitle;
    private String romFile;
    private String pictureUrl;
}
