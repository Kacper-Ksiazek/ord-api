-------------
-- word_details for root user's words
-------------

-- 1. boardwalk
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '6ab0a01f-97eb-4114-8ba2-8f4a657e94cd',
    '["noun", "architecture", "recreation"]'::jsonb,
    '["promenade", "walkway", "pier"]'::jsonb,
    '[]'::jsonb,
    '["Don''t confuse with sidewalk (chodnik) - boardwalk is specifically near water"]'::jsonb,
    '[{"sentence": "We walked along the boardwalk at sunset.", "translation": "Przechadzaliśmy się bulwarem o zachodzie słońca."}, {"sentence": "The Atlantic City boardwalk is famous for its casinos.", "translation": "Bulwar Atlantic City słynie z kasyn."}]'::jsonb,
    '[{"phrase": "stroll along the boardwalk", "translation": "przechadzać się bulwarem", "frequency": "VERY_COMMON"}, {"phrase": "boardwalk vendor", "translation": "sprzedawca na bulwarze", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ˈbɔːrdwɔːk", "syllables": "board-walk", "stress": 1}'::jsonb,
    null,
    'Boardwalks are iconic in American beach culture, especially on the East Coast. Atlantic City and Coney Island have famous boardwalks with shops and amusement parks.',
    'Think of "board" (deska) + "walk" (chodzić) - a wooden walkway',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 2. beehive
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'e1e401da-0224-4356-bcdc-c89e5237f3c8',
    '["noun", "nature", "apiculture"]'::jsonb,
    '["hive", "apiary"]'::jsonb,
    '[]'::jsonb,
    '["Beehive is the structure itself, while hive can refer to the colony or structure"]'::jsonb,
    '[{"sentence": "The beekeeper checked the beehive for honey.", "translation": "Pszczelarz sprawdził ul w poszukiwaniu miodu."}, {"sentence": "A healthy beehive can house thousands of bees.", "translation": "Zdrowy ul może pomieścić tysiące pszczół."}]'::jsonb,
    '[{"phrase": "active beehive", "translation": "aktywny ul", "frequency": "COMMON"}, {"phrase": "beehive structure", "translation": "struktura ula", "frequency": "OCCASIONAL"}]'::jsonb,
    '{"ipa": "ˈbiːhaɪv", "syllables": "bee-hive", "stress": 1}'::jsonb,
    '{"pluralForm": "beehives"}'::jsonb,
    'Beehive can also refer to a 1960s women''s hairstyle that resembles the shape of a beehive.',
    'Compound word: bee + hive (gniazdo)',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 3. mutter
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '907af8cc-c68e-4c11-b506-bbfa7545bc61',
    '["verb", "communication", "complaint"]'::jsonb,
    '["mumble", "murmur", "grumble"]'::jsonb,
    '["shout", "yell", "speak clearly"]'::jsonb,
    '["Mutter is usually negative or complaining, mumble is just unclear"]'::jsonb,
    '[{"sentence": "He muttered something under his breath.", "translation": "Mruknął coś pod nosem."}, {"sentence": "She muttered complaints about the weather.", "translation": "Mamrotała narzekania na pogodę."}]'::jsonb,
    '[{"phrase": "mutter under breath", "translation": "mruczeć pod nosem", "frequency": "VERY_COMMON"}, {"phrase": "mutter darkly", "translation": "mrucze ponuro", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ˈmʌtər", "syllables": "mut-ter", "stress": 1}'::jsonb,
    '{"irregularForms": {"pastTense": "muttered", "presentParticiple": "muttering"}}'::jsonb,
    'Often implies dissatisfaction or complaint, typically spoken quietly so others can''t hear clearly.',
    'Similar sound to Polish "mamrotać"',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 4. sluggish
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '43166a14-fc87-480c-8f4b-4661d3c7aa2a',
    '["adjective", "movement", "performance"]'::jsonb,
    '["slow", "lethargic", "torpid"]'::jsonb,
    '["energetic", "brisk", "lively"]'::jsonb,
    '["Can describe both physical movement and economic/system performance"]'::jsonb,
    '[{"sentence": "I feel sluggish after eating a heavy meal.", "translation": "Czuję się ospały po jedzeniu ciężkiego posiłku."}, {"sentence": "The economy has been sluggish this quarter.", "translation": "Gospodarka była ospała w tym kwartale."}]'::jsonb,
    '[{"phrase": "sluggish economy", "translation": "ospała gospodarka", "frequency": "VERY_COMMON"}, {"phrase": "feel sluggish", "translation": "czuć się ospałym", "frequency": "VERY_COMMON"}]'::jsonb,
    '{"ipa": "ˈslʌɡɪʃ", "syllables": "slug-gish", "stress": 1}'::jsonb,
    null,
    'Derived from "slug" (ślimak) - slow-moving creature. Common in business and economic contexts.',
    'Think of a slug - slow and lacking energy',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 5. lethargic
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'a0992a09-7586-48dc-8b85-d7fa6365565a',
    '["adjective", "health", "mood"]'::jsonb,
    '["sluggish", "listless", "apathetic"]'::jsonb,
    '["energetic", "lively", "vigorous"]'::jsonb,
    '["More formal and medical than sluggish, often implies illness or depression"]'::jsonb,
    '[{"sentence": "The patient appeared lethargic and weak.", "translation": "Pacjent wydawał się letargiczny i słaby."}, {"sentence": "Hot weather makes me feel lethargic.", "translation": "Gorąca pogoda sprawia, że czuję się letargiczny."}]'::jsonb,
    '[{"phrase": "feel lethargic", "translation": "czuć się letargicznie", "frequency": "VERY_COMMON"}, {"phrase": "lethargic state", "translation": "stan letargu", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ləˈθɑːrdʒɪk", "syllables": "le-thar-gic", "stress": 2}'::jsonb,
    null,
    'From Greek "lethargos" meaning forgetful. Often used in medical contexts to describe lack of energy.',
    'Related to "lethargy" (letarg) in Polish',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 6. ephemeral
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '93e418c8-b601-4117-a0d8-83c08d59a0ca',
    '["adjective", "time", "philosophy"]'::jsonb,
    '["transient", "fleeting", "temporary"]'::jsonb,
    '["permanent", "eternal", "lasting"]'::jsonb,
    '["Formal and poetic word, often used in literary or philosophical contexts"]'::jsonb,
    '[{"sentence": "Youth is ephemeral; enjoy it while it lasts.", "translation": "Młodość jest ulotna; ciesz się nią, póki trwa."}, {"sentence": "Instagram stories are ephemeral by design.", "translation": "Instagram stories są z założenia efemeryczne."}]'::jsonb,
    '[{"phrase": "ephemeral nature", "translation": "ulotna natura", "frequency": "COMMON"}, {"phrase": "ephemeral beauty", "translation": "ulotne piękno", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ɪˈfemərəl", "syllables": "e-phem-er-al", "stress": 2}'::jsonb,
    null,
    'From Greek "ephemeros" meaning lasting only a day. Used in technology for temporary data.',
    'Related to ephemera - things that exist briefly',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 7. transient
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '2fb8b299-0199-4e9d-a720-5b44fba977a7',
    '["adjective", "temporary", "passing"]'::jsonb,
    '["ephemeral", "temporary", "fleeting"]'::jsonb,
    '["permanent", "lasting", "enduring"]'::jsonb,
    '["Can be used as a noun to mean a homeless person or temporary resident"]'::jsonb,
    '[{"sentence": "The pain was transient and soon passed.", "translation": "Ból był przemijający i wkrótce minął."}, {"sentence": "Transient workers move from job to job.", "translation": "Pracownicy sezonowi przemieszczają się z pracy do pracy."}]'::jsonb,
    '[{"phrase": "transient period", "translation": "przejściowy okres", "frequency": "COMMON"}, {"phrase": "transient population", "translation": "przejściowa populacja", "frequency": "OCCASIONAL"}]'::jsonb,
    '{"ipa": "ˈtrænziənt", "syllables": "tran-si-ent", "stress": 1}'::jsonb,
    null,
    'In technical contexts, refers to temporary electrical signals or states.',
    'Think "transit" - passing through',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 8. fleeting
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '3acf17a4-770f-4d99-928b-4cb113f94355',
    '["adjective", "time", "moments"]'::jsonb,
    '["brief", "momentary", "ephemeral"]'::jsonb,
    '["lasting", "enduring", "permanent"]'::jsonb,
    '["Emphasizes speed of passing, more poetic than transient"]'::jsonb,
    '[{"sentence": "We shared a fleeting glance.", "translation": "Wymieniliśmy przelotne spojrzenie."}, {"sentence": "Happiness can be fleeting.", "translation": "Szczęście może być ulotne."}]'::jsonb,
    '[{"phrase": "fleeting moment", "translation": "ulotna chwila", "frequency": "VERY_COMMON"}, {"phrase": "fleeting glimpse", "translation": "przelotny rzut oka", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ˈfliːtɪŋ", "syllables": "fleet-ing", "stress": 1}'::jsonb,
    null,
    'From "fleet" meaning fast. Often used in romantic or nostalgic contexts.',
    'Think of something fleeing (uciekający) - passing quickly',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 9. depletes
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'd92400eb-077d-4d7f-a095-9b850c44106f',
    '["verb", "resources", "reduction"]'::jsonb,
    '["exhausts", "drains", "reduces"]'::jsonb,
    '["replenishes", "restores", "fills"]'::jsonb,
    '["Deplete is transitive - it needs an object (depletes something)"]'::jsonb,
    '[{"sentence": "Mining depletes natural resources.", "translation": "Wydobycie wyczerpuje zasoby naturalne."}, {"sentence": "Exercise depletes your energy reserves.", "translation": "Ćwiczenia wyczerpują twoje rezerwy energii."}]'::jsonb,
    '[{"phrase": "deplete resources", "translation": "wyczerpać zasoby", "frequency": "VERY_COMMON"}, {"phrase": "deplete stocks", "translation": "wyczerpać zapasy", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "dɪˈpliːts", "syllables": "de-pletes", "stress": 2}'::jsonb,
    '{"irregularForms": {"baseForm": "deplete", "thirdPerson": "depletes", "pastTense": "depleted"}}'::jsonb,
    'Common in environmental and economic discussions about resource management.',
    'De- (zmniejszenie) + plete (from Latin plenus - full)',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 10. don
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '1ed73d97-9755-4ef6-bfc5-39de627706b6',
    '["verb", "formal", "clothing"]'::jsonb,
    '["put on", "wear"]'::jsonb,
    '["doff", "remove", "take off"]'::jsonb,
    '["Very formal and literary, rarely used in everyday speech. Opposite is doff."]'::jsonb,
    '[{"sentence": "The knight donned his armor.", "translation": "Rycerz założył zbroję."}, {"sentence": "She donned her best dress for the occasion.", "translation": "Założyła swoją najlepszą sukienkę na tę okazję."}]'::jsonb,
    '[{"phrase": "don a costume", "translation": "przywdziać kostium", "frequency": "OCCASIONAL"}, {"phrase": "don protective gear", "translation": "założyć sprzęt ochronny", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "dɒn", "syllables": "don", "stress": 1}'::jsonb,
    '{"irregularForms": {"pastTense": "donned", "presentParticiple": "donning"}}'::jsonb,
    'From "do on" (put on). Often seen in fantasy literature and historical contexts.',
    'Archaic opposite of "doff" (do off = take off)',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 11. fruit of thought
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'd32becba-9815-47e6-b18a-0a5b2c9b7909',
    '["idiom", "result", "intellectual"]'::jsonb,
    '["result of thinking", "intellectual product", "outcome"]'::jsonb,
    '[]'::jsonb,
    '["More common: fruit of labor, food for thought. Fruit of thought is less common."]'::jsonb,
    '[{"sentence": "This theory is the fruit of years of thought.", "translation": "Ta teoria jest owocem lat przemyśleń."}, {"sentence": "His invention was the fruit of deep thought.", "translation": "Jego wynalazek był owocem głębokich przemyśleń."}]'::jsonb,
    '[{"phrase": "the fruit of thought", "translation": "owoc myślenia", "frequency": "OCCASIONAL"}]'::jsonb,
    null,
    null,
    'Metaphorical expression comparing intellectual results to harvesting fruit.',
    'More common: "fruit of labor" or "food for thought"',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 12. down the rabbit hole
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '17d9c6e7-adfd-4a64-a1e2-eff9c9adebed',
    '["idiom", "internet", "investigation"]'::jsonb,
    '["into the unknown", "deep dive", "going deeper"]'::jsonb,
    '[]'::jsonb,
    '["Usually implies losing track of time or getting lost in details"]'::jsonb,
    '[{"sentence": "I went down the rabbit hole researching quantum physics.", "translation": "Zszedłem w króliczą norę badając fizykę kwantową."}, {"sentence": "YouTube algorithms can send you down the rabbit hole.", "translation": "Algorytmy YouTube mogą wysłać cię w króliczą norę."}]'::jsonb,
    '[{"phrase": "go down the rabbit hole", "translation": "wpaść w króliczą norę", "frequency": "VERY_COMMON"}, {"phrase": "fall down the rabbit hole", "translation": "spaść w króliczą norę", "frequency": "VERY_COMMON"}]'::jsonb,
    null,
    null,
    'From Alice in Wonderland. Very popular in internet culture to describe getting lost in browsing or research.',
    'Reference to Alice in Wonderland - entering strange, complex situations',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 13. well-to-do
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'f3b391f5-91d0-4a5f-b8da-db906c812f3d',
    '["adjective", "wealth", "social class"]'::jsonb,
    '["wealthy", "affluent", "prosperous"]'::jsonb,
    '["poor", "impoverished", "needy"]'::jsonb,
    '["Less extreme than rich or wealthy, implies comfortable living"]'::jsonb,
    '[{"sentence": "They come from a well-to-do family.", "translation": "Pochodzą z zamożnej rodziny."}, {"sentence": "The well-to-do neighborhood has low crime rates.", "translation": "Zamożna dzielnica ma niskie wskaźniki przestępczości."}]'::jsonb,
    '[{"phrase": "well-to-do family", "translation": "zamożna rodzina", "frequency": "VERY_COMMON"}, {"phrase": "well-to-do neighborhood", "translation": "zamożna dzielnica", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ˌwel tə ˈduː", "syllables": "well-to-do", "stress": 3}'::jsonb,
    null,
    'Polite, euphemistic way to say someone is rich without sounding too direct.',
    'Well = good + to do = to manage financially',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 14. across-the-board
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '4cea91f7-7ed7-4722-8044-280573fb8ade',
    '["adjective", "universal", "business"]'::jsonb,
    '["universal", "comprehensive", "blanket"]'::jsonb,
    '["selective", "specific", "targeted"]'::jsonb,
    '["Can be adjective or adverb. Always hyphenated."]'::jsonb,
    '[{"sentence": "The company announced across-the-board pay raises.", "translation": "Firma ogłosiła powszechne podwyżki wynagrodzeń."}, {"sentence": "Budget cuts were made across the board.", "translation": "Cięcia budżetowe zostały dokonane we wszystkich obszarach."}]'::jsonb,
    '[{"phrase": "across-the-board increase", "translation": "powszechny wzrost", "frequency": "VERY_COMMON"}, {"phrase": "across-the-board cuts", "translation": "powszechne cięcia", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "əˌkrɔs ðə ˈbɔrd", "syllables": "a-cross-the-board", "stress": 4}'::jsonb,
    null,
    'From horse racing - betting on a horse to win, place, or show (covering all possibilities).',
    'Affects everyone/everything without exception',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 15. run-of-the-mill
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'b1898c39-791a-48f3-ab3a-3ef7e03d8fad',
    '["adjective", "ordinary", "quality"]'::jsonb,
    '["ordinary", "average", "commonplace"]'::jsonb,
    '["exceptional", "extraordinary", "unique"]'::jsonb,
    '["Slightly negative connotation - implies lack of special qualities"]'::jsonb,
    '[{"sentence": "It was just a run-of-the-mill action movie.", "translation": "To był zwykły film akcji."}, {"sentence": "He''s not a genius, just a run-of-the-mill programmer.", "translation": "Nie jest geniuszem, tylko zwykłym programistą."}]'::jsonb,
    '[{"phrase": "run-of-the-mill product", "translation": "zwykły produkt", "frequency": "COMMON"}, {"phrase": "run-of-the-mill performance", "translation": "przeciętne wykonanie", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ˌrʌn əv ðə ˈmɪl", "syllables": "run-of-the-mill", "stress": 4}'::jsonb,
    null,
    'From manufacturing - products that come directly off the assembly line without special features.',
    'Mill = factory, so "from the regular production line"',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 16. state-of-the-art
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '05afa286-5935-4cd4-a9a1-a51038149eb3',
    '["adjective", "technology", "modern"]'::jsonb,
    '["cutting-edge", "advanced", "latest"]'::jsonb,
    '["outdated", "obsolete", "antiquated"]'::jsonb,
    '["Often used for technology but can apply to any field"]'::jsonb,
    '[{"sentence": "The hospital has state-of-the-art equipment.", "translation": "Szpital ma najnowocześniejszy sprzęt."}, {"sentence": "This is a state-of-the-art research facility.", "translation": "To jest najnowocześniejszy ośrodek badawczy."}]'::jsonb,
    '[{"phrase": "state-of-the-art technology", "translation": "najnowocześniejsza technologia", "frequency": "VERY_COMMON"}, {"phrase": "state-of-the-art facility", "translation": "najnowocześniejsza placówka", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ˌsteɪt əv ði ˈɑrt", "syllables": "state-of-the-art", "stress": 5}'::jsonb,
    null,
    'Represents the highest level of development at a particular time.',
    'Current state of the art (skill/craft) = best available now',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 17. off-the-cuff
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '82f7cb46-3e87-433a-a4d5-c5fa2eaf7eff',
    '["adjective", "adverb", "spontaneous"]'::jsonb,
    '["impromptu", "spontaneous", "unrehearsed"]'::jsonb,
    '["prepared", "rehearsed", "planned"]'::jsonb,
    '["Can be adjective (off-the-cuff remark) or adverb (spoke off the cuff)"]'::jsonb,
    '[{"sentence": "He made an off-the-cuff comment that went viral.", "translation": "Zrobił spontaniczną uwagę, która stała się viralem."}, {"sentence": "I can''t give you exact numbers off the cuff.", "translation": "Nie mogę podać ci dokładnych liczb z głowy."}]'::jsonb,
    '[{"phrase": "off-the-cuff remark", "translation": "spontaniczna uwaga", "frequency": "VERY_COMMON"}, {"phrase": "speak off the cuff", "translation": "mówić bez przygotowania", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ˌɔf ðə ˈkʌf", "syllables": "off-the-cuff", "stress": 3}'::jsonb,
    null,
    'From the practice of speakers writing notes on their shirt cuffs for quick reference.',
    'Speaking without notes on your cuff = unprepared',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 18. larger-than-life
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'a9be3be1-4d41-43a1-b8ee-be4d0cb41417',
    '["adjective", "personality", "impressive"]'::jsonb,
    '["extraordinary", "impressive", "remarkable"]'::jsonb,
    '["ordinary", "unremarkable", "modest"]'::jsonb,
    '["Usually positive, describing charismatic or impressive people"]'::jsonb,
    '[{"sentence": "He has a larger-than-life personality.", "translation": "Ma osobowość większą niż życie."}, {"sentence": "The actor''s larger-than-life character captivated audiences.", "translation": "Postać aktora większa niż życie urzekła publiczność."}]'::jsonb,
    '[{"phrase": "larger-than-life personality", "translation": "osobowość większa niż życie", "frequency": "VERY_COMMON"}, {"phrase": "larger-than-life character", "translation": "postać większa niż życie", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ˌlɑrdʒər ðæn ˈlaɪf", "syllables": "larg-er-than-life", "stress": 4}'::jsonb,
    null,
    'Often used for celebrities, historical figures, or fictional characters with exceptional qualities.',
    'So impressive/extraordinary they seem too big to be real',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 19. middle-of-the-road
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'd4673027-c4f5-4ace-8caa-64ca1ea560bb',
    '["adjective", "moderate", "politics"]'::jsonb,
    '["moderate", "centrist", "mainstream"]'::jsonb,
    '["extreme", "radical", "unconventional"]'::jsonb,
    '["Can be positive (balanced) or negative (boring, unambitious)"]'::jsonb,
    '[{"sentence": "He takes a middle-of-the-road approach to politics.", "translation": "Przyjmuje umiarkowane podejście do polityki."}, {"sentence": "The movie was middle-of-the-road entertainment.", "translation": "Film był przeciętną rozrywką."}]'::jsonb,
    '[{"phrase": "middle-of-the-road politics", "translation": "umiarkowana polityka", "frequency": "COMMON"}, {"phrase": "middle-of-the-road approach", "translation": "umiarkowane podejście", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ˌmɪdl̩ əv ðə ˈroʊd", "syllables": "mid-dle-of-the-road", "stress": 5}'::jsonb,
    null,
    'Common in political discourse. Can imply safety but also lack of bold vision.',
    'In the middle of the road = not extreme either way',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 20. thought-out
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'a331d7b8-9da4-46fd-a3ed-47fb3e62927d',
    '["adjective", "planning", "consideration"]'::jsonb,
    '["planned", "considered", "deliberate"]'::jsonb,
    '["hasty", "impulsive", "rash"]'::jsonb,
    '["Usually preceded by well- or poorly-. Rarely used alone."]'::jsonb,
    '[{"sentence": "This is a well-thought-out plan.", "translation": "To jest dobrze przemyślany plan."}, {"sentence": "The proposal was poorly thought-out.", "translation": "Propozycja była słabo przemyślana."}]'::jsonb,
    '[{"phrase": "well-thought-out plan", "translation": "dobrze przemyślany plan", "frequency": "VERY_COMMON"}, {"phrase": "carefully thought-out", "translation": "starannie przemyślany", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ˈθɔt aʊt", "syllables": "thought-out", "stress": 1}'::jsonb,
    null,
    'From the phrasal verb "think out" meaning to plan carefully.',
    'Past participle of "think out" = carefully planned',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 21. paid-for
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '530b52d4-006e-4af5-ad2d-0cb656a4bbfc',
    '["adjective", "payment", "ownership"]'::jsonb,
    '["purchased", "bought", "acquired"]'::jsonb,
    '["free", "unpaid", "complimentary"]'::jsonb,
    '["Past participle of pay for, emphasizes completed payment"]'::jsonb,
    '[{"sentence": "These are paid-for subscriptions.", "translation": "To są opłacone subskrypcje."}, {"sentence": "All expenses are paid-for by the company.", "translation": "Wszystkie wydatki są opłacane przez firmę."}]'::jsonb,
    '[{"phrase": "paid-for service", "translation": "płatna usługa", "frequency": "COMMON"}, {"phrase": "fully paid-for", "translation": "w pełni opłacony", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ˈpeɪd fɔr", "syllables": "paid-for", "stress": 1}'::jsonb,
    null,
    'Often used in business contexts to distinguish from free or trial services.',
    'Past participle emphasizing completion of payment',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 22. take-it-or-leave-it
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '80256eeb-7765-4c23-9fce-9ee67096670a',
    '["adjective", "negotiation", "ultimatum"]'::jsonb,
    '["non-negotiable", "final", "firm"]'::jsonb,
    '["negotiable", "flexible", "open"]'::jsonb,
    '["Usually preceded by article: a take-it-or-leave-it offer"]'::jsonb,
    '[{"sentence": "This is my take-it-or-leave-it offer.", "translation": "To jest moja ostateczna oferta."}, {"sentence": "He gave me a take-it-or-leave-it price.", "translation": "Podał mi ostateczną cenę."}]'::jsonb,
    '[{"phrase": "take-it-or-leave-it offer", "translation": "ostateczna oferta", "frequency": "VERY_COMMON"}, {"phrase": "take-it-or-leave-it basis", "translation": "zasada ostatecznej oferty", "frequency": "COMMON"}]'::jsonb,
    null,
    null,
    'Common in sales and negotiations. Indicates no room for bargaining.',
    'Accept as is or reject completely - no middle ground',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 23. go through
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '57679f5b-de4c-4730-8af9-71d42cfdd015',
    '["phrasal verb", "examine", "experience"]'::jsonb,
    '["review", "examine", "experience"]'::jsonb,
    '["skip", "ignore", "avoid"]'::jsonb,
    '["Multiple meanings: examine carefully, experience hardship, be approved"]'::jsonb,
    '[{"sentence": "Let''s go through the document together.", "translation": "Przejdźmy razem przez dokument."}, {"sentence": "She went through a difficult divorce.", "translation": "Przeszła przez trudny rozwód."}, {"sentence": "The deal finally went through.", "translation": "Transakcja w końcu przeszła."}]'::jsonb,
    '[{"phrase": "go through changes", "translation": "przechodzić zmiany", "frequency": "VERY_COMMON"}, {"phrase": "go through difficulties", "translation": "przechodzić trudności", "frequency": "VERY_COMMON"}]'::jsonb,
    '{"ipa": "ɡoʊ θruː", "syllables": "go through", "stress": 2}'::jsonb,
    '{"irregularForms": {"pastTense": "went through", "pastParticiple": "gone through"}}'::jsonb,
    'One of the most versatile phrasal verbs with many contextual meanings.',
    'Think: moving through something from start to finish',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 24. business-as-usual
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '9027edba-d197-4968-acda-ea6627d2b440',
    '["noun", "phrase", "routine"]'::jsonb,
    '["normal operations", "routine", "status quo"]'::jsonb,
    '["disruption", "change", "innovation"]'::jsonb,
    '["Can have negative connotation - resistance to change"]'::jsonb,
    '[{"sentence": "Despite the crisis, it''s business as usual.", "translation": "Pomimo kryzysu, wszystko działa normalnie."}, {"sentence": "We can''t accept business-as-usual thinking.", "translation": "Nie możemy zaakceptować rutynowego myślenia."}]'::jsonb,
    '[{"phrase": "business-as-usual approach", "translation": "rutynowe podejście", "frequency": "COMMON"}, {"phrase": "return to business as usual", "translation": "powrót do normalności", "frequency": "COMMON"}]'::jsonb,
    null,
    null,
    'Common in corporate and political contexts, often criticized as complacent.',
    'Everything continues as normal/routine',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 25. all-you-can-eat
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '1bc487fd-8aae-4ffa-8cd8-caeac700068a',
    '["adjective", "restaurant", "buffet"]'::jsonb,
    '["unlimited", "buffet-style", "unrestricted"]'::jsonb,
    '["limited", "à la carte", "portioned"]'::jsonb,
    '["Always used before noun: all-you-can-eat buffet"]'::jsonb,
    '[{"sentence": "The restaurant offers an all-you-can-eat buffet.", "translation": "Restauracja oferuje bufet bez ograniczeń."}, {"sentence": "Friday night is all-you-can-eat sushi.", "translation": "Piątkowy wieczór to sushi bez limitu."}]'::jsonb,
    '[{"phrase": "all-you-can-eat buffet", "translation": "bufet bez ograniczeń", "frequency": "VERY_COMMON"}, {"phrase": "all-you-can-eat deal", "translation": "oferta bez limitu", "frequency": "COMMON"}]'::jsonb,
    null,
    null,
    'Popular restaurant promotion where customers pay fixed price for unlimited food.',
    'Eat as much as you want for one price',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 26. meagre
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '28f8f190-046a-413b-9706-efab42539f0b',
    '["adjective", "insufficient", "small"]'::jsonb,
    '["scant", "paltry", "insufficient"]'::jsonb,
    '["abundant", "plentiful", "generous"]'::jsonb,
    '["British spelling: meagre. American: meager"]'::jsonb,
    '[{"sentence": "They survived on meagre rations.", "translation": "Przetrwali na skromnych racjach."}, {"sentence": "His salary is meagre considering his experience.", "translation": "Jego pensja jest mizerna biorąc pod uwagę jego doświadczenie."}]'::jsonb,
    '[{"phrase": "meagre income", "translation": "skromne dochody", "frequency": "VERY_COMMON"}, {"phrase": "meagre resources", "translation": "skromne zasoby", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ˈmiːɡər", "syllables": "mea-gre", "stress": 1}'::jsonb,
    null,
    'Often describes resources, income, or portions that are disappointingly small.',
    'Related to "meager" - lacking in quantity or quality',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 27. firm
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'c659818a-ca32-40e4-aac9-33386432027a',
    '["adjective", "solid", "resolute", "noun"]'::jsonb,
    '["solid", "hard", "steadfast", "company"]'::jsonb,
    '["soft", "weak", "flexible"]'::jsonb,
    '["Multiple meanings: physical (solid), mental (resolute), business (company)"]'::jsonb,
    '[{"sentence": "He has a firm handshake.", "translation": "Ma mocny uścisk dłoni."}, {"sentence": "She was firm in her decision.", "translation": "Była stanowcza w swojej decyzji."}, {"sentence": "He works for a law firm.", "translation": "Pracuje dla kancelarii prawnej."}]'::jsonb,
    '[{"phrase": "firm decision", "translation": "stanowcza decyzja", "frequency": "VERY_COMMON"}, {"phrase": "law firm", "translation": "kancelaria prawna", "frequency": "VERY_COMMON"}]'::jsonb,
    '{"ipa": "fɜrm", "syllables": "firm", "stress": 1}'::jsonb,
    null,
    'As noun, often refers to professional services companies (law firm, consulting firm).',
    'Multiple meanings but all relate to strength/stability',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 28. doting
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'fc4aa25a-7c25-4236-b9bc-c821cb32105d',
    '["adjective", "affection", "excessive love"]'::jsonb,
    '["adoring", "loving", "devoted"]'::jsonb,
    '["indifferent", "neglectful", "uncaring"]'::jsonb,
    '["Often implies excessive or blind affection"]'::jsonb,
    '[{"sentence": "His doting grandmother spoils him.", "translation": "Jego kochająca babcia go rozpieszcza."}, {"sentence": "She''s a doting mother to her children.", "translation": "Jest kochającą matką swoich dzieci."}]'::jsonb,
    '[{"phrase": "doting parent", "translation": "kochający rodzic", "frequency": "VERY_COMMON"}, {"phrase": "doting grandmother", "translation": "kochająca babcia", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ˈdoʊtɪŋ", "syllables": "dot-ing", "stress": 1}'::jsonb,
    null,
    'From "dote on" meaning to be excessively fond of. Can suggest overindulgence.',
    'Think: showing extreme affection or adoration',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 29. bluntly
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '912a72e4-6dd4-4b2e-aad4-5d5c69418042',
    '["adverb", "directness", "frankness"]'::jsonb,
    '["directly", "frankly", "candidly"]'::jsonb,
    '["tactfully", "diplomatically", "gently"]'::jsonb,
    '["Speaking without trying to be polite or indirect"]'::jsonb,
    '[{"sentence": "To put it bluntly, you need to work harder.", "translation": "Mówiąc bez ogródek, musisz ciężej pracować."}, {"sentence": "He spoke bluntly about the company''s problems.", "translation": "Mówił bez ogródek o problemach firmy."}]'::jsonb,
    '[{"phrase": "speak bluntly", "translation": "mówić bez ogródek", "frequency": "VERY_COMMON"}, {"phrase": "put it bluntly", "translation": "mówiąc wprost", "frequency": "VERY_COMMON"}]'::jsonb,
    '{"ipa": "ˈblʌntli", "syllables": "blunt-ly", "stress": 1}'::jsonb,
    null,
    'Common in business and difficult conversations. Can be seen as honest or rude depending on context.',
    'From blunt (tępy) - direct and unrefined in speech',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 30. to add insult to injury
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'db1c9042-bf1b-42e3-b453-f238d4590518',
    '["idiom", "worsening situation"]'::jsonb,
    '["to make matters worse", "on top of everything"]'::jsonb,
    '[]'::jsonb,
    '["Fixed expression - don''t change word order"]'::jsonb,
    '[{"sentence": "I lost my job, and to add insult to injury, my car broke down.", "translation": "Straciłem pracę, a na domiar złego mój samochód się zepsuł."}, {"sentence": "The service was poor, and to add insult to injury, they charged extra.", "translation": "Obsługa była kiepska, a na dodatek doliczyli extra opłaty."}]'::jsonb,
    '[{"phrase": "to add insult to injury", "translation": "na domiar złego", "frequency": "VERY_COMMON"}]'::jsonb,
    null,
    null,
    'One of the most common idioms for describing when bad situations get worse.',
    'Making a bad situation even worse',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 31. to cap it all
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'f5a378cf-dc63-4785-95a0-c7a93ee665d5',
    '["idiom", "culmination"]'::jsonb,
    '["to top it all off", "as if that wasn''t enough"]'::jsonb,
    '[]'::jsonb,
    '["British expression, less common in American English"]'::jsonb,
    '[{"sentence": "It rained all day, and to cap it all, we got lost.", "translation": "Padało cały dzień, a na domiar złego zgubiliśmy się."}, {"sentence": "The project was delayed, and to cap it all, the client cancelled.", "translation": "Projekt był opóźniony, a w dodatku klient odwołał zamówienie."}]'::jsonb,
    '[{"phrase": "to cap it all", "translation": "na domiar złego", "frequency": "COMMON"}, {"phrase": "to cap it all off", "translation": "na domiar złego", "frequency": "COMMON"}]'::jsonb,
    null,
    null,
    'British idiom similar to "to add insult to injury". Cap means to complete or top off.',
    'Cap = put a lid on, to finish/complete something',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 32. flustered
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '19fd0bdc-6404-4d24-aee3-aefba0687ef0',
    '["adjective", "confused", "agitated"]'::jsonb,
    '["confused", "flustered", "agitated"]'::jsonb,
    '["calm", "composed", "collected"]'::jsonb,
    '["Usually temporary state caused by stress or embarrassment"]'::jsonb,
    '[{"sentence": "She got flustered during the interview.", "translation": "Zdenerwowała się podczas rozmowy kwalifikacyjnej."}, {"sentence": "Don''t let the deadline make you flustered.", "translation": "Nie pozwól, by termin cię zdenerwował."}]'::jsonb,
    '[{"phrase": "get flustered", "translation": "zdenerwować się", "frequency": "VERY_COMMON"}, {"phrase": "look flustered", "translation": "wyglądać na zdenerwowanego", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ˈflʌstərd", "syllables": "flus-tered", "stress": 1}'::jsonb,
    null,
    'Often describes visible signs of confusion or agitation, like blushing or fumbling.',
    'From flush (rumienić się) - nervous and confused',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 33. blunt
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '7ef0d8e0-a5df-43d9-8d2c-baf16cb30ab6',
    '["adjective", "direct", "not sharp"]'::jsonb,
    '["direct", "frank", "dull"]'::jsonb,
    '["sharp", "tactful", "diplomatic"]'::jsonb,
    '["Can describe both physical objects (not sharp) and communication style (direct)"]'::jsonb,
    '[{"sentence": "The knife is blunt and won''t cut.", "translation": "Nóż jest tępy i nie przetnie."}, {"sentence": "He''s very blunt when giving feedback.", "translation": "Jest bardzo bezpośredni, gdy udziela informacji zwrotnej."}]'::jsonb,
    '[{"phrase": "blunt instrument", "translation": "tępe narzędzie", "frequency": "COMMON"}, {"phrase": "blunt honesty", "translation": "bezpośrednia szczerość", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "blʌnt", "syllables": "blunt", "stress": 1}'::jsonb,
    null,
    'In communication, blunt means straightforward but possibly insensitive.',
    'Opposite of sharp - both literally (tępy) and figuratively (bezceremonialny)',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 34. out of the blue
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'fb58b90a-4b7e-418c-a225-732451467c04',
    '["idiom", "unexpected", "suddenly"]'::jsonb,
    '["unexpectedly", "suddenly", "without warning"]'::jsonb,
    '["as expected", "predictably"]'::jsonb,
    '["Always with definite article: out of THE blue"]'::jsonb,
    '[{"sentence": "She called me out of the blue after 5 years.", "translation": "Zadzwoniła do mnie ni stąd ni zowąd po 5 latach."}, {"sentence": "The offer came out of the blue.", "translation": "Oferta pojawiła się znikąd."}]'::jsonb,
    '[{"phrase": "come out of the blue", "translation": "przyjść znikąd", "frequency": "VERY_COMMON"}, {"phrase": "happen out of the blue", "translation": "zdarzyć się niespodziewanie", "frequency": "COMMON"}]'::jsonb,
    null,
    null,
    'From "a bolt out of the blue" - lightning from a clear blue sky, completely unexpected.',
    'Like lightning from clear blue sky - totally unexpected',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 35. mingle
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '5c1ed01f-2e2c-4eb7-a8ea-479f21b91dc2',
    '["verb", "socialize", "mix"]'::jsonb,
    '["socialize", "mix", "circulate"]'::jsonb,
    '["isolate", "separate", "avoid"]'::jsonb,
    '["Often used in social contexts - mingling at parties"]'::jsonb,
    '[{"sentence": "Guests mingled in the garden before dinner.", "translation": "Goście mieszali się w ogrodzie przed kolacją."}, {"sentence": "He''s good at mingling with strangers.", "translation": "Jest dobry w kontaktach z obcymi."}]'::jsonb,
    '[{"phrase": "mingle with guests", "translation": "mieszać się z gośćmi", "frequency": "VERY_COMMON"}, {"phrase": "mingle at a party", "translation": "towarzysko się zachowywać na przyjęciu", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ˈmɪŋɡəl", "syllables": "min-gle", "stress": 1}'::jsonb,
    '{"irregularForms": {"presentParticiple": "mingling", "pastTense": "mingled"}}'::jsonb,
    'Common at networking events and social gatherings. Implies moving among groups.',
    'Mix + people = socializing and moving between groups',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 36. hover
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'cadf2f13-c0b9-46dd-b803-e4e11fdbe024',
    '["verb", "float", "linger"]'::jsonb,
    '["float", "hang", "linger"]'::jsonb,
    '["land", "settle", "depart"]'::jsonb,
    '["Can be physical (floating) or metaphorical (lingering nearby)"]'::jsonb,
    '[{"sentence": "The helicopter hovered above the building.", "translation": "Helikopter unosił się nad budynkiem."}, {"sentence": "She hovered near the door, unsure whether to enter.", "translation": "Kręciła się przy drzwiach, niepewna czy wejść."}]'::jsonb,
    '[{"phrase": "hover over", "translation": "unosić się nad", "frequency": "VERY_COMMON"}, {"phrase": "hover around", "translation": "kręcić się wokół", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ˈhʌvər", "syllables": "hov-er", "stress": 1}'::jsonb,
    '{"irregularForms": {"presentParticiple": "hovering", "pastTense": "hovered"}}'::jsonb,
    'In computing, hovering refers to moving cursor over something without clicking.',
    'Think hovercraft - floating in one spot',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 37. pleasantry
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '1532ee60-8388-4882-8f22-82dad6ac96e5',
    '["noun", "small talk", "polite remark"]'::jsonb,
    '["small talk", "courtesy", "polite remark"]'::jsonb,
    '["insult", "rudeness", "serious conversation"]'::jsonb,
    '["Usually plural: pleasantries. Implies superficial politeness."]'::jsonb,
    '[{"sentence": "We exchanged pleasantries before the meeting.", "translation": "Wymieniliśmy uprzejmości przed spotkaniem."}, {"sentence": "After a few pleasantries, they got down to business.", "translation": "Po kilku uprzejmościach przeszli do rzeczy."}]'::jsonb,
    '[{"phrase": "exchange pleasantries", "translation": "wymieniać uprzejmości", "frequency": "VERY_COMMON"}, {"phrase": "skip the pleasantries", "translation": "pominąć uprzejmości", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ˈplezəntri", "syllables": "pleas-ant-ry", "stress": 1}'::jsonb,
    '{"pluralForm": "pleasantries"}'::jsonb,
    'Often implies polite but meaningless conversation, especially in business contexts.',
    'Pleasant remarks that are polite but not deeply meaningful',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 38. pick someone
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'a9e851e6-c7b8-4d70-a285-5d8584735f28',
    '["phrasal verb", "selection", "choose"]'::jsonb,
    '["choose", "select", "collect"]'::jsonb,
    '["reject", "ignore", "avoid"]'::jsonb,
    '["Multiple meanings: choose, collect (pick up), criticize (pick on)"]'::jsonb,
    '[{"sentence": "I''ll pick you up at 7pm.", "translation": "Odbiorę cię o 19:00."}, {"sentence": "She picked him for the team.", "translation": "Wybrała go do zespołu."}, {"sentence": "Don''t pick on your little brother.", "translation": "Nie czepiaj się młodszego brata."}]'::jsonb,
    '[{"phrase": "pick someone up", "translation": "odebrać kogoś", "frequency": "VERY_COMMON"}, {"phrase": "pick on someone", "translation": "czepiać się kogoś", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "pɪk", "syllables": "pick", "stress": 1}'::jsonb,
    '{"irregularForms": {"pastTense": "picked", "presentParticiple": "picking"}}'::jsonb,
    'Very versatile verb with many phrasal verb combinations (pick up, pick on, pick out, etc.).',
    'Context determines meaning: pick (wybierać) + preposition changes meaning',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 39. redundancy
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '1703194b-10c6-4b7e-a973-d9ec3b04b0ed',
    '["noun", "layoff", "duplication"]'::jsonb,
    '["layoff", "termination", "duplication"]'::jsonb,
    '["employment", "necessity", "uniqueness"]'::jsonb,
    '["British: job loss. American: more commonly used for unnecessary duplication."]'::jsonb,
    '[{"sentence": "The company announced 50 redundancies.", "translation": "Firma ogłosiła 50 zwolnień."}, {"sentence": "This feature creates unnecessary redundancy.", "translation": "Ta funkcja tworzy niepotrzebną redundancję."}]'::jsonb,
    '[{"phrase": "make redundancies", "translation": "dokonać zwolnień", "frequency": "COMMON"}, {"phrase": "avoid redundancy", "translation": "unikać redundancji", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "rɪˈdʌndənsi", "syllables": "re-dun-dan-cy", "stress": 2}'::jsonb,
    '{"pluralForm": "redundancies"}'::jsonb,
    'In British English, commonly used for job layoffs. In technical contexts, means backup systems.',
    'From redundant - not needed or excessive',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 40. upward trend
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '427c18c0-c8c3-4245-ba82-a63eee6dd987',
    '["noun phrase", "increase", "improvement"]'::jsonb,
    '["rising tendency", "improvement", "growth"]'::jsonb,
    '["downward trend", "decline", "decrease"]'::jsonb,
    '["Common in business, economics, and statistics"]'::jsonb,
    '[{"sentence": "Sales show an upward trend this quarter.", "translation": "Sprzedaż wykazuje trend wzrostowy w tym kwartale."}, {"sentence": "There''s an upward trend in remote work.", "translation": "Istnieje trend wzrostowy w pracy zdalnej."}]'::jsonb,
    '[{"phrase": "show upward trend", "translation": "wykazywać trend wzrostowy", "frequency": "VERY_COMMON"}, {"phrase": "continue upward trend", "translation": "kontynuować trend wzrostowy", "frequency": "COMMON"}]'::jsonb,
    null,
    null,
    'Very common in business reports, stock market analysis, and data presentations.',
    'Movement in upward direction over time',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 41. tipping point
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'fdd5aa1a-79d9-490a-8aab-c028fc98e350',
    '["noun", "critical moment", "threshold"]'::jsonb,
    '["threshold", "critical point", "turning point"]'::jsonb,
    '["equilibrium", "stability"]'::jsonb,
    '["Made famous by Malcolm Gladwell''s book The Tipping Point"]'::jsonb,
    '[{"sentence": "We''ve reached the tipping point for climate change.", "translation": "Osiągnęliśmy punkt krytyczny dla zmian klimatycznych."}, {"sentence": "The scandal was the tipping point for his resignation.", "translation": "Skandal był punktem przełomowym do jego rezygnacji."}]'::jsonb,
    '[{"phrase": "reach tipping point", "translation": "osiągnąć punkt krytyczny", "frequency": "VERY_COMMON"}, {"phrase": "past the tipping point", "translation": "po punkcie krytycznym", "frequency": "COMMON"}]'::jsonb,
    null,
    null,
    'Popularized by Malcolm Gladwell. Describes moment when small changes lead to large effects.',
    'Point where small change tips balance and causes major shift',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 42. to be on the up
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '06f55bd8-3153-45ce-ae43-bbe05c59f3b3',
    '["idiom", "improving", "rising"]'::jsonb,
    '["improving", "getting better", "rising"]'::jsonb,
    '["declining", "worsening", "falling"]'::jsonb,
    '["British idiom. Can also mean \"honest\" in some contexts."]'::jsonb,
    '[{"sentence": "His career is on the up.", "translation": "Jego kariera jest w fazie wznoszącej."}, {"sentence": "Business is on the up after the crisis.", "translation": "Biznes się poprawia po kryzysie."}]'::jsonb,
    '[{"phrase": "be on the up", "translation": "być na fali wznoszącej", "frequency": "COMMON"}, {"phrase": "on the up and up", "translation": "uczciwy", "frequency": "COMMON"}]'::jsonb,
    null,
    null,
    'Primarily British. \"On the up and up\" in American English means honest/legitimate.',
    'Moving in upward direction - improving',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 43. albeit
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '978a5f4e-666f-4523-8fe4-423b95e884e9',
    '["conjunction", "formal", "concession"]'::jsonb,
    '["although", "though", "even though"]'::jsonb,
    '[]'::jsonb,
    '["Formal word, more common in writing than speech"]'::jsonb,
    '[{"sentence": "The solution is good, albeit imperfect.", "translation": "Rozwiązanie jest dobre, choć niedoskonałe."}, {"sentence": "He agreed, albeit reluctantly.", "translation": "Zgodził się, choć niechętnie."}]'::jsonb,
    '[{"phrase": "albeit briefly", "translation": "choć krótko", "frequency": "COMMON"}, {"phrase": "albeit reluctantly", "translation": "choć niechętnie", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ɔlˈbiːɪt", "syllables": "al-be-it", "stress": 2}'::jsonb,
    null,
    'From Middle English "all be it" (although it be). More formal than "though".',
    'All + be + it = although it may be (formal concession)',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 44. cumbersome
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '21106d94-d625-41ae-9e61-e3c27b42ee58',
    '["adjective", "unwieldy", "difficult"]'::jsonb,
    '["unwieldy", "bulky", "awkward"]'::jsonb,
    '["convenient", "streamlined", "simple"]'::jsonb,
    '["Can describe physical objects or processes/procedures"]'::jsonb,
    '[{"sentence": "The old computer system is cumbersome to use.", "translation": "Stary system komputerowy jest niewygodny w użyciu."}, {"sentence": "Moving this cumbersome furniture is difficult.", "translation": "Przenoszenie tych nieporęcznych mebli jest trudne."}]'::jsonb,
    '[{"phrase": "cumbersome process", "translation": "nieporęczny proces", "frequency": "VERY_COMMON"}, {"phrase": "cumbersome equipment", "translation": "nieporęczny sprzęt", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ˈkʌmbərsəm", "syllables": "cum-ber-some", "stress": 1}'::jsonb,
    null,
    'Often used in business to criticize inefficient procedures or outdated systems.',
    'From "cumber" (obciążać) - heavy and difficult to manage',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 45. infringe
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '98c2e981-3904-48dc-b892-978716d95a57',
    '["verb", "violate", "legal"]'::jsonb,
    '["violate", "breach", "encroach"]'::jsonb,
    '["respect", "comply", "observe"]'::jsonb,
    '["Infringe ON rights/rules. Infringe copyright (no preposition)."]'::jsonb,
    '[{"sentence": "This law infringes on our privacy rights.", "translation": "To prawo narusza nasze prawa do prywatności."}, {"sentence": "They infringed the copyright.", "translation": "Naruszyli prawa autorskie."}]'::jsonb,
    '[{"phrase": "infringe on rights", "translation": "naruszać prawa", "frequency": "VERY_COMMON"}, {"phrase": "infringe copyright", "translation": "naruszać prawa autorskie", "frequency": "VERY_COMMON"}]'::jsonb,
    '{"ipa": "ɪnˈfrɪndʒ", "syllables": "in-fringe", "stress": 2}'::jsonb,
    '{"irregularForms": {"presentParticiple": "infringing", "pastTense": "infringed"}}'::jsonb,
    'Common in legal and intellectual property contexts. Formal word.',
    'Think "break in" - breaking into protected territory/rights',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 46. permissible
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'dbeed3d7-ba5c-4a77-9512-9972336ebd8a',
    '["adjective", "allowed", "acceptable"]'::jsonb,
    '["allowed", "acceptable", "permitted"]'::jsonb,
    '["forbidden", "prohibited", "impermissible"]'::jsonb,
    '["Formal word, common in legal and official contexts"]'::jsonb,
    '[{"sentence": "Smoking is not permissible in this area.", "translation": "Palenie nie jest dozwolone w tym obszarze."}, {"sentence": "What is permissible under the new law?", "translation": "Co jest dozwolone według nowego prawa?"}]'::jsonb,
    '[{"phrase": "legally permissible", "translation": "prawnie dopuszczalne", "frequency": "COMMON"}, {"phrase": "permissible limit", "translation": "dopuszczalny limit", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "pərˈmɪsəbl̩", "syllables": "per-mis-si-ble", "stress": 2}'::jsonb,
    null,
    'More formal than "allowed". Often used in legal documents and regulations.',
    'From permit - able to be permitted',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 47. admissible
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '1b7f72a2-ee9a-4258-9681-05ef20728de6',
    '["adjective", "acceptable", "legal evidence"]'::jsonb,
    '["acceptable", "allowable", "valid"]'::jsonb,
    '["inadmissible", "unacceptable", "invalid"]'::jsonb,
    '["Very common in legal contexts, especially regarding evidence"]'::jsonb,
    '[{"sentence": "The evidence is not admissible in court.", "translation": "Dowód nie jest dopuszczalny w sądzie."}, {"sentence": "Only admissible documents should be submitted.", "translation": "Należy przedkładać tylko dopuszczalne dokumenty."}]'::jsonb,
    '[{"phrase": "admissible evidence", "translation": "dopuszczalny dowód", "frequency": "VERY_COMMON"}, {"phrase": "admissible in court", "translation": "dopuszczalny w sądzie", "frequency": "VERY_COMMON"}]'::jsonb,
    '{"ipa": "ədˈmɪsəbl̩", "syllables": "ad-mis-si-ble", "stress": 2}'::jsonb,
    null,
    'Legal term most commonly used for evidence that can be presented in court.',
    'From admit - able to be admitted (especially as evidence)',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 48. spur
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'bd980035-cab9-4b51-9aa0-2c429ec3ca3c',
    '["verb", "noun", "motivate", "equipment"]'::jsonb,
    '["motivate", "encourage", "stimulate"]'::jsonb,
    '["discourage", "deter", "hinder"]'::jsonb,
    '["As noun: sharp device for horse riding. As verb: to motivate/encourage."]'::jsonb,
    '[{"sentence": "The criticism spurred him to work harder.", "translation": "Krytyka pobudziła go do cięższej pracy."}, {"sentence": "Cowboys wear spurs on their boots.", "translation": "Kowboje noszą ostrogi na butach."}]'::jsonb,
    '[{"phrase": "spur someone on", "translation": "pobudzać kogoś", "frequency": "VERY_COMMON"}, {"phrase": "spur growth", "translation": "pobudzać wzrost", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "spɜr", "syllables": "spur", "stress": 1}'::jsonb,
    '{"irregularForms": {"presentParticiple": "spurring", "pastTense": "spurred"}}'::jsonb,
    'From riding equipment used to urge horses forward. "Spur of the moment" means spontaneous.',
    'Like using spurs on a horse - pushing forward/motivating',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 49. brick-and-mortar
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '69b5ddeb-e688-476e-9e90-e0b223be0c52',
    '["adjective", "physical store", "business"]'::jsonb,
    '["physical", "traditional", "offline"]'::jsonb,
    '["online", "virtual", "e-commerce"]'::jsonb,
    '["Contrasts with online/digital businesses"]'::jsonb,
    '[{"sentence": "They opened a brick-and-mortar store after years online.", "translation": "Otworzyli fizyczny sklep po latach działania online."}, {"sentence": "Brick-and-mortar retail is declining.", "translation": "Handel detaliczny w fizycznych sklepach maleje."}]'::jsonb,
    '[{"phrase": "brick-and-mortar store", "translation": "fizyczny sklep", "frequency": "VERY_COMMON"}, {"phrase": "brick-and-mortar business", "translation": "tradycyjny biznes", "frequency": "COMMON"}]'::jsonb,
    null,
    null,
    'Became popular with rise of e-commerce to distinguish physical stores from online ones.',
    'Brick and mortar = physical building materials = real physical store',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 50. certainty
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'c2c51bc2-d3a1-4bfb-83f3-488dd85159f8',
    '["noun", "assurance", "confidence"]'::jsonb,
    '["assurance", "confidence", "guarantee"]'::jsonb,
    '["uncertainty", "doubt", "ambiguity"]'::jsonb,
    '["Can mean the state of being certain OR something that is certain"]'::jsonb,
    '[{"sentence": "We can say with certainty that he''s guilty.", "translation": "Możemy z pewnością powiedzieć, że jest winny."}, {"sentence": "Death and taxes are certainties in life.", "translation": "Śmierć i podatki są pewniakami w życiu."}]'::jsonb,
    '[{"phrase": "with certainty", "translation": "z pewnością", "frequency": "VERY_COMMON"}, {"phrase": "absolute certainty", "translation": "absolutna pewność", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ˈsɜrtənti", "syllables": "cer-tain-ty", "stress": 1}'::jsonb,
    '{"pluralForm": "certainties"}'::jsonb,
    'Philosophy discusses the limits of certainty and knowledge.',
    'State of being certain (pewny)',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 51. palpable
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'fe21fe74-b689-4dfb-a2be-1e4d0f85c828',
    '["adjective", "tangible", "obvious"]'::jsonb,
    '["tangible", "obvious", "perceptible"]'::jsonb,
    '["imperceptible", "subtle", "intangible"]'::jsonb,
    '["Literally means touchable, but usually used for emotions/atmosphere"]'::jsonb,
    '[{"sentence": "The tension in the room was palpable.", "translation": "Napięcie w pokoju było wyczuwalne."}, {"sentence": "There was palpable excitement in the air.", "translation": "W powietrzu było wyczuwalne podekscytowanie."}]'::jsonb,
    '[{"phrase": "palpable tension", "translation": "wyczuwalne napięcie", "frequency": "VERY_COMMON"}, {"phrase": "palpable excitement", "translation": "wyczuwalne podekscytowanie", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ˈpælpəbl̩", "syllables": "pal-pa-ble", "stress": 1}'::jsonb,
    null,
    'From Latin "palpare" meaning to touch. Often used for intense emotions.',
    'From palpate (badać przez dotyk) - so obvious you can almost touch it',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 52. lull
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '34ad29df-1ab0-4450-9852-2239aeea4e9c',
    '["noun", "verb", "calm period", "soothe"]'::jsonb,
    '["pause", "calm", "respite"]'::jsonb,
    '["commotion", "activity", "agitation"]'::jsonb,
    '["As noun: temporary calm. As verb: to soothe or calm."]'::jsonb,
    '[{"sentence": "There was a lull in the conversation.", "translation": "Nastąpiła przerwa w rozmowie."}, {"sentence": "The music lulled the baby to sleep.", "translation": "Muzyka ukoiła dziecko do snu."}]'::jsonb,
    '[{"phrase": "lull in activity", "translation": "przerwa w aktywności", "frequency": "COMMON"}, {"phrase": "lull before the storm", "translation": "cisza przed burzą", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "lʌl", "syllables": "lull", "stress": 1}'::jsonb,
    '{"irregularForms": {"presentParticiple": "lulling", "pastTense": "lulled"}}'::jsonb,
    '"Lull before the storm" is common idiom for calm period before trouble.',
    'Think lullaby (kołysanka) - calming and soothing',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 53. ruminate
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'f6151878-c7b8-4141-8885-cb12552dbfd7',
    '["verb", "contemplate", "ponder"]'::jsonb,
    '["ponder", "contemplate", "reflect"]'::jsonb,
    '["ignore", "dismiss", "neglect"]'::jsonb,
    '["Formal word. Literally means how cows chew cud - repeatedly thinking."]'::jsonb,
    '[{"sentence": "He ruminated over the decision for weeks.", "translation": "Rozważał decyzję przez tygodnie."}, {"sentence": "Stop ruminating about the past.", "translation": "Przestań przeżuwać przeszłość."}]'::jsonb,
    '[{"phrase": "ruminate over", "translation": "rozważać", "frequency": "COMMON"}, {"phrase": "ruminate on", "translation": "rozmyślać nad", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ˈruməneɪt", "syllables": "ru-mi-nate", "stress": 1}'::jsonb,
    '{"irregularForms": {"presentParticiple": "ruminating", "pastTense": "ruminated"}}'::jsonb,
    'From how ruminant animals (cows, sheep) chew their cud - repetitive process.',
    'Like a cow chewing cud - going over thoughts repeatedly',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 54. chamomile
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '3c1038c6-e343-4bbb-8e92-554e5fc02707',
    '["noun", "herb", "tea"]'::jsonb,
    '[]'::jsonb,
    '[]'::jsonb,
    '["Can be spelled chamomile (US) or camomile (UK)"]'::jsonb,
    '[{"sentence": "Chamomile tea helps me sleep.", "translation": "Herbata z rumianku pomaga mi zasnąć."}, {"sentence": "She grows chamomile in her garden.", "translation": "Uprawia rumianek w swoim ogrodzie."}]'::jsonb,
    '[{"phrase": "chamomile tea", "translation": "herbata z rumianku", "frequency": "VERY_COMMON"}, {"phrase": "chamomile flowers", "translation": "kwiaty rumianku", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ˈkæməmaɪl", "syllables": "cham-o-mile", "stress": 1}'::jsonb,
    null,
    'Popular herbal tea known for calming properties. Used in traditional medicine for centuries.',
    'Remember: cham-o-mile = three syllables',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 55. vocational school
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '87ed9d59-5da6-4b76-a0e8-21c314589ea3',
    '["noun", "education", "training"]'::jsonb,
    '["trade school", "technical school", "career school"]'::jsonb,
    '["academic school", "liberal arts college"]'::jsonb,
    '["Focuses on practical job skills rather than academic education"]'::jsonb,
    '[{"sentence": "He attended vocational school to become an electrician.", "translation": "Uczęszczał do szkoły zawodowej, aby zostać elektrykiem."}, {"sentence": "Vocational schools offer hands-on training.", "translation": "Szkoły zawodowe oferują praktyczne szkolenie."}]'::jsonb,
    '[{"phrase": "vocational school training", "translation": "szkolenie w szkole zawodowej", "frequency": "COMMON"}, {"phrase": "attend vocational school", "translation": "uczęszczać do szkoły zawodowej", "frequency": "COMMON"}]'::jsonb,
    null,
    null,
    'Alternative to traditional academic education. Growing in popularity for direct path to careers.',
    'Vocational = related to vocation (zawód) - job-focused education',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 56. shed
INSERT INTO public.word_details (id, word_id, use_cases, synonyms, antonyms, common_mistakes, example_sentences, collocations, pronunciation, grammar, cultural_notes, learning_tips, user_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    '0a2fb6cb-eba1-404e-8888-f810be0f2f2d',
    '["noun", "verb", "building", "remove"]'::jsonb,
    '["hut", "shack", "drop", "lose"]'::jsonb,
    '["acquire", "gain", "keep"]'::jsonb,
    '["As noun: small building. As verb: to drop/remove (shed tears, shed weight)."]'::jsonb,
    '[{"sentence": "The tools are stored in the shed.", "translation": "Narzędzia są przechowywane w szopie."}, {"sentence": "Snakes shed their skin.", "translation": "Węże zrzucają swoją skórę."}, {"sentence": "She shed tears at the wedding.", "translation": "Ronić łzy na weselu."}]'::jsonb,
    '[{"phrase": "garden shed", "translation": "szopa ogrodowa", "frequency": "VERY_COMMON"}, {"phrase": "shed light on", "translation": "rzucić światło na", "frequency": "VERY_COMMON"}, {"phrase": "shed weight", "translation": "zrzucić wagę", "frequency": "COMMON"}]'::jsonb,
    '{"ipa": "ʃed", "syllables": "shed", "stress": 1}'::jsonb,
    '{"irregularForms": {"pastTense": "shed", "presentParticiple": "shedding"}}'::jsonb,
    '"Shed light on" is very common idiom meaning to clarify or explain.',
    'Same form for present and past (shed/shed/shed) like cut',
    'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
