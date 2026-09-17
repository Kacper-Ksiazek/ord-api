CREATE TABLE IF NOT EXISTS user_sessions
(
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    token_hash           TEXT UNIQUE              NOT NULL,

    user_id              UUID                     NOT NULL REFERENCES users (id) ON DELETE CASCADE,

    created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_seen_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    idle_expires_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    absolute_expires_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_user_sessions_user_id ON user_sessions (user_id);
CREATE INDEX IF NOT EXISTS idx_user_sessions_idle_expires_at ON user_sessions (idle_expires_at);
