package tfip.project.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import tfip.project.model.GameDetails;
import tfip.project.repo.GameRepository;
import tfip.project.repo.RedisRepository;
import tfip.project.repo.S3Repository;

@Service
public class GameService {
    
    @Value("${qr.api.key}")
    private String qrApiKey;
    @Value("${qr.workspace.id}")
    private String qrWorkspaceId;

    @Autowired
    GameRepository gameRepo;

    @Autowired
    RedisRepository redisRepo;
    
    @Autowired
    S3Repository s3Repo;

    public List<GameDetails> getAllGames() {
        return gameRepo.getAllGames();
    }

    @Transactional
    public boolean saveGameRom(String username, MultipartFile rom) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        String s3url;
        try {
            s3url = s3Repo.saveToS3(rom, id);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Rolling back transaction - failed to save to S3");
        }

        GameDetails game = new GameDetails();
        game.setGameId(id);
        game.setRomFile(s3url);
        game.setGameTitle(rom.getOriginalFilename());

        boolean isGameAdded = gameRepo.saveGame(game);
        boolean isGameAddedToUser = gameRepo.saveGameToUser(username, game);
        if (!isGameAdded || !isGameAddedToUser)
            throw new RuntimeException("Rolling back transaction - failed to save to MySQL");

        return true;
    }

    public GameDetails getGameDetailsByGameId(String gameId) {
        return gameRepo.getGameDetailsByGameId(gameId);
    }

    public GameDetails getGameDetailsByGameTitle(String title) {
        return gameRepo.getGameDetailsByGameTitle(title);
    }

    public String getQRCode(String telegramUrl) throws UnirestException {
        String qrCode = redisRepo.getQRLink(telegramUrl);
        if (qrCode == null) {
            System.out.println(">>> QR link not found in Redis... calling external API...");
            JsonObject body = Json.createObjectBuilder().add("url", telegramUrl).build();
            HttpResponse<String> response = Unirest.post("https://api.dub.co/links?workspaceId=" + qrWorkspaceId)
                .header("Authorization", "Bearer " + qrApiKey)
                .header("Content-Type", "application/json")
                .body(body.toString())
                .asString();
            if (response.getStatus() == 200) {
                JsonReader reader = Json.createReader(new StringReader(response.getBody()));
                JsonObject qrResponse = reader.readObject();
                qrCode = qrResponse.getString("qrCode");
                redisRepo.saveQRLink(telegramUrl, qrCode);
            }
        }
        return qrCode;
    }

    public void createNewHost(String hostId, Integer numOfTeams) {
        if (redisRepo.hostExists(hostId))
            deleteGame(hostId);
        List<String> teams = Arrays.asList("Team A", "Team B", "Team C", "Team D");
        for (int i = 0; i < numOfTeams; i++)
            redisRepo.savePlayerByHostId(hostId, teams.get(i), "");
        System.out.println(">>> creating new host. existing players in teams will be wiped.");
    }

    public void savePlayerInfo(String username, String hostId, String teamId) {
        if (redisRepo.playerExists(username))
            redisRepo.deletePlayerByUsername(username);
        redisRepo.savePlayerByUsername(username, hostId, teamId);

        String oldTeamId = redisRepo.userTeamInHost(hostId, username);
        if (oldTeamId != null)
            redisRepo.deletePlayerInHost(hostId, oldTeamId, username);
        redisRepo.savePlayerByHostId(hostId, teamId, username);
    }

    public Map<String,List<String>> getPlayersInTeams(String hostId) {
        return redisRepo.getPlayersInTeams(hostId);
    }

    public String getPlayerHostId(String username) {
        return redisRepo.getPlayerHostId(username);
    }

    public String getPlayerTeam(String username) {
        return redisRepo.getPlayerTeam(username);
    }
    
    public String getPlayerHostAndTeam(String username) {
        return redisRepo.getPlayerHostAndTeam(username);
    }

    public void deletePlayer(String username) {
        if (redisRepo.playerExists(username)) {
            redisRepo.deletePlayerByUsername(username);
            String hostId = redisRepo.getPlayerHostId(username);
            String oldTeamId = redisRepo.userTeamInHost(hostId, username);
            if (oldTeamId != null)
                redisRepo.deletePlayerInHost(hostId, oldTeamId, username);
        }
    }

    public void deleteGame(String hostId) {
        Map<String, List<String>> teamsMap = redisRepo.getPlayersInTeams(hostId);
        System.out.println(">>> deleting game " + hostId + " : " + teamsMap);
        for (List<String> playersList : teamsMap.values()) {
            redisRepo.deletePlayerByUsername(playersList);
        }                    
        redisRepo.deleteHost(hostId);
    }

}