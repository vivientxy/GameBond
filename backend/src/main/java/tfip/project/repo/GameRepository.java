package tfip.project.repo;

import java.sql.ResultSet;
import java.sql.SQLException;
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
