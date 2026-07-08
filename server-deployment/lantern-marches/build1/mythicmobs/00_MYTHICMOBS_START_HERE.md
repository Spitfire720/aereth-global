# Build 01 MythicMobs Pass - Start Here

## Goal

Add the first enemy/ambient encounter layer for The Lantern Marches without turning the starter area into a death blender. Humanity has already invented enough traps.

## Required previous passes

Before live deployment:

- [ ] Lantern's Rest blockout exists.
- [ ] The Bent Road exists.
- [ ] Hollowglass Pool exists.
- [ ] WorldGuard safe/route/anomaly regions exist and pass tests.
- [ ] BetonQuest starter flow at least loads without errors.
- [ ] Test player can walk from Lantern's Rest to The Bent Road without permissions issues.

## Build 01 mob list

| Mob | Role | Zone | Threat |
| --- | --- | --- | --- |
| Roadstray | basic starter hostile | Bent Road | low |
| Bent Road Drifter | slow ambience/pressure | Bent Road edges | low |
| Mirrorfen Wisp | evasive magical nuisance | Mirrorfen edge | low-medium |
| Hollowglass Shardling | anomaly creature | Hollowglass Pool | medium |
| Ash Mile Cinderling | optional edge threat | Ash Mile edge | medium |

## Deployment order

1. Copy skills file.
2. Copy mob file.
3. Copy spawner templates only after coordinates are replaced.
4. Run `/mm reload`.
5. Test each mob manually with `/mm mobs spawn`.
6. Add one spawner area at a time.
7. Run non-OP traversal/combat test.

## Do not

- Do not place spawners inside Lantern's Rest safe zone.
- Do not use MythicMobs to set RPG identity or permanent player state.
- Do not add loot economy yet beyond placeholder/basic drops.
- Do not add boss mechanics in Build 01.
