# Build S4A: Player Ability Experience Bundle

## Goal

Bundle multiple player-facing ability polish features into one build.

## Scope

S4A improves the experience around ability progression, resources, summaries, unlock feedback, and hotbar sync.

## Changes

1. Add `/aereth abilitysummary <player>`.
2. Add `/aereth abilityresources <player>`.
3. Add `/aereth abilitysync`.
4. Add ability unlock recap after Discipline XP rank-ups.
5. Add ability unlock recap after admin rank setting.
6. Auto-refresh ability hotbar after rank changes when the player is online.
7. Add reusable `AbilityPlayerExperienceService` for player-facing ability lines.

## Non-goals

- No combat damage redesign.
- No new abilities.yml upload.
- No schema migration.
- No cross-plugin MythicMobs/MMOItems integration.

## Notes

This build assumes S3E-S3L are already committed and working.
