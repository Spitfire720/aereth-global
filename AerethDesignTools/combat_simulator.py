import argparse
from dataclasses import dataclass

from common import RULES, player_profile_for_level, expected_player_dps, mitigation, difficulty_target, format_number


@dataclass
class MobProfile:
    level: int
    difficulty: str
    hp: float
    damage_per_hit: float
    attack_interval: float
    defense: float


def generate_default_mob(player_level: int, mob_level: int, difficulty: str) -> MobProfile:
    player = player_profile_for_level(player_level)
    player_dps = expected_player_dps(player)
    target = difficulty_target(difficulty)

    target_ttk = target["target_ttk_seconds"]
    hp = player_dps * target_ttk

    player_hp = player["derived"]["max_health"]
    target_hp_loss = player_hp * (target["target_player_hp_loss_percent"] / 100)

    attack_interval = 2.0
    expected_hits_taken = max(1, target_ttk / attack_interval)
    damage_after_mitigation = target_hp_loss / expected_hits_taken

    player_defense = player["derived"]["defense"]
    softener = RULES["combat"]["player"]["armor_mitigation_softener"]
    raw_damage = damage_after_mitigation * ((softener + player_defense) / softener)

    level_delta = mob_level - player_level
    hp *= 1 + (level_delta * 0.035)
    raw_damage *= 1 + (level_delta * 0.03)

    defense = max(0, mob_level * 0.65)

    return MobProfile(
        level=mob_level,
        difficulty=difficulty,
        hp=round(max(1, hp), 2),
        damage_per_hit=round(max(1, raw_damage), 2),
        attack_interval=attack_interval,
        defense=round(defense, 2),
    )


def simulate(player_level: int, mob: MobProfile, erasure_pressure: float = 0.0) -> dict:
    player = player_profile_for_level(player_level, erasure_pressure)
    player_derived = player["derived"]

    player_hp = player_derived["max_health"]
    mob_hp = mob.hp
    seconds = 0.0

    player_attack_interval = RULES["combat"]["player"]["base_attack_interval_seconds"]
    skill_interval = RULES["combat"]["player"]["skill_interval_seconds"]
    skill_multiplier = RULES["combat"]["player"]["skill_damage_multiplier"]
    softener = RULES["combat"]["player"]["armor_mitigation_softener"]

    next_player_attack = 0.0
    next_player_skill = skill_interval
    next_mob_attack = 0.0

    attack_power = player_derived["attack_power"]
    crit_chance = player_derived["crit_chance"]
    crit_damage = player_derived["crit_damage"]
    expected_hit = attack_power * ((1 - crit_chance) + crit_chance * crit_damage)

    player_damage_done = 0.0
    mob_damage_done = 0.0

    while seconds <= 600 and player_hp > 0 and mob_hp > 0:
        next_event = min(next_player_attack, next_player_skill, next_mob_attack)
        seconds = max(seconds, next_event)

        if abs(seconds - next_player_attack) < 0.0001:
            dealt = mitigation(expected_hit, mob.defense, softener)
            mob_hp -= dealt
            player_damage_done += dealt
            next_player_attack += player_attack_interval

        if mob_hp <= 0:
            break

        if abs(seconds - next_player_skill) < 0.0001:
            dealt = mitigation(expected_hit * skill_multiplier, mob.defense, softener)
            mob_hp -= dealt
            player_damage_done += dealt
            next_player_skill += skill_interval

        if mob_hp <= 0:
            break

        if abs(seconds - next_mob_attack) < 0.0001:
            taken = mitigation(mob.damage_per_hit, player_derived["defense"], softener)
            player_hp -= taken
            mob_damage_done += taken
            next_mob_attack += mob.attack_interval

    outcome = "player_wins" if mob_hp <= 0 and player_hp > 0 else "mob_wins"
    if player_hp <= 0 and mob_hp <= 0:
        outcome = "double_ko"

    return {
        "outcome": outcome,
        "duration_seconds": round(seconds, 2),
        "player_level": player_level,
        "player_phase": player["phase"],
        "player_max_hp": player_derived["max_health"],
        "player_remaining_hp": round(max(0, player_hp), 2),
        "player_hp_lost_percent": round((1 - max(0, player_hp) / player_derived["max_health"]) * 100, 2),
        "player_expected_dps_before_mitigation": expected_player_dps(player),
        "player_damage_done": round(player_damage_done, 2),
        "mob_level": mob.level,
        "mob_difficulty": mob.difficulty,
        "mob_hp": mob.hp,
        "mob_remaining_hp": round(max(0, mob_hp), 2),
        "mob_damage_per_hit_raw": mob.damage_per_hit,
        "mob_attack_interval": mob.attack_interval,
        "mob_defense": mob.defense,
        "mob_damage_done": round(mob_damage_done, 2),
    }


def main():
    parser = argparse.ArgumentParser(description="Simulate a basic Aereth player vs mob fight.")
    parser.add_argument("--player-level", type=int, required=True)
    parser.add_argument("--mob-level", type=int, required=True)
    parser.add_argument("--difficulty", type=str, default="normal")
    parser.add_argument("--mob-hp", type=float, default=None)
    parser.add_argument("--mob-damage", type=float, default=None)
    parser.add_argument("--mob-defense", type=float, default=None)
    parser.add_argument("--mob-attack-interval", type=float, default=2.0)
    parser.add_argument("--erasure-pressure", type=float, default=0.0)
    args = parser.parse_args()

    default_mob = generate_default_mob(args.player_level, args.mob_level, args.difficulty)

    mob = MobProfile(
        level=args.mob_level,
        difficulty=args.difficulty,
        hp=args.mob_hp if args.mob_hp is not None else default_mob.hp,
        damage_per_hit=args.mob_damage if args.mob_damage is not None else default_mob.damage_per_hit,
        attack_interval=args.mob_attack_interval,
        defense=args.mob_defense if args.mob_defense is not None else default_mob.defense,
    )

    result = simulate(args.player_level, mob, args.erasure_pressure)

    print("Aereth Combat Simulation")
    print("=" * 72)
    for key, value in result.items():
        print(f"{key}: {format_number(value)}")


if __name__ == "__main__":
    main()
