import json
import math
from pathlib import Path
from typing import Dict, Any


ROOT = Path(__file__).resolve().parent
RULES_PATH = ROOT / "design_rules.json"


def load_rules() -> Dict[str, Any]:
    with RULES_PATH.open("r", encoding="utf-8") as f:
        return json.load(f)


RULES = load_rules()


def clamp(value: float, minimum: float, maximum: float) -> float:
    return max(minimum, min(maximum, value))


def xp_required_for_level(level: int) -> int:
    progression = RULES["progression"]
    level_cap = progression["level_cap"]
    if level >= level_cap:
        return 0
    formula = progression["xp_formula"]
    return math.floor(formula["base"] * (level ** formula["exponent"]))


def cumulative_xp_to_reach_level(level: int) -> int:
    if level <= 1:
        return 0
    return sum(xp_required_for_level(lvl) for lvl in range(1, level))


def phase_for_level(level: int) -> str:
    bands = RULES["progression"]["phase_bands"]
    for phase, bounds in bands.items():
        if bounds["min_level"] <= level <= bounds["max_level"]:
            return phase
    return "unknown"


def default_stats_for_level(level: int) -> Dict[str, float]:
    starting = RULES["stats"]["starting"]
    growth = RULES["stats"]["per_level_growth"]
    extra_levels = max(0, level - 1)
    return {stat: round(starting[stat] + growth.get(stat, 0) * extra_levels, 2) for stat in starting}


def derived_stats(stats: Dict[str, float], erasure_pressure: float = 0.0) -> Dict[str, float]:
    vitality = stats.get("vitality", 0)
    strength = stats.get("strength", 0)
    dexterity = stats.get("dexterity", 0)
    intelligence = stats.get("intelligence", 0)
    willpower = stats.get("willpower", 0)
    endurance = stats.get("endurance", 0)

    max_health = 100 + vitality * 12 + endurance * 6
    attack_power = 10 + strength * 2
    defense = endurance * 1.5
    crit_chance = 0.05 + dexterity * 0.002
    crit_damage = 1.5 + dexterity * 0.01
    magic_power = intelligence * 2
    resistance = willpower * 1.25
    movement_speed = 1.0
    stability = clamp(100 - erasure_pressure + willpower * 0.5, 0, 100)

    return {
        "max_health": round(max_health, 2),
        "attack_power": round(attack_power, 2),
        "defense": round(defense, 2),
        "crit_chance": round(crit_chance, 4),
        "crit_damage": round(crit_damage, 4),
        "magic_power": round(magic_power, 2),
        "resistance": round(resistance, 2),
        "movement_speed": round(movement_speed, 2),
        "stability": round(stability, 2),
    }


def player_profile_for_level(level: int, erasure_pressure: float = 0.0) -> Dict[str, Any]:
    stats = default_stats_for_level(level)
    derived = derived_stats(stats, erasure_pressure)
    return {"level": level, "phase": phase_for_level(level), "stats": stats, "derived": derived}


def mitigation(raw_damage: float, defense: float, softener: float = 100.0) -> float:
    multiplier = softener / (softener + max(0, defense))
    return max(1.0, raw_damage * multiplier)


def expected_player_dps(player: Dict[str, Any]) -> float:
    combat_rules = RULES["combat"]["player"]
    derived = player["derived"]

    attack_power = derived["attack_power"]
    crit_chance = derived["crit_chance"]
    crit_damage = derived["crit_damage"]

    basic_interval = combat_rules["base_attack_interval_seconds"]
    skill_multiplier = combat_rules["skill_damage_multiplier"]
    skill_interval = combat_rules["skill_interval_seconds"]

    expected_basic_hit = attack_power * ((1 - crit_chance) + crit_chance * crit_damage)
    basic_dps = expected_basic_hit / basic_interval

    skill_hit = expected_basic_hit * skill_multiplier
    skill_dps = skill_hit / skill_interval

    return round(basic_dps + skill_dps, 2)


def difficulty_target(difficulty: str) -> Dict[str, float]:
    targets = RULES["combat"]["difficulty_targets"]
    key = difficulty.lower().strip()
    if key not in targets:
        raise ValueError(f"Unknown difficulty '{difficulty}'. Valid: {', '.join(targets.keys())}")
    return targets[key]


def format_number(value) -> str:
    if isinstance(value, int):
        return str(value)
    if isinstance(value, float) and abs(value - round(value)) < 0.0001:
        return str(int(round(value)))
    if isinstance(value, float):
        return f"{value:.2f}"
    return str(value)
