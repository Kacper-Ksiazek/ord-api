CREATE TABLE IF NOT EXISTS conversations
(
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    topic              TEXT                            NOT NULL,
    additional_context TEXT                     DEFAULT NULL,

    language           language_name                   NOT NULL,
    proficiency_level  language_proficiency            NOT NULL,
    goal               conversation_goal               NOT NULL,
    ai_tone            conversation_tone               NOT NULL,
    ai_response_length conversation_ai_response_length NOT NULL,

    user_id            UUID                            NOT NULL,

    created_at         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS conversation_user_message_feedback
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    grammar          INT  NOT NULL CHECK (grammar >= 0 AND grammar <= 10),
    vocabulary       INT  NOT NULL CHECK (vocabulary >= 0 AND vocabulary <= 10),
    answer_length    INT  NOT NULL CHECK (answer_length >= 0 AND answer_length <= 10),

    suggested_answer TEXT                     DEFAULT NULL,
    comment          TEXT                     DEFAULT NULL,

    conversation_id  UUID NOT NULL,

    created_at       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (conversation_id) REFERENCES conversations (id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS conversation_messages
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    message_order   INT                         NOT NULL,
    content         TEXT                        NOT NULL,
    sender          conversation_message_sender NOT NULL,

    conversation_id UUID                        NOT NULL,
    feedback_id     UUID                     DEFAULT NULL,

    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (conversation_id) REFERENCES conversations (id) ON DELETE CASCADE,
    FOREIGN KEY (feedback_id) REFERENCES conversation_user_message_feedback (id) ON DELETE CASCADE
);