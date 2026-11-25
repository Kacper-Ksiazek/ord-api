CREATE TABLE IF NOT EXISTS ai_explanations
(
    id                       UUID PRIMARY KEY,
    user_id                  UUID          NOT NULL REFERENCES users (id) ON DELETE CASCADE,

    phrase                   VARCHAR(255)  NOT NULL,
    language                 language_name NOT NULL,
    explanation              TEXT          NOT NULL,

    simpler_text             TEXT                     DEFAULT NULL,
    more_examples_text       TEXT                     DEFAULT NULL,
    similar_expressions_text TEXT                     DEFAULT NULL,

    created_at               TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ai_explanations_user_created_at ON ai_explanations (user_id, created_at DESC);
