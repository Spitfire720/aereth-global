# Build S3F - Ability Targeting Model Test

## Preconditions

- S3E is working.
- Player has an active character.
- Player has selected a Discipline.
- Player has at least one unlocked ability equipped in Ability Loadout.

## Commands

```text
/aereth abilityactivation
/aereth abilityactivate SpitFire720 1
/aereth abilitycooldowns SpitFire720
```

## Test cases

### 1. GUI opens

Run `/aereth abilityactivation`.

Expected:

- Ability Activation GUI opens.
- Slots show target mode.
- Last target panel exists.
- No console error.

### 2. Activate self/area ability

Equip a defensive/self style ability, then activate it.

Expected:

- Ability activates.
- Cooldown is recorded.
- Target mode records `self` or `area`.
- Character YAML contains `abilities.activation.last.target`.

### 3. Activate aimed ability with entity in sight

Look at a mob/player and activate an aimed entity ability.

Expected:

- Target status records `resolved_entity`.
- Target description names the hit entity.
- Effect feedback appears near the target if possible.

### 4. Activate aimed ability with no entity in sight

Look into empty space and activate an aimed entity ability.

Expected:

- Activation does not crash.
- Target status falls back to location.
- Cooldown still applies.

### 5. Cooldown protection remains

Activate the same slot twice quickly.

Expected:

- Second activation is blocked by cooldown.
- Targeting does not bypass cooldown.

## Pass criteria

- Compile succeeds.
- GUI opens.
- Activation resolves target state.
- Target state is saved to YAML.
- Cooldowns still work.
- No item stealing.
- No console errors.
