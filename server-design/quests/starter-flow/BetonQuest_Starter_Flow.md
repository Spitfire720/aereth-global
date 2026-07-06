# BetonQuest Starter Flow - Lantern Marches

## Purpose

This document maps the first starter questline into BetonQuest concepts. It is a design bridge, not final syntax authority.

## Packages

Suggested package:

```text
lantern_marches_starter
```

## Main NPCs

| NPC | Role | BetonQuest use |
| --- | --- | --- |
| Mara Venn | Roadwarden | Starts intro quest |
| Archivist Cael Orwyn | Registry | Remnant/race registration dialogue |
| Ilya Marr | Ledger clerk | Sends player to Bent Road / Mirrorfen |
| Tavin Echo | Memory echo | Mirrorfen dialogue |
| Sella Quay | Trader | Optional delivery loop |
| Brother Halven | Crossing keeper | Saint's Crossing moral/cultural choice |

## Event names

```text
q01_started
q01_registry_directed
q02_registered
q03_bent_road_started
q03_marker_inspected
q04_mirrorfen_started
q04_reflection_solved
q05_old_wick_started
q05_satchel_recovered
q06_hollowglass_started
q06_hollowglass_survived
q07_completed
```

## Conditions

```text
has_marches_registered
has_satchel
has_hollowglass_seen
is_in_lanterns_rest
is_in_bent_road
is_in_mirrorfen
is_in_old_wick_post
is_in_hollowglass_pool
```

## Folder suggestion

```text
plugins/BetonQuest/QuestPackages/lantern_marches_starter/
  package.yml
  conversations/
    mara_venn.yml
    cael_orwyn.yml
    ilya_marr.yml
    tavin_echo.yml
  objectives.yml
  events.yml
  conditions.yml
  journal.yml
```

## Dialogue writing rule

NPCs should speak like people living in a damaged world, not lore encyclopedias with legs.

Good:

> The road shifted again last night. Same stones, wrong direction. Registry wants every traveller marked before they leave the lamps.

Bad:

> Greetings player, after the catastrophic event known as RE:FRAGMENT, multiple macro-divisions of Aereth underwent ontological destabilization.

Nobody deserves that. Not even plugin documentation readers.
