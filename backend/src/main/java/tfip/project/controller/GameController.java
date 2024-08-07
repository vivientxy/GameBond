package tfip.project.controller;

import java.io.StringReader;
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

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
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
    public ResponseEntity<String> getAllGamesByUsername(@RequestParam String username) {
        List<GameDetails> gameList = gameSvc.getAllGamesByUsername(username);
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
    public ResponseEntity<String> addRom(@RequestPart MultipartFile rom, @RequestPart(required = false) MultipartFile pic, @RequestPart String username) {
        try {
            if (pic == null)
                gameSvc.saveGameRom(username, rom);
            else
                gameSvc.saveGameRom(username, rom, pic);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/get-QR")
    public ResponseEntity<String> getQRCode(@RequestBody String telegramUrl) {
        try {
            String qrCode = gameSvc.getQRCode(telegramUrl);
            JsonObject jsonObject = Json.createObjectBuilder().add("qr", qrCode).build();
            createNewHostInRedis(telegramUrl);
            return new ResponseEntity<String>(jsonObject.toString(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/host/add-hosted-game")
    public ResponseEntity<String> addHostedGameToUser(@RequestBody String body) {
        try {
            JsonReader jsonReader = Json.createReader(new StringReader(body));
            JsonObject json = jsonReader.readObject();
            jsonReader.close();

            String username = json.getString("username");
            String hostId = json.getString("hostId");
            String gameId = json.getString("gameId");
            int numOfTeams = Integer.parseInt(json.getString("numOfTeams"));

            boolean gameSaved = gameSvc.saveHostGameDetails(username, hostId, gameId, numOfTeams);
            if (gameSaved)
                return new ResponseEntity<String>(HttpStatus.OK);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/host/check-monthly-limit")
    public ResponseEntity<String> checkMonthlyLimit(@RequestBody String username) {
        try {
            boolean isHostLimitExceeded = gameSvc.isHostLimit(username);
            if (isHostLimitExceeded)
                return new ResponseEntity<String>("User has hit max monthly host game limit allowed for membership tier. Please upgrade membership to host more games.", HttpStatus.UNAUTHORIZED);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/get-team-members")
    public ResponseEntity<String> getTeamMembers(@RequestBody String hostId) {
        Map<String, List<String>> teamMap = gameSvc.getPlayersInTeams(hostId);
        JsonObject json = mapToJsonObject(teamMap);
        return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    }

    @GetMapping("/end-game")
    public ResponseEntity<String> endGame(@RequestParam(required = true) String hostId) {
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
                .add("romFile", game.getRomFile());
            if (game.getPictureUrl() == null)
                game.setPictureUrl("");
                jBuilder.add("pictureUrl", game.getPictureUrl());
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
