package tfip.project.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import tfip.project.model.GameDetails;
import tfip.project.model.UserMembership;
import tfip.project.repo.GameRepository;
import tfip.project.repo.RedisRepository;
import tfip.project.repo.S3Repository;
import tfip.project.repo.UserRepository;

@Service
public class GameService {

    @Autowired
    GameRepository gameRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    RedisRepository redisRepo;
    
    @Autowired
    S3Repository s3Repo;

    public List<GameDetails> getAllGames() {
        return gameRepo.getAllGames();
    }

    public List<GameDetails> getAllGamesByUsername(String username) {
        return gameRepo.getAllGamesByUsername(username);
    }

    @Transactional
    public boolean saveGameRom(String username, MultipartFile rom) {
        if (isRomLimit(username))
            throw new RuntimeException("User has hit max ROM limit allowed for membership tier. ROM has not been saved");

        String id = UUID.randomUUID().toString().substring(0, 8);
        String s3url;
        try {
            s3url = s3Repo.saveToS3(rom, id);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Internal error - failed to save to S3. Please try again later or contact us at business.gamebond@hotmail.com if issue persists.");
        }

        GameDetails game = new GameDetails();
        game.setGameId(id);
        game.setRomFile(s3url);
        game.setGameTitle(rom.getOriginalFilename());

        boolean isGameAdded = gameRepo.saveGame(game);
        boolean isGameAddedToUser = gameRepo.saveGameToUser(username, game);
        if (!isGameAdded || !isGameAddedToUser)
            throw new RuntimeException("Internal error - failed to save to MySQL. Please try again later or contact us at business.gamebond@hotmail.com if issue persists.");

        return true;
    }

    @Transactional
    public boolean saveGameRom(String username, MultipartFile rom, MultipartFile pic) {
        if (isRomLimit(username))
            throw new RuntimeException("User has hit max ROM limit allowed for membership tier. ROM has not been saved");

        String id = UUID.randomUUID().toString().substring(0, 8);
        String romS3url;
        String picS3url;
        try {
            romS3url = s3Repo.saveToS3(rom, id);
            picS3url = s3Repo.saveToS3(pic, pic.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Internal error - failed to save to S3. Please try again later or contact us at business.gamebond@hotmail.com if issue persists.");
        }

        GameDetails game = new GameDetails();
        game.setGameId(id);
        game.setRomFile(romS3url);
        game.setGameTitle(rom.getOriginalFilename());
        game.setPictureUrl(picS3url);

        boolean isGameAdded = gameRepo.saveGame(game);
        boolean isGameAddedToUser = gameRepo.saveGameToUser(username, game);
        if (!isGameAdded || !isGameAddedToUser)
            throw new RuntimeException("Internal error - failed to save to MySQL. Please try again later or contact us at business.gamebond@hotmail.com if issue persists.");

        return true;
    }

    public boolean isRomLimit(String username) {
        UserMembership membership = userRepo.getMembership(username);
        if (membership == null)
            throw new RuntimeException("Internal error - failed to retrieve user's membership. Please try again later or contact us at business.gamebond@hotmail.com if issue persists.");
        int currRomCount = userRepo.checkRomByUser(username);
        int romEntitlement = membership.getRomEntitlement();
        if (currRomCount < romEntitlement) 
            return false;
        return true;
    }

    public GameDetails getGameDetailsByGameId(String gameId) {
        return gameRepo.getGameDetailsByGameId(gameId);
    }

    public GameDetails getGameDetailsByGameTitle(String title) {
        return gameRepo.getGameDetailsByGameTitle(title);
    }

    public String getQRCode(String telegramUrl) throws Exception {
        String qrCode = redisRepo.getQRLink(telegramUrl);
        if (qrCode == null) {
            System.out.println(">>> QR link not found in Redis... calling external API...");
            RequestEntity<Void> req = RequestEntity
                    .get("https://api.qrserver.com/v1/create-qr-code/?charset-source=UTF-8&size=1000x1000&data=" + telegramUrl)
                    .accept(MediaType.IMAGE_PNG)
                    .build();
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<byte[]> resp = restTemplate.exchange(req, byte[].class);
            if (resp.getStatusCode().is2xxSuccessful()) {
                qrCode = "data:image/png;base64," + Base64.getEncoder().encodeToString(resp.getBody());
                redisRepo.saveQRLink(telegramUrl, qrCode);    
            } else {
                throw new Exception("unable to retrieve QR code from API");
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