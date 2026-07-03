import argparse
import csv
from pathlib import Path

from common import xp_required_for_level, cumulative_xp_to_reach_level, phase_for_level


def main():
    parser = argparse.ArgumentParser(description="Generate Aereth XP curve.")
    parser.add_argument("--from-level", type=int, default=1)
    parser.add_argument("--to-level", type=int, default=60)
    parser.add_argument("--csv", type=str, default=None, help="Optional output CSV path.")
    args = parser.parse_args()

    rows = []
    for level in range(args.from_level, args.to_level + 1):
        rows.append({
            "level": level,
            "phase": phase_for_level(level),
            "xp_to_next": xp_required_for_level(level),
            "cumulative_xp_to_reach": cumulative_xp_to_reach_level(level),
        })

    print("Aereth XP Curve")
    print("=" * 72)
    print(f"{'Level':>5} | {'Phase':<12} | {'XP to next':>12} | {'Total XP to reach':>18}")
    print("-" * 72)

    for row in rows:
        print(
            f"{row['level']:>5} | "
            f"{row['phase']:<12} | "
            f"{row['xp_to_next']:>12} | "
            f"{row['cumulative_xp_to_reach']:>18}"
        )

    if args.csv:
        out = Path(args.csv)
        with out.open("w", newline="", encoding="utf-8") as f:
            writer = csv.DictWriter(f, fieldnames=rows[0].keys())
            writer.writeheader()
            writer.writerows(rows)
        print(f"\nCSV written to: {out}")


if __name__ == "__main__":
    main()
