CREATE TABLE IF NOT EXISTS "word_tokens_usages"
(
    id                          UUID PRIMARY KEY,

    user_id                     UUID                              REFERENCES users (id) ON DELETE SET NULL,

    word                        VARCHAR(255)                      NOT NULL,
    translated_from             language_name                     NOT NULL,
    translated_to               language_name                     NOT NULL,

    consumption_type            words_gpt_tokens_consumption_type NOT NULL,

    -- Number of Open AI tokens consumed
    input_tokens                INTEGER                           NOT NULL CHECK (input_tokens >= 0),
    output_tokens               INTEGER                           NOT NULL CHECK (output_tokens >= 0),

    -- Price for input and output tokens, in USD
    price_for_mln_input_tokens  DECIMAL(20, 10)                   NOT NULL,
    price_for_mln_output_tokens DECIMAL(20, 10)                   NOT NULL,

    -- Total cost of the operation in USD
    cost                        DECIMAL(20, 10)                   NOT NULL,

    "created_at"                TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at"                TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "game_tokens_usages"
(
    id                          UUID PRIMARY KEY,

    user_id                     UUID                              REFERENCES users (id) ON DELETE SET NULL,
    game_id                     UUID                              REFERENCES games (id) ON DELETE SET NULL DEFAULT NULL,

    game_type                   game_type                         NOT NULL,
    game_difficulty             game_difficulty                   NOT NULL,
    consumption_type            games_gpt_tokens_consumption_type NOT NULL,
    translated_from             language_name                     NOT NULL,
    instruction_language        language_name                     NOT NULL,

    -- Number of Open AI tokens consumed
    input_tokens                INTEGER                           NOT NULL CHECK (input_tokens >= 0),
    output_tokens               INTEGER                           NOT NULL CHECK (output_tokens >= 0),

    -- Total cost of the operation in USD
    cost                        DECIMAL(20, 10)                   NOT NULL,

    -- Price for input and output tokens, in USD
    price_for_mln_input_tokens  DECIMAL(20, 10)                   NOT NULL,
    price_for_mln_output_tokens DECIMAL(20, 10)                   NOT NULL,


    created_at                  TIMESTAMP WITH TIME ZONE                                                   DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP WITH TIME ZONE                                                   DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "story_tokens_usages"
(
    id               UUID PRIMARY KEY,

    user_id          UUID                                REFERENCES users (id) ON DELETE SET NULL,
    story_id         UUID                                REFERENCES stories (id) ON DELETE SET NULL,

    consumption_type stories_gpt_tokens_consumption_type NOT NULL,

    -- Number of Open AI tokens consumed
    number_of_tokens INTEGER                             NOT NULL CHECK (number_of_tokens >= 0),

    "created_at"     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    "updated_at"     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);