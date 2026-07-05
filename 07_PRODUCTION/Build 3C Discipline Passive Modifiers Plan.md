# Build 3C - Discipline Passive Modifiers Plan

Status: PLANNED

Date: 2026-07-05

## Objective

Implement passive stat modifiers from selected Discipline and Discipline rank.

Build 3A added Discipline identity.
Build 3B added Discipline progression.
Build 3C makes Discipline rank affect character stats.

## Scope

This build adds:

- Discipline passive stat bonuses
- Discipline passive derived stat bonuses
- Discipline rank scaling
- Discipline bonus storage under stats.discipline-bonus
- Discipline derived bonus storage under derived.discipline-bonus
- PlaceholderAPI passive values
- Agent schema v6

## Out of Scope

This build does not add:

- Active abilities
- Combat execution
- Skill trees
- Mutated Disciplines
- Oraxen icons
- Menus
- Quest rewards

## Architecture

FragmentEngine owns Discipline passive modifiers.

MMOItems does not own Discipline passives.
Oraxen does not own Discipline passives.
MythicMobs does not own Discipline passives.

## Verification

Expected test path:

/version FragmentEngine
/aereth setlevel SpitFire720 16
/aereth setdiscipline SpitFire720 vanguard
/aereth setdisciplinerank SpitFire720 2
/aereth stats SpitFire720
/papi parse SpitFire720 %aereth_discipline_passive_stats%
/papi parse SpitFire720 %aereth_discipline_passive_derived%
/aereth agent export

Expected Vanguard Rank 2 passive output includes:

- vitality bonus
- endurance bonus
- max-health bonus
- defense bonus

## Future Work

Build 3D:

- Discipline ability framework

Future:

- Discipline skill trees
- Mutated Disciplines
- Oraxen UI icons