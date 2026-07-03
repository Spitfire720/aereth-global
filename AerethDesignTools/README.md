# AerethDesignTools

Local design and balance tools for the Aereth Minecraft MMORPG project.

These tools do not call the OpenAI API.
They do not need paid credits.
They are deterministic calculators/simulators for game design work.

## Included tools

- `xp_curve.py` - Generates Aereth XP requirements and cumulative XP.
- `combat_simulator.py` - Simulates a simple player vs mob fight.
- `mob_balancer.py` - Suggests mob HP/damage values based on target fight length and difficulty.
- `quest_reward_budget.py` - Suggests XP/coin/reward budgets for quests.
- `loot_budget.py` - Suggests item power budgets by level and rarity.
- `time_to_level.py` - Estimates level pacing using target minutes per level.
- `design_rules.json` - Shared rules and constants used by the tools.
- `AERETH_BALANCE_BASELINE.md` - Current working balance baseline.

## Requirements

Python 3.11+ recommended.
No external packages required.

## Quick start

Open PowerShell in this folder:

```powershell
cd "C:\Users\Bernardo\Desktop\Aereth global\AerethDesignTools"
```

Run:

```powershell
python xp_curve.py --from-level 1 --to-level 20
python mob_balancer.py --player-level 10 --mob-level 10 --difficulty easy
python combat_simulator.py --player-level 10 --mob-level 10 --difficulty easy
python quest_reward_budget.py --level 12 --minutes 8 --quest-type story
python loot_budget.py --level 20 --rarity rare
python time_to_level.py --start-level 1 --end-level 15 --preset balanced
```

## Discovery pacing targets

The current Discovery phase baseline is:

- Story route 1-15: 391 min / 6.52h
- Balanced route 1-15: 415 min / 6.92h
- Grind route 1-15: 507 min / 8.45h

## Recommended Aereth workflow

1. Use these tools to generate numbers.
2. Paste the output into ChatGPT.
3. Adjust the design.
4. Convert approved values into FragmentEngine, MythicMobs, BetonQuest, WorldFracture, or item configs.

Rule:
LLM suggests. Calculator checks. Human approves.
