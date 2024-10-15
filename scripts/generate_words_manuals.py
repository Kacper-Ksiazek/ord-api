import argparse
import json
import os
import sys
import time

import requests
from dotenv import load_dotenv

# ------
# 1. Load environment variables from .env file
# ------

load_dotenv()

API_URL = os.getenv("API_URL")
USER_EMAIL = os.getenv("USER_EMAIL")
USER_PASSWORD = os.getenv("USER_PASSWORD")

# ------
# 2. Verify positional script arguments
# ------

parser = argparse.ArgumentParser(description="Process some arguments for level and language.")
parser.add_argument('--level', required=True,
                    help="The level to be used in the request (e.g., beginner, intermediate, advanced).")
parser.add_argument('--language', required=True,
                    help="The language to be used in the request (e.g., english, spanish).")

# Parse the arguments
args = parser.parse_args()

# Extracting level and language from parsed arguments
PROFICIENCY_LEVEL = args.level
LANGUAGE = args.language

# ------
# 3. Authenticate and retrieve token
# ------

auth_url = f"{API_URL}/auth/login"
auth_data = {
    "email": USER_EMAIL,
    "password": USER_PASSWORD
}

print(auth_data)

try:
    auth_response = requests.post(
        url=auth_url,
        json=auth_data,
        headers={"Content-Type": "application/json"}
    )
    auth_response.raise_for_status()  # Raise an exception for HTTP errors
    cookies = auth_response.cookies
except requests.exceptions.RequestException as e:
    print(f"Authentication failed: {e}")
    sys.exit(1)

# Headers with the authentication token
headers = {
    "Content-Type": "application/json",
}

# ------
# 4. Send POST requests and save responses to a JSON file
# ------

# Array to send in POST requests
words = [
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
        "down the rabbit hole"
]


# URL for POST requests
GENERATE_WORDS_URL = f"{API_URL}/openai/generate-word-manual?language={LANGUAGE}&originalLanguage={LANGUAGE}&translateTo=POLISH"

# List to store the responses
responses = []

# Send POST requests for each word in the array with additional level and language
for word in words:
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
        print(f"SUCCESS: {word} - {delta_time_in_seconds}s")
    except requests.exceptions.RequestException as e:
        print(f"ERROR: {word} - {e}")

# Save all responses to words_mock.json
print("Saving responses to words_mock.json")

try:
    with open(
            file="words_mock.json",
            mode="w",
            encoding="utf-8"
    ) as file:
        json.dump(
            responses,
            file,
            indent=4,
            ensure_ascii=False
        )
except Exception as e:
    print(f"Error saving responses to words_mock.json: {e}")
