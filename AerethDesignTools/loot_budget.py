import argparse

from common import RULES, phase_for_level, format_number


def loot_budget(level: int, rarity: str) -> dict:
    rarity = rarity.lower().strip()
    rarity_multipliers = RULES["loot"]["rarity_multipliers"]

    if rarity not in rarity_multipliers:
        raise ValueError(f"Unknown rarity '{rarity}'. Valid: {', '.join(rarity_multipliers.keys())}")

    base_power = level * 10 + 25
    multiplier = rarity_multipliers[rarity]
    total_budget = round(base_power * multiplier, 2)

    main_stat = round(total_budget * 0.50, 2)
    secondary_stat = round(total_budget * 0.25, 2)
    utility = round(total_budget * 0.15, 2)
    special_effect = round(total_budget * 0.10, 2)

    return {
        "level": level,
        "phase": phase_for_level(level),
        "rarity": rarity,
        "base_power": base_power,
        "rarity_multiplier": multiplier,
        "total_power_budget": total_budget,
        "suggested_main_stat_budget": main_stat,
        "suggested_secondary_stat_budget": secondary_stat,
        "suggested_utility_budget": utility,
        "suggested_special_effect_budget": special_effect,
        "notes": "Use this as a power budget, not as direct damage/armor values.",
    }


def main():
    parser = argparse.ArgumentParser(description="Generate an Aereth loot power budget.")
    parser.add_argument("--level", type=int, required=True)
    parser.add_argument("--rarity", type=str, default="common")
    args = parser.parse_args()

    result = loot_budget(args.level, args.rarity)

    print("Aereth Loot Budget")
    print("=" * 72)
    for key, value in result.items():
        print(f"{key}: {format_number(value)}")


if __name__ == "__main__":
    main()
