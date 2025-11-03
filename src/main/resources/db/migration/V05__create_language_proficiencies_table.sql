CREATE TABLE IF NOT EXISTS language_proficiencies
(
    id                          UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    level                       language_proficiency NOT NULL,
    language                    language_name        NOT NULL,

    translate_to                language_name        NOT NULL,
    generative_content_language language_name        NOT NULL,

    user_id                     UUID                 NOT NULL,

    created_at                  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT each_user_can_have_only_one_proficiency_per_language UNIQUE (user_id, language)
);