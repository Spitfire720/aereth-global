# Build S3E - Ability Effect Routing

## Goal

Turn the S3D activation foundation into a real routing layer.

S3D confirmed that an equipped ability can be activated, blocked by cooldown, and saved into character YAML. S3E adds an `AbilityEffectService` that receives the activated ability and routes it to simple live feedback effects.

## Scope

S3E adds:

- `AbilityEffectService`
- Effect routing from `AbilityActivationService`
- Online-player effect application
- Message, sound, and particle feedback
- Effect route state written to character YAML
- Ability Activation GUI text updated to show routed effect state

## Effect routes

The first route map is deliberately simple and safe:

| Route | Example trigger |
| --- | --- |
| `defensive_pulse` | guard, stance, wall, oath, root, bind |
| `blood_pressure` | blood, grave, mark, health cost |
| `arcane_spark` | arc, sigil, null, mana cost |
| `focus_thread` | thread, sight, gravity, focus cost |
| `unstable_bloom` | anomaly, paradox, instability cost |
| `stamina_surge` | stamina / energy cost |
| `resonance_ping` | fallback |

This keeps the system functional before we design full ability-specific handlers.

## Character YAML

Activation now writes effect metadata:

```yaml
abilities:
  cooldowns:
    ability_id:
      effect-id: defensive_pulse
      effect-status: applied

  activation:
    last:
      effect-id: defensive_pulse
      effect-status: applied
      effect-detail: Effect route applied.
      status: effect_routed
```

## Boundaries

This build does not add combat damage, healing math, stat buffs, or target selection.

Those belong in later builds after we have a safe effect registry and targeting model.
