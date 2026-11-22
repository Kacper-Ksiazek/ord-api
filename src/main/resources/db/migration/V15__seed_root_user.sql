-------------
-- Seed: root user account, language proficiencies, bank groups and banks
-- Seed data is edited in place (one file per category); do not add patch migrations.
-------------

-------------
--- users
-------------
INSERT INTO public.users (id, name, email, native_language, selected_learning_language, is_account_initialized,
                          created_at, updated_at)
VALUES ('aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'Kacper Książek', 'kacper.b.ksiazek@gmail.com',
        'POLISH', 'ENGLISH', true,
        '2025-06-15 14:33:06.851385 +00:00', '2025-06-15 14:33:06.851387 +00:00');



-------------
-- language_proficiencies
-------------
INSERT INTO public.language_proficiencies (id, language, level, user_id, translate_to, generative_content_language,
                                           created_at)
VALUES ('17963c02-5627-4c10-b0e6-55eb1c6a1c61', 'ENGLISH', 'C1', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'POLISH',
        'ENGLISH',
        '2025-06-15 14:33:06.854763 +00:00'),
('9a3df940-52f4-4d5e-ba36-b2fb6e4715ff', 'GERMAN', 'A2', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'POLISH',
        'ENGLISH',
        '2025-06-15 14:33:06.857153 +00:00'),
('110fbc1d-f265-4905-b0e8-5cc64e9a33e8', 'SLOVENIAN', 'A1', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'POLISH',
        'ENGLISH',
        '2025-06-15 14:33:06.859346 +00:00');


-------------
-- bank groups
-------------
INSERT INTO public.bank_groups (id, name, color, user_id, created_at, updated_at)
VALUES ('4c574cf5-fb00-4285-9e72-eab699c90970', 'Idioms & Phrases', '#8B5CF6', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '2025-06-15 14:33:06.880136 +00:00', '2025-06-15 14:33:06.880136 +00:00'),
       ('0389c54f-cccc-4f41-bc19-bf2af0191209', 'Business English', '#2563EB', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '2025-06-15 14:33:06.881363 +00:00', '2025-06-15 14:33:06.881367 +00:00'),
       ('59ba22c3-0099-4f40-98f0-59c3df4b6d8a', 'Legal & Formal', '#64748B', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '2025-06-15 14:33:06.882265 +00:00', '2025-06-15 14:33:06.882268 +00:00'),
       ('a239f7a6-8ca9-4a59-945a-8e9b20926764', 'Health & Nature', '#059669', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '2025-06-15 14:33:06.882878 +00:00', '2025-06-15 14:33:06.882880 +00:00'),
       ('b8e3f1a2-4c5d-6e7f-8a9b-0c1d2e3f4a5b', 'Everyday Vocabulary', '#F59E0B', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '2025-06-15 14:33:06.883500 +00:00', '2025-06-15 14:33:06.883503 +00:00');

-------------
-- banks
-------------
INSERT INTO public.banks (id, name, description, user_id, group_id, created_at, updated_at)
VALUES ('1ac13fa3-b08b-4982-88ae-da61dce3c354', 'Idioms & Phrases',
        'Idioms, fixed expressions, and multi-word phrases', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '4c574cf5-fb00-4285-9e72-eab699c90970', '2025-06-15 14:33:06.893907 +00:00',
        '2025-06-15 14:33:06.893910 +00:00'),
       ('172bca8f-7386-4dbd-b3d1-60dcce573ec4', 'Business English',
        'Workplace, economics, and professional vocabulary', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', '2025-06-15 14:33:06.895270 +00:00',
        '2025-06-15 14:33:06.895273 +00:00'),
       ('2f3afc16-e1be-44d6-aab0-5b1363e22ec3', 'Legal & Formal',
        'Legal terms and formal register vocabulary', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', '2025-06-15 14:33:06.896361 +00:00',
        '2025-06-15 14:33:06.896363 +00:00'),
       ('edb81261-1f1d-432a-9436-1d9303add361', 'Health & Nature',
        'Health, medicine, and nature-related words', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', '2025-06-15 14:33:06.897409 +00:00',
        '2025-06-15 14:33:06.897411 +00:00'),
       ('caec18c4-3e3c-43c7-8c98-ebefcb0659b7', 'Everyday Vocabulary',
        'General-purpose adjectives, verbs, and nouns for daily use', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        'b8e3f1a2-4c5d-6e7f-8a9b-0c1d2e3f4a5b', '2025-06-15 14:33:06.894716 +00:00',
        '2025-06-15 14:33:06.894718 +00:00');
