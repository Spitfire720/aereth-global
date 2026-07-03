import os
import requests
from dotenv import load_dotenv

load_dotenv()

OWNER = os.getenv("GITHUB_OWNER", "Spitfire720")
REPO = os.getenv("GITHUB_REPO", "aereth-snapshot")
BRANCH = os.getenv("GITHUB_BRANCH", "main")
TOKEN = os.getenv("GITHUB_TOKEN")

BASE_RAW_URL = f"https://raw.githubusercontent.com/{OWNER}/{REPO}/{BRANCH}"


def fetch_snapshot_file(path: str) -> str:
    """
    Fetches a text file from the Aereth GitHub snapshot.
    Use paths like:
    - manifest.json
    - file-tree.txt
    - plugin-list.txt
    - plugins/FragmentEngine/config.yml
    """
    url = f"{BASE_RAW_URL}/{path.lstrip('/')}"
    headers = {}

    if TOKEN:
        headers["Authorization"] = f"Bearer {TOKEN}"

    response = requests.get(url, headers=headers, timeout=20)

    if response.status_code == 404:
        return f"[NOT_FOUND] {path}"

    response.raise_for_status()
    return response.text


def fetch_core_snapshot() -> dict:
    """
    Fetches the core files the agent should inspect first.
    """
    files = {}
    for path in ["manifest.json", "file-tree.txt", "plugin-list.txt"]:
        files[path] = fetch_snapshot_file(path)
    return files