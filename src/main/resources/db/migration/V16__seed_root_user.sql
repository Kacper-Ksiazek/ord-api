-------------
--- users
-------------
INSERT INTO public.users (id, name, email, password, role, native_language, created_at, updated_at)
VALUES ('aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'Kacper Książek', 'kacper.b.ksiazek@gmail.com',
        '$2a$10$5qMhiNmCVG/J.dSL81Qq0exgOUaydblHw25igbUsRpxiFtbm2zyTm', 'ADMIN', 'POLISH',
        '2025-06-15 14:33:06.851385 +00:00', '2025-06-15 14:33:06.851387 +00:00');


-------------
-- language_proficiencies
-------------
INSERT INTO public.language_proficiencies (id, language, proficiency, user_id, generative_content_language, created_at,
                                           updated_at)
VALUES ('17963c02-5627-4c10-b0e6-55eb1c6a1c61', 'ENGLISH', 'C1', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'ENGLISH',
        '2025-06-15 14:33:06.854763 +00:00', '2025-06-15 14:33:06.854766 +00:00');
INSERT INTO public.language_proficiencies (id, language, proficiency, user_id, generative_content_language, created_at,
                                           updated_at)
VALUES ('9a3df940-52f4-4d5e-ba36-b2fb6e4715ff', 'GERMAN', 'A2', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'ENGLISH',
        '2025-06-15 14:33:06.857153 +00:00', '2025-06-15 14:33:06.857155 +00:00');
INSERT INTO public.language_proficiencies (id, language, proficiency, user_id, generative_content_language, created_at,
                                           updated_at)
VALUES ('110fbc1d-f265-4905-b0e8-5cc64e9a33e8', 'SLOVENIAN', 'A1', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'ENGLISH',
        '2025-06-15 14:33:06.859346 +00:00', '2025-06-15 14:33:06.859349 +00:00');

-------------
-- bank groups
-------------
INSERT INTO public.bank_groups (id, name, color, user_id, created_at, updated_at)
VALUES ('4c574cf5-fb00-4285-9e72-eab699c90970', 'Yellow Valley', '#FFD700', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '2025-06-15 14:33:06.880136 +00:00', '2025-06-15 14:33:06.880140 +00:00');
INSERT INTO public.bank_groups (id, name, color, user_id, created_at, updated_at)
VALUES ('0389c54f-cccc-4f41-bc19-bf2af0191209', 'Blue Ocean', '#0000FF', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '2025-06-15 14:33:06.881363 +00:00', '2025-06-15 14:33:06.881367 +00:00');
INSERT INTO public.bank_groups (id, name, color, user_id, created_at, updated_at)
VALUES ('59ba22c3-0099-4f40-98f0-59c3df4b6d8a', 'Green Forest', '#008000', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '2025-06-15 14:33:06.882265 +00:00', '2025-06-15 14:33:06.882268 +00:00');
INSERT INTO public.bank_groups (id, name, color, user_id, created_at, updated_at)
VALUES ('a239f7a6-8ca9-4a59-945a-8e9b20926764', 'Red Desert', '#FF0000', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '2025-06-15 14:33:06.882878 +00:00', '2025-06-15 14:33:06.882880 +00:00');

-------------
-- banks
-------------
INSERT INTO public.banks (id, name, description, user_id, group_id, created_at, updated_at)
VALUES ('1ac13fa3-b08b-4982-88ae-da61dce3c354', 'Valley November 2024',
        'Words associated with the Valley, accumulated in November 2024', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '4c574cf5-fb00-4285-9e72-eab699c90970', '2025-06-15 14:33:06.893907 +00:00',
        '2025-06-15 14:33:06.893910 +00:00');
INSERT INTO public.banks (id, name, description, user_id, group_id, created_at, updated_at)
VALUES ('caec18c4-3e3c-43c7-8c98-ebefcb0659b7', 'Valley December 2024',
        'Words associated with the Valley, accumulated in December 2024', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '4c574cf5-fb00-4285-9e72-eab699c90970', '2025-06-15 14:33:06.894716 +00:00',
        '2025-06-15 14:33:06.894718 +00:00');
INSERT INTO public.banks (id, name, description, user_id, group_id, created_at, updated_at)
VALUES ('172bca8f-7386-4dbd-b3d1-60dcce573ec4', 'Ocean June 2025',
        'Words associated with the Ocean, accumulated in June 2025', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', '2025-06-15 14:33:06.895270 +00:00',
        '2025-06-15 14:33:06.895273 +00:00');
INSERT INTO public.banks (id, name, description, user_id, group_id, created_at, updated_at)
VALUES ('929f555b-f372-48df-a9f7-7a4ec628d79e', 'Ocean July 2025',
        'Words associated with the Ocean, accumulated in July 2025', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', '2025-06-15 14:33:06.895821 +00:00',
        '2025-06-15 14:33:06.895823 +00:00');
INSERT INTO public.banks (id, name, description, user_id, group_id, created_at, updated_at)
VALUES ('2f3afc16-e1be-44d6-aab0-5b1363e22ec3', 'Forest August 2025',
        'Words associated with the Forest, accumulated in August 2025', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', '2025-06-15 14:33:06.896361 +00:00',
        '2025-06-15 14:33:06.896363 +00:00');
INSERT INTO public.banks (id, name, description, user_id, group_id, created_at, updated_at)
VALUES ('bcefd281-0385-412c-9c83-31484e56dd8c', 'Forest September 2025',
        'Words associated with the Forest, accumulated in September 2025', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', '2025-06-15 14:33:06.896894 +00:00',
        '2025-06-15 14:33:06.896896 +00:00');
INSERT INTO public.banks (id, name, description, user_id, group_id, created_at, updated_at)
VALUES ('edb81261-1f1d-432a-9436-1d9303add361', 'Desert October 2025',
        'Words associated with the Desert, accumulated in October 2025', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', '2025-06-15 14:33:06.897409 +00:00',
        '2025-06-15 14:33:06.897411 +00:00');
INSERT INTO public.banks (id, name, description, user_id, group_id, created_at, updated_at)
VALUES ('8e3a727a-908a-4c06-8ff5-d508f6868f90', 'Desert November 2025',
        'Words associated with the Desert, accumulated in November 2025', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', '2025-06-15 14:33:06.897851 +00:00',
        '2025-06-15 14:33:06.897853 +00:00');

-------------
-- words
-------------
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('6ab0a01f-97eb-4114-8ba2-8f4a657e94cd', 'NOUN', null, 'boardwalk', 'bulevard',
        'A *boardwalk* is a wooden walkway, often found near beaches, used for pedestrian traffic and entertainment.',
        '[
          "The amusement park is located next to the *boardwalk*.",
          "Many tourists enjoy walking along the *boardwalk* at the beach.",
          "You can find numerous shops and restaurants along the *boardwalk*."
        ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "We strolled along the *boardwalk* while enjoying the ocean view.",
      "translation": "Spacerowaliśmy po *bulevardzie*, podziwiając widok na ocean."
    },
    {
      "sentence": "There are many street performers on the *boardwalk* at night.",
      "translation": "W nocy na *bulevardzie* jest wielu artystów ulicznych."
    },
    {
      "sentence": "The *boardwalk* was crowded with people enjoying the summer sun.",
      "translation": "*Bulevard* był zatłoczony ludźmi cieszącymi się letnim słońcem."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '8e3a727a-908a-4c06-8ff5-d508f6868f90',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', null, '2025-06-15 14:33:06.983653 +00:00',
        '2025-06-15 14:33:06.983658 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('e1e401da-0224-4356-bcdc-c89e5237f3c8', 'NOUN', null, 'beehive', 'ul',
        'A *beehive* is a structure where bees live and produce honey.', '[
    "A *beehive* can be made of straw, wood, or plastic.",
    "The *beehive* is essential for the pollination of many plants.",
    "Beekeepers often inspect the *beehive* for honey and signs of pests."
  ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "A healthy *beehive* can produce a large amount of honey.",
      "translation": "Zdrowy *ul* może wyprodukować dużą ilość miodu."
    },
    {
      "sentence": "The *beehive* was buzzing with activity during summer.",
      "translation": "W *ulu* panował zgiełk podczas lata."
    },
    {
      "sentence": "She learned how to care for a *beehive* from her grandfather.",
      "translation": "Nauczyła się jak dbać o *ul* od swojego dziadka."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null, '2025-06-15 14:33:06.985073 +00:00',
        '2025-06-15 14:33:06.985075 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('907af8cc-c68e-4c11-b506-bbfa7545bc61', 'VERB', null, 'mutter', 'mamroczyć',
        'To speak quietly and indistinctly, often expressing dissatisfaction.', '[
    "Commonly used when someone is murmuring under their breath.",
    "Can imply that someone is complaining or expressing annoyance in a subtle manner.",
    "Used to describe the act of speaking in a low voice, often unintelligibly."
  ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "I heard him *mutter* a few words of frustration.",
      "translation": "Słyszałem, jak *mamroczył* kilka słów frustracji."
    },
    {
      "sentence": "She tends to *mutter* when she is unhappy about something.",
      "translation": "Ona zwykle *mamrocze*, gdy jest czegoś niezadowolona."
    },
    {
      "sentence": "He couldn''t help but *mutter* under his breath during the lecture.",
      "translation": "Nie mógł się powstrzymać od *mamrotania* pod nosem podczas wykładu."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '929f555b-f372-48df-a9f7-7a4ec628d79e',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', null, '2025-06-15 14:33:06.985881 +00:00',
        '2025-06-15 14:33:06.985883 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('43166a14-fc87-480c-8f4b-4661d3c7aa2a', 'ADJECTIVE', null, 'sluggish', 'ospale',
        'The word ''sluggish'' describes a state of being slow or lacking energy.', '[
    "Referring to a person''s lack of energy.",
    "Indicating a slow or inactive process.",
    "Describing a slow-moving vehicle or object."
  ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "The *sluggish* economy is causing concern among analysts.",
      "translation": "Ospala gospodarka budzi zaniepokojenie wśród analityków."
    },
    {
      "sentence": "The engine was *sluggish* to respond, indicating a potential issue.",
      "translation": "Silnik zareagował *ospale*, co wskazuje na potencjalny problem."
    },
    {
      "sentence": "After the long flight, I felt really *sluggish*.",
      "translation": "Po długim locie czułem się naprawdę *ospale*."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null, '2025-06-15 14:33:06.986633 +00:00',
        '2025-06-15 14:33:06.986635 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('a0992a09-7586-48dc-8b85-d7fa6365565a', 'ADJECTIVE', null, 'lethargic', 'letargiczny',
        'The word describes a state of sluggishness or lack of energy. It is often used to describe a person who is inactive or apathetic.',
        '[
          "Describing someone who is feeling worn out or tired.",
          "Referring to a temporary condition after illness or fatigue.",
          "Indicating a lack of motivation or enthusiasm."
        ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "He was unusually *lethargic* after recovering from the flu.",
      "translation": "Po wyzdrowieniu z grypy był niezwykle *letargiczny*."
    },
    {
      "sentence": "After the long meeting, I felt *lethargic* and needed a break.",
      "translation": "Po długim spotkaniu czułem się *letargiczny* i potrzebowałem przerwy."
    },
    {
      "sentence": "The hot weather made everyone feel *lethargic* during the afternoon.",
      "translation": "Gorąca pogoda sprawiła, że wszyscy czuli się *letargiczni* w ciągu popołudnia."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'bcefd281-0385-412c-9c83-31484e56dd8c',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', null, '2025-06-15 14:33:06.987352 +00:00',
        '2025-06-15 14:33:06.987354 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('93e418c8-b601-4117-a0d8-83c08d59a0ca', 'ADJECTIVE', null, 'ephemeral', 'ephemeralny',
        'Something that lasts for a very short time.', '[
    "Many digital forms of art are considered ephemeral, existing only for a brief period.",
    "Ephemeral beauty refers to beauty that is fleeting and temporary.",
    "The ephemeral nature of life reminds us to cherish each moment."
  ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "Art installations often embrace the *ephemeral* aspect of creativity.",
      "translation": "Instalacje artystyczne często akceptują *ephemeralny* aspekt kreatywności."
    },
    {
      "sentence": "He enjoyed the *ephemeral* joy of the moment.",
      "translation": "Cieszył się *ephemeralną* radością chwili."
    },
    {
      "sentence": "The flowers have an *ephemeral* beauty that lasts only a few days.",
      "translation": "Kwiaty mają *ephemeralny* urok, który trwa tylko kilka dni."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'edb81261-1f1d-432a-9436-1d9303add361',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', null, '2025-06-15 14:33:06.988006 +00:00',
        '2025-06-15 14:33:06.988008 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('2fb8b299-0199-4e9d-a720-5b44fba977a7', 'ADJECTIVE', null, 'transient', 'przejrzysty',
        'Transient refers to something temporary or lasting for a short period of time.', '[
    "The transient population of the town changes with the seasons.",
    "He had a transient stay in the hotel before moving to another city.",
    "Transient emotions can lead to temporary decisions."
  ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "Many transient workers come to the area for seasonal jobs.",
      "translation": "Wielu przejrzystych pracowników przyjeżdża do okolicy na sezonowe prace."
    },
    {
      "sentence": "Her transient happiness was evident during the brief celebration.",
      "translation": "Jej przejrzysta radość była widoczna podczas krótkiej celebracji."
    },
    {
      "sentence": "The transient guests often leave their mark on the community.",
      "translation": "Przejrzystych gości często pozostawia ślad w społeczności."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'caec18c4-3e3c-43c7-8c98-ebefcb0659b7',
        '4c574cf5-fb00-4285-9e72-eab699c90970', null, '2025-06-15 14:33:06.988655 +00:00',
        '2025-06-15 14:33:06.988657 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('3acf17a4-770f-4d99-928b-4cb113f94355', 'ADJECTIVE', null, 'fleeting', 'ulotny',
        'Something that lasts for a very short time or is difficult to hold onto.', '[
    "He captured the fleeting beauty of the sunset in his painting.",
    "The joy of the moment was fleeting.",
    "The fleeting nature of youth is often reflected upon."
  ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "He captured the *fleeting* beauty of the sunset in his painting.",
      "translation": "Uchwycił *ulotne* piękno zachodu słońca w swoim obrazie."
    },
    {
      "sentence": "The *fleeting* nature of youth is often reflected upon.",
      "translation": "Ulotna natura młodości jest często przedmiotem refleksji."
    },
    {
      "sentence": "The joy of the moment was *fleeting*.",
      "translation": "Radość chwili była *ulotna*."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '929f555b-f372-48df-a9f7-7a4ec628d79e',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', null, '2025-06-15 14:33:06.989442 +00:00',
        '2025-06-15 14:33:06.989452 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('d92400eb-077d-4d7f-a095-9b850c44106f', 'VERB', null, 'depletes', 'wyczerpuje',
        'To diminish the quantity or supply of something; to exhaust resources or energy.', '[
    "Running excessively can deplete one''s energy levels.",
    "He is trying to deplete his debt by making regular payments.",
    "To deplete natural resources can cause environmental issues."
  ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "If we continue to *deplete* fossil fuels, future generations will face severe consequences.",
      "translation": "Jeśli nadal będziemy *wyczerpywać* paliwa kopalne, przyszłe pokolenia staną przed poważnymi konsekwencjami."
    },
    {
      "sentence": "The intense training can *deplete* an athlete''s stamina.",
      "translation": "Intensywne treningi mogą *wyczerpywać* wytrzymałość sportowca."
    },
    {
      "sentence": "The factory''s operations *deplete* the local water supply.",
      "translation": "Działalność fabryki *wyczerpuje* lokalne zasoby wody."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '2f3afc16-e1be-44d6-aab0-5b1363e22ec3',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', null, '2025-06-15 14:33:06.990228 +00:00',
        '2025-06-15 14:33:06.990232 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('1ed73d97-9755-4ef6-bfc5-39de627706b6', 'IDIOM', null, 'don''t shoot them down in flames',
        'nie zestrzelaj ich w płomieniach',
        'This idiom means to harshly criticize or dismiss someone''s ideas or efforts, often in a way that can be humiliating.',
        '[
          "Used to warn someone not to undermine or belittle someone else''s efforts.",
          "Applicable in professional settings where constructive feedback is essential.",
          "Commonly used in informal contexts to advise someone to be supportive rather than destructive."
        ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "Instead of shooting her down in flames, try to offer some constructive criticism.",
      "translation": "Zamiast *zestrzelić jej w płomieniach*, spróbuj zaproponować konstruktywną krytykę."
    },
    {
      "sentence": "He was afraid to present his ideas because he didn''t want others to shoot him down in flames.",
      "translation": "Obawiał się przedstawić swoje pomysły, ponieważ nie chciał, żeby inni *zestrzelali go w płomieniach*."
    },
    {
      "sentence": "When giving feedback, remember: don''t shoot them down in flames.",
      "translation": "Podczas udzielania informacji zwrotnej pamiętaj: *nie zestrzelaj ich w płomieniach*."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'edb81261-1f1d-432a-9436-1d9303add361',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', null, '2025-06-15 14:33:06.990929 +00:00',
        '2025-06-15 14:33:06.990932 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('d32becba-9815-47e6-b18a-0a5b2c9b7909', 'IDIOM', null, 'fruit of thought', 'owoce myśli',
        'The phrase ''fruit of thought'' refers to the results or outcomes of contemplation, reflection, or intellectual effort.',
        '[
          "Reflecting on a difficult decision often leads to the *fruit of thought* that guides your actions.",
          "The *fruit of thought* from our discussions helped us to arrive at a consensus.",
          "Through meditation, she found the *fruit of thought* that would inspire her next project."
        ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "He wrote down the *fruit of thought* that came to him during his walk in the park.",
      "translation": "Zanotował owoce myśli, które przyszły mu do głowy podczas spaceru w parku."
    },
    {
      "sentence": "After hours of reflection, she finally understood the *fruit of thought* she had been searching for.",
      "translation": "Po wielu godzinach refleksji w końcu zrozumiała owoce myśli, których szukała."
    },
    {
      "sentence": "The *fruit of thought* from our brainstorming session was truly impressive.",
      "translation": "Owoce myśli z naszej sesji burzy mózgów były naprawdę imponujące."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'bcefd281-0385-412c-9c83-31484e56dd8c',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', null, '2025-06-15 14:33:06.991540 +00:00',
        '2025-06-15 14:33:06.991543 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('17d9c6e7-adfd-4a64-a1e2-eff9c9adebed', 'IDIOM', null, 'down the rabbit hole', 'w dół króliczej nory',
        'The phrase ''down the rabbit hole'' refers to a situation that becomes increasingly complex or bizarre, often leading to unexpected outcomes.',
        '[
          "Getting involved in conspiracy theories can take you down a rabbit hole of doubt and confusion.",
          "Exploring a new topic can sometimes lead you down the rabbit hole of information.",
          "Once you start watching those videos, you might find yourself going down the rabbit hole."
        ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "After reading one article, I went down the rabbit hole and spent hours online.",
      "translation": "Po przeczytaniu jednego artykułu, wpadłem w dół króliczej nory i spędziłem godziny online."
    },
    {
      "sentence": "He started researching a simple question, but it led him down the rabbit hole of quantum physics.",
      "translation": "Zaczął badać proste pytanie, ale to zaprowadziło go w dół króliczej nory fizyki kwantowej."
    },
    {
      "sentence": "Once she started following the links, there was no stopping her; she was down the rabbit hole.",
      "translation": "Gdy tylko zaczęła podążać za linkami, nie dało się jej zatrzymać; była w dół króliczej nory."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'bcefd281-0385-412c-9c83-31484e56dd8c',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', null, '2025-06-15 14:33:06.992074 +00:00',
        '2025-06-15 14:33:06.992079 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('f3b391f5-91d0-4a5f-b8da-db906c812f3d', 'ADJECTIVE', null, 'well-to-do', 'zamożny',
        'A term used to describe someone who is wealthy or affluent.', '[
    "Indicating communities or neighborhoods characterized by high-income residents.",
    "Describing individuals with significant financial resources.",
    "Referring to families with considerable wealth."
  ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "The *well-to-do* family donated generously to the local charity.",
      "translation": "Rodzina *zamożna* hojnie przekazała darowiznę na lokalną organizację charytatywną."
    },
    {
      "sentence": "She moved to a *well-to-do* neighborhood to provide her children with better opportunities.",
      "translation": "Przeprowadziła się do *zamożnej* dzielnicy, aby zapewnić swoim dzieciom lepsze możliwości."
    },
    {
      "sentence": "Many *well-to-do* individuals participate in philanthropic activities.",
      "translation": "Wielu *zamożnych* ludzi angażuje się w działalność filantropijną."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null, '2025-06-15 14:33:06.992581 +00:00',
        '2025-06-15 14:33:06.992584 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('4cea91f7-7ed7-4722-8044-280573fb8ade', 'ADJECTIVE', null, 'across-the-board', 'ogólny',
        'The term ''across-the-board'' refers to something that applies universally or to all parts or members without exception.',
        '[
          "There was an across-the-board increase in salaries this year.",
          "The new policy will apply across-the-board, affecting all employees.",
          "The changes will be implemented across-the-board for all departments."
        ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "We expect an *across-the-board* improvement in performance.",
      "translation": "Spodziewamy się *ogólnej* poprawy wydajności."
    },
    {
      "sentence": "The company announced an *across-the-board* salary increase.",
      "translation": "Firma ogłosiła *ogólny* wzrost wynagrodzeń."
    },
    {
      "sentence": "The reforms are *across-the-board* and will impact everyone.",
      "translation": "Reformy są *ogólne* i wpłyną na wszystkich."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null, '2025-06-15 14:33:06.993075 +00:00',
        '2025-06-15 14:33:06.993078 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('b1898c39-791a-48f3-ab3a-3ef7e03d8fad', 'ADJECTIVE', null, 'run-of-the-mill', 'przeciętny',
        'The term describes something that is ordinary or of average quality, lacking any special features or characteristics.',
        '[
          "To refer to a person or thing that is not exceptional in any way.",
          "To describe an average quality or typical example of something."
        ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "Her performance was just *run-of-the-mill*.",
      "translation": "Jej występ był po prostu *przeciętny*."
    },
    {
      "sentence": "He has a *run-of-the-mill* job that doesn''t challenge him.",
      "translation": "Ma *przeciętną* pracę, która go nie wyzwala."
    },
    {
      "sentence": "The restaurant served a *run-of-the-mill* meal that didn''t impress anyone.",
      "translation": "Restauracja serwowała *przeciętny* posiłek, który nikogo nie zachwycił."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '929f555b-f372-48df-a9f7-7a4ec628d79e',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', null, '2025-06-15 14:33:06.993569 +00:00',
        '2025-06-15 14:33:06.993572 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('05afa286-5935-4cd4-a9a1-a51038149eb3', 'ADJECTIVE', null, 'state-of-the-art', 'najnowocześniejszy',
        'The term refers to the most advanced or sophisticated technology, methods, or design currently available.', '[
    "Describing the most advanced methods in a specific field.",
    "Referring to the latest technology in a product.",
    "Indicating that something is at the forefront of innovation."
  ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "This smartphone features a *state-of-the-art* camera.",
      "translation": "Ten smartfon ma *najnowocześniejszy* aparat."
    },
    {
      "sentence": "Our company uses *state-of-the-art* equipment for manufacturing.",
      "translation": "Nasza firma używa *najnowocześniejszego* sprzętu do produkcji."
    },
    {
      "sentence": "The laboratory is equipped with *state-of-the-art* technology.",
      "translation": "Laboratorium jest wyposażone w *najnowocześniejszą* technologię."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'bcefd281-0385-412c-9c83-31484e56dd8c',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', null, '2025-06-15 14:33:06.994106 +00:00',
        '2025-06-15 14:33:06.994213 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('82f7cb46-3e87-433a-a4d5-c5fa2eaf7eff', 'ADJECTIVE', null, 'off-the-cuff', 'z marszu',
        'The term ''off-the-cuff'' refers to something said or done spontaneously, without preparation.', '[
    "Her off-the-cuff remarks often make the atmosphere lively.",
    "Giving a speech off-the-cuff can be challenging but also exciting.",
    "He answered the questions off-the-cuff, demonstrating his deep knowledge."
  ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "He made an *off-the-cuff* comment that caught everyone by surprise.",
      "translation": "Zrobił *z marszu* uwagi, które zaskoczyły wszystkich."
    },
    {
      "sentence": "His *off-the-cuff* jokes often lighten the mood in meetings.",
      "translation": "Jego *z marszu* żarty często rozluźniają atmosferę na spotkaniach."
    },
    {
      "sentence": "When asked about the policy, she provided an *off-the-cuff* response.",
      "translation": "Zapytana o politykę, dała *z marszu* odpowiedź."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null, '2025-06-15 14:33:06.994772 +00:00',
        '2025-06-15 14:33:06.994775 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('a9be3be1-4d41-43a1-b8ee-be4d0cb41417', 'IDIOM', null, 'larger-than-life', 'osobowość większa niż życie',
        'This phrase describes someone or something that is notably impressive or larger than the ordinary, often in a charismatic or dramatic way.',
        '[
          "Talking about someone with grand ambitions.",
          "Describing a charismatic performer.",
          "Referring to an extravagant character in a story."
        ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "The actor is known for his *larger-than-life* persona on and off stage.",
      "translation": "Ten aktor jest znany z *osobowości większej niż życie* na scenie i poza nią."
    },
    {
      "sentence": "The event was a *larger-than-life* celebration, filled with extravagance.",
      "translation": "Wydarzenie było *świętowaniem większym niż życie*, wypełnionym ekstrawagancją."
    },
    {
      "sentence": "Her *larger-than-life* ambitions led her to achieve remarkable success.",
      "translation": "Jej *ambicje większe niż życie* doprowadziły ją do osiągnięcia niezwykłego sukcesu."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '8e3a727a-908a-4c06-8ff5-d508f6868f90',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', null, '2025-06-15 14:33:06.995239 +00:00',
        '2025-06-15 14:33:06.995242 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('d4673027-c4f5-4ace-8caa-64ca1ea560bb', 'ADJECTIVE', null, 'middle-of-the-road', 'umiarkowany',
        'The term ''middle-of-the-road'' describes something that is moderate or not extreme.', '[
    "Her taste in music is quite middle-of-the-road, favoring popular artists.",
    "The candidate has a middle-of-the-road approach to policies, appealing to both sides.",
    "The restaurant offers a middle-of-the-road menu that caters to a variety of tastes."
  ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "His views are quite *middle-of-the-road*, which makes him relatable to many voters.",
      "translation": "Jego poglądy są dość *umiarkowane*, co sprawia, że jest bliski wielu wyborcom."
    },
    {
      "sentence": "The film is a *middle-of-the-road* comedy that doesn''t take too many risks.",
      "translation": "Film to *umiarkowana* komedia, która nie podejmuje zbyt wielu ryzyk."
    },
    {
      "sentence": "She prefers a *middle-of-the-road* style when decorating her home.",
      "translation": "Preferuje *umiarkowany* styl przy urządzaniu swojego domu."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null, '2025-06-15 14:33:06.995661 +00:00',
        '2025-06-15 14:33:06.995664 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('a331d7b8-9da4-46fd-a3ed-47fb3e62927d', 'ADVERB', null, 'though-out', 'pomimo',
        'The word ''though'' is used to indicate a contrast with something previously stated or understood.', '[
    "Used to introduce a concessive clause.",
    "Used informally to add a non-essential element to a sentence."
  ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "I love to travel, *though* I don''t have much time.",
      "translation": "Uwielbiam podróżować, *pomimo* że nie mam zbyt wiele czasu."
    },
    {
      "sentence": "It was raining, *though* we still went for a walk.",
      "translation": "Padał deszcz, *pomimo* że i tak poszliśmy na spacer."
    },
    {
      "sentence": "I think the movie was good, *though* the ending was a bit predictable.",
      "translation": "Myślę, że film był dobry, *pomimo* że zakończenie było nieco przewidywalne."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '929f555b-f372-48df-a9f7-7a4ec628d79e',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', null, '2025-06-15 14:33:06.996100 +00:00',
        '2025-06-15 14:33:06.996102 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('530b52d4-006e-4af5-ad2d-0cb656a4bbfc', 'ADJECTIVE', null, 'paid-for', 'płatny',
        'The term ''paid-for'' refers to something that has been purchased or is compensated by payment.', '[
    "A paid-for subscription provides access to premium content.",
    "She prefers paid-for services for more reliable support.",
    "They invested in a paid-for advertisement to promote their product."
  ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "He subscribed to a *paid-for* streaming service for better quality.",
      "translation": "Zarejestrował się w *płatnej* usłudze strumieniowej dla lepszej jakości."
    },
    {
      "sentence": "The *paid-for* content was significantly more detailed than the free version.",
      "translation": "Treść *płatna* była znacznie bardziej szczegółowa niż wersja darmowa."
    },
    {
      "sentence": "They are considering a *paid-for* advertising campaign to increase visibility.",
      "translation": "Rozważają *płatną* kampanię reklamową, aby zwiększyć widoczność."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '1ac13fa3-b08b-4982-88ae-da61dce3c354',
        '4c574cf5-fb00-4285-9e72-eab699c90970', null, '2025-06-15 14:33:06.996522 +00:00',
        '2025-06-15 14:33:06.996525 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('80256eeb-7765-4c23-9fce-9ee67096670a', 'PHRASE', null, 'take-it-or-leave-it', 'bierz albo zostaw',
        'A phrase indicating that a person must accept the terms offered or forgo the opportunity altogether.', '[
    "Expressing a take-it-or-leave-it attitude in discussions.",
    "Insisting on a decision without further discussion.",
    "Used in negotiations to indicate finality of an offer."
  ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "The deal is a *take-it-or-leave-it* offer, so you need to decide quickly.",
      "translation": "Umowa jest *bierzesz albo zostawiasz*, więc musisz szybko podjąć decyzję."
    },
    {
      "sentence": "During the meeting, he made it clear that the terms were *take-it-or-leave-it*.",
      "translation": "Podczas spotkania jasno dał do zrozumienia, że warunki były *bierzesz albo zostawiasz*."
    },
    {
      "sentence": "She told him it’s a *take-it-or-leave-it* situation regarding the job offer.",
      "translation": "Powiedziała mu, że to sytuacja *bierzesz albo zostawiasz* odnośnie oferty pracy."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'bcefd281-0385-412c-9c83-31484e56dd8c',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', null, '2025-06-15 14:33:06.996925 +00:00',
        '2025-06-15 14:33:06.996927 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('57679f5b-de4c-4730-8af9-71d42cfdd015', 'VERB', null, 'go through', 'przejrzeć',
        'To examine or consider something thoroughly, often as part of a process.', '[
    "To endure a process or series of events.",
    "To assess or analyze information.",
    "To review documents or materials."
  ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "I need to *go through* these reports before the meeting.",
      "translation": "Muszę *przejrzeć* te raporty przed spotkaniem."
    },
    {
      "sentence": "She had to *go through* a lot of challenges to achieve her goals.",
      "translation": "Musiała *przejść przez* wiele wyzwań, aby osiągnąć swoje cele."
    },
    {
      "sentence": "Can you *go through* this list and check for errors?",
      "translation": "Czy możesz *przejrzeć* tę listę i sprawdzić błędy?"
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '929f555b-f372-48df-a9f7-7a4ec628d79e',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', null, '2025-06-15 14:33:07.004391 +00:00',
        '2025-06-15 14:33:07.004394 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('9027edba-d197-4968-acda-ea6627d2b440', 'PHRASE', null, 'business-as-usual', 'normalne funkcjonowanie',
        'The term ''business-as-usual'' refers to the usual or expected activities and processes in an organization, especially during unusual circumstances.',
        '[
          "Despite the disruptions, the team focused on achieving ''business-as-usual'' results.",
          "The firm''s ''business-as-usual'' strategy helped them to navigate through the pandemic.",
          "In a crisis, the company aims to maintain ''business-as-usual'' operations to ensure stability."
        ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "Even during the pandemic, we strive for *business-as-usual* operations.",
      "translation": "Nawet w czasie pandemii dążymy do *normalnego funkcjonowania*."
    },
    {
      "sentence": "Our goal is to ensure *business-as-usual* despite the recent challenges.",
      "translation": "Naszym celem jest zapewnienie *normalnego funkcjonowania* pomimo ostatnich wyzwań."
    },
    {
      "sentence": "The manager emphasized the importance of returning to *business-as-usual* after the merger.",
      "translation": "Menadżer podkreślił znaczenie powrotu do *normalnego funkcjonowania* po fuzji."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'caec18c4-3e3c-43c7-8c98-ebefcb0659b7',
        '4c574cf5-fb00-4285-9e72-eab699c90970', null, '2025-06-15 14:33:06.997297 +00:00',
        '2025-06-15 14:33:06.997300 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('1bc487fd-8aae-4ffa-8cd8-caeac700068a', 'PHRASE', null, 'all-you-can-eat', 'wszystko, co możesz zjeść',
        'An offer at a restaurant where customers can eat as much food as they want for a set price.', '[
    "Often used in promotions to attract customers.",
    "A common type of dining experience in buffet-style restaurants."
  ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "I love going to places that have *all-you-can-eat* sushi.",
      "translation": "Uwielbiam chodzić do miejsc, które mają *wszystko, co możesz zjeść* sushi."
    },
    {
      "sentence": "They advertised an *all-you-can-eat* deal for the holiday season.",
      "translation": "Reklamowali ofertę *wszystko, co możesz zjeść* na sezon świąteczny."
    },
    {
      "sentence": "The restaurant is offering an *all-you-can-eat* buffet on weekends.",
      "translation": "Restauracja oferuje *wszystko, co możesz zjeść* w formie bufetu w weekendy."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '172bca8f-7386-4dbd-b3d1-60dcce573ec4',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', null, '2025-06-15 14:33:06.997745 +00:00',
        '2025-06-15 14:33:06.997748 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('28f8f190-046a-413b-9706-efab42539f0b', 'VERB', null, 'meagre', 'fuzja',
        'To *merge* means to combine two or more entities into one.', '[
    "To *merge* data from different sources for analysis.",
    "To *merge* layers in graphic design software.",
    "To *merge* two companies into one."
  ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "Please *merge* the files into a single document.",
      "translation": "Proszę *połączyć* pliki w jeden dokument."
    },
    {
      "sentence": "The artist used the *merge* tool to combine different elements in her artwork.",
      "translation": "Artystka użyła narzędzia *fuzji*, aby połączyć różne elementy w swoim dziele."
    },
    {
      "sentence": "The two companies decided to *merge* to improve their market position.",
      "translation": "Dwie firmy zdecydowały się na *fuzję*, aby poprawić swoją pozycję na rynku."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '1ac13fa3-b08b-4982-88ae-da61dce3c354',
        '4c574cf5-fb00-4285-9e72-eab699c90970', null, '2025-06-15 14:33:06.998250 +00:00',
        '2025-06-15 14:33:06.998253 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('c659818a-ca32-40e4-aac9-33386432027a', 'NOUN', null, 'firm', 'firma',
        'A firm is a business or company, often used to refer specifically to one providing professional services.', '[
    "She works for a marketing firm.",
    "A law firm specializes in legal matters.",
    "The firm has expanded its services internationally."
  ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "She started her own *firm* last year.",
      "translation": "Założyła swoją własną *firmę* w zeszłym roku."
    },
    {
      "sentence": "The *firm* is known for its innovative products.",
      "translation": "Ta *firma* jest znana ze swoich innowacyjnych produktów."
    },
    {
      "sentence": "They hired a *firm* to handle their taxes.",
      "translation": "Zatrudnili *firmę*, aby zajęła się ich podatkami."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '8e3a727a-908a-4c06-8ff5-d508f6868f90',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', null, '2025-06-15 14:33:06.998661 +00:00',
        '2025-06-15 14:33:06.998663 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('fc4aa25a-7c25-4236-b9bc-c821cb32105d', 'ADJECTIVE', null, 'doting', 'roztkliwiony',
        'Doting refers to showing excessive love or fondness, often in a way that can be considered overly indulgent.',
        '[
          "In romantic relationships, a doting partner may be overly attentive and caring.",
          "A doting parent may spoil their child with gifts and affection.",
          "People often describe grandparents as doting because they tend to shower their grandchildren with love."
        ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "His *doting* behavior towards his dog was evident in how he pampered it.",
      "translation": "Jego *roztkliwione* zachowanie wobec psa było ewidentne w tym, jak je rozpieszczał."
    },
    {
      "sentence": "Despite being a strict teacher, he had a *doting* side that emerged during class trips.",
      "translation": "Mimo że był surowym nauczycielem, miał *roztkliwioną* stronę, która ujawniała się podczas wycieczek klasowych."
    },
    {
      "sentence": "She was a *doting* mother, always providing her children with everything they needed.",
      "translation": "Była *roztkliwioną* matką, zawsze zapewniającą swoim dzieciom wszystko, czego potrzebowały."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'edb81261-1f1d-432a-9436-1d9303add361',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', null, '2025-06-15 14:33:06.999079 +00:00',
        '2025-06-15 14:33:06.999081 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('912a72e4-6dd4-4b2e-aad4-5d5c69418042', 'ADJECTIVE', null, 'bluntly', 'szczery',
        'To speak in a straightforward and honest manner, often without regard for others'' feelings.', '[
    "Addressing difficult topics without sugar-coating.",
    "Giving a direct opinion.",
    "Providing criticism or feedback in an unfiltered way."
  ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "He was *blunt* about the project''s shortcomings during the meeting.",
      "translation": "Był *szczery* co do niedociągnięć projektu podczas spotkania."
    },
    {
      "sentence": "The teacher addressed the class *bluntly*, explaining the importance of studying.",
      "translation": "Nauczyciel *szczerze* wyjaśnił klasie znaczenie nauki."
    },
    {
      "sentence": "She told him, *bluntly*, that she didn''t like his new haircut.",
      "translation": "Powiedziała mu *szczerze*, że nie podoba jej się jego nowa fryzura."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null, '2025-06-15 14:33:06.999485 +00:00',
        '2025-06-15 14:33:06.999487 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('db1c9042-bf1b-42e3-b453-f238d4590518', 'IDIOM', null, 'to add insult to injury', 'przykładać sól do rany',
        'To add insult to injury means to make a bad situation even worse by saying or doing something that is hurtful.',
        '[
          "Criticizing a person''s efforts after they''ve already failed only adds insult to injury.",
          "If a company''s financial problems worsen and they also lose a key client, one could say they added insult to injury.",
          "When someone is already upset, making a mocking comment can add insult to injury."
        ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "She was already feeling bad about the mistake, and his sarcastic remarks just added insult to injury.",
      "translation": "Już czuła się źle z powodu błędu, a jego sarkastyczne uwagi tylko *przykładały sól do rany*."
    },
    {
      "sentence": "Their criticism after the team lost the match was adding insult to injury.",
      "translation": "Ich krytyka po tym, jak drużyna przegrała mecz, *przykładała sól do rany*."
    },
    {
      "sentence": "When he made fun of her outfit after she lost her job, he really added insult to injury.",
      "translation": "Kiedy zażartował z jej stroju po tym, jak straciła pracę, naprawdę *przykładał sól do rany*."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null, '2025-06-15 14:33:06.999869 +00:00',
        '2025-06-15 14:33:06.999871 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('f5a378cf-dc63-4785-95a0-c7a93ee665d5', 'IDIOM', null, 'to cap it all', 'a na dodatek',
        'This phrase is used to emphasize an additional, often negative, fact that culminates a series of events or situations.',
        '[
          "To conclude a list of complaints with a significant point.",
          "To express frustration by highlighting an additional issue.",
          "To indicate that something has gotten worse than expected."
        ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "I forgot my wallet, lost my job, and to cap it all, my car broke down.",
      "translation": "Zapomniałem portfela, straciłem pracę, a na dodatek zepsuł mi się samochód."
    },
    {
      "sentence": "She was late to the meeting, her presentation was a mess, and to cap it all, she spilled coffee on her boss.",
      "translation": "Spóźniła się na zebranie, jej prezentacja była bałaganem, a na dodatek przewróciła kawę na swojego szefa."
    },
    {
      "sentence": "We planned a nice vacation, but the weather was terrible, and to cap it all, we got into an accident.",
      "translation": "Zaplanowaliśmy miłe wakacje, ale pogoda była okropna, a na dodatek mieliśmy wypadek."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null, '2025-06-15 14:33:07.000326 +00:00',
        '2025-06-15 14:33:07.000328 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('19fd0bdc-6404-4d24-aee3-aefba0687ef0', 'ADJECTIVE', null, 'flustered', 'roztrzęsiony',
        'To be flustered means to be in a state of agitated confusion or nervous excitement.', '[
    "Being flustered when receiving unexpected news.",
    "Feeling flustered before an important presentation.",
    "Getting flustered when trying to multitask under pressure."
  ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "He seemed *flustered* by the sudden changes in the plan.",
      "translation": "Wydawał się *roztrzęsiony* nagłymi zmianami w planie."
    },
    {
      "sentence": "She became *flustered* when asked to speak in front of the crowd.",
      "translation": "Ona stała się *roztrzęsiona*, gdy poproszono ją o przemówienie przed tłumem."
    },
    {
      "sentence": "I always get *flustered* when I have too many things to do at once.",
      "translation": "Zawsze czuję się *roztrzęsiony*, gdy mam za dużo rzeczy do zrobienia na raz."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null, '2025-06-15 14:33:07.000953 +00:00',
        '2025-06-15 14:33:07.000956 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('7ef0d8e0-a5df-43d9-8d2c-baf16cb30ab6', 'ADJECTIVE', null, 'blunt', 'tępy',
        'The word ''blunt'' typically describes an object with a dull edge or point, or a person who speaks in a straightforward, often harsh manner.',
        '[
          "A knife that is not sharp is referred to as ''blunt''.",
          "Blunt objects do not have a cutting edge.",
          "Someone who is very direct and unrefined in their speech may be described as ''blunt''."
        ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "His *blunt* manner often offends people.",
      "translation": "Jego *tępy* sposób bycia często obraża ludzi."
    },
    {
      "sentence": "This knife is too *blunt* to cut through the meat.",
      "translation": "Ten nóż jest zbyt *tępy*, aby przeciąć mięso."
    },
    {
      "sentence": "She was *blunt* in her criticism, which surprised everyone.",
      "translation": "Była *tępą* w swojej krytyce, co zaskoczyło wszystkich."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'edb81261-1f1d-432a-9436-1d9303add361',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', null, '2025-06-15 14:33:07.001641 +00:00',
        '2025-06-15 14:33:07.001651 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('fb58b90a-4b7e-418c-a225-732451467c04', 'IDIOM', null, 'out of the blue', 'znikąd',
        'The phrase ''out of the blue'' refers to something unexpected or surprising that happens suddenly.', '[
    "Someone you haven''t spoken to in a long time contacts you suddenly.",
    "An unexpected event occurred without any warning."
  ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "She called me *out of the blue* after years of silence.",
      "translation": "Zadzwoniła do mnie *znikąd* po latach milczenia."
    },
    {
      "sentence": "I received a message from her *out of the blue*.",
      "translation": "Otrzymałem wiadomość od niej *znikąd*."
    },
    {
      "sentence": "The news of his promotion came *out of the blue*.",
      "translation": "Wiadomość o jego awansie przyszła *znikąd*."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'edb81261-1f1d-432a-9436-1d9303add361',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', null, '2025-06-15 14:33:07.002245 +00:00',
        '2025-06-15 14:33:07.002250 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('5c1ed01f-2e2c-4eb7-a8ea-479f21b91dc2', 'VERB', null, 'mingle', 'mieszać',
        'To mingle means to mix or combine with others in a social setting.', '[
    "To mingle at a party involves socializing with various guests.",
    "During the networking event, professionals mingled to exchange ideas.",
    "It''s important to mingle with your classmates to build relationships."
  ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "It''s easier to *mingle* when the atmosphere is relaxed.",
      "translation": "Łatwiej jest się *mieszać*, gdy atmosfera jest luźna."
    },
    {
      "sentence": "At the event, guests were encouraged to *mingle* and get to know each other.",
      "translation": "Na wydarzeniu goście byli zachęcani do *mieszania się* i poznawania się nawzajem."
    },
    {
      "sentence": "I love to *mingle* with different people during festivals.",
      "translation": "Uwielbiam się *mieszać* z różnymi ludźmi podczas festiwali."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'edb81261-1f1d-432a-9436-1d9303add361',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', null, '2025-06-15 14:33:07.002620 +00:00',
        '2025-06-15 14:33:07.002624 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('cadf2f13-c0b9-46dd-b803-e4e11fdbe024', 'VERB', null, 'hover', 'unosić się',
        'To remain suspended in the air without making physical contact with a surface.', '[
    "To refer to a feeling of uncertainty or indecision that persists.",
    "To describe a cursor on a computer screen that is over an item but not clicked.",
    "To indicate a hovering presence, like a bird in the air."
  ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "She felt a sense of doubt *hovering* around her decision.",
      "translation": "Czuła, jak wokół jej decyzji *unosi się* uczucie wątpliwości."
    },
    {
      "sentence": "A hummingbird *hovers* in front of the flowers.",
      "translation": "Koliber *unosi się* przed kwiatami."
    },
    {
      "sentence": "The cursor *hovers* over the file icon.",
      "translation": "Kursor *unosi się* nad ikoną pliku."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null, '2025-06-15 14:33:07.002955 +00:00',
        '2025-06-15 14:33:07.002957 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('1532ee60-8388-4882-8f22-82dad6ac96e5', 'NOUN', 'FORMAL', 'pleasantry', 'uprzejmość',
        'A pleasantry is a remark or action that is polite or friendly, often used to create a pleasant conversation atmosphere.',
        '[
          "Pleasantries are often exchanged at the beginning of conversations.",
          "In social gatherings, people might engage in pleasantries to break the ice.",
          "Writing a note of pleasantry can help establish a good rapport with recipients."
        ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "They exchanged pleasantries before discussing business.",
      "translation": "Wymienili *uprzejmości* przed omówieniem spraw biznesowych."
    },
    {
      "sentence": "He greeted her with a simple pleasantry.",
      "translation": "Przywitał ją prostą *uprzejmością*."
    },
    {
      "sentence": "Pleasantries are an essential part of friendly communication.",
      "translation": "*Uprzejmości* są nieodłącznym elementem przyjaznej komunikacji."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '1ac13fa3-b08b-4982-88ae-da61dce3c354',
        '4c574cf5-fb00-4285-9e72-eab699c90970', null, '2025-06-15 14:33:07.003373 +00:00',
        '2025-06-15 14:33:07.003376 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('a9e851e6-c7b8-4d70-a285-5d8584735f28', 'IDIOM', null, 'pick someone''s brain', 'wyciągnąć kogoś na rozmowę',
        'To ''pick someone''s brain'' means to seek advice or information from someone, often about their expertise or knowledge.',
        '[
          "Consulting a friend about their experience with a particular topic.",
          "Gathering insights from an expert in a specific field.",
          "Asking a colleague for their thoughts on a project."
        ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "Could I *pick your brain* for some ideas on this project?",
      "translation": "Czy mogę *wyciągnąć kogoś na rozmowę* o pomysłach na ten projekt?"
    },
    {
      "sentence": "She often *picks the brains* of her mentors for advice.",
      "translation": "Często *wyciąga kogoś na rozmowę* u swoich mentorów w poszukiwaniu rad."
    },
    {
      "sentence": "I need to *pick my coworker''s brain* about the new marketing strategy.",
      "translation": "Muszę *wyciągnąć kogoś na rozmowę* o nowej strategii marketingowej."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '2f3afc16-e1be-44d6-aab0-5b1363e22ec3',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', null, '2025-06-15 14:33:07.003933 +00:00',
        '2025-06-15 14:33:07.003937 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('1703194b-10c6-4b7e-a973-d9ec3b04b0ed', 'NOUN', null, 'redundancy', 'nadmiar',
        'Redundancy refers to the state of being not or no longer needed or useful; the presence of extra components that are not necessary.',
        '[
          "In engineering, redundancy may refer to extra components added to systems for reliability.",
          "In employment, redundancy refers to a situation where a job position is no longer required.",
          "In language and writing, redundancy can refer to unnecessary repetition of ideas or words."
        ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "The *redundancy* in the system helps prevent failures.",
      "translation": "Nadmiar w systemie pomaga zapobiegać awariom."
    },
    {
      "sentence": "Due to company restructuring, many employees faced *redundancy*.",
      "translation": "Z powodu restrukturyzacji firmy wielu pracowników stanęło w obliczu *nadmiaru*."
    },
    {
      "sentence": "His writing was criticized for its *redundancy* and lack of clarity.",
      "translation": "Jego pisanie było krytykowane za *nadmiar* i brak przejrzystości."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null, '2025-06-15 14:33:07.004795 +00:00',
        '2025-06-15 14:33:07.004799 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('427c18c0-c8c3-4245-ba82-a63eee6dd987', 'NOUN', null, 'upward trend', 'tendencja wzrostowa',
        'An upward trend refers to a general increase or improvement in a particular situation over a period of time.',
        '[
          "The data shows an upward trend in the population of the city.",
          "There has been an upward trend in sales over the last quarter.",
          "Experts predict an upward trend in job availability in the coming years."
        ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "We need to analyze the *upward trend* in customer satisfaction ratings.",
      "translation": "Musimy przeanalizować *tendencję wzrostową* w ocenach satysfakcji klientów."
    },
    {
      "sentence": "The *upward trend* in renewable energy use is encouraging.",
      "translation": " *Tendencja wzrostowa* w wykorzystaniu energii odnawialnej jest zachęcająca."
    },
    {
      "sentence": "The stock market has shown an *upward trend* despite recent economic challenges.",
      "translation": "Rynek akcji wykazał *tendencję wzrostową* pomimo ostatnich wyzwań gospodarczych."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'edb81261-1f1d-432a-9436-1d9303add361',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', null, '2025-06-15 14:33:07.005364 +00:00',
        '2025-06-15 14:33:07.005368 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('fdd5aa1a-79d9-490a-8aab-c028fc98e350', 'NOUN', null, 'tipping point', 'punkt zwrotny',
        'A ''tipping point'' is a critical moment or threshold that leads to a significant change or event.', '[
    "The decision was a tipping point in the company''s strategy, leading to increased profit.",
    "The team reached a tipping point in their project when they secured the necessary funding.",
    "Climate change may reach a tipping point if carbon emissions continue to rise."
  ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "He felt that the discussion had reached a tipping point and needed immediate action.",
      "translation": "Czuł, że dyskusja osiągnęła punkt zwrotny i wymagała natychmiastowego działania."
    },
    {
      "sentence": "The team''s dedication was the tipping point for their eventual success.",
      "translation": "Poświęcenie zespołu było punktem zwrotnym dla ich ostatecznego sukcesu."
    },
    {
      "sentence": "Many believe that education is a tipping point for social change.",
      "translation": "Wielu uważa, że edukacja jest punktem zwrotnym dla zmiany społecznej."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null, '2025-06-15 14:33:07.005682 +00:00',
        '2025-06-15 14:33:07.005686 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('06f55bd8-3153-45ce-ae43-bbe05c59f3b3', 'IDIOM', null, 'to be on the up', 'mieć się lepiej',
        'To be in a better state, improving or recovering.', '[
    "Referring to someone''s health improving.",
    "Describing a person''s happiness and overall well-being improving.",
    "Indicating that a situation or trend is becoming more positive."
  ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "After a few weeks of rest, he is finally *on the up*.",
      "translation": "Po kilku tygodniach odpoczynku, on w końcu *ma się lepiej*."
    },
    {
      "sentence": "The economy is *on the up*, showing signs of recovery.",
      "translation": "Gospodarka *ma się lepiej*, pokazując oznaki poprawy."
    },
    {
      "sentence": "She was feeling down, but now she''s *on the up* and excited about life.",
      "translation": "Czuła się przygnębiona, ale teraz *ma się lepiej* i jest podekscytowana życiem."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '929f555b-f372-48df-a9f7-7a4ec628d79e',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', null, '2025-06-15 14:33:07.006011 +00:00',
        '2025-06-15 14:33:07.006013 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('978a5f4e-666f-4523-8fe4-423b95e884e9', 'ADVERB', null, 'albeit', 'chociaż',
        'Albeit is used to introduce a contrasting statement or to acknowledge something while still recognizing a limitation or an exception.',
        '[
          "Often introduces a minor qualifier or limitation in an argument or statement.",
          "Used to indicate a contrast or concession."
        ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "She accepted the job offer, *albeit* with some hesitations.",
      "translation": "Ona przyjęła ofertę pracy, *chociaż* z pewnymi wątpliwościami."
    },
    {
      "sentence": "He is very talented, *albeit* somewhat inexperienced.",
      "translation": "On jest bardzo utalentowany, *chociaż* nieco niedoświadczony."
    },
    {
      "sentence": "The project was successful, *albeit* behind schedule.",
      "translation": "Projekt był udany, *chociaż* z opóźnieniem."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '929f555b-f372-48df-a9f7-7a4ec628d79e',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', null, '2025-06-15 14:33:07.006348 +00:00',
        '2025-06-15 14:33:07.006352 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('21106d94-d625-41ae-9e61-e3c27b42ee58', 'ADJECTIVE', null, 'cumbersome', 'niewygodny',
        'The word describes something that is large, heavy, or complicated, making it difficult to handle or manage.',
        '[
          "A cumbersome package that is hard to carry.",
          "Her cumbersome dress made it hard for her to move swiftly.",
          "The cumbersome process made it difficult to complete the task."
        ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "The *cumbersome* furniture filled the small apartment.",
      "translation": "To *niewygodne* meble zapełniły małe mieszkanie."
    },
    {
      "sentence": "The project was delayed due to the *cumbersome* requirements.",
      "translation": "Projekt został opóźniony z powodu *niewygodnych* wymagań."
    },
    {
      "sentence": "He found the *cumbersome* procedure frustrating.",
      "translation": "Znalazł tę *niewygodną* procedurę frustrującą."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null, '2025-06-15 14:33:07.006677 +00:00',
        '2025-06-15 14:33:07.006680 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('98c2e981-3904-48dc-b892-978716d95a57', 'VERB', 'LEGAL', 'infringe', 'naruszyć',
        'To infringe means to violate or encroach upon a law, agreement, or right.', '[
    "Policies that may infringe upon freedom of speech.",
    "Actions that infringe on someone''s personal rights.",
    "Infringe copyright laws by copying a protected work."
  ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "The new policy could *infringe* the workers'' rights.",
      "translation": "Nowa polityka może *naruszać* prawa pracowników."
    },
    {
      "sentence": "If you *infringe* on someone''s rights, you may face legal consequences.",
      "translation": "Jeśli *naruszasz* czyjeś prawa, możesz spotkać się z konsekwencjami prawnymi."
    },
    {
      "sentence": "They were accused of *infringing* on the patent.",
      "translation": "Oskarżono ich o *naruszenie* patentu."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '172bca8f-7386-4dbd-b3d1-60dcce573ec4',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', null, '2025-06-15 14:33:07.006970 +00:00',
        '2025-06-15 14:33:07.006972 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('dbeed3d7-ba5c-4a77-9512-9972336ebd8a', 'ADJECTIVE', null, 'permissible', 'dozwolony',
        'Permissible refers to something that is allowed or permitted according to rules or regulations.', '[
    "This action is permissible under the company''s policy.",
    "In most countries, this type of behavior is not permissible in public.",
    "It is permissible to use your phone during breaks at work."
  ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "*Permissible* actions must be clearly defined in the rules.",
      "translation": "*Dozwolone* działania muszą być jasno określone w zasadach."
    },
    {
      "sentence": "It is *permissible* to park here for up to two hours.",
      "translation": "Tutaj można *parkować* przez maksymalnie dwie godziny."
    },
    {
      "sentence": "Taking a short break is *permissible* during long meetings.",
      "translation": "Zrobienie krótkiej przerwy jest *dozwolone* podczas długich spotkań."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'caec18c4-3e3c-43c7-8c98-ebefcb0659b7',
        '4c574cf5-fb00-4285-9e72-eab699c90970', null, '2025-06-15 14:33:07.007340 +00:00',
        '2025-06-15 14:33:07.007343 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('1b7f72a2-ee9a-4258-9681-05ef20728de6', 'ADJECTIVE', 'LEGAL', 'admissible', 'dozwolony',
        'The word ''admissible'' refers to something that is allowed or permitted, especially in a legal context.', '[
    "The application was deemed admissible for consideration.",
    "Certain documents are admissible for verification purposes.",
    "Evidence that is admissible in court."
  ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "Only admissible applications will be processed further.",
      "translation": "Tylko *dozwolone* wnioski będą przetwarzane dalej."
    },
    {
      "sentence": "The judge declared that the evidence presented was *admissible*.",
      "translation": "Sędzia ogłosił, że przedstawione dowody były *dozwolone*."
    },
    {
      "sentence": "Her testimony was found to be *admissible* in the trial.",
      "translation": "Jej zeznanie uznano za *dozwolone* w procesie."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '929f555b-f372-48df-a9f7-7a4ec628d79e',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', null, '2025-06-15 14:33:07.007705 +00:00',
        '2025-06-15 14:33:07.007709 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('bd980035-cab9-4b51-9aa0-2c429ec3ca3c', 'NOUN', null, 'spur', 'bodziec',
        'A *spur* is something that encourages or motivates someone to take action or to do something.', '[
    "The funding will be a *spur* for the local economy.",
    "The unexpected news served as a *spur* to take decisive action.",
    "His success acted as a *spur* for her to strive for her own goals."
  ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "The unexpected promotion was a *spur* for him to work even harder.",
      "translation": "Nieoczekiwana awans była *bodźcem* dla niego, aby jeszcze bardziej się starać."
    },
    {
      "sentence": "She used the criticism as a *spur* to improve her performance.",
      "translation": "Użyła krytyki jako *bodźca* do poprawy swojego występu."
    },
    {
      "sentence": "The new policy will act as a *spur* for innovation within the company.",
      "translation": "Nowa polityka będzie działać jako *bodziec* do innowacji w firmie."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '929f555b-f372-48df-a9f7-7a4ec628d79e',
        '0389c54f-cccc-4f41-bc19-bf2af0191209', null, '2025-06-15 14:33:07.008104 +00:00',
        '2025-06-15 14:33:07.008108 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('69b5ddeb-e688-476e-9e90-e0b223be0c52', 'NOUN', null, 'brick-and-mortar', 'sklep stacjonarny',
        'A ''brick-and-mortar'' refers to a physical retail store that operates from a traditional building, as opposed to an online store.',
        '[
          "The local *brick-and-mortar* bookstore offers a unique selection of titles.",
          "The rise of online shopping has affected many *brick-and-mortar* stores.",
          "Some customers prefer shopping at *brick-and-mortar* locations for a tangible experience."
        ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "The convenience of online shopping has challenged *brick-and-mortar* stores.",
      "translation": "Wygoda zakupów online stanowi wyzwanie dla *sklepów stacjonarnych*."
    },
    {
      "sentence": "She enjoys visiting *brick-and-mortar* shops to support local businesses.",
      "translation": "Lubi odwiedzać *sklepy stacjonarne*, aby wspierać lokalne przedsiębiorstwa."
    },
    {
      "sentence": "Many *brick-and-mortar* businesses are adapting to digital sales.",
      "translation": "Wiele *sklepów stacjonarnych* dostosowuje się do sprzedaży internetowej."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '1ac13fa3-b08b-4982-88ae-da61dce3c354',
        '4c574cf5-fb00-4285-9e72-eab699c90970', null, '2025-06-15 14:33:07.008419 +00:00',
        '2025-06-15 14:33:07.008422 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('c2c51bc2-d3a1-4bfb-83f3-488dd85159f8', 'NOUN', null, 'certainty', 'pewność',
        'A state of being sure, confident, or certain about something.', '[
    "He expressed his *certainty* about the outcome of the election.",
    "There was a *certainty* in her voice that made everyone listen.",
    "Scientific evidence provides *certainty* about the safety of vaccines."
  ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "In moments of doubt, find your *certainty* to move forward.",
      "translation": "W chwilach wątpliwości znajdź swoją *pewność*, aby iść naprzód."
    },
    {
      "sentence": "She spoke with *certainty*, which impressed everyone around her.",
      "translation": "Mówiła z *pewnością*, co zrobiło wrażenie na wszystkich wokół niej."
    },
    {
      "sentence": "His *certainty* about the decision reassured the team.",
      "translation": "Jego *pewność* co do decyzji uspokoiła zespół."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null, '2025-06-15 14:33:07.008796 +00:00',
        '2025-06-15 14:33:07.008799 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('fe21fe74-b689-4dfb-a2be-1e4d0f85c828', 'ADJECTIVE', null, 'palpable', 'namacalny',
        'The word describes something that is so intense or obvious that it can be physically felt or perceived.', '[
    "Referring to a tangible feeling or atmosphere, such as tension in a room.",
    "Indicating something that can be immediately understood or recognized.",
    "Describing an obvious and strong emotion that can be sensed."
  ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "There was a *palpable* sense of excitement in the air before the concert.",
      "translation": "W powietrzu panowało *namacalne* poczucie ekscytacji przed koncertem."
    },
    {
      "sentence": "The tension in the room was *palpable* as the results were announced.",
      "translation": "Napięcie w pokoju było *namacalne*, gdy ogłoszono wyniki."
    },
    {
      "sentence": "Her disappointment was *palpable* when she heard the news.",
      "translation": "Jej rozczarowanie było *namacalne*, gdy usłyszała wiadomości."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', null, null, null, '2025-06-15 14:33:07.009269 +00:00',
        '2025-06-15 14:33:07.009276 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('34ad29df-1ab0-4450-9852-2239aeea4e9c', 'VERB', null, 'lull', 'uspokajać',
        'To lull means to calm someone or something, usually by soothing sounds or actions.', '[
    "To lull a baby to sleep with a soft melody.",
    "To lull someone''s fears with reassuring words.",
    "To lull an audience into a state of relaxation before a performance."
  ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "The speaker''s calm voice seemed to *lull* the nervous crowd.",
      "translation": "Spokojny głos mówcy wydawał się *uspokajać* nerwowy tłum."
    },
    {
      "sentence": "The mother tried to *lull* her child to sleep with a gentle song.",
      "translation": "Matka próbowała *uspokoić* swoje dziecko do snu delikatną piosenką."
    },
    {
      "sentence": "The warm breeze helped to *lull* him into a peaceful state.",
      "translation": "Ciepły wiatr pomógł mu *uspokoić* się w spokojny stan."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'bcefd281-0385-412c-9c83-31484e56dd8c',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', null, '2025-06-15 14:33:07.009703 +00:00',
        '2025-06-15 14:33:07.009707 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('f6151878-c7b8-4141-8885-cb12552dbfd7', 'VERB', null, 'ruminate', 'rozmyślać',
        'To ruminate means to think deeply about something or to consider various aspects before making a decision.', '[
    "Before making a major decision, it''s wise to ruminate on the possible outcomes.",
    "He likes to ruminate on philosophical questions.",
    "She needed time to ruminate over the proposal."
  ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "Sometimes, we need to *ruminate* on our choices before we act.",
      "translation": "Czasami musimy *rozmyślać* nad naszymi wyborami, zanim podejmiemy działanie."
    },
    {
      "sentence": "He often *ruminates* about his experiences in life.",
      "translation": "On często *rozmyśla* o swoich doświadczeniach w życiu."
    },
    {
      "sentence": "It''s important to *ruminate* before reaching a conclusion.",
      "translation": "Ważne jest, aby *rozmyślać* przed wyciągnięciem wniosku."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'bcefd281-0385-412c-9c83-31484e56dd8c',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', null, '2025-06-15 14:33:07.010030 +00:00',
        '2025-06-15 14:33:07.010033 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('3c1038c6-e343-4bbb-8e92-554e5fc02707', 'NOUN', 'MEDICAL', 'chamomile', 'rumianek',
        'Chamomile is a flowering plant known for its calming properties, often used to make herbal tea.', '[
    "Chamomile can be found in various skin care products for its anti-inflammatory properties.",
    "Many people use chamomile extract as a natural remedy for anxiety.",
    "Chamomile tea is popular for its soothing effects."
  ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "I drink *chamomile* tea before bed to help me relax.",
      "translation": "Piję herbatę z *rumianku* przed snem, żeby się zrelaksować."
    },
    {
      "sentence": "She applied a chamomile lotion on her skin to reduce irritation.",
      "translation": "Nałożyła na skórę lotion z *rumianku*, aby zmniejszyć podrażnienie."
    },
    {
      "sentence": "Chamomile is often recommended for its calming effects.",
      "translation": "*Rumianek* jest często polecany ze względu na swoje uspokajające działanie."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '8e3a727a-908a-4c06-8ff5-d508f6868f90',
        'a239f7a6-8ca9-4a59-945a-8e9b20926764', null, '2025-06-15 14:33:07.010332 +00:00',
        '2025-06-15 14:33:07.010334 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('87ed9d59-5da6-4b76-a0e8-21c314589ea3', 'NOUN', null, 'vocational school', 'szkoła zawodowa',
        'A vocational school is an educational institution that provides practical and skills-oriented training for specific trades or careers.',
        '[
          "Vocational schools often focus on technical skills and job readiness.",
          "Graduates of vocational schools may enter the workforce with valuable certifications.",
          "Students attend a vocational school to gain hands-on experience in a particular field."
        ]', 'ENGLISH', 'POLISH', false, false, 0, '[
    {
      "sentence": "Many students prefer *vocational school* because it leads directly to employment.",
      "translation": "Wielu uczniów wybiera *szkołę zawodową*, ponieważ prowadzi to bezpośrednio do zatrudnienia."
    },
    {
      "sentence": "He decided to enroll in a *vocational school* to become an electrician.",
      "translation": "Postanowił zapisać się do *szkoły zawodowej*, aby zostać elektrykiem."
    },
    {
      "sentence": "Vocational schools offer programs that are often shorter than traditional college degrees.",
      "translation": "Szkoły zawodowe oferują programy, które są często krótsze niż tradycyjne stopnie akademickie."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', '2f3afc16-e1be-44d6-aab0-5b1363e22ec3',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', null, '2025-06-15 14:33:07.010724 +00:00',
        '2025-06-15 14:33:07.010727 +00:00');
INSERT INTO public.words (id, type, extra_mark, origin, translation, definition, use_cases, translated_from,
                          translated_to, is_completed, is_bookmarked, points, example_sentences, user_id, bank_id,
                          bank_group_id, completed_at, created_at, updated_at)
VALUES ('0a2fb6cb-eba1-404e-8888-f810be0f2f2d', 'NOUN', null, 'shed', 'szopa',
        'A *shed* is a simple, often small building used for storage or to provide shelter for tools and equipment.', '[
    "A garden *shed* can be used to store gardening tools.",
    "She decided to convert the old *shed* into a workshop.",
    "The wood *shed* offers protection from the elements."
  ]', 'ENGLISH', 'POLISH', false, true, 0, '[
    {
      "sentence": "They built a *shed* to store their gardening supplies.",
      "translation": "Zbudowali *szopę*, aby przechować swoje narzędzia ogrodnicze."
    },
    {
      "sentence": "The old *shed* was falling apart, so they decided to rebuild it.",
      "translation": "Stara *szopa* się rozpadała, więc postanowili ją odbudować."
    },
    {
      "sentence": "He keeps his bike in the *shed* behind the house.",
      "translation": "Trzyma rower w *szopie* za domem."
    }
  ]', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'bcefd281-0385-412c-9c83-31484e56dd8c',
        '59ba22c3-0099-4f40-98f0-59c3df4b6d8a', null, '2025-06-15 14:33:07.011081 +00:00',
        '2025-06-15 14:33:07.011084 +00:00');

-------------
--- words tokens usage
-------------
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('d8455db2-94c5-4aa0-8d3b-c9143ed3ecc7', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'boardwalk', 'ENGLISH',
        'POLISH', 'GENERATE_ENTIRE_MANUAL', 355, 290, 0.1500000000, 0.6000000000, 0.0002272500,
        '2025-06-15 14:33:07.035511 +00:00', '2025-06-15 14:33:07.035515 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('51464ba2-bb20-4f4d-b586-b351b396244a', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'beehive', 'ENGLISH', 'POLISH',
        'GENERATE_ENTIRE_MANUAL', 356, 244, 0.1500000000, 0.6000000000, 0.0001998000,
        '2025-06-15 14:33:07.036292 +00:00', '2025-06-15 14:33:07.036296 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('113d8ae5-572a-4159-b5bc-4039d420cbd3', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'mutter', 'ENGLISH', 'POLISH',
        'GENERATE_ENTIRE_MANUAL', 355, 258, 0.1500000000, 0.6000000000, 0.0002080500,
        '2025-06-15 14:33:07.036648 +00:00', '2025-06-15 14:33:07.036652 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('2e876c7d-68c2-4aaf-a7d5-fe1cfb89c0e1', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'sluggish', 'ENGLISH', 'POLISH',
        'GENERATE_ENTIRE_MANUAL', 355, 286, 0.1500000000, 0.6000000000, 0.0002248500,
        '2025-06-15 14:33:07.037099 +00:00', '2025-06-15 14:33:07.037105 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('9915a478-4b06-434e-a120-cf9afb3e7673', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'lethargic', 'ENGLISH',
        'POLISH', 'GENERATE_ENTIRE_MANUAL', 357, 285, 0.1500000000, 0.6000000000, 0.0002245500,
        '2025-06-15 14:33:07.037765 +00:00', '2025-06-15 14:33:07.037769 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('e18fac43-548c-4471-be59-c83136767690', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'ephemeral', 'ENGLISH',
        'POLISH', 'GENERATE_ENTIRE_MANUAL', 355, 240, 0.1500000000, 0.6000000000, 0.0001972500,
        '2025-06-15 14:33:07.038099 +00:00', '2025-06-15 14:33:07.038101 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('14251277-c6ee-4c99-bd67-468f5ea4aa2d', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'transient', 'ENGLISH',
        'POLISH', 'GENERATE_ENTIRE_MANUAL', 355, 262, 0.1500000000, 0.6000000000, 0.0002104500,
        '2025-06-15 14:33:07.038359 +00:00', '2025-06-15 14:33:07.038361 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('dfe3ae7f-6382-41dd-b3ce-32fb95011965', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'fleeting', 'ENGLISH', 'POLISH',
        'GENERATE_ENTIRE_MANUAL', 355, 209, 0.1500000000, 0.6000000000, 0.0001786500,
        '2025-06-15 14:33:07.038619 +00:00', '2025-06-15 14:33:07.038621 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('f8f71f7c-3bab-4019-b26e-12feaa50a2c3', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'depletes', 'ENGLISH', 'POLISH',
        'GENERATE_ENTIRE_MANUAL', 356, 271, 0.1500000000, 0.6000000000, 0.0002160000,
        '2025-06-15 14:33:07.038893 +00:00', '2025-06-15 14:33:07.038896 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('9d21338b-be3b-45b7-98c8-a26bac35da41', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        'don''t shoot them down in flames', 'ENGLISH', 'POLISH', 'GENERATE_ENTIRE_MANUAL', 359, 270, 0.1500000000,
        0.6000000000, 0.0002158500, '2025-06-15 14:33:07.039197 +00:00', '2025-06-15 14:33:07.039200 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('2fe1d2f9-7150-455f-a591-14fe22487d6f', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'fruit of thought', 'ENGLISH',
        'POLISH', 'GENERATE_ENTIRE_MANUAL', 356, 252, 0.1500000000, 0.6000000000, 0.0002046000,
        '2025-06-15 14:33:07.039482 +00:00', '2025-06-15 14:33:07.039484 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('e2e42cfc-07a0-49a4-a5b8-573f17206dfd', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'down the rabbit hole',
        'ENGLISH', 'POLISH', 'GENERATE_ENTIRE_MANUAL', 357, 281, 0.1500000000, 0.6000000000, 0.0002221500,
        '2025-06-15 14:33:07.039763 +00:00', '2025-06-15 14:33:07.039766 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('25070dee-6179-40e4-bc38-5bd8a3716660', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'uheard', 'ENGLISH', 'POLISH',
        'GENERATE_ENTIRE_MANUAL', 355, 233, 0.1500000000, 0.6000000000, 0.0001930500,
        '2025-06-15 14:33:07.040091 +00:00', '2025-06-15 14:33:07.040094 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('0bb4c11c-5cc8-4671-a324-8d0c11d9e56d', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'well-to-do', 'ENGLISH',
        'POLISH', 'GENERATE_ENTIRE_MANUAL', 356, 230, 0.1500000000, 0.6000000000, 0.0001914000,
        '2025-06-15 14:33:07.040379 +00:00', '2025-06-15 14:33:07.040381 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('1ef901f1-c04c-46c8-942c-418fcb18edd2', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'across-the-board', 'ENGLISH',
        'POLISH', 'GENERATE_ENTIRE_MANUAL', 357, 247, 0.1500000000, 0.6000000000, 0.0002017500,
        '2025-06-15 14:33:07.040642 +00:00', '2025-06-15 14:33:07.040645 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('15ef4abe-8192-4ffd-a840-be025dc09e22', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'run-of-the-mill', 'ENGLISH',
        'POLISH', 'GENERATE_ENTIRE_MANUAL', 358, 281, 0.1500000000, 0.6000000000, 0.0002223000,
        '2025-06-15 14:33:07.041164 +00:00', '2025-06-15 14:33:07.041172 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('c489d317-d843-4a82-94b9-351ddbdb2c3a', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'state-of-the-art', 'ENGLISH',
        'POLISH', 'GENERATE_ENTIRE_MANUAL', 357, 270, 0.1500000000, 0.6000000000, 0.0002155500,
        '2025-06-15 14:33:07.041598 +00:00', '2025-06-15 14:33:07.041604 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('ae73f712-e43f-4527-9c5e-f2ef838007be', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'off-the-cuff', 'ENGLISH',
        'POLISH', 'GENERATE_ENTIRE_MANUAL', 357, 252, 0.1500000000, 0.6000000000, 0.0002047500,
        '2025-06-15 14:33:07.041883 +00:00', '2025-06-15 14:33:07.041885 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('2787e384-7764-48c3-a428-8bb4d3cb3dda', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'larger-than-life', 'ENGLISH',
        'POLISH', 'GENERATE_ENTIRE_MANUAL', 357, 249, 0.1500000000, 0.6000000000, 0.0002029500,
        '2025-06-15 14:33:07.042164 +00:00', '2025-06-15 14:33:07.042167 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('557d14b6-3924-4ab3-a21e-98dd020d6ac7', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'middle-of-the-road', 'ENGLISH',
        'POLISH', 'GENERATE_ENTIRE_MANUAL', 357, 283, 0.1500000000, 0.6000000000, 0.0002233500,
        '2025-06-15 14:33:07.042433 +00:00', '2025-06-15 14:33:07.042435 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('4b7b8c04-a365-42ba-9ae1-8b02558692a7', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'though-out', 'ENGLISH',
        'POLISH', 'GENERATE_ENTIRE_MANUAL', 355, 218, 0.1500000000, 0.6000000000, 0.0001840500,
        '2025-06-15 14:33:07.042711 +00:00', '2025-06-15 14:33:07.042713 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('da836977-9d55-4f52-b910-f1c0012ac3f1', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'paid-for', 'ENGLISH', 'POLISH',
        'GENERATE_ENTIRE_MANUAL', 355, 263, 0.1500000000, 0.6000000000, 0.0002110500,
        '2025-06-15 14:33:07.042979 +00:00', '2025-06-15 14:33:07.042982 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('3e3e2096-e7f7-4541-a4c8-95483bd22893', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'take-it-or-leave-it',
        'ENGLISH', 'POLISH', 'GENERATE_ENTIRE_MANUAL', 359, 306, 0.1500000000, 0.6000000000, 0.0002374500,
        '2025-06-15 14:33:07.043253 +00:00', '2025-06-15 14:33:07.043256 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('723779c4-ddad-4c2d-ac01-304eca2b4d40', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'business-as-usual', 'ENGLISH',
        'POLISH', 'GENERATE_ENTIRE_MANUAL', 357, 286, 0.1500000000, 0.6000000000, 0.0002251500,
        '2025-06-15 14:33:07.043516 +00:00', '2025-06-15 14:33:07.043518 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('85f4801d-dbe6-43ec-bfb6-721602e7386c', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'all-you-can-eat', 'ENGLISH',
        'POLISH', 'GENERATE_ENTIRE_MANUAL', 358, 268, 0.1500000000, 0.6000000000, 0.0002145000,
        '2025-06-15 14:33:07.043770 +00:00', '2025-06-15 14:33:07.043773 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('4dbef6dc-0276-496f-a0bb-417b192fe76b', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'mearge', 'ENGLISH', 'POLISH',
        'GENERATE_ENTIRE_MANUAL', 356, 248, 0.1500000000, 0.6000000000, 0.0002022000,
        '2025-06-15 14:33:07.044044 +00:00', '2025-06-15 14:33:07.044046 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('b4d91e1e-1fe5-4ce5-9028-e460cb3a2d42', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'firm', 'ENGLISH', 'POLISH',
        'GENERATE_ENTIRE_MANUAL', 354, 226, 0.1500000000, 0.6000000000, 0.0001887000,
        '2025-06-15 14:33:07.044304 +00:00', '2025-06-15 14:33:07.044307 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('dbbda002-d3ee-494a-a21e-13a590ce9bb0', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'doting', 'ENGLISH', 'POLISH',
        'GENERATE_ENTIRE_MANUAL', 355, 301, 0.1500000000, 0.6000000000, 0.0002338500,
        '2025-06-15 14:33:07.044566 +00:00', '2025-06-15 14:33:07.044568 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('6253d030-2b91-4f05-82a0-f5685e2dcb57', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'bluntly', 'ENGLISH', 'POLISH',
        'GENERATE_ENTIRE_MANUAL', 356, 228, 0.1500000000, 0.6000000000, 0.0001902000,
        '2025-06-15 14:33:07.044834 +00:00', '2025-06-15 14:33:07.044837 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('be6dfbcf-8035-43f6-9a5c-c8e46b869b2a', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'to add insult to injury',
        'ENGLISH', 'POLISH', 'GENERATE_ENTIRE_MANUAL', 358, 323, 0.1500000000, 0.6000000000, 0.0002475000,
        '2025-06-15 14:33:07.045091 +00:00', '2025-06-15 14:33:07.045094 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('e85ace6c-b809-4a3a-be86-c6f5ffda9448', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'to cap it all', 'ENGLISH',
        'POLISH', 'GENERATE_ENTIRE_MANUAL', 357, 276, 0.1500000000, 0.6000000000, 0.0002191500,
        '2025-06-15 14:33:07.045355 +00:00', '2025-06-15 14:33:07.045357 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('0f11542e-122f-419b-81b3-4887efd23d71', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'flusterted', 'ENGLISH',
        'POLISH', 'GENERATE_ENTIRE_MANUAL', 357, 253, 0.1500000000, 0.6000000000, 0.0002053500,
        '2025-06-15 14:33:07.045627 +00:00', '2025-06-15 14:33:07.045630 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('c152d356-abb8-4ab5-839d-a8dfdaf28e28', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'blunt', 'ENGLISH', 'POLISH',
        'GENERATE_ENTIRE_MANUAL', 355, 258, 0.1500000000, 0.6000000000, 0.0002080500,
        '2025-06-15 14:33:07.045882 +00:00', '2025-06-15 14:33:07.045884 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('96332a41-303f-44f6-be2b-9ae2100e3c0c', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'out of the blue', 'ENGLISH',
        'POLISH', 'GENERATE_ENTIRE_MANUAL', 357, 243, 0.1500000000, 0.6000000000, 0.0001993500,
        '2025-06-15 14:33:07.046140 +00:00', '2025-06-15 14:33:07.046142 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('388d684d-8731-48a0-8ddc-787344f89a8a', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'mingle', 'ENGLISH', 'POLISH',
        'GENERATE_ENTIRE_MANUAL', 355, 262, 0.1500000000, 0.6000000000, 0.0002104500,
        '2025-06-15 14:33:07.046401 +00:00', '2025-06-15 14:33:07.046402 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('c5cf71c5-6443-4755-9106-7a3c92e63c29', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'hover', 'ENGLISH', 'POLISH',
        'GENERATE_ENTIRE_MANUAL', 354, 241, 0.1500000000, 0.6000000000, 0.0001977000,
        '2025-06-15 14:33:07.046654 +00:00', '2025-06-15 14:33:07.046656 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('c80e031a-9abb-402b-b38f-0c39084bfe2f', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'pleasantry', 'ENGLISH',
        'POLISH', 'GENERATE_ENTIRE_MANUAL', 356, 237, 0.1500000000, 0.6000000000, 0.0001956000,
        '2025-06-15 14:33:07.046898 +00:00', '2025-06-15 14:33:07.046901 +00:00');
INSERT INTO public.word_tokens_usages (id, user_id, word, language, translated_to, consumption_type, input_tokens,
                                       output_tokens, price_for_mln_input_tokens, price_for_mln_output_tokens, cost,
                                       created_at, updated_at)
VALUES ('a654e516-3b8c-4ba8-bc29-579eef76dab3', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4', 'pick someones''s brain',
        'ENGLISH', 'POLISH', 'GENERATE_ENTIRE_MANUAL', 358, 255, 0.1500000000, 0.6000000000, 0.0002067000,
        '2025-06-15 14:33:07.047136 +00:00', '2025-06-15 14:33:07.047138 +00:00');
