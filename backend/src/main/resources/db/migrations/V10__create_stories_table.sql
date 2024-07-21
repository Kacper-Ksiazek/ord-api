CREATE TABLE IF NOT EXISTS "stories"
(
    id               UUID PRIMARY KEY,

    title            VARCHAR(64) NOT NULL,
    content          TEXT        NOT NULL,

    -- Map<String, String> of explanations
    explanations     JSONB       NOT NULL,

    user_id          UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    story_context_id UUID        NOT NULL REFERENCES story_contexts (id) ON DELETE CASCADE,

    "created_at"     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at"     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);