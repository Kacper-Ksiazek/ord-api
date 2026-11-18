-------------
-- Seed: manuals (word_details) for the root user's vocabulary
-- Seed data is edited in place (one file per category); do not add patch migrations.
-------------

-------------
-- word_details (manuals)
-------------
-- boardwalk
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('6ab0a01f-97eb-4114-8ba2-8f4a657e94cd', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Describing a seaside town or beach holiday", "Giving directions along the waterfront", "Walking trails through wetlands or national parks"]'::jsonb,
        '["promenade", "walkway"]'::jsonb,
        '[]'::jsonb,
        '["Using *boardwalk* for any pavement in town — a city pavement is a *sidewalk* (US) or *pavement* (UK); a *boardwalk* is made of wooden boards.", "Saying *on the boardwalk* vs *in the boardwalk* — you walk *on* or *along* a boardwalk, never *in* it."]'::jsonb,
        '[{"context": "holiday", "sentence": "We walked along the *boardwalk* at sunset.", "translation": "Spacerowaliśmy *promenadą* o zachodzie słońca."}, {"context": "describing a place", "sentence": "The *boardwalk* was lined with ice-cream stands and arcades.", "translation": "Wzdłuż *promenady* stały budki z lodami i salony gier."}, {"context": "nature trail", "sentence": "A wooden *boardwalk* leads you across the marsh to the lake.", "translation": "Drewniany *pomost* prowadzi przez mokradła aż do jeziora."}]'::jsonb,
        '[{"phrase": "stroll along the boardwalk", "translation": "spacerować promenadą", "frequency": "VERY_COMMON"}, {"phrase": "seaside boardwalk", "translation": "nadmorska promenada", "frequency": "COMMON"}, {"phrase": "wooden boardwalk", "translation": "drewniany pomost", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ˈbɔːrdwɔːk", "syllables": "board-walk", "stress": 1}'::jsonb,
        '{"pluralForm": "boardwalks"}'::jsonb,
        'Boardwalks are a classic part of American beach towns on the East Coast, such as Atlantic City or Coney Island, and are often lined with shops, food stands and amusement rides.',
        'Split it into *board* (deska) + *walk* (chodzić) — literally a path made of boards.');

-- beehive
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('e1e401da-0224-4356-bcdc-c89e5237f3c8', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Talking about beekeeping or honey", "Describing a garden or farm", "Comparing a busy place to a beehive"]'::jsonb,
        '["hive"]'::jsonb,
        '[]'::jsonb,
        '["Confusing *beehive* with *bee nest* — a *beehive* is usually built by people for bees; wild bees live in a *nest* or *colony*.", "Writing it as two words (*bee hive*) — the standard spelling is one word: *beehive*."]'::jsonb,
        '[{"context": "beekeeping", "sentence": "The beekeeper checked the *beehive* for honey.", "translation": "Pszczelarz sprawdził, czy w *ulu* jest miód."}, {"context": "garden", "sentence": "We keep two *beehives* at the end of the garden.", "translation": "Na końcu ogrodu mamy dwa *ule*."}, {"context": "figurative", "sentence": "The office was a *beehive* of activity before the launch.", "translation": "Przed premierą biuro tętniło życiem jak *ul*."}]'::jsonb,
        '[{"phrase": "a beehive of activity", "translation": "miejsce tętniące życiem", "frequency": "COMMON"}, {"phrase": "keep a beehive", "translation": "trzymać ul", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ˈbiːhaɪv", "syllables": "bee-hive", "stress": 1}'::jsonb,
        '{"pluralForm": "beehives"}'::jsonb,
        'A *beehive* is also a tall, rounded 1960s women''s hairstyle, which you may hear in films or descriptions of that era.',
        'Polish uses the same image: a busy place is *jak ul* — English says *a beehive of activity*.');

-- mutter
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('907af8cc-c68e-4c11-b506-bbfa7545bc61', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Describing someone complaining quietly", "Telling a story about an awkward moment", "Writing dialogue in fiction"]'::jsonb,
        '["mumble", "murmur", "grumble"]'::jsonb,
        '["shout", "yell"]'::jsonb,
        '["Using *mutter* and *mumble* as exact synonyms — *mutter* usually suggests annoyance or complaining, *mumble* just means speaking unclearly.", "Forgetting the preposition in *mutter under your breath* — not *mutter under breath*."]'::jsonb,
        '[{"context": "storytelling", "sentence": "He *muttered* something under his breath and left the room.", "translation": "*Mruknął* coś pod nosem i wyszedł z pokoju."}, {"context": "everyday complaint", "sentence": "She *muttered* about the weather all the way to the station.", "translation": "Przez całą drogę na dworzec *mamrotała* coś o pogodzie."}, {"context": "dialogue", "sentence": "\"Typical,\" he *muttered* when the train was cancelled.", "translation": "„No jasne” — *mruknął*, gdy odwołano pociąg."}]'::jsonb,
        '[{"phrase": "mutter under your breath", "translation": "mruczeć pod nosem", "frequency": "VERY_COMMON"}, {"phrase": "mutter something", "translation": "mruknąć coś", "frequency": "VERY_COMMON"}, {"phrase": "mutter darkly", "translation": "mruczeć złowrogo", "frequency": "OCCASIONAL"}]'::jsonb,
        '{"ipa": "ˈmʌtər", "syllables": "mut-ter", "stress": 1}'::jsonb,
        NULL,
        'In stories *mutter* often signals a character''s quiet irritation, so writers use it instead of *said* to show mood.',
        'Imagine someone grumbling behind your back: *mutter, mutter, mutter* — the sound of the word imitates the action.');

-- sluggish
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('43166a14-fc87-480c-8f4b-4661d3c7aa2a', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Describing how you feel after a heavy meal or poor sleep", "Talking about a slow computer or app", "Business news about a weak economy"]'::jsonb,
        '["slow", "lethargic", "listless"]'::jsonb,
        '["energetic", "lively", "brisk"]'::jsonb,
        '["Using *sluggish* as an adverb (*he worked sluggish*) — the adverb is *sluggishly*.", "Using *sluggish* for a person who is simply lazy — it describes low energy or slow reaction, not unwillingness to work."]'::jsonb,
        '[{"context": "everyday", "sentence": "I always feel *sluggish* after a big lunch.", "translation": "Po dużym obiedzie zawsze czuję się *ospały*."}, {"context": "technology", "sentence": "My laptop has been really *sluggish* since the update.", "translation": "Od aktualizacji mój laptop działa bardzo *powoli*."}, {"context": "business news", "sentence": "Growth remained *sluggish* for most of the year.", "translation": "Przez większą część roku wzrost pozostawał *słaby*."}]'::jsonb,
        '[{"phrase": "feel sluggish", "translation": "czuć się ospałym", "frequency": "VERY_COMMON"}, {"phrase": "sluggish economy", "translation": "słaba gospodarka", "frequency": "VERY_COMMON"}, {"phrase": "sluggish growth", "translation": "powolny wzrost", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ˈslʌɡɪʃ", "syllables": "slug-gish", "stress": 1}'::jsonb,
        '{"comparativeForm": "more sluggish", "superlativeForm": "most sluggish"}'::jsonb,
        'Economists and journalists use *sluggish* very often for markets and sales, so it is worth knowing beyond its everyday meaning.',
        'Think of a *slug* (ślimak bez skorupy) — slow and low on energy, just like *sluggish*.');

-- lethargic
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('a0992a09-7586-48dc-8b85-d7fa6365565a', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Describing how you feel when you are ill or exhausted", "Talking to a doctor about symptoms", "Describing a pet that seems unwell"]'::jsonb,
        '["sluggish", "listless", "drowsy"]'::jsonb,
        '["energetic", "lively", "alert"]'::jsonb,
        '["Translating *lethargic* as *letargiczny* in every context — in everyday Polish *ospały* or *bez energii* sounds much more natural.", "Using *lethargic* for a short moment of tiredness — it suggests a stronger, longer lack of energy than *tired*."]'::jsonb,
        '[{"context": "weather", "sentence": "The heat made everyone *lethargic*.", "translation": "Upał sprawił, że wszyscy byli *ospali*."}, {"context": "pets", "sentence": "If your dog seems *lethargic*, take it to the vet.", "translation": "Jeśli twój pies wydaje się *apatyczny*, zabierz go do weterynarza."}, {"context": "health", "sentence": "I''ve been feeling *lethargic* since I caught the flu.", "translation": "Odkąd złapałem grypę, jestem *bez energii*."}]'::jsonb,
        '[{"phrase": "feel lethargic", "translation": "czuć się ospałym", "frequency": "VERY_COMMON"}, {"phrase": "become lethargic", "translation": "stać się apatycznym", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ləˈθɑːrdʒɪk", "syllables": "le-thar-gic", "stress": 2}'::jsonb,
        '{"comparativeForm": "more lethargic", "superlativeForm": "most lethargic"}'::jsonb,
        'Doctors and vets use *lethargic* as a symptom, so you will often hear it in medical questions like *Is the child lethargic?*',
        'The noun is *lethargy*; Polish *letarg* is the same root, but means a much deeper state — so prefer *ospały* in translation.');

-- ephemeral
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('93e418c8-b601-4117-a0d8-83c08d59a0ca', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Talking about trends, fashions or fame", "Describing social media stories that disappear", "Writing about art, nature or memories"]'::jsonb,
        '["fleeting", "short-lived", "transient"]'::jsonb,
        '["permanent", "lasting", "enduring"]'::jsonb,
        '["Using *ephemeral* in casual chat where *short-lived* or *brief* sounds more natural — it is a fairly literary word.", "Mispronouncing it with stress on the first syllable — the stress is on *-phem-*: e-PHEM-er-al."]'::jsonb,
        '[{"context": "social media", "sentence": "Fame on social media is often *ephemeral*.", "translation": "Sława w mediach społecznościowych bywa *ulotna*."}, {"context": "technology", "sentence": "Stories are *ephemeral* — they disappear after 24 hours.", "translation": "Relacje są *efemeryczne* — znikają po 24 godzinach."}, {"context": "art", "sentence": "She loves photographing the *ephemeral* beauty of cherry blossoms.", "translation": "Uwielbia fotografować *ulotne* piękno kwitnących wiśni."}]'::jsonb,
        '[{"phrase": "ephemeral nature", "translation": "ulotna natura", "frequency": "COMMON"}, {"phrase": "ephemeral beauty", "translation": "ulotne piękno", "frequency": "COMMON"}, {"phrase": "ephemeral trend", "translation": "przelotna moda", "frequency": "OCCASIONAL"}]'::jsonb,
        '{"ipa": "ɪˈfemərəl", "syllables": "e-phem-er-al", "stress": 2}'::jsonb,
        '{"comparativeForm": "more ephemeral", "superlativeForm": "most ephemeral"}'::jsonb,
        'From Greek *ephemeros* — "lasting a day". In tech, *ephemeral* data or messages are deleted automatically.',
        'Link it to *efemeryda* — something that appears for a moment and is gone.');

-- transient
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('2fb8b299-0199-4e9d-a720-5b44fba977a7', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Describing a temporary problem or symptom", "Talking about people who stay somewhere only briefly", "Formal or technical writing"]'::jsonb,
        '["temporary", "short-lived", "passing"]'::jsonb,
        '["permanent", "lasting", "persistent"]'::jsonb,
        '["Translating *transient* as *przejrzysty* — that means *transparent*; *transient* means *przejściowy*.", "Using *transient* in casual speech where *temporary* is more natural."]'::jsonb,
        '[{"context": "health", "sentence": "The side effects are usually *transient*.", "translation": "Skutki uboczne są zwykle *przejściowe*."}, {"context": "technology", "sentence": "It''s a *transient* problem — the server will be back soon.", "translation": "To *przejściowy* problem — serwer zaraz wróci."}, {"context": "society", "sentence": "The town has a large *transient* population of students.", "translation": "Miasto ma dużą *napływową* populację studentów."}]'::jsonb,
        '[{"phrase": "transient symptoms", "translation": "przejściowe objawy", "frequency": "COMMON"}, {"phrase": "transient population", "translation": "ludność napływowa", "frequency": "OCCASIONAL"}, {"phrase": "transient error", "translation": "błąd przejściowy", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ˈtrænziənt", "syllables": "tran-si-ent", "stress": 1}'::jsonb,
        '{"comparativeForm": "more transient", "superlativeForm": "most transient"}'::jsonb,
        'In IT and engineering *transient* errors are temporary failures that usually fix themselves when you try again.',
        'Think of *transit* — something just passing through.');

-- fleeting
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('3acf17a4-770f-4d99-928b-4cb113f94355', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Describing a quick look, smile or feeling", "Talking about memories and special moments", "Writing in a romantic or nostalgic style"]'::jsonb,
        '["brief", "momentary", "ephemeral"]'::jsonb,
        '["lasting", "enduring", "permanent"]'::jsonb,
        '["Putting *fleeting* after the noun (*a glance fleeting*) — it almost always comes before it: *a fleeting glance*.", "Using *fleeting* for long periods — it is about moments, not months."]'::jsonb,
        '[{"context": "storytelling", "sentence": "I caught a *fleeting* glimpse of her in the crowd.", "translation": "*Przelotnie* mignęła mi w tłumie."}, {"context": "reflection", "sentence": "Happiness can be *fleeting*, so enjoy it.", "translation": "Szczęście bywa *ulotne*, więc się nim ciesz."}, {"context": "sport", "sentence": "For a *fleeting* moment, I thought we''d won.", "translation": "Przez *krótką* chwilę myślałem, że wygraliśmy."}]'::jsonb,
        '[{"phrase": "a fleeting glimpse", "translation": "przelotne spojrzenie, mignięcie", "frequency": "VERY_COMMON"}, {"phrase": "a fleeting moment", "translation": "krótka chwila", "frequency": "VERY_COMMON"}, {"phrase": "fleeting smile", "translation": "przelotny uśmiech", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ˈfliːtɪŋ", "syllables": "fleet-ing", "stress": 1}'::jsonb,
        NULL,
        '*Fleeting* is very common in descriptions of feelings and moments in novels, songs and reviews.',
        '*To fleet* is an old verb meaning to move fast — a *fleeting* moment flies past.');

-- deplete
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('d92400eb-077d-4d7f-a095-9b850c44106f', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Discussing the environment and natural resources", "Talking about savings or budgets", "Describing physical exhaustion"]'::jsonb,
        '["use up", "drain", "exhaust"]'::jsonb,
        '["replenish", "restore", "top up"]'::jsonb,
        '["Using *deplete* without an object (*our savings depleted*) — in active voice it needs an object: *the trip depleted our savings*, or use passive: *our savings were depleted*.", "Using *deplete* for something that is completely gone in casual speech — people usually say *run out of*."]'::jsonb,
        '[{"context": "environment", "sentence": "Overfishing has *depleted* fish stocks in the area.", "translation": "Nadmierne połowy *uszczupliły* zasoby ryb w tym rejonie."}, {"context": "money", "sentence": "The renovation *depleted* our savings.", "translation": "Remont *wyczerpał* nasze oszczędności."}, {"context": "work", "sentence": "Long meetings *deplete* my energy by Friday.", "translation": "Długie spotkania do piątku *wysysają* ze mnie całą energię."}]'::jsonb,
        '[{"phrase": "deplete resources", "translation": "wyczerpywać zasoby", "frequency": "VERY_COMMON"}, {"phrase": "depleted savings", "translation": "uszczuplone oszczędności", "frequency": "COMMON"}, {"phrase": "ozone depletion", "translation": "zubożenie warstwy ozonowej", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "dɪˈpliːt", "syllables": "de-plete", "stress": 2}'::jsonb,
        NULL,
        'Very frequent in environmental topics: *ozone depletion*, *depleted fish stocks*, *resource depletion*.',
        'Think of a *plate* being emptied — *de-plete* takes things away until little is left.');

-- shoot someone down in flames
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('1ed73d97-9755-4ef6-bfc5-39de627706b6', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Describing a tough meeting or presentation", "Warning someone before they share a risky idea", "Talking about harsh reviews or feedback"]'::jsonb,
        '["tear apart", "rip to shreds", "slam"]'::jsonb,
        '["praise", "back", "support"]'::jsonb,
        '["Taking it literally — it is about criticism, not shooting anything.", "Forgetting the object — you shoot *someone* or *an idea* down: *They shot my proposal down in flames*."]'::jsonb,
        '[{"context": "work", "sentence": "I suggested a four-day week and my boss *shot me down in flames*.", "translation": "Zaproponowałem czterodniowy tydzień pracy, a szef *nie zostawił na mnie suchej nitki*."}, {"context": "feedback", "sentence": "Don''t *shoot them down in flames* — they''re new and still learning.", "translation": "Nie *miażdż ich krytyką* — są nowi i dopiero się uczą."}, {"context": "reviews", "sentence": "The critics *shot the film down in flames*.", "translation": "Krytycy *nie zostawili na filmie suchej nitki*."}]'::jsonb,
        '[{"phrase": "shoot an idea down", "translation": "storpedować pomysł", "frequency": "VERY_COMMON"}, {"phrase": "get shot down", "translation": "zostać zgaszonym, odrzuconym", "frequency": "COMMON"}]'::jsonb,
        NULL,
        NULL,
        'The image comes from air combat, where a plane is shot down and falls in flames — the phrase is used informally in offices and reviews.',
        'The shorter *shoot down* (an idea, a proposal) is even more common in everyday speech.');

-- food for thought
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('d32becba-9815-47e6-b18a-0a5b2c9b7909', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Reacting to an interesting talk, article or conversation", "Ending a presentation or discussion", "Giving polite feedback about an idea"]'::jsonb,
        '["something to think about"]'::jsonb,
        '[]'::jsonb,
        '["Saying *a food for thought* — it is uncountable, so no article: *That gave me food for thought*.", "Saying *fruit of thought* — that is not an English idiom; the expression is *food for thought*."]'::jsonb,
        '[{"context": "work", "sentence": "Your presentation gave us a lot of *food for thought*.", "translation": "Twoja prezentacja dała nam wiele *do myślenia*."}, {"context": "media", "sentence": "That documentary was real *food for thought*.", "translation": "Ten dokument był prawdziwym *materiałem do przemyśleń*."}, {"context": "conversation", "sentence": "I don''t agree, but it''s *food for thought*.", "translation": "Nie zgadzam się, ale to *daje do myślenia*."}]'::jsonb,
        '[{"phrase": "give someone food for thought", "translation": "dać komuś do myślenia", "frequency": "VERY_COMMON"}, {"phrase": "plenty of food for thought", "translation": "mnóstwo do przemyślenia", "frequency": "COMMON"}]'::jsonb,
        NULL,
        NULL,
        'A friendly, common way to show you found something interesting even if you are not fully convinced.',
        'Polish *daje do myślenia* is the closest natural match — reach for it first when translating.');

-- down the rabbit hole
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('17d9c6e7-adfd-4a64-a1e2-eff9c9adebed', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Talking about browsing the internet or watching videos", "Describing a research session that went on too long", "Chatting about hobbies or obsessions"]'::jsonb,
        '["get sucked in", "lose track of time"]'::jsonb,
        '[]'::jsonb,
        '["Translating it literally as *w dół króliczej nory* — in Polish say *wciągnąłem się* or *przepadłem*.", "Dropping the verb — the phrase is usually *go down the rabbit hole* or *fall down the rabbit hole*."]'::jsonb,
        '[{"context": "internet", "sentence": "I went *down the rabbit hole* reading about ancient Rome.", "translation": "*Przepadłem* na długie godziny, czytając o starożytnym Rzymie."}, {"context": "social media", "sentence": "One video sent me *down the rabbit hole* until 3 a.m.", "translation": "Jeden filmik *wciągnął mnie* do trzeciej w nocy."}, {"context": "hobbies", "sentence": "Be careful, it''s easy to go *down the rabbit hole* with this hobby.", "translation": "Uważaj, przy tym hobby łatwo *wsiąknąć bez reszty*."}]'::jsonb,
        '[{"phrase": "go down the rabbit hole", "translation": "wciągnąć się bez reszty", "frequency": "VERY_COMMON"}, {"phrase": "fall down a rabbit hole", "translation": "przepaść w czymś", "frequency": "COMMON"}]'::jsonb,
        NULL,
        NULL,
        'From *Alice in Wonderland*, where Alice follows the White Rabbit into a strange world. Today it is mostly used about the internet.',
        'Picture yourself clicking link after link — that''s the rabbit hole.');

-- well-to-do
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('f3b391f5-91d0-4a5f-b8da-db906c812f3d', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Describing someone''s family background", "Talking about neighbourhoods and housing", "Polite conversation about money"]'::jsonb,
        '["wealthy", "affluent", "well-off"]'::jsonb,
        '["poor", "hard-up", "badly off"]'::jsonb,
        '["Dropping the hyphens before a noun — write *a well-to-do family*.", "Treating it as very rich — it means comfortable, not extremely wealthy."]'::jsonb,
        '[{"context": "background", "sentence": "She comes from a *well-to-do* family.", "translation": "Pochodzi z *zamożnej* rodziny."}, {"context": "housing", "sentence": "It''s a quiet, *well-to-do* neighbourhood.", "translation": "To spokojna, *dobrze sytuowana* dzielnica."}, {"context": "travel", "sentence": "The resort attracts *well-to-do* tourists.", "translation": "Kurort przyciąga *zamożnych* turystów."}]'::jsonb,
        '[{"phrase": "well-to-do family", "translation": "zamożna rodzina", "frequency": "VERY_COMMON"}, {"phrase": "well-to-do area", "translation": "zamożna okolica", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ˌwel tə ˈduː", "syllables": "well-to-do", "stress": 3}'::jsonb,
        NULL,
        'A polite, slightly old-fashioned way to say someone has money without sounding blunt.',
        'Synonym pair to remember: *well-to-do* ≈ *well-off*.');

-- across-the-board
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('4cea91f7-7ed7-4722-8044-280573fb8ade', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Business news about pay or price changes", "Company announcements about cuts or raises", "Discussing policies that apply to everyone"]'::jsonb,
        '["general", "universal", "blanket"]'::jsonb,
        '["selective", "targeted"]'::jsonb,
        '["Hyphenating it after a verb — before a noun: *across-the-board cuts*; after a verb: *prices rose across the board* (no hyphens).", "Using it for small groups — it implies a whole organisation, sector or set of items."]'::jsonb,
        '[{"context": "work", "sentence": "The company announced *across-the-board* pay rises.", "translation": "Firma ogłosiła *powszechne* podwyżki."}, {"context": "economy", "sentence": "Prices went up *across the board*.", "translation": "Ceny wzrosły *we wszystkich kategoriach*."}, {"context": "results", "sentence": "We saw improvements *across the board*.", "translation": "Widzieliśmy poprawę *na całej linii*."}]'::jsonb,
        '[{"phrase": "across-the-board cuts", "translation": "cięcia we wszystkich obszarach", "frequency": "VERY_COMMON"}, {"phrase": "across-the-board increase", "translation": "ogólna podwyżka", "frequency": "COMMON"}]'::jsonb,
        NULL,
        NULL,
        'Originally from horse-race betting: a bet *across the board* covers win, place and show.',
        'Polish *na całej linii* is a natural match when the phrase comes after a verb.');

-- run-of-the-mill
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('b1898c39-791a-48f3-ab3a-3ef7e03d8fad', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Reviewing films, restaurants or products", "Describing a routine day or task", "Comparing something special with something average"]'::jsonb,
        '["ordinary", "average", "unremarkable"]'::jsonb,
        '["exceptional", "outstanding", "unusual"]'::jsonb,
        '["Using it as praise — it is mildly negative: it means *nothing special*.", "Forgetting the hyphens before a noun: *a run-of-the-mill hotel*."]'::jsonb,
        '[{"context": "review", "sentence": "It was just a *run-of-the-mill* action film.", "translation": "To był zupełnie *przeciętny* film akcji."}, {"context": "work", "sentence": "This isn''t a *run-of-the-mill* job interview.", "translation": "To nie jest *zwykła* rozmowa o pracę."}, {"context": "travel", "sentence": "The hotel was clean but pretty *run-of-the-mill*.", "translation": "Hotel był czysty, ale dość *przeciętny*."}]'::jsonb,
        '[{"phrase": "a run-of-the-mill job", "translation": "zwykła praca", "frequency": "COMMON"}, {"phrase": "run-of-the-mill product", "translation": "przeciętny produkt", "frequency": "COMMON"}]'::jsonb,
        NULL,
        NULL,
        'From factories: goods that *ran off the mill* without being sorted or improved.',
        'Use it when you''d say *nic specjalnego* in Polish.');

-- state-of-the-art
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('05afa286-5935-4cd4-a9a1-a51038149eb3', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Describing equipment, buildings or technology", "Marketing and product descriptions", "Job interviews about a company''s tools"]'::jsonb,
        '["cutting-edge", "advanced", "modern"]'::jsonb,
        '["outdated", "obsolete", "old-fashioned"]'::jsonb,
        '["Dropping the hyphens before a noun — write *state-of-the-art equipment*.", "Overusing it in casual speech — it sounds like marketing; friends might just say *really modern*."]'::jsonb,
        '[{"context": "health", "sentence": "The hospital has *state-of-the-art* equipment.", "translation": "Szpital ma *najnowocześniejszy* sprzęt."}, {"context": "work", "sentence": "They work in a *state-of-the-art* office in the city centre.", "translation": "Pracują w *supernowoczesnym* biurze w centrum."}, {"context": "sport", "sentence": "The stadium''s sound system is *state-of-the-art*.", "translation": "Nagłośnienie stadionu to *najwyższa półka*."}]'::jsonb,
        '[{"phrase": "state-of-the-art technology", "translation": "najnowocześniejsza technologia", "frequency": "VERY_COMMON"}, {"phrase": "state-of-the-art facility", "translation": "nowoczesny obiekt", "frequency": "VERY_COMMON"}]'::jsonb,
        NULL,
        NULL,
        'Very common in adverts, press releases and job offers.',
        'Synonym worth learning together: *cutting-edge*.');

-- off-the-cuff
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('82f7cb46-3e87-433a-a4d5-c5fa2eaf7eff', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Talking about speeches, comments or answers", "Meetings where you''re asked for a quick estimate", "Describing someone''s sense of humour"]'::jsonb,
        '["spontaneous", "improvised", "impromptu"]'::jsonb,
        '["prepared", "rehearsed", "scripted"]'::jsonb,
        '["Using hyphens after a verb — *an off-the-cuff remark*, but *he spoke off the cuff*.", "Thinking it means rude — it only means unprepared."]'::jsonb,
        '[{"context": "conversation", "sentence": "It was just an *off-the-cuff* remark — don''t take it seriously.", "translation": "To była tylko *rzucona mimochodem* uwaga — nie bierz jej na serio."}, {"context": "work", "sentence": "I can''t give you exact numbers *off the cuff*.", "translation": "Nie podam ci dokładnych liczb *z głowy*."}, {"context": "event", "sentence": "Her *off-the-cuff* speech was the best of the evening.", "translation": "Jej *spontaniczne* przemówienie było najlepsze tego wieczoru."}]'::jsonb,
        '[{"phrase": "off-the-cuff remark", "translation": "uwaga rzucona mimochodem", "frequency": "VERY_COMMON"}, {"phrase": "speak off the cuff", "translation": "mówić bez przygotowania", "frequency": "COMMON"}]'::jsonb,
        NULL,
        NULL,
        'Comes from speakers writing quick notes on their shirt cuffs instead of preparing a full speech.',
        'Polish equivalents to keep in mind: *z marszu*, *na gorąco*, *z głowy*.');

-- larger-than-life
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('a9be3be1-4d41-43a1-b8ee-be4d0cb41417', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Describing a colourful person or character", "Talking about celebrities or historical figures", "Book and film reviews"]'::jsonb,
        '["flamboyant", "charismatic", "colourful"]'::jsonb,
        '["dull", "unremarkable", "low-key"]'::jsonb,
        '["Translating it literally as *większy niż życie* — in Polish say *barwna postać* or *charyzmatyczny*.", "Using it for physical size — it describes personality or presence, not height."]'::jsonb,
        '[{"context": "family", "sentence": "My uncle was a *larger-than-life* character.", "translation": "Mój wujek był niezwykle *barwną* postacią."}, {"context": "review", "sentence": "The film celebrates a *larger-than-life* rock star.", "translation": "Film opowiada o *charyzmatycznej* gwieździe rocka."}, {"context": "describing people", "sentence": "She has a *larger-than-life* personality.", "translation": "Ma bardzo *wyrazistą* osobowość."}]'::jsonb,
        '[{"phrase": "larger-than-life character", "translation": "barwna postać", "frequency": "VERY_COMMON"}, {"phrase": "larger-than-life personality", "translation": "wyrazista osobowość", "frequency": "VERY_COMMON"}]'::jsonb,
        NULL,
        NULL,
        'Usually positive or affectionate, often used about performers, leaders and memorable relatives.',
        'Picture someone who fills the whole room with their presence.');

-- middle-of-the-road
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('d4673027-c4f5-4ace-8caa-64ca1ea560bb', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Discussing politics and opinions", "Reviewing music, films or products", "Talking about cautious decisions"]'::jsonb,
        '["moderate", "mainstream", "centrist"]'::jsonb,
        '["radical", "extreme", "bold"]'::jsonb,
        '["Assuming it is always positive — for music or art it often means bland.", "Hyphenation: *a middle-of-the-road policy*, but *he stays in the middle of the road*."]'::jsonb,
        '[{"context": "politics", "sentence": "The party is trying to win *middle-of-the-road* voters.", "translation": "Partia próbuje zdobyć *umiarkowanych* wyborców."}, {"context": "music", "sentence": "Their new album is pleasant but very *middle-of-the-road*.", "translation": "Ich nowy album jest przyjemny, ale bardzo *zachowawczy*."}, {"context": "decisions", "sentence": "We chose a *middle-of-the-road* option to keep everyone happy.", "translation": "Wybraliśmy *kompromisowe* rozwiązanie, żeby wszyscy byli zadowoleni."}]'::jsonb,
        '[{"phrase": "middle-of-the-road voters", "translation": "umiarkowani wyborcy", "frequency": "COMMON"}, {"phrase": "middle-of-the-road approach", "translation": "umiarkowane podejście", "frequency": "COMMON"}]'::jsonb,
        NULL,
        NULL,
        'Common in British and American political commentary; in music reviews it often appears as *MOR*.',
        'The middle of the road is the safe place, far from both edges.');

-- thought-out
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('a331d7b8-9da4-46fd-a3ed-47fb3e62927d', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Giving feedback on a plan or proposal", "Describing a decision or argument", "Reviewing a product''s design"]'::jsonb,
        '["considered", "well-planned"]'::jsonb,
        '["hasty", "half-baked", "ill-considered"]'::jsonb,
        '["Confusing it with *though* or *throughout* — the root is *think → thought*.", "Using it alone — it is almost always combined with *well-*, *carefully* or *poorly*."]'::jsonb,
        '[{"context": "work", "sentence": "It''s a *well-thought-out* plan.", "translation": "To dobrze *przemyślany* plan."}, {"context": "review", "sentence": "The ending of the film felt poorly *thought-out*.", "translation": "Zakończenie filmu wydawało się słabo *przemyślane*."}, {"context": "conversation", "sentence": "She gave a carefully *thought-out* answer.", "translation": "Udzieliła starannie *przemyślanej* odpowiedzi."}]'::jsonb,
        '[{"phrase": "well-thought-out plan", "translation": "dobrze przemyślany plan", "frequency": "VERY_COMMON"}, {"phrase": "poorly thought-out", "translation": "słabo przemyślany", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ˌθɔːt ˈaʊt", "syllables": "thought-out", "stress": 2}'::jsonb,
        NULL,
        'A common phrase in feedback at work and school.',
        'It comes from the phrasal verb *think something out* — to plan it carefully.');

-- paid-for
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('530b52d4-006e-4af5-ad2d-0cb656a4bbfc', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Talking about subscriptions and services", "Business travel and expenses", "Discussing ownership of a house or car"]'::jsonb,
        '["prepaid", "settled"]'::jsonb,
        '["unpaid", "free"]'::jsonb,
        '["Translating it as *płatny* — *płatny* means *you must pay*; *paid-for* means *already paid*: *opłacony*.", "Hyphenation: *a paid-for trip*, but *the trip is paid for*."]'::jsonb,
        '[{"context": "travel", "sentence": "It''s a fully *paid-for* trip, so you don''t need any money.", "translation": "To w pełni *opłacona* wycieczka, więc nie potrzebujesz pieniędzy."}, {"context": "finance", "sentence": "The house is *paid for* — we have no mortgage.", "translation": "Dom jest *spłacony* — nie mamy kredytu."}, {"context": "software", "sentence": "Is this a *paid-for* feature or a free one?", "translation": "To funkcja *płatna* czy darmowa?"}]'::jsonb,
        '[{"phrase": "paid-for subscription", "translation": "opłacona subskrypcja", "frequency": "COMMON"}, {"phrase": "fully paid for", "translation": "w pełni opłacony", "frequency": "COMMON"}]'::jsonb,
        NULL,
        NULL,
        'Often used in business to separate paid services from free trials.',
        'Paid + for — the payment has already happened.');

-- take-it-or-leave-it
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('80256eeb-7765-4c23-9fce-9ee67096670a', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Negotiating a price or salary", "Describing a tough deal", "Talking about someone''s attitude"]'::jsonb,
        '["final", "non-negotiable"]'::jsonb,
        '["negotiable", "flexible"]'::jsonb,
        '["Saying *take it or let it* — the fixed phrase is *take it or leave it*.", "Hyphenation: *a take-it-or-leave-it offer*, but as a sentence: *Take it or leave it!*"]'::jsonb,
        '[{"context": "business", "sentence": "They made us a *take-it-or-leave-it* offer.", "translation": "Złożyli nam *ostateczną* ofertę, bez negocjacji."}, {"context": "market", "sentence": "\"Fifty euros. *Take it or leave it*.\"", "translation": "„Pięćdziesiąt euro. *Bierzesz albo nie*”."}, {"context": "attitude", "sentence": "I don''t like his *take-it-or-leave-it* attitude.", "translation": "Nie podoba mi się jego podejście *„bierzesz albo nie”*."}]'::jsonb,
        '[{"phrase": "take-it-or-leave-it offer", "translation": "ostateczna oferta", "frequency": "VERY_COMMON"}, {"phrase": "take-it-or-leave-it deal", "translation": "umowa bez negocjacji", "frequency": "COMMON"}]'::jsonb,
        NULL,
        NULL,
        'Common in negotiations; using it can sound tough or even rude.',
        'Polish *bierzesz albo nie* covers the spoken version nicely.');

-- go through
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('57679f5b-de4c-4730-8af9-71d42cfdd015', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Reviewing documents, emails or notes", "Talking about a hard period in life", "Checking whether a payment or deal was accepted"]'::jsonb,
        '["review", "examine", "experience"]'::jsonb,
        '[]'::jsonb,
        '["Saying *go through with* when you mean *examine* — *go through with* means *carry out something you planned*.", "Forgetting the irregular forms: *went through*, *gone through*."]'::jsonb,
        '[{"context": "work", "sentence": "Let''s *go through* the contract together.", "translation": "*Przejrzyjmy* razem umowę."}, {"context": "life", "sentence": "She''s *going through* a difficult time.", "translation": "*Przechodzi* teraz trudny okres."}, {"context": "shopping", "sentence": "Did the payment *go through*?", "translation": "Czy płatność *przeszła*?"}]'::jsonb,
        '[{"phrase": "go through the details", "translation": "omówić szczegóły", "frequency": "VERY_COMMON"}, {"phrase": "go through a hard time", "translation": "przechodzić trudny okres", "frequency": "VERY_COMMON"}, {"phrase": "the payment went through", "translation": "płatność przeszła", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ɡəʊ ˈθruː", "syllables": "go through", "stress": 2}'::jsonb,
        '{"irregularForms": {"pastTense": "went through", "pastParticiple": "gone through", "presentParticiple": "going through"}}'::jsonb,
        'One of the most useful phrasal verbs in everyday English — the meaning depends on context.',
        'Learn it with three objects: *go through papers*, *go through a divorce*, *the payment went through*.');

-- business as usual
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('9027edba-d197-4968-acda-ea6627d2b440', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Company updates during a crisis or change", "Reassuring customers that nothing changes", "Criticising a lack of change"]'::jsonb,
        '["status quo", "normal operations"]'::jsonb,
        '[]'::jsonb,
        '["Hyphenating it after a verb — *it''s business as usual*; hyphens only before a noun: *a business-as-usual approach*.", "Thinking it is always positive — it can criticise a refusal to change."]'::jsonb,
        '[{"context": "crisis", "sentence": "Despite the storm, it''s *business as usual* at the shop.", "translation": "Mimo burzy sklep *działa normalnie*."}, {"context": "business", "sentence": "After the merger, it will be *business as usual* for customers.", "translation": "Po fuzji dla klientów *nic się nie zmieni*."}, {"context": "criticism", "sentence": "We can''t go back to *business as usual* after this.", "translation": "Po czymś takim nie możemy wrócić *do starego trybu*."}]'::jsonb,
        '[{"phrase": "it''s business as usual", "translation": "wszystko działa normalnie", "frequency": "VERY_COMMON"}, {"phrase": "return to business as usual", "translation": "powrót do normalności", "frequency": "COMMON"}]'::jsonb,
        NULL,
        NULL,
        'Frequent in company statements; in politics it is often used critically, e.g. about climate policy.',
        'Polish *wszystko po staremu* captures the spoken meaning.');

-- all-you-can-eat
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('1bc487fd-8aae-4ffa-8cd8-caeac700068a', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Choosing a restaurant", "Talking about buffets on holiday", "Restaurant adverts and menus"]'::jsonb,
        '["unlimited", "buffet-style"]'::jsonb,
        '[]'::jsonb,
        '["Translating it word for word as *wszystko, co możesz zjeść* — say *bufet bez limitu* or *jedzenie do woli*.", "Using it after the noun — it goes before: *an all-you-can-eat buffet*."]'::jsonb,
        '[{"context": "going out", "sentence": "Let''s try the *all-you-can-eat* sushi place.", "translation": "Chodźmy do tej knajpy z sushi *bez limitu*."}, {"context": "travel", "sentence": "The hotel has an *all-you-can-eat* breakfast buffet.", "translation": "Hotel ma śniadanie w formie bufetu *do woli*."}, {"context": "everyday", "sentence": "I always eat too much at *all-you-can-eat* restaurants.", "translation": "W restauracjach *bez limitu* zawsze przesadzam z jedzeniem."}]'::jsonb,
        '[{"phrase": "all-you-can-eat buffet", "translation": "bufet bez limitu", "frequency": "VERY_COMMON"}, {"phrase": "all-you-can-eat deal", "translation": "oferta jedzenia do woli", "frequency": "COMMON"}]'::jsonb,
        NULL,
        NULL,
        'Popular in American and Asian restaurant culture, especially for sushi, pizza and barbecue.',
        'Remember it as a whole label you''d see on a restaurant window.');

-- meagre
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('28f8f190-046a-413b-9706-efab42539f0b', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Talking about low pay or income", "Describing small food portions", "Commenting on weak results"]'::jsonb,
        '["scanty", "paltry", "modest"]'::jsonb,
        '["generous", "ample", "plentiful"]'::jsonb,
        '["Using *meagre* to describe a thin person — for people say *thin* or *skinny*; *meagre* is for amounts (*a meagre salary*).", "Treating *meager* as a typo — it is simply the American spelling of *meagre*; pick one and use it consistently.", "Using it for small objects — it describes amounts and quantities, not size."]'::jsonb,
        '[{"context": "money", "sentence": "They live on a *meagre* income.", "translation": "Utrzymują się ze *skromnych* dochodów."}, {"context": "restaurant", "sentence": "The portions were pretty *meagre* for the price.", "translation": "Porcje były dość *mizerne* jak na tę cenę."}, {"context": "work", "sentence": "Our efforts produced *meagre* results.", "translation": "Nasze starania przyniosły *mizerne* rezultaty."}]'::jsonb,
        '[{"phrase": "meagre salary", "translation": "marna pensja", "frequency": "VERY_COMMON"}, {"phrase": "meagre portions", "translation": "skromne porcje", "frequency": "COMMON"}, {"phrase": "meagre results", "translation": "mizerne wyniki", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ˈmiːɡər", "syllables": "mea-gre", "stress": 1}'::jsonb,
        '{"comparativeForm": "meagrer", "superlativeForm": "meagrest"}'::jsonb,
        'Often used with money and food — and almost always with a disappointed tone.',
        'Don''t confuse it with *merge* (łączyć) — they only look similar.');

-- firm
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('c659818a-ca32-40e4-aac9-33386432027a', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Talking about where you work", "Business news and job offers", "Professional services like law or consulting"]'::jsonb,
        '["company", "business", "practice"]'::jsonb,
        '[]'::jsonb,
        '["Using *firm* for any company — it typically refers to professional services (*law firm*, *accounting firm*), less often to factories or shops.", "Confusing the noun with the adjective *firm* (stanowczy, twardy) — context tells you which one is meant."]'::jsonb,
        '[{"context": "work", "sentence": "He works for a law *firm* in London.", "translation": "Pracuje w *kancelarii* prawnej w Londynie."}, {"context": "business", "sentence": "She started her own consulting *firm*.", "translation": "Założyła własną *firmę* doradczą."}, {"context": "business news", "sentence": "The *firm* employs over two hundred people.", "translation": "*Firma* zatrudnia ponad dwieście osób."}]'::jsonb,
        '[{"phrase": "law firm", "translation": "kancelaria prawna", "frequency": "VERY_COMMON"}, {"phrase": "accounting firm", "translation": "biuro rachunkowe", "frequency": "COMMON"}, {"phrase": "consulting firm", "translation": "firma doradcza", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "fɜːrm", "syllables": "firm", "stress": 1}'::jsonb,
        '{"pluralForm": "firms"}'::jsonb,
        'In English-speaking countries the big accounting and law companies are often called *the big firms*.',
        'Notice that *law firm* is best translated as *kancelaria*, not *firma prawnicza*.');

-- doting
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('fc4aa25a-7c25-4236-b9bc-c821cb32105d', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Describing parents and grandparents", "Talking about a loving partner", "Family stories"]'::jsonb,
        '["adoring", "devoted", "loving"]'::jsonb,
        '["cold", "indifferent", "neglectful"]'::jsonb,
        '["Using *doting* for objects or activities — it describes people who love other people (or pets).", "Forgetting the verb form *dote on*: *She dotes on her grandchildren*."]'::jsonb,
        '[{"context": "family", "sentence": "He has a *doting* grandmother who spoils him.", "translation": "Ma *zakochaną w nim po uszy* babcię, która go rozpieszcza."}, {"context": "family", "sentence": "She''s a *doting* mother of three.", "translation": "Jest *czułą* matką trójki dzieci."}, {"context": "relationships", "sentence": "Her *doting* husband brought her flowers every week.", "translation": "Jej *zapatrzony w nią* mąż co tydzień przynosił jej kwiaty."}]'::jsonb,
        '[{"phrase": "doting parents", "translation": "kochający rodzice", "frequency": "VERY_COMMON"}, {"phrase": "doting grandmother", "translation": "czuła babcia", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ˈdəʊtɪŋ", "syllables": "dot-ing", "stress": 1}'::jsonb,
        NULL,
        'Usually warm and affectionate, sometimes with a hint of spoiling.',
        'Learn it with *dote on someone* — to love and spoil them.');

-- bluntly
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('912a72e4-6dd4-4b2e-aad4-5d5c69418042', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Giving honest feedback", "Difficult conversations at work", "Introducing an uncomfortable opinion"]'::jsonb,
        '["frankly", "directly", "candidly"]'::jsonb,
        '["tactfully", "diplomatically", "gently"]'::jsonb,
        '["Using *bluntly* as an adjective — the adjective is *blunt*: *a blunt answer*, *he answered bluntly*.", "Saying *to put it blunt* — the fixed phrase is *to put it bluntly*."]'::jsonb,
        '[{"context": "work", "sentence": "To put it *bluntly*, the plan won''t work.", "translation": "Mówiąc *bez ogródek*, ten plan nie wypali."}, {"context": "conversation", "sentence": "She told him *bluntly* that he was late again.", "translation": "Powiedziała mu *prosto z mostu*, że znowu się spóźnił."}, {"context": "business", "sentence": "The report *bluntly* criticises the management.", "translation": "Raport *otwarcie* krytykuje zarząd."}]'::jsonb,
        '[{"phrase": "to put it bluntly", "translation": "mówiąc wprost", "frequency": "VERY_COMMON"}, {"phrase": "speak bluntly", "translation": "mówić bez ogródek", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ˈblʌntli", "syllables": "blunt-ly", "stress": 1}'::jsonb,
        NULL,
        'Being blunt can be seen as honest or rude; British speakers often soften it with *to be honest* first.',
        '*To put it bluntly* is a ready-made phrase — learn it as one chunk.');

-- to add insult to injury
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('db1c9042-bf1b-42e3-b453-f238d4590518', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Telling a story about a bad day", "Complaining about poor service", "Describing a series of misfortunes"]'::jsonb,
        '["to make matters worse", "on top of that"]'::jsonb,
        '[]'::jsonb,
        '["Changing the word order (*add injury to insult*) — it is a fixed phrase.", "Using it for good news — it always introduces something negative."]'::jsonb,
        '[{"context": "bad day", "sentence": "I missed the bus and, *to add insult to injury*, it started raining.", "translation": "Uciekł mi autobus, a *na domiar złego* zaczęło padać."}, {"context": "complaint", "sentence": "The food was cold, and *to add insult to injury*, they charged us extra.", "translation": "Jedzenie było zimne, a *na domiar złego* doliczyli nam opłatę."}, {"context": "sport", "sentence": "We lost the match, and *to add insult to injury*, our best player got injured.", "translation": "Przegraliśmy mecz, a *na domiar złego* nasz najlepszy zawodnik doznał kontuzji."}]'::jsonb,
        '[{"phrase": "and to add insult to injury", "translation": "a na domiar złego", "frequency": "VERY_COMMON"}]'::jsonb,
        NULL,
        NULL,
        'One of the most common English idioms for things going from bad to worse.',
        'Polish *na domiar złego* is the perfect, natural equivalent.');

-- to cap it all
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('f5a378cf-dc63-4785-95a0-c7a93ee665d5', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Telling a story about a terrible trip", "Complaining about a bad week", "Humorous storytelling"]'::jsonb,
        '["to top it all off", "on top of everything"]'::jsonb,
        '[]'::jsonb,
        '["Saying *to cap it* without *all* — the idiom is *to cap it all* (or *to top it all*).", "Using it mid-story — it normally introduces the final event."]'::jsonb,
        '[{"context": "travel", "sentence": "It rained all week and, *to cap it all*, our flight was cancelled.", "translation": "Cały tydzień padało, a *na dodatek* odwołali nam lot."}, {"context": "work", "sentence": "The project was late and, *to cap it all*, the client cancelled.", "translation": "Projekt się opóźnił, a *w dodatku* klient się wycofał."}, {"context": "bad day", "sentence": "*To cap it all*, I locked my keys in the car.", "translation": "*Na dodatek* zatrzasnąłem kluczyki w samochodzie."}]'::jsonb,
        '[{"phrase": "and to cap it all", "translation": "a na dodatek", "frequency": "COMMON"}]'::jsonb,
        NULL,
        NULL,
        'More common in British English; Americans tend to say *to top it all off*.',
        'A *cap* goes on top — this idiom puts the final thing on top of the pile.');

-- flustered
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('19fd0bdc-6404-4d24-aee3-aefba0687ef0', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Describing a stressful interview or exam", "Talking about embarrassing moments", "Stories about being in a hurry"]'::jsonb,
        '["rattled", "agitated", "flummoxed"]'::jsonb,
        '["calm", "composed", "relaxed"]'::jsonb,
        '["Confusing it with *frustrated* — *flustered* is nervous and confused, *frustrated* is annoyed because you can''t do something.", "Using it for long-term states — it describes a short, sudden moment."]'::jsonb,
        '[{"context": "interview", "sentence": "She got *flustered* when they asked about her salary.", "translation": "*Speszyła się*, gdy zapytali ją o pensję."}, {"context": "embarrassment", "sentence": "He was so *flustered* that he forgot his own phone number.", "translation": "Był tak *zdenerwowany*, że zapomniał własnego numeru telefonu."}, {"context": "reassurance", "sentence": "Don''t get *flustered* — we have plenty of time.", "translation": "Nie *denerwuj się* — mamy mnóstwo czasu."}]'::jsonb,
        '[{"phrase": "get flustered", "translation": "stracić głowę, speszyć się", "frequency": "VERY_COMMON"}, {"phrase": "look flustered", "translation": "wyglądać na zdenerwowanego", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ˈflʌstərd", "syllables": "flus-tered", "stress": 1}'::jsonb,
        NULL,
        'Often describes visible signs like blushing, stammering or dropping things.',
        'Remember the contrast: *flustered* ≠ *frustrated*.');

-- blunt
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('7ef0d8e0-a5df-43d9-8d2c-baf16cb30ab6', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Describing a knife or tool", "Giving or receiving honest feedback", "Describing someone''s communication style"]'::jsonb,
        '["dull", "direct", "frank"]'::jsonb,
        '["sharp", "tactful", "diplomatic"]'::jsonb,
        '["Using *blunt* as an adverb — the adverb is *bluntly*: *she spoke bluntly*.", "Assuming it is always rude — a *blunt* answer can simply be honest."]'::jsonb,
        '[{"context": "kitchen", "sentence": "This knife is too *blunt* to cut bread.", "translation": "Ten nóż jest zbyt *tępy*, żeby kroić chleb."}, {"context": "feedback", "sentence": "Sorry to be *blunt*, but your idea won''t work.", "translation": "Przepraszam, że mówię *wprost*, ale twój pomysł nie wypali."}, {"context": "describing people", "sentence": "He''s very *blunt* — he says exactly what he thinks.", "translation": "Jest bardzo *bezpośredni* — mówi dokładnie to, co myśli."}]'::jsonb,
        '[{"phrase": "blunt knife", "translation": "tępy nóż", "frequency": "COMMON"}, {"phrase": "blunt honesty", "translation": "brutalna szczerość", "frequency": "COMMON"}, {"phrase": "to be blunt", "translation": "mówiąc wprost", "frequency": "VERY_COMMON"}]'::jsonb,
        '{"ipa": "blʌnt", "syllables": "blunt", "stress": 1}'::jsonb,
        '{"comparativeForm": "blunter", "superlativeForm": "bluntest"}'::jsonb,
        '*Sorry to be blunt, but…* is a common way to warn people that honest criticism is coming.',
        'A *blunt* knife has no sharp edge; a *blunt* person has no polite "soft edge".');

-- out of the blue
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('fb58b90a-4b7e-418c-a225-732451467c04', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Talking about unexpected calls or messages", "Describing sudden news or offers", "Telling stories"]'::jsonb,
        '["unexpectedly", "suddenly", "out of nowhere"]'::jsonb,
        '["as expected"]'::jsonb,
        '["Dropping the article (*out of blue*) — it is always *out of THE blue*.", "Using it for things you planned — it only fits surprises."]'::jsonb,
        '[{"context": "personal", "sentence": "She called me *out of the blue* after five years.", "translation": "Zadzwoniła do mnie *ni stąd, ni zowąd* po pięciu latach."}, {"context": "work", "sentence": "The job offer came completely *out of the blue*.", "translation": "Oferta pracy przyszła zupełnie *niespodziewanie*."}, {"context": "storytelling", "sentence": "*Out of the blue*, he started crying.", "translation": "*Nagle* zaczął płakać."}]'::jsonb,
        '[{"phrase": "come out of the blue", "translation": "pojawić się znikąd", "frequency": "VERY_COMMON"}, {"phrase": "completely out of the blue", "translation": "zupełnie niespodziewanie", "frequency": "COMMON"}]'::jsonb,
        NULL,
        NULL,
        'From *a bolt from the blue* — lightning from a clear blue sky.',
        'Imagine a lightning bolt from a clear blue sky — totally unexpected.');

-- mingle
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('5c1ed01f-2e2c-4eb7-a8ea-479f21b91dc2', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Networking events and conferences", "Parties and weddings", "Describing how someone behaves socially"]'::jsonb,
        '["socialize", "circulate", "network"]'::jsonb,
        '["keep to yourself"]'::jsonb,
        '["Translating it as *mieszać* — for people it means moving around and chatting, not *mixing* things.", "Forgetting the preposition — you mingle *with* people: *mingle with the guests*."]'::jsonb,
        '[{"context": "party", "sentence": "Grab a drink and *mingle* — you''ll meet lots of nice people.", "translation": "Weź coś do picia i *pokręć się wśród gości* — poznasz mnóstwo fajnych ludzi."}, {"context": "networking", "sentence": "At conferences I find it hard to *mingle* with strangers.", "translation": "Na konferencjach trudno mi *nawiązywać rozmowy* z obcymi."}, {"context": "event", "sentence": "The host *mingled* with the guests all evening.", "translation": "Gospodarz przez cały wieczór *krążył wśród gości*."}]'::jsonb,
        '[{"phrase": "mingle with guests", "translation": "rozmawiać z gośćmi", "frequency": "VERY_COMMON"}, {"phrase": "mingle at a party", "translation": "integrować się na imprezie", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ˈmɪŋɡl", "syllables": "min-gle", "stress": 1}'::jsonb,
        NULL,
        'At English-speaking events *mingling* is expected — staying only with people you know can seem unfriendly.',
        'Polish has no single verb for it; think *krążyć wśród gości i zagadywać*.');

-- hover
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('cadf2f13-c0b9-46dd-b803-e4e11fdbe024', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Describing helicopters, drones or insects", "Talking about computer mouse actions", "Describing someone waiting around nervously"]'::jsonb,
        '["float", "linger", "hang around"]'::jsonb,
        '["land", "leave"]'::jsonb,
        '["Using *hover* for things floating on water — that is *float*; *hover* is in the air.", "Forgetting *over/above*: *The drone hovered above the field*."]'::jsonb,
        '[{"context": "everyday", "sentence": "A drone was *hovering* above the house.", "translation": "Nad domem *unosił się* dron."}, {"context": "computers", "sentence": "*Hover* over the icon to see more options.", "translation": "*Najedź kursorem* na ikonę, żeby zobaczyć więcej opcji."}, {"context": "home", "sentence": "Stop *hovering* — I''ll call you when dinner''s ready.", "translation": "Przestań *kręcić się koło mnie* — zawołam cię, jak obiad będzie gotowy."}]'::jsonb,
        '[{"phrase": "hover over", "translation": "najechać na, unosić się nad", "frequency": "VERY_COMMON"}, {"phrase": "hover around", "translation": "kręcić się wokół", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ˈhʌvər", "syllables": "hov-er", "stress": 1}'::jsonb,
        NULL,
        'In computing, *hovering* means moving the mouse over something without clicking.',
        'Think of a hummingbird or a helicopter staying still in the air.');

-- pleasantry
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('1532ee60-8388-4882-8f22-82dad6ac96e5', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Business meetings and calls", "Meeting neighbours or acquaintances", "Describing small talk"]'::jsonb,
        '["small talk", "polite remark", "courtesy"]'::jsonb,
        '[]'::jsonb,
        '["Using the singular — it is almost always plural: *exchange pleasantries*.", "Thinking it means a pleasant event — it is only about polite words."]'::jsonb,
        '[{"context": "business", "sentence": "We *exchanged pleasantries* before the meeting started.", "translation": "Przed spotkaniem *wymieniliśmy uprzejmości*."}, {"context": "meeting", "sentence": "After a few *pleasantries*, they got down to business.", "translation": "Po kilku *grzecznościowych zdaniach* przeszli do konkretów."}, {"context": "negotiation", "sentence": "Let''s skip the *pleasantries* and talk about the contract.", "translation": "Darujmy sobie *uprzejmości* i porozmawiajmy o umowie."}]'::jsonb,
        '[{"phrase": "exchange pleasantries", "translation": "wymienić uprzejmości", "frequency": "VERY_COMMON"}, {"phrase": "skip the pleasantries", "translation": "pominąć uprzejmości", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ˈplezəntri", "syllables": "pleas-ant-ry", "stress": 1}'::jsonb,
        '{"pluralForm": "pleasantries"}'::jsonb,
        'Typical English small talk — weather, travel, weekend plans — is exactly what *pleasantries* are.',
        'Learn the whole phrase *exchange pleasantries*.');

-- pick someone's brain
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('a9e851e6-c7b8-4d70-a285-5d8584735f28', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Asking an expert or colleague for advice", "Networking and informal meetings", "Starting a conversation at work"]'::jsonb,
        '["ask for advice", "consult"]'::jsonb,
        '[]'::jsonb,
        '["Taking it literally — it is a friendly request for advice, not something unpleasant.", "Forgetting the possessive — it is *pick your brain* / *pick her brain*, not *pick you*."]'::jsonb,
        '[{"context": "personal", "sentence": "Can I *pick your brain* about moving to Berlin?", "translation": "Mogę cię *podpytać* o przeprowadzkę do Berlina?"}, {"context": "networking", "sentence": "I''d love to *pick your brain* about marketing over coffee.", "translation": "Chętnie *zasięgnąłbym twojej rady* w sprawie marketingu przy kawie."}, {"context": "shopping", "sentence": "She *picked the engineer''s brain* before buying the car.", "translation": "Zanim kupiła auto, *wypytała* znajomego inżyniera."}]'::jsonb,
        '[{"phrase": "pick your brain about something", "translation": "podpytać cię o coś", "frequency": "VERY_COMMON"}]'::jsonb,
        NULL,
        '{"irregularForms": {"pastTense": "picked", "presentParticiple": "picking"}}'::jsonb,
        'A casual, friendly phrase often used in emails and networking: *Could I pick your brain for 15 minutes?*',
        'Imagine picking ideas out of someone''s head like fruit from a tree.');

-- redundancy
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('1703194b-10c6-4b7e-a973-d9ec3b04b0ed', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["News about company layoffs", "Talking about your own job situation", "Technical talk about backup systems"]'::jsonb,
        '["layoff", "dismissal", "duplication"]'::jsonb,
        '["hiring", "recruitment"]'::jsonb,
        '["Translating it as *nadmiar* in work contexts — *redundancy* there means *zwolnienie (z przyczyn ekonomicznych)*.", "Confusing *being made redundant* with being fired for bad performance — redundancy is about the job, not the person."]'::jsonb,
        '[{"context": "news", "sentence": "The factory announced fifty *redundancies*.", "translation": "Fabryka ogłosiła pięćdziesiąt *zwolnień*."}, {"context": "work", "sentence": "He was made *redundant* after twenty years.", "translation": "Po dwudziestu latach *stracił pracę w wyniku redukcji etatów*."}, {"context": "technology", "sentence": "The system has built-in *redundancy* in case a server fails.", "translation": "System ma wbudowaną *redundancję* na wypadek awarii serwera."}]'::jsonb,
        '[{"phrase": "redundancy pay", "translation": "odprawa", "frequency": "VERY_COMMON"}, {"phrase": "be made redundant", "translation": "zostać zwolnionym (redukcja etatów)", "frequency": "VERY_COMMON"}, {"phrase": "voluntary redundancy", "translation": "dobrowolne odejście", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "rɪˈdʌndənsi", "syllables": "re-dun-dan-cy", "stress": 2}'::jsonb,
        '{"pluralForm": "redundancies"}'::jsonb,
        'In the UK *redundancy* is the standard word for layoffs; Americans usually say *layoff*.',
        'Remember the phrase *be made redundant* — it''s how British people say they lost their job.');

-- upward trend
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('427c18c0-c8c3-4245-ba82-a63eee6dd987', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Presenting sales or business results", "Talking about prices or statistics", "Discussing market data"]'::jsonb,
        '["rise", "growth", "upturn"]'::jsonb,
        '["downward trend", "decline"]'::jsonb,
        '["Saying *an upward trend of sales* — the usual preposition is *in*: *an upward trend in sales*.", "Using *upwards trend* — the adjective form is *upward*."]'::jsonb,
        '[{"context": "business", "sentence": "Sales show a clear *upward trend* this quarter.", "translation": "Sprzedaż wykazuje w tym kwartale wyraźną *tendencję wzrostową*."}, {"context": "society", "sentence": "There''s an *upward trend* in remote work.", "translation": "Praca zdalna jest *w trendzie wzrostowym*."}, {"context": "economy", "sentence": "House prices continue their *upward trend*.", "translation": "Ceny mieszkań wciąż *rosną*."}]'::jsonb,
        '[{"phrase": "show an upward trend", "translation": "wykazywać tendencję wzrostową", "frequency": "VERY_COMMON"}, {"phrase": "steady upward trend", "translation": "stały wzrost", "frequency": "COMMON"}]'::jsonb,
        NULL,
        '{"pluralForm": "upward trends"}'::jsonb,
        'A typical phrase in reports, presentations and financial news.',
        'Learn it in a pair: *upward trend* ↔ *downward trend*.');

-- tipping point
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('fdd5aa1a-79d9-490a-8aab-c028fc98e350', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Discussing climate and social change", "Business and marketing trends", "Talking about personal decisions"]'::jsonb,
        '["turning point", "critical point", "threshold"]'::jsonb,
        '[]'::jsonb,
        '["Using it for any important moment — it means the point where change becomes unstoppable.", "Saying *tip point* — the form is *tipping point*."]'::jsonb,
        '[{"context": "environment", "sentence": "Scientists warn we are close to a climate *tipping point*.", "translation": "Naukowcy ostrzegają, że jesteśmy blisko klimatycznego *punktu krytycznego*."}, {"context": "politics", "sentence": "The scandal was the *tipping point* for his resignation.", "translation": "Skandal był *kroplą, która przelała czarę* i doprowadził do jego rezygnacji."}, {"context": "technology", "sentence": "Electric cars may soon reach a *tipping point*.", "translation": "Samochody elektryczne mogą wkrótce osiągnąć *punkt zwrotny*."}]'::jsonb,
        '[{"phrase": "reach a tipping point", "translation": "osiągnąć punkt krytyczny", "frequency": "VERY_COMMON"}, {"phrase": "climate tipping point", "translation": "klimatyczny punkt krytyczny", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ˈtɪpɪŋ pɔɪnt", "syllables": "tip-ping point", "stress": 1}'::jsonb,
        '{"pluralForm": "tipping points"}'::jsonb,
        'Popularised by Malcolm Gladwell''s book *The Tipping Point* (2000).',
        'Picture a glass tipping over — a tiny push and suddenly it falls.');

-- to be on the up
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('06f55bd8-3153-45ce-ae43-bbe05c59f3b3', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Talking about someone''s career", "Describing a recovering business or economy", "Sports commentary"]'::jsonb,
        '["be improving", "be on the rise"]'::jsonb,
        '["be in decline", "be on the way down"]'::jsonb,
        '["Confusing it with American *on the up and up*, which means *honest, legal*.", "Using it without *be* — say *things are on the up*, not *things on the up*."]'::jsonb,
        '[{"context": "work", "sentence": "Her career is really *on the up*.", "translation": "Jej kariera naprawdę *idzie w górę*."}, {"context": "life", "sentence": "Things are *on the up* after a tough year.", "translation": "Po trudnym roku sprawy *mają się coraz lepiej*."}, {"context": "sport", "sentence": "The club has been *on the up* since the new coach arrived.", "translation": "Odkąd przyszedł nowy trener, klub *jest na fali wznoszącej*."}]'::jsonb,
        '[{"phrase": "things are on the up", "translation": "jest coraz lepiej", "frequency": "COMMON"}, {"phrase": "on the up and up (US)", "translation": "uczciwy, legalny", "frequency": "OCCASIONAL"}]'::jsonb,
        NULL,
        NULL,
        'Mostly British English and quite informal.',
        '*Up* = better: if something is *on the up*, it''s going upwards.');

-- albeit
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('978a5f4e-666f-4523-8fe4-423b95e884e9', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Formal writing and reports", "Academic essays", "Careful, balanced opinions"]'::jsonb,
        '["although", "though", "even if"]'::jsonb,
        '[]'::jsonb,
        '["Following it with a full clause (*albeit it was late*) — it is followed by a word or short phrase: *albeit late*, *albeit slowly*.", "Overusing it in casual speech — in conversation *though* sounds more natural."]'::jsonb,
        '[{"context": "conversation", "sentence": "He agreed, *albeit* reluctantly.", "translation": "Zgodził się, *choć* niechętnie."}, {"context": "report", "sentence": "The results were positive, *albeit* small.", "translation": "Wyniki były pozytywne, *aczkolwiek* niewielkie."}, {"context": "work", "sentence": "We finished the project, *albeit* two weeks late.", "translation": "Skończyliśmy projekt, *choć* z dwutygodniowym opóźnieniem."}]'::jsonb,
        '[{"phrase": "albeit slowly", "translation": "choć powoli", "frequency": "COMMON"}, {"phrase": "albeit briefly", "translation": "choć krótko", "frequency": "COMMON"}, {"phrase": "albeit reluctantly", "translation": "choć niechętnie", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ɔːlˈbiːɪt", "syllables": "al-be-it", "stress": 2}'::jsonb,
        NULL,
        'From Middle English *al be it* — "although it be". It sounds formal and is common in writing.',
        'Use the pattern *X, albeit Y* where Y is just an adjective or adverb.');

-- cumbersome
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('21106d94-d625-41ae-9e61-e3c27b42ee58', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Complaining about bureaucracy or procedures", "Describing heavy luggage or furniture", "Reviewing software or tools"]'::jsonb,
        '["unwieldy", "bulky", "awkward"]'::jsonb,
        '["handy", "convenient", "streamlined"]'::jsonb,
        '["Translating it as *niewygodny* for things like chairs — *cumbersome* is about being hard to handle or use, not uncomfortable to sit on.", "Spelling it *cumbersom* or *cumbersume*."]'::jsonb,
        '[{"context": "bureaucracy", "sentence": "The application process is long and *cumbersome*.", "translation": "Proces składania wniosku jest długi i *uciążliwy*."}, {"context": "moving", "sentence": "We carried the *cumbersome* sofa up five floors.", "translation": "Wnieśliśmy tę *nieporęczną* kanapę na piąte piętro."}, {"context": "technology", "sentence": "The old software is *cumbersome* to use.", "translation": "Stary program jest *toporny* w obsłudze."}]'::jsonb,
        '[{"phrase": "cumbersome process", "translation": "uciążliwy proces", "frequency": "VERY_COMMON"}, {"phrase": "cumbersome procedure", "translation": "skomplikowana procedura", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ˈkʌmbərsəm", "syllables": "cum-ber-some", "stress": 1}'::jsonb,
        '{"comparativeForm": "more cumbersome", "superlativeForm": "most cumbersome"}'::jsonb,
        'Often used in workplace complaints about outdated systems or paperwork.',
        'It *encumbers* you — slows you down and gets in the way.');

-- infringe
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('98c2e981-3904-48dc-b892-978716d95a57', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Legal texts and contracts", "Copyright and intellectual property", "Debates about privacy and rights"]'::jsonb,
        '["violate", "breach", "encroach on"]'::jsonb,
        '["respect", "comply with", "obey"]'::jsonb,
        '["Adding *on* with laws or copyright — *infringe a copyright*; use *infringe on/upon* with rights or freedom: *infringe on privacy*.", "Using it in everyday speech for small rules — *break a rule* is more natural."]'::jsonb,
        '[{"context": "legal", "sentence": "The app may *infringe* the company''s copyright.", "translation": "Aplikacja może *naruszać* prawa autorskie firmy."}, {"context": "rights", "sentence": "These cameras *infringe on* our privacy.", "translation": "Te kamery *naruszają* naszą prywatność."}, {"context": "business", "sentence": "They were accused of *infringing* the patent.", "translation": "Oskarżono ich o *naruszenie* patentu."}]'::jsonb,
        '[{"phrase": "infringe copyright", "translation": "naruszać prawa autorskie", "frequency": "VERY_COMMON"}, {"phrase": "infringe on someone''s rights", "translation": "naruszać czyjeś prawa", "frequency": "VERY_COMMON"}, {"phrase": "patent infringement", "translation": "naruszenie patentu", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ɪnˈfrɪndʒ", "syllables": "in-fringe", "stress": 2}'::jsonb,
        NULL,
        'Most common in legal and intellectual property contexts; the noun is *infringement*.',
        'Learn the noun too: *copyright infringement* — you will see it on many websites.');

-- permissible
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('dbeed3d7-ba5c-4a77-9512-9972336ebd8a', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Official rules and regulations", "Workplace policies", "Legal and formal documents"]'::jsonb,
        '["allowed", "permitted", "acceptable"]'::jsonb,
        '["forbidden", "prohibited", "banned"]'::jsonb,
        '["Using it in casual speech — *allowed* sounds much more natural in conversation.", "Spelling it *permissable* — the correct ending is *-ible*."]'::jsonb,
        '[{"context": "school", "sentence": "Is it *permissible* to bring food into the exam?", "translation": "Czy *wolno* wnosić jedzenie na egzamin?"}, {"context": "regulations", "sentence": "The *permissible* limit for this chemical is very low.", "translation": "*Dopuszczalny* limit tej substancji jest bardzo niski."}, {"context": "rules", "sentence": "Smoking is not *permissible* anywhere on the premises.", "translation": "Palenie nie jest *dozwolone* nigdzie na terenie obiektu."}]'::jsonb,
        '[{"phrase": "permissible limit", "translation": "dopuszczalny limit", "frequency": "VERY_COMMON"}, {"phrase": "legally permissible", "translation": "prawnie dopuszczalny", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "pərˈmɪsəbl", "syllables": "per-mis-si-ble", "stress": 2}'::jsonb,
        NULL,
        'Typical of official notices and regulations.',
        '*permit* → *permissible*: something you are permitted to do.');

-- admissible
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('1b7f72a2-ee9a-4258-9681-05ef20728de6', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Courtrooms and legal dramas", "Legal articles and news", "Formal decisions about what is accepted"]'::jsonb,
        '["acceptable", "allowable", "valid"]'::jsonb,
        '["inadmissible", "unacceptable"]'::jsonb,
        '["Confusing it with *permissible* — *admissible* is mainly about evidence or arguments being accepted, not general permission.", "Spelling it *admissable* — the correct ending is *-ible*."]'::jsonb,
        '[{"context": "legal", "sentence": "The recording is not *admissible* in court.", "translation": "To nagranie nie jest *dopuszczalne* jako dowód w sądzie."}, {"context": "courtroom", "sentence": "The judge ruled the evidence *admissible*.", "translation": "Sędzia uznał dowód za *dopuszczalny*."}, {"context": "formal", "sentence": "Only *admissible* claims will be reviewed.", "translation": "Rozpatrywane będą tylko *dopuszczalne* roszczenia."}]'::jsonb,
        '[{"phrase": "admissible evidence", "translation": "dopuszczalny dowód", "frequency": "VERY_COMMON"}, {"phrase": "admissible in court", "translation": "dopuszczalny w sądzie", "frequency": "VERY_COMMON"}]'::jsonb,
        '{"ipa": "ədˈmɪsəbl", "syllables": "ad-mis-si-ble", "stress": 2}'::jsonb,
        NULL,
        'Very frequent in courtroom dramas; its opposite *inadmissible* is just as common.',
        '*admit* → *admissible*: the court can admit it.');

-- spur
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('bd980035-cab9-4b51-9aa0-2c429ec3ca3c', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Talking about motivation", "Economic news about growth", "Spontaneous decisions (spur of the moment)"]'::jsonb,
        '["incentive", "stimulus", "motivation"]'::jsonb,
        '["deterrent", "obstacle"]'::jsonb,
        '["Saying *in the spur of the moment* — the phrase is *on the spur of the moment*.", "Forgetting that *spur* is also a verb: *The prize spurred her on*."]'::jsonb,
        '[{"context": "motivation", "sentence": "The award was a *spur* to work even harder.", "translation": "Nagroda była *bodźcem* do jeszcze cięższej pracy."}, {"context": "travel", "sentence": "We booked the trip on the *spur* of the moment.", "translation": "Zarezerwowaliśmy wycieczkę *pod wpływem chwili*."}, {"context": "economy", "sentence": "Lower taxes could be a *spur* to growth.", "translation": "Niższe podatki mogą być *impulsem* do wzrostu."}]'::jsonb,
        '[{"phrase": "on the spur of the moment", "translation": "pod wpływem chwili", "frequency": "VERY_COMMON"}, {"phrase": "a spur to growth", "translation": "impuls do wzrostu", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "spɜːr", "syllables": "spur", "stress": 1}'::jsonb,
        '{"pluralForm": "spurs"}'::jsonb,
        'Literally, a *spur* is a small metal point on a rider''s boot used to make a horse go faster.',
        'Learn the very common *on the spur of the moment* — a spontaneous decision.');

-- brick-and-mortar
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('69b5ddeb-e688-476e-9e90-e0b223be0c52', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Comparing online and physical shops", "Retail and e-commerce news", "Talking about where you prefer to shop"]'::jsonb,
        '["physical", "high-street", "offline"]'::jsonb,
        '["online", "e-commerce"]'::jsonb,
        '["Using it as a noun (*a brick-and-mortar*) — it describes a noun: *a brick-and-mortar store*.", "Using it for buildings in general — it is about businesses, in contrast to online ones."]'::jsonb,
        '[{"context": "business", "sentence": "They opened their first *brick-and-mortar* store after years online.", "translation": "Po latach działania w sieci otworzyli pierwszy sklep *stacjonarny*."}, {"context": "news", "sentence": "*Brick-and-mortar* retail is struggling.", "translation": "Handel *stacjonarny* przeżywa trudności."}, {"context": "personal", "sentence": "I prefer *brick-and-mortar* bookshops to ordering online.", "translation": "Wolę księgarnie *stacjonarne* od zamawiania przez internet."}]'::jsonb,
        '[{"phrase": "brick-and-mortar store", "translation": "sklep stacjonarny", "frequency": "VERY_COMMON"}, {"phrase": "brick-and-mortar retail", "translation": "handel stacjonarny", "frequency": "COMMON"}]'::jsonb,
        NULL,
        NULL,
        'The phrase became popular with the rise of e-commerce, to contrast physical shops with online ones.',
        'Bricks and mortar are what real buildings are made of — the opposite of a website.');

-- certainty
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('c2c51bc2-d3a1-4bfb-83f3-488dd85159f8', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Expressing how sure you are", "Discussing predictions and decisions", "Formal or analytical writing"]'::jsonb,
        '["confidence", "assurance", "sureness"]'::jsonb,
        '["doubt", "uncertainty"]'::jsonb,
        '["Saying *with a certainty* — the phrase is *with certainty* (no article).", "Confusing *certainty* with *certificate* — they share a root but mean different things."]'::jsonb,
        '[{"context": "conversation", "sentence": "I can''t say with *certainty* when he''ll be back.", "translation": "Nie mogę powiedzieć *na pewno*, kiedy wróci."}, {"context": "economy", "sentence": "There''s no *certainty* that prices will fall.", "translation": "Nie ma *pewności*, że ceny spadną."}, {"context": "sport", "sentence": "Victory seemed a *certainty* until the last minute.", "translation": "Zwycięstwo wydawało się *pewne* aż do ostatniej minuty."}]'::jsonb,
        '[{"phrase": "with certainty", "translation": "z pewnością, na pewno", "frequency": "VERY_COMMON"}, {"phrase": "absolute certainty", "translation": "absolutna pewność", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ˈsɜːrtnti", "syllables": "cer-tain-ty", "stress": 1}'::jsonb,
        '{"pluralForm": "certainties"}'::jsonb,
        'Famous saying: *Nothing is certain except death and taxes* (Benjamin Franklin).',
        '*certain* + *-ty* → the noun, just like *safe → safety*.');

-- palpable
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('fe21fe74-b689-4dfb-a2be-1e4d0f85c828', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Describing tension or excitement in a room", "Writing about emotions and atmosphere", "News and sports commentary"]'::jsonb,
        '["tangible", "noticeable", "obvious"]'::jsonb,
        '["imperceptible", "subtle", "intangible"]'::jsonb,
        '["Using it for physical objects — it is mostly used for feelings and atmosphere: *palpable tension*.", "Translating it as *namacalny* for emotions — *wyczuwalny* sounds more natural there."]'::jsonb,
        '[{"context": "atmosphere", "sentence": "The tension in the room was *palpable*.", "translation": "Napięcie w pokoju było *wyczuwalne*."}, {"context": "event", "sentence": "There was *palpable* excitement before the concert.", "translation": "Przed koncertem panowało *wyczuwalne* podekscytowanie."}, {"context": "emotions", "sentence": "Her relief was *palpable* when she heard the news.", "translation": "Jej ulga była *wyraźnie widoczna*, gdy usłyszała wiadomość."}]'::jsonb,
        '[{"phrase": "palpable tension", "translation": "wyczuwalne napięcie", "frequency": "VERY_COMMON"}, {"phrase": "palpable excitement", "translation": "wyczuwalne podekscytowanie", "frequency": "COMMON"}, {"phrase": "palpable sense of relief", "translation": "wyraźna ulga", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ˈpælpəbl", "syllables": "pal-pa-ble", "stress": 1}'::jsonb,
        NULL,
        'From Latin *palpare* — "to touch". Writers use it for very strong atmosphere.',
        'Collocation to memorise: *the tension was palpable*.');

-- lull
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('34ad29df-1ab0-4450-9852-2239aeea4e9c', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Talking about babies and sleep", "Describing calming sounds or music", "Warning about a false sense of security"]'::jsonb,
        '["soothe", "calm", "rock to sleep"]'::jsonb,
        '["wake", "alarm", "agitate"]'::jsonb,
        '["Forgetting the pattern *lull someone to sleep* or *lull someone into a false sense of security*.", "Confusing the verb with the noun *a lull* (a quiet pause) — both exist."]'::jsonb,
        '[{"context": "everyday", "sentence": "The sound of the rain *lulled* me to sleep.", "translation": "Szum deszczu *ukołysał* mnie do snu."}, {"context": "family", "sentence": "She sang softly to *lull* the baby to sleep.", "translation": "Śpiewała cicho, żeby *ukołysać* dziecko do snu."}, {"context": "games", "sentence": "Don''t let the easy first level *lull* you into a false sense of security.", "translation": "Nie daj się *uśpić* łatwemu pierwszemu poziomowi."}]'::jsonb,
        '[{"phrase": "lull someone to sleep", "translation": "ukołysać kogoś do snu", "frequency": "VERY_COMMON"}, {"phrase": "lull into a false sense of security", "translation": "uśpić czyjąś czujność", "frequency": "COMMON"}, {"phrase": "a lull in the conversation", "translation": "chwila ciszy w rozmowie", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "lʌl", "syllables": "lull", "stress": 1}'::jsonb,
        NULL,
        'The noun appears in *the lull before the storm* — the calm before trouble begins.',
        '*Lullaby* (kołysanka) comes from the same word.');

-- ruminate
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('f6151878-c7b8-4141-8885-cb12552dbfd7', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Talking about stress, worry or overthinking", "Psychology and wellbeing", "Describing careful decision-making"]'::jsonb,
        '["ponder", "brood", "dwell on"]'::jsonb,
        '["ignore", "let go"]'::jsonb,
        '["Forgetting the preposition — *ruminate on/about/over something*.", "Using it casually — it sounds formal; in conversation *overthink* or *dwell on* is more common."]'::jsonb,
        '[{"context": "wellbeing", "sentence": "I tend to *ruminate* on my mistakes at night.", "translation": "W nocy często *roztrząsam* swoje błędy."}, {"context": "decisions", "sentence": "He *ruminated* over the offer for weeks before deciding.", "translation": "Przez tygodnie *rozmyślał* nad ofertą, zanim się zdecydował."}, {"context": "advice", "sentence": "Try not to *ruminate* — talk to someone instead.", "translation": "Staraj się nie *rozpamiętywać* — lepiej z kimś porozmawiaj."}]'::jsonb,
        '[{"phrase": "ruminate on", "translation": "rozmyślać nad", "frequency": "COMMON"}, {"phrase": "ruminate about the past", "translation": "rozpamiętywać przeszłość", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ˈruːmɪneɪt", "syllables": "ru-mi-nate", "stress": 1}'::jsonb,
        NULL,
        'Psychologists use *rumination* for repetitive negative thinking linked to anxiety.',
        'Cows *ruminate* (chew the same food again and again) — people ruminate on the same thoughts.');

-- chamomile
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('3c1038c6-e343-4bbb-8e92-554e5fc02707', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Ordering tea in a café", "Talking about sleep and relaxation", "Gardening and herbs"]'::jsonb,
        '["camomile"]'::jsonb,
        '[]'::jsonb,
        '["Pronouncing the *ch* as in *chair* — it is pronounced like *k*: KAM-uh-mile.", "Saying *chamomile tea* is only for illness — in English-speaking countries it is a common everyday evening drink."]'::jsonb,
        '[{"context": "everyday", "sentence": "I drink *chamomile* tea before bed.", "translation": "Przed snem piję herbatę z *rumianku*."}, {"context": "café", "sentence": "Could I have a *chamomile*, please?", "translation": "Poproszę *rumianek*."}, {"context": "gardening", "sentence": "She grows *chamomile* and mint in her garden.", "translation": "Uprawia w ogrodzie *rumianek* i miętę."}]'::jsonb,
        '[{"phrase": "chamomile tea", "translation": "herbata rumiankowa", "frequency": "VERY_COMMON"}, {"phrase": "a cup of chamomile", "translation": "filiżanka rumianku", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ˈkæməmaɪl", "syllables": "cham-o-mile", "stress": 1}'::jsonb,
        '{"pluralForm": "chamomiles"}'::jsonb,
        'A very popular herbal tea in the UK and US, usually drunk in the evening to relax.',
        'British spelling *camomile* shows the real pronunciation — it starts with a *k* sound.');

-- vocational school
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('87ed9d59-5da6-4b76-a0e8-21c314589ea3', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Talking about education and career paths", "Job interviews and CVs", "Discussing the education system"]'::jsonb,
        '["trade school", "technical college"]'::jsonb,
        '["university", "academic school"]'::jsonb,
        '["Using *professional school* — in English that often means law or medical school; for practical trades say *vocational school* or *trade school*.", "Spelling it *vocal school* — *vocal* is about the voice."]'::jsonb,
        '[{"context": "career", "sentence": "He went to *vocational school* to become an electrician.", "translation": "Poszedł do *szkoły zawodowej*, żeby zostać elektrykiem."}, {"context": "education", "sentence": "*Vocational schools* offer a lot of hands-on training.", "translation": "*Szkoły zawodowe* oferują dużo zajęć praktycznych."}, {"context": "personal story", "sentence": "After *vocational school*, she opened her own bakery.", "translation": "Po *szkole zawodowej* otworzyła własną piekarnię."}]'::jsonb,
        '[{"phrase": "attend vocational school", "translation": "chodzić do szkoły zawodowej", "frequency": "COMMON"}, {"phrase": "vocational training", "translation": "kształcenie zawodowe", "frequency": "VERY_COMMON"}]'::jsonb,
        '{"ipa": "vəʊˈkeɪʃənl skuːl", "syllables": "vo-ca-tion-al school", "stress": 2}'::jsonb,
        '{"pluralForm": "vocational schools"}'::jsonb,
        'In the US *trade school* is a common informal name; in the UK people often say *college* or *technical college*.',
        '*Vocation* means a job you are trained for — *vocational* = related to a job.');

-- shed
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('0a2fb6cb-eba1-404e-8888-f810be0f2f2d', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Talking about your garden or backyard", "Describing where tools or bikes are kept", "DIY and home projects"]'::jsonb,
        '["hut", "shack", "outbuilding"]'::jsonb,
        '[]'::jsonb,
        '["Confusing the noun *a shed* (szopa) with the verb *to shed* (zrzucać, np. skórę) — they look the same but mean different things.", "Using *shed* for a large building — a *shed* is small and simple; a big farm building is a *barn*."]'::jsonb,
        '[{"context": "home", "sentence": "The tools are kept in the *shed*.", "translation": "Narzędzia trzymamy w *szopie*."}, {"context": "DIY", "sentence": "We built a small garden *shed* last summer.", "translation": "Zeszłego lata postawiliśmy małą *szopę* w ogrodzie."}, {"context": "everyday", "sentence": "Can you put your bike in the *shed*?", "translation": "Możesz wstawić rower do *szopy*?"}]'::jsonb,
        '[{"phrase": "garden shed", "translation": "szopa ogrodowa", "frequency": "VERY_COMMON"}, {"phrase": "tool shed", "translation": "szopa na narzędzia", "frequency": "COMMON"}, {"phrase": "bike shed", "translation": "wiata na rowery", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ʃed", "syllables": "shed", "stress": 1}'::jsonb,
        '{"pluralForm": "sheds"}'::jsonb,
        'In the UK the garden *shed* is a cultural symbol — a quiet place where people do hobbies or DIY.',
        'Be ready for the verb too: *shed light on* (rzucić światło na) and *shed tears* (ronić łzy) use the same spelling.');

-- contingency
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('14091f1c-1d09-4e39-89f6-1ea6624cfe52', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Planning a project or an event", "Discussing budgets and risk at work", "Preparing for a trip that might not go as planned"]'::jsonb,
        '["eventuality", "possibility", "backup"]'::jsonb,
        '[]'::jsonb,
        '["Spelling it *contigency* — there is an *n* before the *g*: con-TIN-gen-cy.", "Using *contingency* for something that already happened — it is about what *might* happen, so you plan *for* a contingency.", "Translating *contingency plan* word for word — in Polish it is simply *plan awaryjny* or *plan B*."]'::jsonb,
        '[{"context": "work", "sentence": "We need a *contingency* plan in case the supplier lets us down.", "translation": "Potrzebujemy *planu awaryjnego* na wypadek, gdyby dostawca nas zawiódł."}, {"context": "budget", "sentence": "Keep some money aside for *contingencies*.", "translation": "Odłóż trochę pieniędzy na *nieprzewidziane wydatki*."}, {"context": "planning", "sentence": "The team is prepared for every *contingency*.", "translation": "Zespół jest przygotowany na każdą *ewentualność*."}]'::jsonb,
        '[{"phrase": "contingency plan", "translation": "plan awaryjny", "frequency": "VERY_COMMON"}, {"phrase": "contingency fund", "translation": "rezerwa finansowa", "frequency": "COMMON"}, {"phrase": "prepare for every contingency", "translation": "przygotować się na każdą ewentualność", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "kənˈtɪndʒənsi", "syllables": "con-tin-gen-cy", "stress": 2}'::jsonb,
        '{"pluralForm": "contingencies"}'::jsonb,
        'In business and project management a *contingency* budget is a standard line item, often around 10% of the total.',
        'Learn it through its most common partner: *contingency plan* = *plan B*.');

-- suffocating
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('7aee9a44-abab-4a28-80b6-59b6473454d3', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Complaining about heat or a stuffy room", "Describing a controlling relationship or job", "Talking about crowds and small spaces"]'::jsonb,
        '["stifling", "oppressive", "airless"]'::jsonb,
        '["fresh", "airy", "liberating"]'::jsonb,
        '["Using *suffocated* when you mean the feeling the place causes — the room is *suffocating*; you feel *suffocated*.", "Taking it only literally — people very often use it for relationships, rules or jobs that feel too controlling."]'::jsonb,
        '[{"context": "weather", "sentence": "The heat in the tram was *suffocating*.", "translation": "Upał w tramwaju był *duszący*."}, {"context": "family", "sentence": "Her parents'' rules felt *suffocating*.", "translation": "Zasady jej rodziców były *przytłaczające*."}, {"context": "work", "sentence": "I left that job because the atmosphere was *suffocating*.", "translation": "Odszedłem z tej pracy, bo atmosfera była *nie do wytrzymania*."}]'::jsonb,
        '[{"phrase": "suffocating heat", "translation": "duszący upał", "frequency": "VERY_COMMON"}, {"phrase": "suffocating atmosphere", "translation": "przytłaczająca atmosfera", "frequency": "COMMON"}, {"phrase": "feel suffocating", "translation": "być przytłaczającym", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "ˈsʌfəkeɪtɪŋ", "syllables": "suf-fo-cat-ing", "stress": 1}'::jsonb,
        '{"comparativeForm": "more suffocating", "superlativeForm": "most suffocating"}'::jsonb,
        'A close, more formal synonym is *stifling*, which is especially common with heat: *a stifling summer afternoon*.',
        'Polish *duszący* works for both the heat and the relationship — just like *suffocating*.');

-- verbatim
INSERT INTO public.word_details (word_id, user_id, use_cases, synonyms, antonyms, common_mistakes,
                                 example_sentences, collocations, pronunciation, grammar,
                                 cultural_notes, learning_tips)
VALUES ('53c48d3f-1712-4b07-aaf8-9eaf58b42126', 'aab3de51-c9ed-4b53-b6ba-927f0bf567e4',
        '["Quoting someone in an article or report", "Copying text from a document or contract", "Talking about meeting notes and transcripts"]'::jsonb,
        '["word for word", "exactly", "literally"]'::jsonb,
        '["in your own words", "loosely"]'::jsonb,
        '["Using *verbatim* to mean *very* (*it was verbatim the best*) — it only means *word for word*.", "Adding a preposition (*quote it in verbatim*) — say *quote it verbatim*."]'::jsonb,
        '[{"context": "conversation", "sentence": "She repeated his answer *verbatim*.", "translation": "Powtórzyła jego odpowiedź *słowo w słowo*."}, {"context": "school", "sentence": "Please don''t copy the text *verbatim* — rewrite it in your own words.", "translation": "Nie przepisuj tekstu *dosłownie* — napisz go własnymi słowami."}, {"context": "news", "sentence": "The article quotes the minister *verbatim*.", "translation": "Artykuł cytuje ministra *dosłownie*."}]'::jsonb,
        '[{"phrase": "quote verbatim", "translation": "cytować dosłownie", "frequency": "VERY_COMMON"}, {"phrase": "verbatim transcript", "translation": "dosłowny zapis", "frequency": "COMMON"}, {"phrase": "repeat verbatim", "translation": "powtórzyć słowo w słowo", "frequency": "COMMON"}]'::jsonb,
        '{"ipa": "vɜːrˈbeɪtɪm", "syllables": "ver-ba-tim", "stress": 2}'::jsonb,
        NULL,
        'From Latin *verbum* (word). Courts, journalists and researchers use *verbatim* for exact records of what was said.',
        '*Verb-* is Latin for *word* — *verbatim* is "word by word".');
