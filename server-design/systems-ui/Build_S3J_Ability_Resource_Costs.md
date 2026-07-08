# Build S3J - Ability Resource Costs

## Goal
Make ability cost text real. Abilities now check and spend resource pools before cooldowns and effects resolve.

## Resources
- stamina
- mana
- focus
- instability
- health

## Storage
Resource state is stored under:

```yaml
abilities:
  resources:
    stamina:
      current: 118.5
      max: 128.0
      regen-per-second: 8.4
      last-updated-ms: 1783520000000
```

Last activation stores:

```yaml
abilities:
  activation:
    last:
      resource:
        type: stamina
        amount: 18.0
        remaining: 102.4
        max: 128.0
        status: paid_resource
```

## Rules
- Resource validation happens after lock/unlock/cooldown validation.
- Cooldown starts only after cost is paid.
- Health-cost abilities cannot reduce the player below the safety reserve.
- Resource pools regenerate lazily when checked or spent.
- Existing targeting and effect routing remain unchanged.

## UI
The Ability Activation GUI shows resource pools and per-ability affordability.
Hotbar ability bind items show cost and resource status in lore.
