# The Lantern Marches - Server Region Implementation Spec

## Status

Draft v1 for playable starter vertical slice.

## Canon role

The Lantern Marches are the first playable subregion of Aereth. They sit between Valterra and Lyssara influence and act as a frontier corridor where broken roads, local records, trade routes, registry culture, and low-risk memory anomalies overlap.

The region is not a full continent. It is a marchland, route-region, and borderland starter zone.

## Gameplay role

| Area | Use |
| --- | --- |
| Lantern's Rest | Starter hub, safe zone, registry, first NPCs |
| The Bent Road | Tutorial route, first travel path, road weirdness |
| Mirrorfen | Reflection / memory anomaly wetland |
| Old Wick Post | Abandoned outpost, first combat pressure |
| The Ash Mile | Burned road stretch, environmental danger tutorial |
| Saint's Crossing | Shrine / crossing / moral choice location |
| The Quiet Ledger | Archive/registry site and record mystery |
| Hollowglass Pool | Low-risk Fragment disturbance site |

## Target player phase

Levels 1-15, Discovery phase.

The player should not choose a combat Discipline yet. This area introduces identity, race, Remnant condition, Fragments, Intent, and RE:FRAGMENT through play, not through a wall of exposition, because nobody logs into Minecraft to read a municipal law code with particle effects.

## Tone

- Settled but fragile.
- Beautiful, strange, and uneasy.
- Roads matter.
- Records matter.
- Memory is unreliable.
- The world is wounded, but not hopeless.

## Terrain palette

| Terrain | Notes |
| --- | --- |
| Temperate frontier woodland | Oak, dark oak, birch, moss, mixed leaf canopy |
| Broken old roads | Andesite, tuff, gravel, cracked stone brick, coarse dirt |
| Wetland pockets | Mud, waterlogged roots, moss carpet, blue/green fog effects |
| Charred route scars | Blackstone, basalt, ash blocks, dead shrubs |
| Registry ruins | Stone brick, calcite, copper, deepslate trim, lanterns |
| Memory glass anomalies | Tinted glass, amethyst, prismarine, cyan/teal Oraxen props |

## Build philosophy

The Marches should not look like a theme park. It should feel like people live here and are trying to hold routes open after the world broke.

Use vertical landmarks sparingly:

- Lantern towers along roads.
- Registry posts with notice boards.
- Waystones cracked by RE:FRAGMENT pressure.
- Half-submerged road markers in Mirrorfen.
- Burned mile markers along The Ash Mile.

## Safe / unsafe gradient

| Zone | Risk | Notes |
| --- | --- | --- |
| Lantern's Rest core | Safe | No hostile mobs, story onboarding |
| Outer Lantern's Rest | Very low | Practice interactions, fetch tasks |
| The Bent Road | Low | First weak mobs, route tutorial |
| Mirrorfen edge | Low-mid | Reflection anomalies, first puzzle pressure |
| Old Wick Post | Mid starter | First proper hostile cluster |
| Hollowglass Pool | Controlled anomaly | Fragment tutorial site, scripted pressure |
| Ash Mile | Environmental risk | Fire/ash hazards, low visibility |
| Saint's Crossing | Story risk | Choice, consequence, faction hint |

## Player journey summary

1. Arrive in Lantern's Rest.
2. Register as a Remnant.
3. Confirm race identity.
4. Learn RE:FRAGMENT through local records and NPC reactions.
5. Follow The Bent Road.
6. Investigate low-risk anomaly at Hollowglass Pool.
7. Fight memory-touched starter mobs.
8. Return evidence to The Quiet Ledger.
9. Receive first title/world-recognition hint.
10. Unlock wider Lantern Marches tasks.

## Integration priorities

1. Build Lantern's Rest core.
2. Define WorldGuard safe and transition regions.
3. Implement starter NPCs and first quest chain with BetonQuest.
4. Add 3-5 MythicMobs starter enemies.
5. Add Oraxen props/icons for registry, fragments, waystones, lanterns.
6. Connect FragmentEngine placeholder state.
7. Add first repeatable route/task loop.
