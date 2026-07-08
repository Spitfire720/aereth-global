# FragmentEngine Build 01 Integration Spec

## Objective

Make the Lantern Marches starter experience read and write only the minimal RPG state needed for a first playable vertical slice.

## Build 01 integration points

1. Player identity check at Registrar.
2. Registration completion state.
3. Hollowglass Pool first Fragment contact state.
4. First Intent tutorial prompt state.
5. Starter flow completion state.

## Data ownership

| System | Owns |
| --- | --- |
| FragmentEngine | identity, RPG state, persistent progression |
| BetonQuest | dialogue, objectives, temporary quest flags |
| WorldGuard | protection and safe zones |
| MythicMobs | enemy behavior |
| Oraxen | prop visuals |
| PlaceholderAPI | display mirrors |

## Success criteria

A non-OP test player completes the intro loop, relogs, and still has the correct FragmentEngine state.
