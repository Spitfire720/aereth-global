# Plugin Boundary Map - Lantern Marches Starter Slice

## Hard ownership rules

| System | Owns | Does not own |
| --- | --- | --- |
| FragmentEngine | RPG state, Fragments, Intent, player identity states | Physical item stats, mob AI, quest dialogue |
| AerethCharacterCreatorCore | Character creation UI/front-end | Long-term RPG state |
| AerethCreatorFragmentBridge | Sync pipe between creator and RPG state | Canon decisions |
| WorldFracture | World reaction layer | Player identity |
| BetonQuest | Quest/dialogue/story flow | RPG state source of truth |
| MythicMobs | Enemy behavior and encounters | Player identity/progression |
| Oraxen | Visual/resource pack assets | Progression systems |
| MMOItems/MythicLib | Physical gear stats/items | Fragments, Intent, Disciplines |
| WorldGuard | Region protection and location boundaries | Quest state or RPG identity |
| PlaceholderAPI | Display bridge | State authority |

## Starter slice implications

- A player's Remnant condition is stored/read through FragmentEngine.
- Race choice is not a BetonQuest flag pretending to be canon unless bridged properly.
- Fragments are not MMOItems.
- Intent is not an MMOItems stat.
- Titles are not cosmetic plugin badges.
- Oraxen props can represent registry documents, lanterns, memory glass, and route markers, but they do not define progression.

## First integrations needed

1. PlaceholderAPI placeholders for player level/race/remnant state.
2. BetonQuest condition checks against placeholders where possible.
3. MythicMobs spawn zones tied to WorldGuard/region design.
4. Oraxen starter assets for route markers and registry visuals.
5. FragmentEngine event hook for Hollowglass disturbance.

## Non-goals for v1

- Full Discipline system integration.
- Full mutation systems.
- Complex faction reputation.
- World-scale dynamic fracture simulation.
- Endgame Void mechanics.

The first slice needs to work. The cosmic horror can wait in line like everyone else.
