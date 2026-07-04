# Build 3A Live Verification

Status: PASSED

Date: 2026-07-04

## Build

FragmentEngine 1.12.1

## Objective

Verify that Build 3A Discipline backend is live on the server and working through commands, PlaceholderAPI, and agent export.

## Commands Tested

/version FragmentEngine
/aereth status
/aereth disciplinelist
/aereth discipline SpitFire720
/aereth setlevel SpitFire720 16
/aereth setdiscipline SpitFire720 vanguard
/aereth discipline SpitFire720
/papi parse SpitFire720 %aereth_discipline%
/papi parse SpitFire720 %aereth_discipline_display%
/papi parse SpitFire720 %aereth_discipline_family%
/papi parse SpitFire720 %aereth_discipline_unlocked%
/papi parse SpitFire720 %aereth_discipline_level_required%
/aereth cleardiscipline SpitFire720
/aereth discipline SpitFire720
/aereth agent export

## Expected Results

- FragmentEngine reports version 1.12.1
- Discipline list shows all starting Disciplines
- Discipline can be selected after level 16
- Vanguard can be selected successfully
- Discipline status shows Vanguard
- Discipline family shows martial
- PlaceholderAPI exposes Discipline values
- Discipline can be cleared back to Unformed
- Agent export completes successfully
- Agent schema reports schema-version 4
- Agent schema includes discipline fields

## Architecture Confirmed

- Discipline is owned by FragmentEngine
- Discipline is not an MMOItems class
- Discipline is not an Oraxen visual object
- Discipline does not grant active combat abilities yet
- PlaceholderAPI exposes Discipline state for future UI/HUD work
- Agent export exposes Discipline data for external project tracking

## Remaining Work

- Discipline progression
- Discipline passive modifiers
- Discipline ability framework
- Discipline UI/HUD visualization
- Oraxen Discipline icons
- Combat ability integration
- Mutated Disciplines
