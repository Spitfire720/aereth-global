# Build S3C - Ability Loadout UI

## Purpose

Build S3C turns the Ability Codex from a read-only museum display into a usable loadout manager.

Players can now equip unlocked Discipline abilities into active loadout slots. The result is persisted to the active character YAML under `abilities.loadout.*`.

## Scope

Included:

- `/aereth abilityloadout`
- Ability Loadout GUI
- Ability Codex button to open Ability Loadout
- Loadout slot selection
- Equip unlocked abilities
- Block locked abilities
- Clear selected slot
- Persist selected loadout to character YAML
- Prevent duplicate abilities across loadout slots
- GUI item protection through the FragmentEngine GUI listener

Not included:

- Combat activation
- Cooldown runtime enforcement
- Cost payment
- Hotbar binding
- MMOItems/MythicMobs skill execution
- Ability effects

Those belong to later activation/effect builds.

## Character YAML fields

```yaml
abilities:
  loadout:
    slots:
      slot1: vanguard_guardian_stance
      slot2: vanguard_linebreaker
      slot3: null
      slot4: null
    active:
      - vanguard_guardian_stance
      - vanguard_linebreaker
    count: 2
    max-slots: 4
```

## Slot unlock rule

Current S3C rule:

```text
Rank 1 -> loadout slot 1
Rank 2 -> loadout slot 2
Rank 3 -> loadout slot 3
Rank 4 -> loadout slot 4
```

This is intentionally simple. It can be moved to config later if we stop enjoying hardcoded little rituals.

## GUI flow

```text
Character Card -> Ability Codex -> Ability Loadout
Discipline Codex -> Ability Codex -> Ability Loadout
/aereth abilityloadout -> Ability Loadout
```

## Files touched

```text
FragmentEngine_Build1_Source/src/main/java/live/aereth/fragmentengine/gui/AbilityLoadoutGui.java
FragmentEngine_Build1_Source/src/main/java/live/aereth/fragmentengine/gui/AbilityCodexGui.java
FragmentEngine_Build1_Source/src/main/java/live/aereth/fragmentengine/listener/FragmentEngineGuiListener.java
FragmentEngine_Build1_Source/src/main/java/live/aereth/fragmentengine/command/AerethCommand.java
```
