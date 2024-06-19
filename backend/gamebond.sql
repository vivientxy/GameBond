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
INSERT INTO games(game_id, game_title, rom_file, picture_url) VALUES ('8a6e0804','Bomberman','https://gamebond.sgp1.digitaloceanspaces.com/Bomberman.gb','https://gamebond.sgp1.digitaloceanspaces.com/Bomberman.jpg');
INSERT INTO games(game_id, game_title, rom_file, picture_url) VALUES ('4fc803sb','Motocross Maniacs','https://gamebond.sgp1.digitaloceanspaces.com/Motocross%20Maniacs.gb','https://gamebond.sgp1.digitaloceanspaces.com/Motocross%20Maniacs.jpg');
INSERT INTO games(game_id, game_title, rom_file, picture_url) VALUES ('ib218tr3','Pokemon - Emerald Version','https://gamebond.sgp1.digitaloceanspaces.com/Pokemon%20-%20Emerald%20Version.gba','https://gamebond.sgp1.digitaloceanspaces.com/Pokemon%20-%20Emerald%20Version.jpg');
INSERT INTO games(game_id, game_title, rom_file, picture_url) VALUES ('n5e3d12f','Pokemon Red','https://gamebond.sgp1.digitaloceanspaces.com/Pokemon%20Red.gb','https://gamebond.sgp1.digitaloceanspaces.com/Pokemon%20Red.jpg');
INSERT INTO games(game_id, game_title, rom_file, picture_url) VALUES ('1vd6r80s','Super Mario Advance 4 - Super Mario Bros. 3','https://gamebond.sgp1.digitaloceanspaces.com/Super%20Mario%20Advance%204%20-%20Super%20Mario%20Bros.%203.gba','https://gamebond.sgp1.digitaloceanspaces.com/Super%20Mario%20Advance%204%20-%20Super%20Mario%20Bros.%203.jpg');
INSERT INTO games(game_id, game_title, rom_file, picture_url) VALUES ('8er6d4as','Tetris','https://gamebond.sgp1.digitaloceanspaces.com/Tetris.gb','https://gamebond.sgp1.digitaloceanspaces.com/Tetris.jpg');

GRANT ALL PRIVILEGES ON gamebond.* TO 'betty'@'%';
FLUSH PRIVILEGES;