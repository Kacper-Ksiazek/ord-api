CREATE TABLE IF NOT EXISTS conversation
(
    id                 UUID PRIMARY KEY,

    language           language_name                   NOT NULL,
    proficiency_level  language_proficiency            NOT NULL,

    goal               conversation_goal               NOT NULL,
    ai_tone            conversation_tone               NOT NULL,
    ai_response_length conversation_ai_response_length NOT NULL,

    created_at         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    user_id            UUID                            NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS conversation_message
(
    id              UUID PRIMARY KEY,

    sender          conversation_message_sender NOT NULL,
    content         TEXT                        NOT NULL,

    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    user_id         UUID                        NOT NULL,
    conversation_id UUID                        NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (conversation_id) REFERENCES conversation (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS conversation_user_message_feedback
(
    id           UUID PRIMARY KEY,

    rating       INT  NOT NULL CHECK (rating >= 0 AND rating <= 10),
    comment      TEXT,
    correct_form TEXT,

    created_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    message_id   UUID NOT NULL,
    FOREIGN KEY (message_id) REFERENCES conversation_message (id) ON DELETE CASCADE
);