package tfip.project.service;

import java.io.StringReader;
import java.util.List;

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

}