# Build S3F - Ability Targeting Model

## Purpose

Build S3F adds a first targeting model to the FragmentEngine ability activation pipeline.

S3D recorded activation and cooldown state. S3E routed successful activations into visual and sound feedback. S3F resolves *what the ability is pointed at* before the effect route is applied.

## Scope

S3F adds:

- `AbilityTargetingService`
- Target mode inference per ability definition
- Target resolution during activation
- Target data persisted into character YAML
- Ability Activation GUI target preview and last target display
- Effect feedback moved toward the resolved target location where possible

## Target modes

The model currently supports:

- `self`
- `aimed_entity`
- `aimed_location`
- `forward_line`
- `area`

Target modes are inferred from ability IDs and cost type for now. This avoids changing the ability schema too early.

## Persisted state

The last activation writes:

```yaml
abilities:
  activation:
    last:
      target:
        mode: aimed_entity
        status: resolved_entity
        description: Zombie (zombie)
        type: entity
        name: Zombie
        entity-type: zombie
        distance: 8.25
        world: main
        x: 120.5
        y: 65.0
        z: -44.5
```

Cooldown entries also store target information under the matching ability cooldown path.

## Explicit non-goals

S3F does not add direct damage, healing, buffs, debuffs, or crowd control. This build only resolves and records the target model used by later effect handlers.
