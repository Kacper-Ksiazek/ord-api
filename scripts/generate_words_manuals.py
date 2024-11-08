import json
import os
import sys
import time
from math import floor

import requests
from dotenv import load_dotenv

# TODO: Implement a way to store output files in separate directories!

OUTPUT_FILE_NAMES = {
    "words": "ai_generated_words_manuals.json",
    "statistics": "gpt_tokens_used_to_generate_ai_words_manuals.json"
}

LANGUAGE = "ENGLISH"
PROFICIENCY_LEVEL = "C1"
WORDS = [
    "boardwalk",
    "beehive",
    "mutter",
    "sluggish",
    "lethargic",
    "ephemeral",
    "transient",
    "fleeting",
    "depletes",
    "don't shoot them down in flames",
    "fruit of thought",
    "down the rabbit hole",
    "uheard",
    "well-to-do",
    "across-the-board",
    "run-of-the-mill",
    "state-of-the-art",
    "off-the-cuff",
    "larger-than-life",
    "middle-of-the-road",
    "thought-out",
    "paid-for",
    "take-it-or-leave-it",
    "business-as-usual",
    "all-you-can-eat",
    "mearge",
    "firm",
    "doting",
    "bluntly",
    "to add insult to injury",
    "to cap it all",
    "flusterted",
    "blunt",
    "out of the blue",
    "mingle",
    "hover",
    "pleasantry",
    "pick someones's brain",
    "go through",
    "redundancy",
    "upward trend",
    "tipping point",
    "to be on the up",
    "albeit",
    "cumbersome",
    "infringe",
    "permissible",
    "admissible",
    "spur",
    "brick-and-mortar",
    "certanity",
    "palpable",
    "lull",
    "ruminate",
    "chamomile",
    "vocational school",
    "shed",
]


def convert_seconds_into_time(_seconds):
    seconds = floor(_seconds) % 60
    minutes = floor(seconds / 60)
    hours = floor(minutes / 60)

    human_readable_time = ""

    if hours:
        human_readable_time += f"{hours}hours "
    if minutes:
        human_readable_time += f"{minutes}min "

    human_readable_time += f"{seconds}sec"

    return {
        "hours": hours,
        "minutes": minutes,
        "seconds": seconds,
        "human_readable_time": human_readable_time
    }


def generate_words_manuals(words):
    # ------
    # 1. Load environment variables from .env file
    # ------

    load_dotenv()

    API_URL = os.getenv("API_URL")
    USER_EMAIL = os.getenv("USER_EMAIL")
    USER_PASSWORD = os.getenv("USER_PASSWORD")
    OUTPUT_DIR = os.getenv("OUTPUT_DIR") or "./output"

    # ------
    # 2. Authenticate and retrieve token
    # ------

    auth_url = f"{API_URL}/auth/login"
    auth_data = {
        "email": USER_EMAIL,
        "password": USER_PASSWORD
    }

    try:
        auth_response = requests.post(
            url=auth_url,
            json=auth_data,
            headers={"Content-Type": "application/json"}
        )
        auth_response.raise_for_status()  # Raise an exception for HTTP errors
        cookies = auth_response.cookies
        print("🔓 Authentication successful")

    except requests.exceptions.RequestException as e:
        print(f"Authentication failed: {e}")
        sys.exit(1)

    # Headers with the authentication token
    headers = {
        "Content-Type": "application/json",
    }

    # ------
    # 3. Send GET requests and save responses to a JSON file
    # ------

    # URL for POST requests
    GENERATE_WORDS_URL = f"{API_URL}/openai/generate-word-manual?language={LANGUAGE}&originalLanguage={LANGUAGE}&translateTo=POLISH"

    # List to store the responses
    responses = []

    print(f"\nGenerating manuals for {len(words)} words in {PROFICIENCY_LEVEL} {LANGUAGE.lower()}\n")

    beginning_time = time.time()

    # Send POST requests for each word in the array with additional level and language
    for _i, word in enumerate(words):
        i = _i + 1

        try:
            time_start = time.time()

            generated_manual = requests.get(
                url=GENERATE_WORDS_URL + f"&word={word}",
                headers=headers,
                cookies=cookies
            )
            generated_manual.raise_for_status()  # Check for HTTP errors

            # Add the response to the list
            responses.append(generated_manual.json())

            delta_time = time.time() - time_start
            delta_time_in_seconds = round(delta_time, 2)

            # Sleep for 1 second to avoid rate limiting
            print(f"{i}. ✅ {word} - {delta_time_in_seconds}s")
        except requests.exceptions.RequestException as e:
            print(f"{i}. ❌ {word} - {e}")

    # Calculate the total time
    generation_duration = convert_seconds_into_time(time.time() - beginning_time)["human_readable_time"]

    print(f"\n⏰ Total time: {generation_duration}s")

    # ------
    # 4. Send GET request to get statistics
    # ------

    statistics_url = f"{API_URL}/gpt-tokens-consumption/words-detailed"
    statistics_response = requests.get(
        url=statistics_url,
        headers=headers,
        cookies=cookies
    )
    statistics_response.raise_for_status()  # Check for HTTP errors

    # ------
    # 5. Save responses to a JSON file
    # ------
    print(f"\n📝 Saving responses to a JSON files in {OUTPUT_DIR} directory...")

    # 5.1 - Save words to a JSON file
    word_file_name = OUTPUT_FILE_NAMES["words"]
    print(f"\nSaving responses to {word_file_name} file... \n")

    try:
        with open(
                file=os.path.expanduser(f"{OUTPUT_DIR}/{word_file_name}"),
                mode="w",
                encoding="utf-8"
        ) as file:
            json.dump(
                responses,
                file,
                indent=4,
                ensure_ascii=False
            )

        print("✅ Responses saved successfully")
    except Exception as e:
        print(f"❌ Failed to save responses to words_mock.json: {word_file_name}")

    # 5.2 - Save statistics to a JSON file
    statistics_file_name = OUTPUT_FILE_NAMES["statistics"]
    print(f"\nSaving statistics to {statistics_file_name} file... \n")

    try:
        with open(
                file=os.path.expanduser(f"{OUTPUT_DIR}/{statistics_file_name}"),
                mode="w",
                encoding="utf-8"
        ) as file:
            json.dump(
                statistics_response.json(),
                file,
                indent=4,
                ensure_ascii=False
            )

        print("✅ Statistics saved successfully")
    except Exception as e:
        print(f"❌ Failed to save statistics to words_mock.json: {statistics_file_name}")


generate_words_manuals(WORDS)
