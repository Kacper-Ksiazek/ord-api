CREATE TABLE IF NOT EXISTS "stories"
(
    id               UUID PRIMARY KEY,

    user_id          UUID        REFERENCES users (id) ON DELETE SET NULL,

    title            VARCHAR(64) NOT NULL,
    content          TEXT        NOT NULL,

    -- Map<String, String> of explanations
    explanations     JSONB       NOT NULL,

    -- Number of Open AI tokens consumed
    number_of_tokens INTEGER     NOT NULL CHECK (number_of_tokens >= 0),

    "created_at"     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at"     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);