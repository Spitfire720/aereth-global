# Build 3D - Discipline Ability Framework Plan

Status: PLANNED

Date: 2026-07-05

## Objective

Implement the backend framework for Discipline abilities.

Build 3A added Discipline identity.
Build 3B added Discipline progression.
Build 3C added Discipline passive modifiers.
Build 3D adds ability definitions and unlock logic.

## Scope

This build adds:

- Ability definition backend
- Ability IDs
- Ability display names
- Ability descriptions
- Ability Discipline ownership
- Ability unlock ranks
- Ability cost fields
- Ability cooldown fields
- Player ability summaries
- Ability list command
- Player abilities command
- PlaceholderAPI ability values
- Agent schema v7

## Out of Scope

This build does not add:

- Combat execution
- Damage dealing
- Mob targeting
- Projectile logic
- Particle effects
- Oraxen icons
- Skill trees
- Mutated abilities
- Ability casting

## Architecture

FragmentEngine owns Discipline ability identity and unlock logic.

MMOItems does not own Discipline abilities.
Oraxen does not own Discipline abilities.
MythicMobs does not own player ability identity.
BetonQuest does not own player ability progression.

## Commands Planned

/aereth abilitylist
/aereth abilities <player>

## PlaceholderAPI Planned

%aereth_abilities_unlocked%
%aereth_abilities_locked%
%aereth_abilities_active%
%aereth_ability_count%

## Agent Schema v7 Planned

ability-fields:
  - abilities.unlocked
  - abilities.locked
  - abilities.available
  - abilities.count
  - abilities.by-discipline

## Verification Plan

/version FragmentEngine
/aereth setlevel SpitFire720 16
/aereth setdiscipline SpitFire720 vanguard
/aereth setdisciplinerank SpitFire720 2
/aereth abilitylist
/aereth abilities SpitFire720
/papi parse SpitFire720 %aereth_abilities_unlocked%
/papi parse SpitFire720 %aereth_ability_count%
/aereth agent export

## Expected Result

- FragmentEngine reports the Build 3D version
- Ability definitions load
- Ability list command displays registered abilities
- Player abilities command displays abilities unlocked by selected Discipline and rank
- PlaceholderAPI exposes ability summary values
- Agent export reports schema-version 7
- Combat execution remains intentionally unimplemented

## Future Work

Build 3E:
- Ability casting shell

Build 4:
- Combat execution and ability effects

Future:
- Oraxen ability icons
- Mutated Discipline abilities
- Ability UI
