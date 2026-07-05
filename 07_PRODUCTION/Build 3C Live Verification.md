# Build 3C Live Verification

Status: PASSED

Date: 2026-07-05

## Build

FragmentEngine 1.14.0

## Objective

Verify that Build 3C Discipline Passive Modifiers are live on the server and working through stats, PlaceholderAPI, and agent export.

## Commands Tested

/version FragmentEngine
/aereth setlevel SpitFire720 16
/aereth setdiscipline SpitFire720 vanguard
/aereth setdisciplinerank SpitFire720 2
/aereth stats SpitFire720
/papi parse SpitFire720 %aereth_discipline_passive_stats%
/papi parse SpitFire720 %aereth_discipline_passive_derived%
/papi parse SpitFire720 %aereth_discipline_bonus_vitality%
/papi parse SpitFire720 %aereth_discipline_bonus_endurance%
/aereth agent export

## Expected Results

- FragmentEngine reports version 1.14.0
- Vanguard can be selected at level 16
- Vanguard rank can be set to rank 2
- Discipline passive stat bonuses are applied
- Discipline passive derived bonuses are applied
- Stats command displays Discipline bonus values
- PlaceholderAPI exposes Discipline passive values
- Agent export completes successfully
- Agent schema reports schema-version 6
- Agent schema includes Discipline passive fields

## Expected Vanguard Rank 2 Values

- Passive stats: vitality=2.0,endurance=1.0
- Passive derived: max-health=20.0,defense=2.0
- Vitality bonus: 2.0
- Endurance bonus: 1.0

## Architecture Confirmed

- Discipline passive modifiers are owned by FragmentEngine
- Discipline rank affects passive stat bonuses
- Discipline rank affects derived stat bonuses
- Passive bonuses are stored under stats.discipline-bonus
- Derived passive bonuses are stored under derived.discipline-bonus
- PlaceholderAPI exposes passive state
- Agent export exposes passive fields
- This build does not add active abilities yet

## Remaining Work

- Build 3D: Discipline ability framework
- Future: ability cooldowns
- Future: ability costs
- Future: combat execution
- Future: Oraxen Discipline icons
- Future: Mutated Disciplines
