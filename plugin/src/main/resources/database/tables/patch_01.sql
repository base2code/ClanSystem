CREATE TABLE IF NOT EXISTS clans (
                       clan_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                       name VARCHAR(20) UNIQUE NOT NULL,
                       tag VARCHAR(4) UNIQUE NOT NULL,
                       max_members INT NOT NULL,
                       balance INT NOT NULL,
                       owner VARCHAR(64) UNIQUE NOT NULL
);