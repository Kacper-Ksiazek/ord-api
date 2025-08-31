CREATE TABLE IF NOT EXISTS user_sessions
(
    id         UUID PRIMARY KEY,

    token      TEXT UNIQUE              NOT NULL,

    user_id    UUID                     NOT NULL REFERENCES users (id) ON DELETE CASCADE,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
)