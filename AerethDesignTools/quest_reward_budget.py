import argparse

from common import RULES, xp_required_for_level, phase_for_level, format_number


def quest_budget(level: int, minutes: float, quest_type: str) -> dict:
    quest_type = quest_type.lower().strip()
    rewards = RULES["quest_rewards"]
    xp_rates = rewards["xp_per_minute_by_type"]
    multipliers = rewards["reward_multipliers"]

    if quest_type not in xp_rates:
        raise ValueError(f"Unknown quest type '{quest_type}'. Valid: {', '.join(xp_rates.keys())}")

    xp_to_next = xp_required_for_level(level)
    xp_percent_per_minute = xp_rates[quest_type]
    multiplier = multipliers[quest_type]

    recommended_xp = round(xp_to_next * xp_percent_per_minute * minutes * multiplier)
    recommended_coins = round(rewards["coins_per_minute_base"] * level * minutes * multiplier)

    reward_styles = {
        "story": "higher XP, light coins, lore/unlock reward",
        "side": "balanced XP and coins",
        "daily": "lower XP, repeatable, material/coin-focused",
        "exploration": "moderate XP, discovery/fragments chance",
        "elite": "high XP, higher risk, stronger loot chance",
    }

    return {
        "level": level,
        "phase": phase_for_level(level),
        "quest_type": quest_type,
        "minutes": minutes,
        "xp_to_next_level": xp_to_next,
        "recommended_xp": recommended_xp,
        "recommended_xp_percent_of_level": round((recommended_xp / xp_to_next) * 100, 2) if xp_to_next else 0,
        "recommended_coins": recommended_coins,
        "reward_style": reward_styles.get(quest_type, "standard"),
    }


def main():
    parser = argparse.ArgumentParser(description="Budget XP/coin rewards for an Aereth quest.")
    parser.add_argument("--level", type=int, required=True)
    parser.add_argument("--minutes", type=float, required=True)
    parser.add_argument("--quest-type", type=str, default="side")
    args = parser.parse_args()

    result = quest_budget(args.level, args.minutes, args.quest_type)

    print("Aereth Quest Reward Budget")
    print("=" * 72)
    for key, value in result.items():
        print(f"{key}: {format_number(value)}")


if __name__ == "__main__":
    main()
