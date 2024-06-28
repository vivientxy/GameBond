package tfip.project.controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/get-rom")
    public ResponseEntity<String> getRom(@RequestBody String gameId) {
        GameDetails game = gameSvc.getGameDetailsByGameId(gameId);
        String romUrl = game.getRomFile();
        JsonObject json = Json.createObjectBuilder().add("romUrl", romUrl).build();
        return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    }

    @PostMapping(path = {"/add-rom"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addRom(@RequestPart MultipartFile rom, @RequestPart String username) {
        try {
            gameSvc.saveGameRom(username, rom);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/get-QR")
    public ResponseEntity<String> getQRCode(@RequestBody String telegramUrl) {
        try {
            String qrCode = gameSvc.getQRCode(telegramUrl);
            JsonObject jsonObject = Json.createObjectBuilder().add("qr", qrCode).build();
            createNewHostInRedis(telegramUrl);
            return new ResponseEntity<String>(jsonObject.toString(), HttpStatus.OK);
        } catch (UnirestException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/get-team-members")
    public ResponseEntity<String> getTeamMembers(@RequestBody String hostId) {
        System.out.println(">>> /api/get-team-members called: " + hostId);
        Map<String, List<String>> teamMap = gameSvc.getPlayersInTeams(hostId);
        JsonObject json = mapToJsonObject(teamMap);
        return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    }

    @GetMapping("/end-game")
    public ResponseEntity<String> endGame(@RequestParam(required = true) String hostId) {
        System.out.println(">>> triggered /api/end-game for hostId: " + hostId);
        gameSvc.deleteGame(hostId);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    

    /* HELPER METHODS */

    private void createNewHostInRedis(String telegramUrl) {
        String payload = telegramUrl.substring(32);
        String decodedPayload = new String(Base64.getDecoder().decode(payload), StandardCharsets.UTF_8);
        String hostId = decodedPayload.substring(7);
        Integer numOfTeams = Integer.parseInt(hostId.substring(7));
        gameSvc.createNewHost(hostId, numOfTeams);
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
    
    private JsonObject mapToJsonObject(Map<String,List<String>> teamMap) {
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        for (Map.Entry<String, List<String>> entry : teamMap.entrySet()) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (String value : entry.getValue())
                arrayBuilder.add(value);
            objectBuilder.add(entry.getKey(), arrayBuilder);
        }
        return objectBuilder.build();
    }
    
    
}
