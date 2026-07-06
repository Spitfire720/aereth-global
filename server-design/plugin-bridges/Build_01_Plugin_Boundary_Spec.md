# Build 01 Plugin Boundary Spec

## Rule

Every plugin must do its job and nothing more. This is how we avoid building a lore system out of duct tape and regret.

## FragmentEngine

Owns:

- player RPG state
- Remnant condition flags
- Fragment state
- Intent state
- future Discipline state
- backend progression logic

Does not own:

- NPC dialogue text
- map props
- mob AI
- WorldGuard protection

## BetonQuest

Owns:

- quest steps
- dialogue
- journal flow
- conditions/events that call or check backend state

Does not own:

- permanent RPG identity
- Fragment definitions
- race data as source of truth

## MythicMobs

Owns:

- Roadstray
- Hollowglass Wisp
- encounter behavior
- mob skills
- mob drops

Does not own:

- player identity
- Fragment state
- Discipline state

## Oraxen

Owns:

- registry ledger visual
- road marker prop
- hollowglass shard prop
- waystone visuals
- item/resource pack presentation

Does not own:

- progression
- Fragment mechanics
- Intent
- race/class/Discipline

## WorldGuard

Owns:

- region boundaries
- PVP flags
- mob-spawning flags
- interact/build protections
- starter safety

Does not own:

- story state
- progression
- quest logic

## PlaceholderAPI

Owns:

- display bridge only

Does not own:

- data source
