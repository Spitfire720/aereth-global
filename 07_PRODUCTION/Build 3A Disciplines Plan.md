# Build 3A - Disciplines Plan

Status: PLANNED

Date: 2026-07-04

## Objective

Implement the first production backend version of the Discipline system inside FragmentEngine.

Disciplines are not starting classes.
Disciplines are not MMOItems item types.
Disciplines do not grant full combat abilities yet.

Disciplines represent the player's first committed identity path after the Discovery phase.

## Design Rule

Level 1-15:
- No Discipline lock
- Player remains unformed/uncommitted

Level 16-35:
- Discipline can be selected
- Player enters Commitment phase

Level 36-50:
- Discipline becomes optimization foundation

Level 51-60:
- Discipline becomes eligible for Mutation later

## Build 3A Scope

This build adds:

- Discipline definitions
- Discipline family metadata
- Discipline unlock level
- Discipline selection command
- Discipline clear command
- Discipline status command
- Discipline list command
- Discipline PlaceholderAPI values
- Agent schema update

## Out of Scope

This build does not add:

- Active abilities
- Combat skill trees
- Mutated Disciplines
- Oraxen visuals
- MythicMobs reactions
- WorldFracture reactions
- Quest gating

Those come later, because apparently building systems in order is better than throwing plugins into a blender and calling it gameplay.

## Commands

/aereth disciplinelist
/aereth discipline <player>
/aereth setdiscipline <player> <disciplineId>
/aereth cleardiscipline <player>

Examples:

/aereth disciplinelist
/aereth discipline SpitFire720
/aereth setdiscipline SpitFire720 vanguard
/aereth cleardiscipline SpitFire720

## PlaceholderAPI

%aereth_discipline%
%aereth_discipline_display%
%aereth_discipline_family%
%aereth_discipline_unlocked%
%aereth_discipline_level_required%

## Starting Disciplines

Martial:
- vanguard
- reaver
- skirmisher
- assassin
- marksman
- controller

Arcane / Reality:
- spellweaver
- arcanist
- chronomancer
- fateweaver

Defensive / Support:
- sentinel
- warden
- vitalist
- necrosage

Summoning / Construction:
- summoner
- binder
- warforger
- architect

Aereth Identity:
- oracle
- paradox
- suppressor
- anomaly

## Architecture Boundary

FragmentEngine owns Discipline identity.

MMOItems may later provide discipline-themed gear.
Oraxen may later provide discipline icons and HUD visuals.
MythicMobs may later react to Discipline.
WorldFracture may later react to Discipline.
BetonQuest may later branch dialogue based on Discipline.

## Future Work

- Discipline abilities
- Discipline progression
- Discipline passive modifiers
- Discipline UI
- Discipline quests
- Mutated Disciplines
