CREATE TABLE IF NOT EXISTS queue (
    timestamp BIGINT NOT NULL,
    topic VARCHAR(255) NOT NULL,
    message TEXT NOT NULL
)