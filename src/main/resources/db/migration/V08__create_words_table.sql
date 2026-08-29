CREATE TABLE IF NOT EXISTS words
(
    id            UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    status        word_status   NOT NULL   DEFAULT 'CAPTURED',
    type          word_type                DEFAULT NULL,
    source_word   varchar(255)  NOT NULL,
    translation   VARCHAR(255)             DEFAULT NULL,
    definition    TEXT                     DEFAULT NULL,
    extra_mark    word_extra_mark          DEFAULT NULL,

    language      language_name NOT NULL,

    is_bookmarked BOOLEAN       NOT NULL   DEFAULT FALSE,

    user_id       UUID          NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    bank_id       UUID                     DEFAULT NULL REFERENCES banks (id) ON DELETE SET NULL,
    bank_group_id UUID                     DEFAULT NULL,

    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_words_active_fields CHECK (
        (status = 'CAPTURED')
            OR (
            status = 'ACTIVE'
                AND type IS NOT NULL
                AND translation IS NOT NULL
                AND definition IS NOT NULL
            )
        )
);

CREATE UNIQUE INDEX uq_words_active_per_user_language_type
    ON words (user_id, language, lower(source_word), type)
    WHERE status = 'ACTIVE'::word_status;

CREATE UNIQUE INDEX uq_words_captured_per_user_language_source
    ON words (user_id, language, lower(source_word))
    WHERE status = 'CAPTURED'::word_status;

CREATE INDEX idx_words_id_user_id ON words (id, user_id);
CREATE INDEX idx_words_user_language ON words (user_id, language);
CREATE INDEX idx_words_user_status_created_at ON words (user_id, status, created_at DESC);

CREATE TABLE IF NOT EXISTS word_progress
(
    id                 UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    word_id            UUID          NOT NULL REFERENCES words (id) ON DELETE CASCADE,
    user_id            UUID          NOT NULL REFERENCES users (id) ON DELETE CASCADE,

    points             INTEGER       NOT NULL   DEFAULT 0,
    completed_at       TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    first_completed_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,

    created_at         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_word_progress_word_user UNIQUE (word_id, user_id),
    CONSTRAINT chk_word_progress_points_non_negative CHECK (points >= 0)
);

CREATE INDEX idx_word_progress_word_id ON word_progress (word_id);
CREATE INDEX idx_word_progress_user_points ON word_progress (user_id, points);
CREATE INDEX idx_word_progress_user_first_completed_at ON word_progress (user_id, first_completed_at);

CREATE TABLE IF NOT EXISTS word_details
(
    id                UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    word_id           UUID  NOT NULL REFERENCES words (id) ON DELETE CASCADE,

    use_cases         JSONB NOT NULL,
    synonyms          JSONB NOT NULL,
    antonyms          JSONB NOT NULL,
    common_mistakes   JSONB NOT NULL,

    example_sentences JSONB NOT NULL,
    collocations      JSONB NOT NULL,
    pronunciation     JSONB                    DEFAULT NULL,
    grammar           JSONB                    DEFAULT NULL,

    cultural_notes    TEXT                     DEFAULT NULL,
    learning_tips     TEXT                     DEFAULT NULL,

    user_id           UUID  NOT NULL REFERENCES users (id) ON DELETE CASCADE,

    created_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_word_details_word_user UNIQUE (word_id, user_id)
);

CREATE INDEX idx_word_details_word_id ON word_details (word_id);
