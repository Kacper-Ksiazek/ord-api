CREATE TABLE IF NOT EXISTS "words_used_in_game"
(
    id      UUID PRIMARY KEY,

    game_id UUID REFERENCES games (id) ON DELETE CASCADE,
    word_id UUID REFERENCES words (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "words_used_in_stories"
(
    id       UUID PRIMARY KEY,

    story_id UUID REFERENCES stories (id) ON DELETE CASCADE,
    word_id  UUID REFERENCES words (id) ON DELETE CASCADE
);