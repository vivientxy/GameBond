package tfip.project.repo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import tfip.project.model.GameDetails;

@Repository
public class GameRepository implements SqlQueries {

    @Autowired
    JdbcTemplate template;

    public List<GameDetails> getAllGames() {
        try {
            return template.query(SQL_GET_ALL_GAMES, new GameDetailsRowMapper());
        } catch (Exception e) {
            return null;
        }
    }

    public List<GameDetails> getAllGamesByUsername(String username) {
        try {
            return template.query(SQL_GET_ALL_GAMES_BY_USERNAME, new GameDetailsRowMapper(), username);
        } catch (Exception e) {
            return null;
        }
    }

    public GameDetails getGameDetailsByGameId(String gameId) {
        try {
            return template.queryForObject(SQL_GET_GAME_BY_ID,
                    BeanPropertyRowMapper.newInstance(GameDetails.class),
                    gameId);
        } catch (Exception e) {
            return null;
        }
    }

    public GameDetails getGameDetailsByGameTitle(String title) {
        try {
            return template.queryForObject(SQL_GET_GAME_BY_TITLE,
                    BeanPropertyRowMapper.newInstance(GameDetails.class),
                    title);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean saveGame(GameDetails game) {
        try {
            if (game.getPictureUrl() != null)
                return template.update(SQL_ADD_GAME, game.getGameId(), game.getGameTitle(), game.getRomFile(), game.getPictureUrl()) > 0 ? true : false;
            return template.update(SQL_ADD_GAME_WITHOUT_PICTURE, game.getGameId(), game.getGameTitle(), game.getRomFile()) > 0 ? true : false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean saveGameToUser(String username, GameDetails game) {
        try {
            return template.update(SQL_ADD_GAME_TO_USER, username, game.getGameId()) > 0 ? true : false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean saveDefaultGamesToNewUser(String username) {
        try {
            List<String> defaultGameList = Arrays.asList("default1", "default2", "default3", "default4");
            int counter = 0;
            for (String gameId : defaultGameList)
                counter += template.update(SQL_ADD_GAME_TO_USER, username, gameId);
            if (counter == 4)
                return true;
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean saveHostGameDetails(String username, String hostId, String gameId, int numOfTeams) {
        try {
            return template.update(SQL_ADD_HOSTED_GAME, username, hostId, gameId, numOfTeams) > 0 ? true : false;
        } catch (Exception e) {
            return false;
        }
    }

    private class GameDetailsRowMapper implements RowMapper<GameDetails> {
        @Override
        public GameDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            GameDetails game = new GameDetails();
            game.setGameId(rs.getString("game_id"));
            game.setGameTitle(rs.getString("game_title"));
            game.setRomFile(rs.getString("rom_file"));
            game.setPictureUrl(rs.getString("picture_url"));
            return game;
        }
    }

}
