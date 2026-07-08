# Build S3H - Ability Balance + Definition Expansion

## Goal
Expand the Ability catalogue so every base Discipline has a coherent rank 1-4 progression instead of one placeholder ability pretending to be a whole RPG system in a trench coat.

## Scope
- Replace `src/main/resources/abilities.yml` with an expanded schema-version 2 catalogue.
- Add 4 abilities for each of the 22 base Disciplines.
- Preserve the runtime fields currently read by `AbilityService`: `display-name`, `discipline`, `unlock-rank`, `cost-type`, `cost-amount`, `cooldown-seconds`, and `description`.
- Add forward-compatible metadata: `target-mode` and `effect-route`.

## Balance Rules
- Rank 1: low-cost identity button.
- Rank 2: stronger utility or focused target option.
- Rank 3: larger tactical value or area/location tool.
- Rank 4: capstone-style ability with longer cooldown.
- PvP damage remains disabled by the current effect layer.
- Effects remain routed through the existing S3G effect service and current ID/cost inference.

## Live Config Warning
`AbilityService` loads `plugins/FragmentEngine/abilities.yml` from the plugin data folder. The jar resource only seeds this file if it does not already exist. Deploying a rebuilt jar alone will not update live ability definitions. Back up and upload the live config file separately.
