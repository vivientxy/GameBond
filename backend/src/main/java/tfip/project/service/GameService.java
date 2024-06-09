package tfip.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tfip.project.model.GameDetails;
import tfip.project.repo.GameRepository;

@Service
public class GameService {
    
    @Autowired
    GameRepository gameRepo;

    public List<GameDetails> getAllGames() {
        return gameRepo.getAllGames();
    }

    public GameDetails getGameDetailsByGameId(String gameId) {
        return gameRepo.getGameDetailsByGameId(gameId);
    }

    public GameDetails getGameDetailsByGameTitle(String title) {
        return gameRepo.getGameDetailsByGameTitle(title);
    }

}
