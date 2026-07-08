# Build S3D - Ability Activation Foundation

## Purpose

S3D gives Ability Loadout its first activation layer.

Previous builds:

- S3B shows abilities.
- S3C equips abilities into loadout slots.

S3D lets a player activate an equipped unlocked ability and records the result. This is deliberately a foundation layer, not final combat implementation.

## Non-goals

S3D does not:

- deal combat damage
- consume real stamina/mana/health resources
- cast particles
- bind hotbar keys
- integrate with MythicMobs/MMOItems skill execution
- create final ability effects

Those belong to later builds.

## Activation rules

A player can activate an ability only if:

1. They have an active character.
2. A Discipline is selected.
3. The selected loadout slot is unlocked.
4. The slot has an ability equipped.
5. The equipped ability still exists in abilities.yml.
6. The ability is unlocked by current Discipline rank.
7. The ability is not on cooldown.

## Cooldown rule

Cooldown is read from the ability definition:

```yaml
cooldown-seconds: 20
```

Cooldown state is stored on the character YAML under:

```yaml
abilities.cooldowns.<abilityId>
```

## Why this exists before real effects

This creates a safe bridge between identity/progression and actual gameplay effects.

It lets us test:

- loadout persistence
- unlocked/locked restrictions
- cooldown persistence
- command and GUI activation
- character YAML state writes

Without prematurely tying Aereth identity systems to MMOItems/MythicMobs effect execution. Shocking restraint from software, frankly.
