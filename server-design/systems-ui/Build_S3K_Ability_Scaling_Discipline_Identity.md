# Build S3K - Ability Scaling + Discipline Identity

## Goal
Make activated abilities scale through level, Discipline rank, and inferred Discipline role.

## Added
- AbilityScalingService
- Discipline role inference
- Scaled resource cost
- Scaled cooldown
- Scaled effect potency
- Scaled duration, radius, and movement impulse
- Scaling metadata saved into character YAML
- Ability Activation GUI scaling preview
- Hotbar item scaling lore

## Discipline Role Categories
- defender
- striker
- precision
- control
- arcane
- temporal
- support
- occult
- technical
- aberrant
- balanced

## Persistence
Last activation writes:

```yaml
abilities:
  activation:
    last:
      scaling:
        role: defender
        potency-multiplier: 1.23
        duration-multiplier: 1.31
        radius-multiplier: 1.12
        cooldown-multiplier: 0.88
        cost-multiplier: 0.97
        identity-line: Vanguard scales toward longer protection and steadier uptime.
```

## Not Included
- PvP damage
- final numeric balance pass
- per-ability hand-authored formulas
- MythicMobs/MMOItems bridge
