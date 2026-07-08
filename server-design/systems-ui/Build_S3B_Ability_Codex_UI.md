# Build S3B - Ability Codex UI

## Goal

Make Discipline abilities visible through a player-facing menu.

S3A lets players select a Discipline. S3B lets players see what that Discipline unlocks as it ranks up.

## Scope

Implemented:

- Ability Codex GUI
- `/aereth abilitygui`
- Character Card navigation to Ability Codex
- Discipline Codex navigation to Ability Codex
- Current Discipline summary
- Rank and XP progress summary
- Unlocked and locked ability state

Not implemented:

- Ability casting
- Keybinds or hotbar binding
- Rank XP reward sources
- Combat integration
- Mutation abilities

## Design rule

Abilities belong to the Discipline layer. MMOItems can later provide physical items and combat mechanics, but FragmentEngine remains the source of truth for selected Discipline, rank, and ability availability.

## Future S3C direction

S3C should define how ability use actually works: active/passive loadout, cooldowns, command hooks, and plugin bridge boundaries.
