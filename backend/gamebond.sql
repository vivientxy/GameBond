DROP DATABASE IF EXISTS gamebond;

CREATE DATABASE gamebond;

USE gamebond;

CREATE TABLE users (
    username VARCHAR(32) PRIMARY KEY,
    password VARCHAR(32) NOT NULL,
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


-- add fake items
INSERT INTO users(username, password, email, firstname, lastname) VALUES ('vivientxy','password#123','vivientxy@hotmail.com','Vivien','Tang');
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

GRANT ALL PRIVILEGES ON gamebond.* TO 'betty'@'%';
FLUSH PRIVILEGES;