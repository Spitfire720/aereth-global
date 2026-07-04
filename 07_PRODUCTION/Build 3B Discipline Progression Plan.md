# Build 3B - Discipline Progression Plan

Status: PLANNED

Date: 2026-07-04

## Objective

Implement backend progression for selected Disciplines inside FragmentEngine.

Build 3A allowed players to select a Discipline.
Build 3B allows that selected Discipline to grow.

This build does not add active abilities yet.
This build does not add combat execution yet.
This build does not add Mutated Disciplines yet.

## Design Rule

A Discipline is not just a label.
A Discipline should become a long-term identity path.

Progression should be stored inside the active character profile and owned by FragmentEngine.

## Build 3B Scope

This build adds:

- Discipline XP
- Discipline rank
- Discipline XP required
- Discipline progress percentage
- Discipline progression commands
- Discipline progression PlaceholderAPI values
- Agent schema update to v5

## Out of Scope

This build does not add:

- Active combat abilities
- Passive modifiers
- Skill trees
- Mutated Disciplines
- Oraxen icons
- HUD menus
- MythicMobs reactions
- WorldFracture reactions
- Quest gates

Those come later, because apparently building a functional RPG requires not shoving every system into one cursed Java bowl.

## Progression Model

Discipline Rank Range:

- Rank 0: Untrained
- Rank 1: Initiate
- Rank 2: Adept
- Rank 3: Specialist
- Rank 4: Master
- Rank 5: Ascendant

Initial values after selecting a Discipline:

discipline.progression.rank: 1
discipline.progression.xp: 0
discipline.progression.xp-required: 1000

## XP Formula

Discipline XP required for next rank:

rank 1 -> 2: 1000
rank 2 -> 3: 2500
rank 3 -> 4: 5000
rank 4 -> 5: 9000

Formula baseline:

xpRequired = floor(1000 * rank^1.5)

Rank 5 is current cap for non-mutated Disciplines.

## Commands

/aereth disciplineprogress <player>
/aereth adddisciplinexp <player> <amount>
/aereth setdisciplinerank <player> <rank>
/aereth resetdisciplineprogress <player>

Examples:

/aereth disciplineprogress SpitFire720
/aereth adddisciplinexp SpitFire720 100
/aereth setdisciplinerank SpitFire720 2
/aereth resetdisciplineprogress SpitFire720

## PlaceholderAPI

%aereth_discipline_rank%
%aereth_discipline_rank_name%
%aereth_discipline_xp%
%aereth_discipline_xp_required%
%aereth_discipline_progress_percent%

## Agent Export

Agent schema should update to schema-version 5.

New exported fields:

discipline.progression.rank
discipline.progression.rank-name
discipline.progression.xp
discipline.progression.xp-required
discipline.progression.progress-percent
discipline.progression.max-rank

## Architecture Boundary

FragmentEngine owns Discipline progression.

MMOItems does not own Discipline XP.
Oraxen does not own Discipline XP.
BetonQuest may later reward Discipline XP.
MythicMobs may later reward Discipline XP from combat.
WorldFracture may later react to Discipline rank.

## Future Work

Build 3C:
- Discipline passive modifiers

Build 3D:
- Discipline ability framework

Build 4:
- Mutated Disciplines
