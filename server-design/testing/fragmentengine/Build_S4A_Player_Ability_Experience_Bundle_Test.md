# Build S4A Test Plan

## Commands

```text
/aereth abilitysummary SpitFire720
/aereth abilityresources SpitFire720
/aereth abilitysync
/aereth setdisciplinerank SpitFire720 1
/aereth setdisciplinerank SpitFire720 4
/aereth adddisciplinexp SpitFire720 1000
```

## Expected results

- `/aereth abilitysummary` shows Discipline, rank, unlocked abilities, locked abilities, next reveal, and roadmap.
- `/aereth abilityresources` shows stamina, mana, focus, instability, and health.
- `/aereth abilitysync` refreshes bound ability hotbar items.
- Rank changes print ability unlock recap.
- Online player receives ability unlock feedback when another sender updates rank.
- Online player hotbar refreshes after rank changes.
- Existing GUI/loadout/activation/hotbar behavior remains working.

## Regression checks

```text
/aereth abilitygui
/aereth abilityloadout
/aereth abilityactivation
Sneak + swap hands
Right-click ability bind item
```

No console errors should appear.
