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
INSERT INTO users(username, password, email, firstname, lastname) VALUES ('betty','password#123','betty@hotmail.com','betty','chew');
INSERT INTO users(username, password, email) VALUES ('hoseh','password#123','hoseh@hotmail.com');
INSERT INTO users(username, password, email, firstname, lastname) VALUES ('kiat','password#123','kiat@hotmail.com','kiat','heong');
INSERT INTO users(username, password, email) VALUES ('ryo','password#123','ryo@hotmail.com');
INSERT INTO membership(username, membership) VALUES ('vivientxy',2);
INSERT INTO membership(username, membership) VALUES ('fred',1);
INSERT INTO membership(username) VALUES ('betty');
INSERT INTO membership(username) VALUES ('hoseh');
INSERT INTO membership(username) VALUES ('kiat');
INSERT INTO membership(username) VALUES ('ryo');
INSERT INTO games(game_id, game_title, rom_file, picture_url) VALUES ('default1','Tetris','https://gamebond.sgp1.digitaloceanspaces.com/Tetris.gb','https://gamebond.sgp1.digitaloceanspaces.com/Tetris.jpg');
INSERT INTO games(game_id, game_title, rom_file, picture_url) VALUES ('default2','Wordtris','https://gamebond.sgp1.digitaloceanspaces.com/Wordtris.gb','https://gamebond.sgp1.digitaloceanspaces.com/Wordtris.jpg');
INSERT INTO games(game_id, game_title, rom_file, picture_url) VALUES ('default3','Motocross Maniacs','https://gamebond.sgp1.digitaloceanspaces.com/Motocross%20Maniacs.gb','https://gamebond.sgp1.digitaloceanspaces.com/Motocross%20Maniacs.jpg');
INSERT INTO games(game_id, game_title, rom_file, picture_url) VALUES ('default4','Pokemon Red','https://gamebond.sgp1.digitaloceanspaces.com/Pokemon%20Red.gb','https://gamebond.sgp1.digitaloceanspaces.com/Pokemon%20Red.jpg');
-- INSERT INTO games(game_id, game_title, rom_file, picture_url) VALUES ('8a6e0804','Bomberman','https://gamebond.sgp1.digitaloceanspaces.com/Bomberman.gb','https://gamebond.sgp1.digitaloceanspaces.com/Bomberman.jpg');
-- INSERT INTO games(game_id, game_title, rom_file, picture_url) VALUES ('sv8t4s6d','Marios Picross','https://gamebond.sgp1.digitaloceanspaces.com/Marios%20Picross.gb','https://gamebond.sgp1.digitaloceanspaces.com/Marios%20Picross.jpg');
-- INSERT INTO games(game_id, game_title, rom_file, picture_url) VALUES ('n8y56rd1','Pokemon Pinball','https://gamebond.sgp1.digitaloceanspaces.com/Pokemon%20Pinball.gbc','https://gamebond.sgp1.digitaloceanspaces.com/Pokemon%20Pinball.jpg');
INSERT INTO user_games(username, game_id) VALUES ('vivientxy','default1');
INSERT INTO user_games(username, game_id) VALUES ('vivientxy','default2');
INSERT INTO user_games(username, game_id) VALUES ('vivientxy','default3');
INSERT INTO user_games(username, game_id) VALUES ('vivientxy','default4');

GRANT ALL PRIVILEGES ON gamebond.* TO 'betty'@'%';
FLUSH PRIVILEGES;