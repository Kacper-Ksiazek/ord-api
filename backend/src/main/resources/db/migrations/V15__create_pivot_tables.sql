CREATE TABLE IF NOT EXISTS "words_used_in_games"
(
    id      UUID PRIMARY KEY,

    game_id UUID REFERENCES games (id) ON DELETE CASCADE,
    word_id UUID REFERENCES words (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS "words_used_in_stories"
(
    id       UUID PRIMARY KEY,

    story_id UUID REFERENCES stories (id) ON DELETE CASCADE,
    word_id UUID REFERENCES words (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS "banks_used_in_games"
(
    id      UUID PRIMARY KEY,

    game_id UUID REFERENCES games (id) ON DELETE CASCADE,
    bank_id UUID REFERENCES banks (id) ON DELETE SET NULL
);