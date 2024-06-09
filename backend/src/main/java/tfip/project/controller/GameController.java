package tfip.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import tfip.project.model.GameDetails;
import tfip.project.service.GameService;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api")
public class GameController {

    @Autowired
    GameService gameSvc;

    @GetMapping("/get-all-games")
    public ResponseEntity<String> getAllGames() {
        List<GameDetails> gameList = gameSvc.getAllGames();
        JsonArray gameArray = listToJsonArray(gameList);
        return new ResponseEntity<String>(gameArray.toString(), HttpStatus.OK);
    }



    private JsonArray listToJsonArray(List<GameDetails> gameList) {
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        for (GameDetails game : gameList) {
            JsonObjectBuilder jBuilder = Json.createObjectBuilder()
                .add("gameId", game.getGameId())
                .add("gameTitle", game.getGameTitle())
                .add("romFile", game.getRomFile())
                .add("pictureUrl", game.getPictureUrl());
            arrBuilder.add(jBuilder);
        }
        return arrBuilder.build();
    }
    
    
}
