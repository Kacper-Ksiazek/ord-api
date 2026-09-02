CREATE TABLE IF NOT EXISTS gpt_tokens_usage
(
    id             UUID PRIMARY KEY         DEFAULT gen_random_uuid(),

    user_id        UUID                     REFERENCES users (id) ON DELETE SET NULL,

    operation_type VARCHAR(128)    NOT NULL,
    model          VARCHAR(100)     NOT NULL,

    input_tokens   INTEGER         NOT NULL CHECK (input_tokens >= 0),
    output_tokens  INTEGER         NOT NULL CHECK (output_tokens >= 0),

    created_at     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_gpt_tokens_usage_user_created ON gpt_tokens_usage (user_id, created_at DESC);
