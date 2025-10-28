-------------
-- quickly_added_words for root user
-------------

-- 1. ephemeral (English)
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'ephemeral',
    'ENGLISH',
    'efemeryczny, ulotny',
    'Lasting for a very short time',
    null,
    'ADJECTIVE',
    false,
    CURRENT_TIMESTAMP
);

-- 2. serendipity (English)
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'serendipity',
    'ENGLISH',
    'szczęśliwy traf',
    'The occurrence of events by chance in a happy or beneficial way',
    null,
    'NOUN',
    false,
    CURRENT_TIMESTAMP
);

-- 3. ubiquitous (English)
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'ubiquitous',
    'ENGLISH',
    'wszechobecny',
    'Present, appearing, or found everywhere',
    null,
    'ADJECTIVE',
    false,
    CURRENT_TIMESTAMP
);

-- 4. conundrum (English)
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'conundrum',
    'ENGLISH',
    'zagadka, dylemat',
    'A confusing and difficult problem or question',
    null,
    'NOUN',
    false,
    CURRENT_TIMESTAMP
);

-- 5. juxtapose (English)
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'juxtapose',
    'ENGLISH',
    'zestawiać',
    'Place or deal with close together for contrasting effect',
    null,
    'VERB',
    false,
    CURRENT_TIMESTAMP
);

-- 6. surreptitious (English) - approved word
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'surreptitious',
    'ENGLISH',
    'potajemny, ukradkowy',
    'Kept secret, especially because it would not be approved of',
    null,
    'ADJECTIVE',
    true,
    CURRENT_TIMESTAMP
);

-- 7. procrastinate (English)
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'procrastinate',
    'ENGLISH',
    'zwlekać, odkładać',
    'Delay or postpone action; put off doing something',
    null,
    'VERB',
    false,
    CURRENT_TIMESTAMP
);

-- 8. eloquent (English)
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'eloquent',
    'ENGLISH',
    'elokwentny, wymowny',
    'Fluent or persuasive in speaking or writing',
    null,
    'ADJECTIVE',
    false,
    CURRENT_TIMESTAMP
);

-- 9. ambiguous (English)
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'ambiguous',
    'ENGLISH',
    'niejednoznaczny',
    'Open to more than one interpretation; not having one obvious meaning',
    null,
    'ADJECTIVE',
    false,
    CURRENT_TIMESTAMP
);

-- 10. quixotic (English) - word with extra mark
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'quixotic',
    'ENGLISH',
    'donkiszotowski',
    'Exceedingly idealistic; unrealistic and impractical',
    'FORMAL',
    'ADJECTIVE',
    false,
    CURRENT_TIMESTAMP
);

-- 11. melancholy (English) - approved word
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'melancholy',
    'ENGLISH',
    'melancholia, smutek',
    'A feeling of pensive sadness, typically with no obvious cause',
    null,
    'NOUN',
    true,
    CURRENT_TIMESTAMP
);

-- 12. meticulous (English)
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'meticulous',
    'ENGLISH',
    'skrupulatny, drobiazgowy',
    'Showing great attention to detail; very careful and precise',
    null,
    'ADJECTIVE',
    false,
    CURRENT_TIMESTAMP
);

-- 13. reticent (English)
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'reticent',
    'ENGLISH',
    'powściągliwy, małomówny',
    'Not revealing one''s thoughts or feelings readily',
    null,
    'ADJECTIVE',
    false,
    CURRENT_TIMESTAMP
);

-- 14. gregarious (English)
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'gregarious',
    'ENGLISH',
    'towarzyski',
    'Fond of company; sociable',
    null,
    'ADJECTIVE',
    false,
    CURRENT_TIMESTAMP
);

-- 15. verbose (English)
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'verbose',
    'ENGLISH',
    'rozwlekły, wielosłowny',
    'Using or expressed in more words than are needed',
    null,
    'ADJECTIVE',
    false,
    CURRENT_TIMESTAMP
);
