package tfip.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mashape.unirest.http.exceptions.UnirestException;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import tfip.project.model.GameDetails;
import tfip.project.service.GameService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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

    @PostMapping("/get-QR")
    public ResponseEntity<String> getQRCode(@RequestBody String telegramUrl) {
        try {
            String qrCode = gameSvc.getQRCode(telegramUrl);
            JsonObject jsonObject = Json.createObjectBuilder().add("qr", qrCode).build();
            return new ResponseEntity<String>(jsonObject.toString(), HttpStatus.OK);
        } catch (UnirestException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
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
