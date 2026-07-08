# Ability Balance Field Contract

## Current status

Balance fields are deliberately simple because the real ability design pass has not happened yet.

The purpose is to preserve stable configuration shape.

## Core balance fields

```yaml
cost-type: stamina
cost-amount: 10.0
cooldown-seconds: 20.0
unlock-rank: 1
```

## Recommended early ranges

These are placeholders, not final values.

### Rank 1

```text
Cost: 6-14
Cooldown: 8-24s
```

### Rank 2

```text
Cost: 10-20
Cooldown: 12-32s
```

### Rank 3

```text
Cost: 16-30
Cooldown: 18-45s
```

### Rank 4

```text
Cost: 24-45
Cooldown: 25-70s
```

## Cost type intent

```text
stamina      physical action, movement, martial pressure
mana         spellcasting, arcane projection
focus        precision, control, timing, technique
instability  anomaly/paradox/volatile abilities
health       blood, sacrifice, grave, corruption themes
none         utility/debug only, avoid for final combat abilities
```

## Cooldown intent

Cooldown should represent decision weight, not just damage output.

Long cooldowns are justified by:

- large area influence
- hard control
- survivability burst
- movement escape
- fight-opening advantage
- boss-safe utility

## Scaling notes

The current scaling system is still framework-level. Final scaling formulas can be revised later.

For now, the important contract is that every ability has cost/cooldown fields that can be scaled without rewriting the loadout or activation pipeline.
