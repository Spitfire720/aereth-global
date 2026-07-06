# Starter Questline 01 - After RE:FRAGMENT

## Purpose

Introduce Aereth, Remnants, RE:FRAGMENT, race identity, and the Lantern Marches through gameplay.

## Questline summary

| Quest | Name | Main action | System touched |
| --- | --- | --- | --- |
| 1 | The Road Still Burns | Arrive and speak to Roadwarden | BetonQuest, WorldGuard |
| 2 | Register What Remains | Register at The Quiet Ledger | BetonQuest, FragmentEngine read |
| 3 | The Bent Road | Follow route markers and inspect anomaly | BetonQuest objectives |
| 4 | Names in the Water | Investigate Mirrorfen reflection | Puzzle + dialogue |
| 5 | Old Wick Post | Recover satchel from hostile pocket | MythicMobs combat |
| 6 | Hollowglass | Witness controlled Fragment disturbance | FragmentEngine bridge |
| 7 | A Name Held in Lanternlight | Return, receive recognition/title hint | Titles/world recognition |

## Quest 1 - The Road Still Burns

### Start

Player spawns or arrives near Lantern's Rest road gate.

### NPC

Mara Venn, Roadwarden.

### Objectives

1. Speak with Mara Venn.
2. Walk to Lantern's Rest Registry Hall.
3. Speak with Archivist Cael Orwyn.

### Design note

Do not start with a lore lecture. Start with practical tension: the roads are failing, names are missing from the ledgers, and the player needs to be registered before leaving the safe road.

## Quest 2 - Register What Remains

### NPC

Archivist Cael Orwyn.

### Objectives

1. Confirm player is a Remnant.
2. Confirm race identity.
3. Receive temporary travel mark: `marches_registered`.
4. Speak to Ilya Marr at The Quiet Ledger Annex.

### Lore beats

- Remnant is an existential condition, not a race.
- Race remains separate from Remnant condition.
- RE:FRAGMENT is the event/condition that changed Aereth.
- The Erasure/Unmaking/Veiling are names people use, not necessarily the true explanation.

## Quest 3 - The Bent Road

### NPC

Ilya Marr, ledger clerk.

### Objectives

1. Follow three road markers.
2. Interact with the repeated mile marker.
3. Defeat or avoid 3 Roadstrays.
4. Return to the broken marker.

### Gameplay

Teach route markers and first weak mobs.

## Quest 4 - Names in the Water

### Location

Mirrorfen.

### Objectives

1. Inspect three reflected markers.
2. Choose the marker that appears only in reflection.
3. Speak to Echo of Tavin.
4. Receive clue: `ledger_name_missing`.

### Gameplay

Introduce memory anomaly without heavy combat.

## Quest 5 - Old Wick Post

### Objectives

1. Enter Old Wick Post.
2. Recover registry satchel.
3. Defeat Wick-Touched enemies.
4. Optional: restore one lantern.

### Gameplay

First combat pocket. No boss required for v1.

## Quest 6 - Hollowglass

### Location

Hollowglass Pool.

### Objectives

1. Place recovered satchel by the pool.
2. Watch anomaly event.
3. Survive minor wave.
4. Receive Fragment pressure reading.

### System note

This should call FragmentEngine only for state display / early flags. Do not grant raw power. Fragments alter perception, access, responsibility, consequence, pressure, and world interaction.

## Quest 7 - A Name Held in Lanternlight

### Objectives

1. Return to Lantern's Rest.
2. Speak with Archivist Cael Orwyn.
3. Unlock local recognition flag.
4. Receive title hint, not final title if title backend is not ready.

### Reward design

- Small XP.
- Basic tool/gear item.
- Lantern Marches route access.
- Recognition flag: `lantern_marches_known`.
- Optional title hint: `Of the Lantern Road`.

## Required flags

```text
marches_registered
bent_road_marker_seen
mirrorfen_reflection_solved
old_wick_satchel_recovered
hollowglass_event_seen
lantern_marches_known
```
