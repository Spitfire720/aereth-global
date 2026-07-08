# Build S3L - Ability Progression Polish

## Goal

Make ability progression readable and satisfying before adding more combat depth.

Previous S3 builds made ability selection, loadouts, activation, targeting, effects, resources, and scaling function. S3L improves feedback around unlocks and Discipline growth so players understand why abilities are locked, what comes next, and how close they are to a complete rank path.

## Adds

- `AbilityProgressionPolishService`
- Ability path completion percentage
- Next ability reveal panel
- Rank roadmap display
- Cleaner ability item lore
- Reveal state in Ability Activation GUI
- No command patching
- No listener patching
- No `abilities.yml` upload required

## Player-facing UX

Ability Codex now shows:

- Unlocked abilities out of total abilities
- Completion percentage
- Next reveal
- Required rank
- Ranks away
- Roadmap
- Per-ability reveal status

Ability Activation now shows:

- Ability reveal count in header
- Progression roadmap panel
- Reveal state on each activation slot

## Intent

This build is deliberately UI/state polish. It does not change power, costs, cooldowns, or target resolution. Those systems were handled in previous S3 builds.
