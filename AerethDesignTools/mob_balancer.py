import argparse

from common import player_profile_for_level, expected_player_dps, difficulty_target, RULES, format_number


def balance_mob(player_level: int, mob_level: int, difficulty: str) -> dict:
    player = player_profile_for_level(player_level)
    dps = expected_player_dps(player)
    target = difficulty_target(difficulty)

    target_ttk = target["target_ttk_seconds"]
    hp = dps * target_ttk

    level_delta = mob_level - player_level
    hp *= 1 + (level_delta * 0.035)

    player_hp = player["derived"]["max_health"]
    target_hp_loss = player_hp * (target["target_player_hp_loss_percent"] / 100)

    attack_interval = 2.0
    expected_hits = max(1, target_ttk / attack_interval)
    mitigated_damage_per_hit = target_hp_loss / expected_hits

    player_defense = player["derived"]["defense"]
    softener = RULES["combat"]["player"]["armor_mitigation_softener"]
    raw_damage = mitigated_damage_per_hit * ((softener + player_defense) / softener)
    raw_damage *= 1 + (level_delta * 0.03)

    mob_defense = max(0, mob_level * 0.65)

    return {
        "player_level": player_level,
        "mob_level": mob_level,
        "difficulty": difficulty,
        "target_ttk_seconds": target_ttk,
        "target_player_hp_loss_percent": target["target_player_hp_loss_percent"],
        "recommended_mob_hp": round(max(1, hp), 2),
        "recommended_mob_damage_per_hit_raw": round(max(1, raw_damage), 2),
        "recommended_mob_attack_interval": attack_interval,
        "recommended_mob_defense": round(mob_defense, 2),
        "player_expected_dps": dps,
        "player_max_hp": player_hp,
        "notes": "Use this as a starting point, then test with combat_simulator.py.",
    }


def main():
    parser = argparse.ArgumentParser(description="Suggest Aereth mob stats from target difficulty.")
    parser.add_argument("--player-level", type=int, required=True)
    parser.add_argument("--mob-level", type=int, required=True)
    parser.add_argument("--difficulty", type=str, default="normal")
    args = parser.parse_args()

    result = balance_mob(args.player_level, args.mob_level, args.difficulty)

    print("Aereth Mob Balancer")
    print("=" * 72)
    for key, value in result.items():
        print(f"{key}: {format_number(value)}")


if __name__ == "__main__":
    main()
