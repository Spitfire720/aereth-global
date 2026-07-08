# Ability Design Template

Copy this for every future real ability.

## Ability ID

```text
<discipline>_<ability_name>
```

## Display Name

```text
Player-facing name
```

## Discipline

```text
vanguard / reaver / skirmisher / etc.
```

## Unlock Rank

```text
1 / 2 / 3 / 4
```

## Slot Role

What should this ability do inside the 4-slot loadout?

```text
Core identity / opener / defensive tool / movement tool / control tool / finisher / utility
```

## Target Mode

```text
self
 aimed_entity
 aimed_location
 forward_line
 area
```

## Resource Cost

```yaml
cost-type: stamina | mana | focus | instability | health | none
cost-amount: 0.0
```

## Cooldown

```yaml
cooldown-seconds: 0.0
```

## Player Fantasy

What should the player feel when using it?

```text
...
```

## Gameplay Effect

Describe what actually happens.

```text
...
```

## Constraints

```text
PvE only?
No PvP damage?
Requires target?
Requires line of sight?
Can miss?
Can affect bosses?
```

## Scaling Intent

```text
Scales with level?
Scales with Discipline rank?
Scales with resource cost?
Does duration/radius/damage scale?
```

## Placeholder Route

```yaml
effect-route: placeholder
```

## Future Implementation Notes

```text
Particles:
Sound:
Damage/heal/control:
MythicMobs integration:
MMOItems stat interaction:
Quest restrictions:
```

## YAML Draft

```yaml
example_ability_id:
  display-name: Example Ability
  discipline: vanguard
  unlock-rank: 1
  cost-type: stamina
  cost-amount: 10.0
  cooldown-seconds: 20.0
  target-mode: self
  effect-route: placeholder
  description: Short description here.
```

## Test Checklist

```text
Appears in Ability Codex
Unlocks at correct rank
Can be equipped only once
Cannot occupy locked slot
Shows in hotbar bind
Can activate from GUI
Can activate from hotbar
Pays resource
Starts cooldown
Persists activation state
Does not damage players unless PvP is explicitly designed later
```
