import json
from tools.github_snapshot import fetch_snapshot_file


def check_manifest() -> str:
    raw = fetch_snapshot_file("manifest.json")

    if raw.startswith("[NOT_FOUND]"):
        return "manifest.json was not found. Snapshot is not readable."

    try:
        manifest = json.loads(raw.lstrip("\ufeff"))
    except Exception as exc:
        return f"manifest.json exists but could not be parsed: {exc}"

    project = manifest.get("project", "unknown")
    generated_local = manifest.get("generated_at_local", "unknown")
    generated_utc = manifest.get("generated_at_utc", "unknown")
    file_count = manifest.get("file_count", "unknown")

    return (
        f"Snapshot project: {project}\n"
        f"Generated local: {generated_local}\n"
        f"Generated UTC: {generated_utc}\n"
        f"File count: {file_count}"
    )


def check_worldguard_hardening() -> str:
    raw = fetch_snapshot_file("plugins/WorldGuard/config.yml")

    if raw.startswith("[NOT_FOUND]"):
        return "WorldGuard config was not found."

    checks = {
        "block-tnt: true": "TNT ignition blocked",
        "block-tnt-block-damage: true": "TNT block damage blocked",
        "block-lighter: true": "Lighters blocked",
        "disable-lava-fire-spread: true": "Lava fire spread disabled",
        "disable-all-fire-spread: true": "All fire spread disabled",
        "block-creeper-block-damage: true": "Creeper block damage blocked",
        "disable-enderman-griefing: true": "Enderman griefing disabled",
    }

    results = []
    for needle, label in checks.items():
        status = "PASS" if needle in raw else "FAIL"
        results.append(f"{status}: {label}")

    return "\n".join(results)


def check_fragmentengine_config() -> str:
    raw = fetch_snapshot_file("plugins/FragmentEngine/config.yml")

    if raw.startswith("[NOT_FOUND]"):
        return "FragmentEngine config was not found."

    results = []

    if "world: creator_world" in raw:
        results.append("PASS: FragmentEngine lobby points to creator_world.")
    else:
        results.append("FAIL: FragmentEngine lobby does not point to creator_world.")

    if "agent-support:" in raw:
        results.append("PASS: FragmentEngine agent-support config exists.")
    else:
        results.append("WARN: FragmentEngine agent-support config not found. Build 1 may not be installed yet.")

    return "\n".join(results)


def check_worldfracture_zones() -> str:
    raw = fetch_snapshot_file("plugins/WorldFracture/zones.yml")

    if raw.startswith("[NOT_FOUND]"):
        return "WorldFracture zones.yml was not found."

    if 'world: "main"' in raw:
        return "PASS: WorldFracture zones target main."
    return "FAIL: WorldFracture zones may not target main."


def check_bluemap() -> str:
    raw = fetch_snapshot_file("plugins/BlueMap/maps/world.conf")

    if raw.startswith("[NOT_FOUND]"):
        return "BlueMap world.conf was not found."

    if 'world: "main"' in raw:
        return "PASS: BlueMap overworld points to main."
    return "FAIL: BlueMap overworld does not point to main."


def run_basic_aereth_audit() -> str:
    sections = [
        ("Manifest", check_manifest()),
        ("WorldGuard", check_worldguard_hardening()),
        ("FragmentEngine", check_fragmentengine_config()),
        ("WorldFracture", check_worldfracture_zones()),
        ("BlueMap", check_bluemap()),
    ]

    output = []
    for title, body in sections:
        output.append(f"## {title}\n{body}")

    return "\n\n".join(output)