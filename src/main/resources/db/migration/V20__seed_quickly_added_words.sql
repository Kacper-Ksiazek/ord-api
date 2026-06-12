-------------
-- quickly_added_words for root user
-- Rich seed data: translation, definition, type, extra_mark, and staggered created_at
-- for list UI (week grouping, pagination, optional fields).
-------------

-- 1. ephemeral — this week
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'ephemeral',
    'ENGLISH',
    'efemeryczny, ulotny',
    'Lasting for a very short time. Example: Social media trends often feel ephemeral — here today, forgotten tomorrow.',
    'FORMAL',
    'ADJECTIVE',
    false,
    '2026-05-23 09:15:00+00'
);

-- 2. serendipity — this week
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'serendipity',
    'ENGLISH',
    'szczęśliwy traf, przypadkowe szczęście',
    'The occurrence of events by chance in a happy or beneficial way. Example: I found this café by serendipity while getting lost in Lisbon.',
    null,
    'NOUN',
    false,
    '2026-05-22 18:40:00+00'
);

-- 3. ubiquitous — this week
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'ubiquitous',
    'ENGLISH',
    'wszechobecny, powszechny',
    'Present, appearing, or found everywhere. Example: Smartphones became ubiquitous within a decade.',
    'SCIENTIFIC',
    'ADJECTIVE',
    false,
    '2026-05-21 11:05:00+00'
);

-- 4. conundrum — this week
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'conundrum',
    'ENGLISH',
    'zagadka, dylemat',
    'A confusing and difficult problem or question. Example: The team faced a conundrum: ship fast or refactor first?',
    null,
    'NOUN',
    false,
    '2026-05-20 08:30:00+00'
);

-- 5. juxtapose — last week
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'juxtapose',
    'ENGLISH',
    'zestawiać obok siebie (dla kontrastu)',
    'Place or deal with close together for contrasting effect. Example: The documentary juxtaposes wealth and poverty in the same city block.',
    'FORMAL',
    'VERB',
    false,
    '2026-05-17 16:20:00+00'
);

-- 6. surreptitious — last week (approved)
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'surreptitious',
    'ENGLISH',
    'potajemny, ukradkowy',
    'Kept secret, especially because it would not be approved of. Example: He took a surreptitious glance at his phone during the meeting.',
    'FORMAL',
    'ADJECTIVE',
    true,
    '2026-05-16 13:45:00+00'
);

-- 7. procrastinate — last week
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'procrastinate',
    'ENGLISH',
    'zwlekać, odkładać na później',
    'Delay or postpone action; put off doing something. Example: I tend to procrastinate when a task feels ambiguous.',
    'INFORMAL',
    'VERB',
    false,
    '2026-05-15 20:10:00+00'
);

-- 8. eloquent — last week
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'eloquent',
    'ENGLISH',
    'elokwentny, wymowny',
    'Fluent or persuasive in speaking or writing. Example: Her eloquent apology defused the tension immediately.',
    null,
    'ADJECTIVE',
    false,
    '2026-05-14 07:55:00+00'
);

-- 9. ambiguous — two weeks ago
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'ambiguous',
    'ENGLISH',
    'niejednoznaczny, dwuznaczny',
    'Open to more than one interpretation; not having one obvious meaning. Example: The email was deliberately ambiguous about deadlines.',
    'FORMAL',
    'ADJECTIVE',
    false,
    '2026-05-08 15:00:00+00'
);

-- 10. quixotic — two weeks ago
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'quixotic',
    'ENGLISH',
    'donkiszotowski, nierealistycznie idealistyczny',
    'Exceedingly idealistic; unrealistic and impractical. Example: His quixotic plan assumed everyone would cooperate willingly.',
    'FORMAL',
    'ADJECTIVE',
    false,
    '2026-05-07 10:25:00+00'
);

-- 11. melancholy — two weeks ago (approved)
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'melancholy',
    'ENGLISH',
    'melancholia, smutek',
    'A feeling of pensive sadness, typically with no obvious cause. Example: Rainy Sundays always put me in a melancholy mood.',
    'POETIC',
    'NOUN',
    true,
    '2026-05-06 19:30:00+00'
);

-- 12. meticulous — three weeks ago
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'meticulous',
    'ENGLISH',
    'skrupulatny, drobiazgowy',
    'Showing great attention to detail; very careful and precise. Example: He meticulously checked every migration before deploy.',
    'FORMAL',
    'ADJECTIVE',
    false,
    '2026-04-30 12:00:00+00'
);

-- 13. reticent — three weeks ago
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'reticent',
    'ENGLISH',
    'powściągliwy, małomówny',
    'Not revealing one''s thoughts or feelings readily. Example: He was reticent about his previous job during the interview.',
    'FORMAL',
    'ADJECTIVE',
    false,
    '2026-04-29 09:40:00+00'
);

-- 14. gregarious — four weeks ago
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'gregarious',
    'ENGLISH',
    'towarzyski, kontaktowy',
    'Fond of company; sociable. Example: She is gregarious at conferences and remembers everyone''s name.',
    null,
    'ADJECTIVE',
    false,
    '2026-04-22 17:15:00+00'
);

-- 15. verbose — four weeks ago
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'verbose',
    'ENGLISH',
    'rozwlekły, wielosłowny',
    'Using or expressed in more words than are needed. Example: The report was so verbose that the main point got buried.',
    'INFORMAL',
    'ADJECTIVE',
    false,
    '2026-04-21 06:50:00+00'
);

-- 16. capricious — unconfirmed (this week)
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'capricious',
    'ENGLISH',
    'kapryśny, zmienny',
    'Given to sudden and unaccountable changes of mood or behavior. Example: The weather here is capricious — sunshine one hour, hail the next.',
    'FORMAL',
    'ADJECTIVE',
    false,
    '2026-05-24 14:20:00+00'
);

-- 17. obfuscate — unconfirmed (this week)
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'obfuscate',
    'ENGLISH',
    'zaciemniać, utrudniać zrozumienie',
    'Render obscure, unclear, or unintelligible. Example: The legal jargon obfuscates what the contract actually requires.',
    'FORMAL',
    'VERB',
    false,
    '2026-05-24 10:05:00+00'
);

-- 18. laconic — unconfirmed (this week)
INSERT INTO public.quickly_added_words (id, user_id, word, language, translation, definition, extra_mark, type, is_approved, created_at)
VALUES (
    gen_random_uuid(),
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    'laconic',
    'ENGLISH',
    'lakoniczny, zwięzły',
    'Using very few words. Example: His laconic reply — "Fine." — ended the discussion.',
    'FORMAL',
    'ADJECTIVE',
    false,
    '2026-05-24 08:00:00+00'
);
