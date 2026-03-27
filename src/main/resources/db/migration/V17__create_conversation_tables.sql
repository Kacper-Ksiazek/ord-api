CREATE TABLE IF NOT EXISTS conversations
(
    id                        UUID PRIMARY KEY         DEFAULT gen_random_uuid(),

    topic                     TEXT                 NOT NULL,
    additional_context        TEXT                     DEFAULT NULL,

    language                  language_name        NOT NULL,
    proficiency_level         language_proficiency NOT NULL,
    type                      conversation_type    NOT NULL,
    ai_tone                   conversation_tone    NOT NULL,
    ai_interlocutor_name      TEXT                 NOT NULL,
    ai_interlocutor_avatar_id VARCHAR(64)          NOT NULL,

    user_id                   UUID                 NOT NULL,

    created_at                TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at                TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS conversation_user_message_analysis
(
    id               UUID PRIMARY KEY         DEFAULT gen_random_uuid(),

    tutor_comment     TEXT NOT NULL,
    corrected_message TEXT,

    grammar                INT  NOT NULL CHECK (grammar >= 0 AND grammar <= 10),
    vocabulary             INT  NOT NULL CHECK (vocabulary >= 0 AND vocabulary <= 10),
    naturalness            INT  NOT NULL CHECK (naturalness >= 0 AND naturalness <= 10),
    coherence_with_context INT  NOT NULL CHECK (coherence_with_context >= 0 AND coherence_with_context <= 10),

    mistakes         JSONB NOT NULL,
    strengths        JSONB NOT NULL,
    suggestions      JSONB NOT NULL,

    message_id       UUID NOT NULL,

    created_at       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS conversation_messages
(
    id              UUID PRIMARY KEY         DEFAULT gen_random_uuid(),

    message_order   INT                         NOT NULL,
    content         TEXT                        NOT NULL,
    sender          conversation_message_sender NOT NULL,

    conversation_id UUID                        NOT NULL,
    feedback_id     UUID                     DEFAULT NULL,

    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (conversation_id) REFERENCES conversations (id) ON DELETE CASCADE,
    FOREIGN KEY (feedback_id) REFERENCES conversation_user_message_analysis (id) ON DELETE CASCADE
);

-- Add foreign key constraint after both tables are created
ALTER TABLE conversation_user_message_analysis
    ADD CONSTRAINT fk_message_analysis_message_id
        FOREIGN KEY (message_id) REFERENCES conversation_messages (id) ON DELETE CASCADE;


CREATE TABLE IF NOT EXISTS conversation_ai_message_learning_tips
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    grammar_tips     JSONB NOT NULL,
    vocabulary_tips  JSONB NOT NULL,
    phrase_tips      JSONB NOT NULL,

    message_id       UUID NOT NULL,

    created_at       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_learning_tips_message_id
        FOREIGN KEY (message_id) REFERENCES conversation_messages (id) ON DELETE CASCADE
);

COMMENT ON COLUMN conversation_ai_message_learning_tips.grammar_tips IS
'JSONB array (0-2 items). Schema: {phrase, explanation, grammarPoint, exampleSentences, register}';

COMMENT ON COLUMN conversation_ai_message_learning_tips.vocabulary_tips IS
'JSONB array (0-2 items). Schema: {word, definition, usageNote, wordType, exampleSentences, register, nativeLanguageEquivalent}';

COMMENT ON COLUMN conversation_ai_message_learning_tips.phrase_tips IS
'JSONB array (0-2 items). Schema: {phrase, phraseType, meaning, exampleSentences, register}';