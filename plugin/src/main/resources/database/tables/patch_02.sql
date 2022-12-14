CREATE TABLE IF NOT EXISTS users (
                                     uuid VARCHAR(36) PRIMARY KEY,
                                     clan_id INT NOT NULL,
                                     role VARCHAR(25) NOT NULL,
                                     FOREIGN KEY (clan_id) REFERENCES clans(clan_id)
);