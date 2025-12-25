CREATE TABLE IF NOT EXISTS conversation_ai_message_learning_tips
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    grammar_tips     JSONB NOT NULL,
    vocabulary_tips  JSONB NOT NULL,
    idiom_tips       JSONB NOT NULL,
    cultural_tips    JSONB NOT NULL,

    message_id       UUID NOT NULL,

    created_at       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_learning_tips_message_id
        FOREIGN KEY (message_id) REFERENCES conversation_messages (id) ON DELETE CASCADE
);

-- Add learning_tips_id column to conversation_messages
ALTER TABLE conversation_messages
    ADD COLUMN learning_tips_id UUID DEFAULT NULL,
    ADD CONSTRAINT fk_message_learning_tips_id
        FOREIGN KEY (learning_tips_id) REFERENCES conversation_ai_message_learning_tips (id) ON DELETE CASCADE;
