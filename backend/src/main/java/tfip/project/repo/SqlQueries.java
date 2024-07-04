package tfip.project.repo;

public interface SqlQueries {

    // USERS
    public static final String SQL_ADD_USER = """
        INSERT INTO users(username, password, email, firstname, lastname)
        VALUES (?,?,?,?,?)
        """;
    public static final String SQL_GET_USER_BY_USERNAME = """
        SELECT *
        FROM gamebond.users
        WHERE username = ?
        """;
    public static final String SQL_GET_USER_BY_EMAIL = """
        SELECT *
        FROM gamebond.users
        WHERE email = ?
        """;
    public static final String SQL_UPDATE_USER = """
        UPDATE gamebond.users
        SET password=?, email=?, firstname=?, lastname=?, active=?
        WHERE username=?;
        """;
    public static final String SQL_DELETE_USER = """
        UPDATE gamebond.users
        SET active=0
        WHERE username=?;
        """;
    public static final String SQL_REACTIVATE_USER = """
        UPDATE gamebond.users
        SET active=1
        WHERE username=?;
        """;

    // MEMBERSHIP
    public static final String SQL_ADD_NEW_USER_MEMBERSHIP = """
        INSERT INTO membership(username)
        VALUES (?)
        """;
    public static final String SQL_GET_MEMBERSHIP = """
        SELECT *
        FROM gamebond.membership
        WHERE username = ?
        """;
    public static final String SQL_UPDATE_MEMBERSHIP = """
        UPDATE gamebond.membership
        SET tier=?, membership_date=?, monthly_games_entitlement=?, rom_entitlement=?
        WHERE username=?;
        """;
    public static final String SQL_ROM_COUNT_BY_USER = """
        SELECT COUNT(*) FROM gamebond.user_games
        WHERE username = ?
        """;
    
    // HOSTED GAMES
    public static final String SQL_ADD_HOSTED_GAME = """
        INSERT INTO hosted_games(username, host_id, game_id, num_of_teams)
        VALUES (?,?,?,?)
        """;

    public static final String SQL_HOSTED_GAMES_COUNT_BY_USER = """
        WITH info AS (
            SELECT 
                username,
                EXTRACT(DAY FROM membership_date) AS mem_day,
                EXTRACT(MONTH FROM CURRENT_DATE) AS curr_month,
                EXTRACT(YEAR FROM CURRENT_DATE) AS curr_year,
                (EXTRACT(DAY FROM CURRENT_DATE) - EXTRACT(DAY FROM membership_date)) < 0 AS is_curr_smaller_than_mem
            FROM gamebond.membership
            WHERE username = ?
        ),
        calc1 AS (
            SELECT 
                username,
                CASE WHEN is_curr_smaller_than_mem = 0 
                    THEN DATE(CONCAT(curr_year, '-', LPAD(curr_month, 2, '0'), '-', LPAD(mem_day, 2, '0')))
                    ELSE DATE(CONCAT(curr_year, '-', LPAD(curr_month - 1, 2, '0'), '-', LPAD(mem_day, 2, '0')))
                END AS start
            FROM info
        ),
        calc2 AS (
            SELECT
                username,
                CASE WHEN start IS NULL
                    THEN DATE_ADD(LAST_DAY(DATE_ADD(CURRENT_DATE, INTERVAL -1 month)), INTERVAL 1 day)
                    ELSE start
                END AS curr_cycle_start
            FROM calc1
        )
        SELECT COUNT(*) FROM calc2
        INNER JOIN gamebond.hosted_games
        ON calc2.username = hosted_games.username
        WHERE game_date BETWEEN curr_cycle_start AND CURRENT_TIMESTAMP();
        """;

    // GAMES
    public static final String SQL_GET_ALL_GAMES = """
        SELECT *
        FROM gamebond.games
        """;
    public static final String SQL_GET_ALL_GAMES_BY_USERNAME = """
        SELECT games.game_id, game_title, rom_file, picture_url FROM gamebond.user_games
        INNER JOIN gamebond.games
        ON games.game_id = user_games.game_id
        WHERE user_games.username = ?
        """;
    public static final String SQL_GET_GAME_BY_ID = """
        SELECT *
        FROM gamebond.games
        WHERE game_id = ?
        """;
    public static final String SQL_GET_GAME_BY_TITLE = """
        SELECT *
        FROM gamebond.games
        WHERE game_title = ?
        """;
    public static final String SQL_ADD_GAME = """
        INSERT INTO games(game_id, game_title, rom_file, picture_url) VALUES (?,?,?,?);
        """;
    public static final String SQL_ADD_GAME_WITHOUT_PICTURE = """
        INSERT INTO games(game_id, game_title, rom_file) VALUES (?,?,?);
        """;
    public static final String SQL_ADD_GAME_TO_USER = """
        INSERT INTO user_games(username, game_id) VALUES (?,?);
        """;

}
