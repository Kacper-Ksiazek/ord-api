CREATE TABLE IF NOT EXISTS users
(
    id                         UUID PRIMARY KEY         DEFAULT gen_random_uuid(),

    name                       VARCHAR(255)  NOT NULL,
    email                      VARCHAR(255)  NOT NULL,
    native_language            language_name NOT NULL,
    selected_learning_language language_name NOT NULL,
    is_account_initialized     BOOLEAN       NOT NULL   DEFAULT FALSE,

    created_at                 TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at                 TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT email_must_be_unique UNIQUE (email)
);
