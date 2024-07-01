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
    membership INT NOT NULL DEFAULT 0,
    membership_date DATETIME DEFAULT current_timestamp,

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


-- add fake items
INSERT INTO users(username, password, email, firstname, lastname) VALUES ('vivientxy','123123123','vivientxy@hotmail.com','Vivien','Tang');
INSERT INTO users(username, password, email, firstname) VALUES ('fred','password#123','fred@hotmail.com','fred');
INSERT INTO membership(username, membership) VALUES ('vivientxy',2);
INSERT INTO membership(username, membership) VALUES ('fred',0);
INSERT INTO games(game_id, game_title, rom_file, picture_url) VALUES ('default1','Tetris','https://gamebond.sgp1.digitaloceanspaces.com/Tetris.gb','https://gamebond.sgp1.digitaloceanspaces.com/Tetris.jpg');
INSERT INTO games(game_id, game_title, rom_file, picture_url) VALUES ('default2','Wordtris','https://gamebond.sgp1.digitaloceanspaces.com/Wordtris.gb','https://gamebond.sgp1.digitaloceanspaces.com/Wordtris.jpg');
INSERT INTO games(game_id, game_title, rom_file, picture_url) VALUES ('default3','Motocross Maniacs','https://gamebond.sgp1.digitaloceanspaces.com/Motocross%20Maniacs.gb','https://gamebond.sgp1.digitaloceanspaces.com/Motocross%20Maniacs.jpg');
INSERT INTO games(game_id, game_title, rom_file, picture_url) VALUES ('default4','Pokemon Red','https://gamebond.sgp1.digitaloceanspaces.com/Pokemon%20Red.gb','https://gamebond.sgp1.digitaloceanspaces.com/Pokemon%20Red.jpg');
INSERT INTO user_games(username, game_id) VALUES ('vivientxy','default1');
INSERT INTO user_games(username, game_id) VALUES ('vivientxy','default2');
INSERT INTO user_games(username, game_id) VALUES ('vivientxy','default3');
INSERT INTO user_games(username, game_id) VALUES ('vivientxy','default4');
INSERT INTO user_games(username, game_id) VALUES ('fred','default1');
INSERT INTO user_games(username, game_id) VALUES ('fred','default2');
INSERT INTO user_games(username, game_id) VALUES ('fred','default3');
INSERT INTO user_games(username, game_id) VALUES ('fred','default4');

GRANT ALL PRIVILEGES ON gamebond.* TO 'betty'@'%';
FLUSH PRIVILEGES;