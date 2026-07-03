import argparse
from typing import Dict

from common import RULES, xp_required_for_level, phase_for_level, format_number


def get_target_minutes(level: int, preset: str) -> float:
    targets = RULES["progression"]["discovery_time_targets_minutes"]
    preset_targets: Dict[str, float] = targets[preset]
    key = str(level)

    if key in preset_targets:
        return float(preset_targets[key])

    highest_known = max(int(k) for k in preset_targets.keys())
    highest_value = float(preset_targets[str(highest_known)])
    extra_levels = max(0, level - highest_known)
    return highest_value + extra_levels * 5


def simulate(start_level: int, end_level: int, preset: str) -> dict:
    targets = RULES["progression"]["discovery_time_targets_minutes"]

    if preset not in targets:
        raise ValueError(f"Unknown preset '{preset}'. Valid: {', '.join(targets.keys())}")

    rows = []
    total_minutes = 0.0

    for level in range(start_level, end_level):
        xp_to_next = xp_required_for_level(level)
        target_minutes = get_target_minutes(level, preset)
        required_xp_per_minute = xp_to_next / target_minutes if target_minutes > 0 else 0

        total_minutes += target_minutes

        rows.append({
            "level": level,
            "phase": phase_for_level(level),
            "xp_to_next": xp_to_next,
            "target_minutes": round(target_minutes, 2),
            "required_xp_per_minute": round(required_xp_per_minute, 2),
            "cumulative_minutes": round(total_minutes, 2),
        })

    return {
        "preset": preset,
        "rows": rows,
        "total_minutes": round(total_minutes, 2),
        "total_hours": round(total_minutes / 60, 2),
    }


def print_report(result: dict):
    print("Aereth Time-to-Level Target Model")
    print("=" * 94)
    print(f"Preset: {result['preset']}")
    print()
    print(f"{'Level':>5} | {'Phase':<12} | {'XP next':>10} | {'Target min':>10} | {'Req XP/min':>12} | {'Cum min':>10}")
    print("-" * 94)

    for row in result["rows"]:
        print(
            f"{row['level']:>5} | "
            f"{row['phase']:<12} | "
            f"{row['xp_to_next']:>10} | "
            f"{format_number(row['target_minutes']):>10} | "
            f"{format_number(row['required_xp_per_minute']):>12} | "
            f"{format_number(row['cumulative_minutes']):>10}"
        )

    print("-" * 94)
    print(f"Total time: {format_number(result['total_minutes'])} minutes")
    print(f"Total time: {format_number(result['total_hours'])} hours")
    print()
    print("Interpretation:")
    print("- Target min is the intended time to complete that level.")
    print("- Req XP/min is how much XP content must provide per minute at that level.")
    print("- This model avoids the old flat pacing where every level took the exact same time.")


def main():
    parser = argparse.ArgumentParser(description="Estimate Aereth target time-to-level pacing.")
    parser.add_argument("--start-level", type=int, default=1)
    parser.add_argument("--end-level", type=int, default=15)
    parser.add_argument(
        "--preset",
        type=str,
        default="balanced",
        choices=RULES["progression"]["discovery_time_targets_minutes"].keys(),
        help="Pacing preset.",
    )
    args = parser.parse_args()

    result = simulate(
        start_level=args.start_level,
        end_level=args.end_level,
        preset=args.preset,
    )

    print_report(result)


if __name__ == "__main__":
    main()
