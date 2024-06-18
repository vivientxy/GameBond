package tfip.project.service;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import tfip.project.model.GameDetails;
import tfip.project.repo.GameRepository;
import tfip.project.repo.RedisRepository;

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

    public List<GameDetails> getAllGames() {
        return gameRepo.getAllGames();
    }

    public GameDetails getGameDetailsByGameId(String gameId) {
        return gameRepo.getGameDetailsByGameId(gameId);
    }

    public GameDetails getGameDetailsByGameTitle(String title) {
        return gameRepo.getGameDetailsByGameTitle(title);
    }

    public String getQRCode(String telegramUrl) throws UnirestException {
        String qrCode = redisRepo.getQRLink(telegramUrl);
        System.out.println(">>> getting QR link...");

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
            redisRepo.deleteHost(hostId);
        List<String> teams = Arrays.asList("Team A", "Team B", "Team C", "Team D");
        for (int i = 0; i < numOfTeams; i++)
            redisRepo.savePlayerByHostId(hostId, teams.get(i), "");
        System.out.println(">>> createNewHost completed... pls check");
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
        for (List<String> playersList : teamsMap.values()) {
            redisRepo.deletePlayerByUsername(playersList);
        }                    
        redisRepo.deleteHost(hostId);
    }

}