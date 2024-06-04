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
    public static final String SQL_ADD_USER_MEMBERSHIP = """
        INSERT INTO membership(username,membership)
        VALUES (?,?)
        """;
    public static final String SQL_GET_MEMBERSHIP = """
        SELECT *
        FROM gamebond.membership
        WHERE username = ?
        """;
    public static final String SQL_UPDATE_MEMBERSHIP = """
        UPDATE gamebond.membership
        SET membership=?, membership_date=?
        WHERE username=?;
        """;

}
