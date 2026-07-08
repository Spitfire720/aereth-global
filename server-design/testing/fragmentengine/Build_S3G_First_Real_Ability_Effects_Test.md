# Build S3G - First Real Ability Effects Test

## Commands
```text
/aereth abilityactivation
/aereth abilityactivate SpitFire720 1
/aereth abilitycooldowns SpitFire720
```

## Manual Test
1. Equip at least one unlocked ability in `/aereth abilityloadout`.
2. Open `/aereth abilityactivation`.
3. Activate slot 1.
4. Confirm sound/particles appear.
5. Confirm self effects or target effects are applied depending on ability route.
6. Try activating again during cooldown.
7. Confirm cooldown blocks repeat use.
8. Confirm no console errors.

## Expected
- Real Bukkit potion/movement/control effects occur.
- PvP damage is not applied.
- Cooldown still applies.
- Target model still records last target.
- Ability Activation GUI still opens and refreshes.
