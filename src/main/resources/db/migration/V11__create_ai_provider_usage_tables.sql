CREATE TABLE IF NOT EXISTS ai_provider_usage
(
    id              UUID PRIMARY KEY         DEFAULT gen_random_uuid(),

    user_id         UUID                     REFERENCES users (id) ON DELETE SET NULL,

    provider        VARCHAR(32)     NOT NULL,
    operation_type  VARCHAR(128)    NOT NULL,
    model           VARCHAR(100)    NOT NULL,
    voice_id        VARCHAR(64),

    input_units     INTEGER         NOT NULL CHECK (input_units >= 0),
    output_units    INTEGER         NOT NULL CHECK (output_units >= 0),
    unit_type       VARCHAR(16)     NOT NULL,

    estimated_price NUMERIC(12, 6)  NOT NULL CHECK (estimated_price >= 0),

    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ai_provider_usage_user_created ON ai_provider_usage (user_id, created_at DESC);
