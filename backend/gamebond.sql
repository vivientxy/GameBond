DROP DATABASE IF EXISTS gamebond;

CREATE DATABASE gamebond;

USE gamebond;

CREATE TABLE users (
    username VARCHAR(32) PRIMARY KEY,
    password VARCHAR(128) NOT NULL,
    email VARCHAR(64) NOT NULL UNIQUE,
    firstname VARCHAR(32),
    lastname VARCHAR(32),
    active BOOLEAN DEFAULT true
);

CREATE TABLE membership (
    username VARCHAR(32) NOT NULL UNIQUE,
    tier INT NOT NULL DEFAULT 0,
    membership_date DATETIME DEFAULT current_timestamp,
    monthly_games_entitlement INT NOT NULL DEFAULT 5,
    rom_entitlement INT NOT NULL DEFAULT 5,

    CONSTRAINT fk_username FOREIGN KEY(username)
        REFERENCES users(username)
);

CREATE TABLE games (
    game_id VARCHAR(8) PRIMARY KEY,
    game_title VARCHAR(64) NOT NULL,
    rom_file VARCHAR(256) NOT NULL,
    picture_url VARCHAR(256)
);

CREATE TABLE user_games (
    username VARCHAR(32) NOT NULL,
    game_id VARCHAR(8) NOT NULL,

    CONSTRAINT fk_username_games FOREIGN KEY(username)
        REFERENCES users(username),
    CONSTRAINT fk_game_id FOREIGN KEY(game_id)
        REFERENCES games(game_id)
);

CREATE TABLE hosted_games (
    username VARCHAR(32) NOT NULL,
    host_id VARCHAR(8) NOT NULL,
    game_id VARCHAR(8) NOT NULL,
    num_of_teams INT NOT NULL,
    game_date DATETIME DEFAULT current_timestamp,

    CONSTRAINT fk_username_hosted_games FOREIGN KEY(username)
        REFERENCES users(username),
    CONSTRAINT fk_hosted_game_id FOREIGN KEY(game_id)
        REFERENCES games(game_id)
);


INSERT INTO games(game_id, game_title, rom_file, picture_url) VALUES ('default1','Tetris','https://gamebond.sgp1.digitaloceanspaces.com/Tetris.gb','https://gamebond.sgp1.digitaloceanspaces.com/Tetris.jpg');
INSERT INTO games(game_id, game_title, rom_file, picture_url) VALUES ('default2','Wordtris','https://gamebond.sgp1.digitaloceanspaces.com/Wordtris.gb','https://gamebond.sgp1.digitaloceanspaces.com/Wordtris.jpg');
INSERT INTO games(game_id, game_title, rom_file, picture_url) VALUES ('default3','Motocross Maniacs','https://gamebond.sgp1.digitaloceanspaces.com/Motocross%20Maniacs.gb','https://gamebond.sgp1.digitaloceanspaces.com/Motocross%20Maniacs.jpg');
INSERT INTO games(game_id, game_title, rom_file, picture_url) VALUES ('default4','Pokemon Red','https://gamebond.sgp1.digitaloceanspaces.com/Pokemon%20Red.gb','https://gamebond.sgp1.digitaloceanspaces.com/Pokemon%20Red.jpg');

GRANT ALL PRIVILEGES ON gamebond.* TO 'betty'@'%';
FLUSH PRIVILEGES;