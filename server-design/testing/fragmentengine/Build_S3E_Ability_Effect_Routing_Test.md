# Build S3E - Ability Effect Routing Test

## Objective

Confirm ability activation routes to effect handlers while preserving S3D cooldown and loadout behavior.

## Pre-conditions

- S3C Ability Loadout is applied.
- S3D Ability Activation Foundation is applied.
- Player has an active character.
- Player has a selected Discipline.
- Player has at least one unlocked ability equipped in loadout slot 1.

## Commands

```text
/aereth abilityloadout
/aereth abilityactivation
/aereth abilityactivate SpitFire720 1
/aereth abilitycooldowns SpitFire720
```

## Expected result

- Ability Activation GUI opens.
- Clicking a ready loadout slot activates the equipped ability.
- Player receives an effect route message.
- Sound and particle feedback play if supported by the server version.
- Cooldown is applied.
- A second activation during cooldown is blocked.
- `/aereth abilitycooldowns SpitFire720` shows the active cooldown.
- Character YAML contains `abilities.activation.last.effect-id`.
- No console errors.

## Regression checks

```text
/aereth card
/aereth abilitygui
/aereth abilityloadout
/aereth abilityactivation
/aereth intentgui
/aereth disciplinegui
```

All GUIs should still open and block item stealing.
