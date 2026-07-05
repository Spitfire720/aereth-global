# Aereth Architecture

## Core rule

If it defines who the player is, FragmentEngine owns it.

If it defines what the player sees, Oraxen or PlaceholderAPI displays it.

If it defines what an enemy does, MythicMobs owns it.

If it defines what a model looks like, ModelEngine owns it.

If it defines physical gear mechanics, MMOItems + MythicLib own it.

If it defines quest/story flow, BetonQuest owns it.

If it defines world reaction, WorldFracture owns it.

## Ownership map

| System | Owner |
| --- | --- |
| Player profile | FragmentEngine |
| Character slots | FragmentEngine |
| Race | FragmentEngine |
| Progression | FragmentEngine |
| Stats | FragmentEngine |
| Derived stats | FragmentEngine |
| Fragments | FragmentEngine |
| Intent | FragmentEngine |
| Disciplines | FragmentEngine |
| Discipline progression | FragmentEngine |
| Discipline passives | FragmentEngine |
| Discipline ability definitions | FragmentEngine |
| Visual icons/items | Oraxen |
| Gear mechanics | MMOItems + MythicLib |
| Enemies | MythicMobs |
| Models | ModelEngine |
| Quests/dialogue | BetonQuest |
| Display bridge | PlaceholderAPI |
| World reaction | WorldFracture |

## Hard boundaries

Fragments are not MMOItems items.

Intent is not an MMOItems stat.

Disciplines are not Oraxen items.

Oraxen does not define progression.

MythicMobs does not own player identity.

BetonQuest does not own RPG state.