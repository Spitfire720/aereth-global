# Build S3I - Ability Hotbar Binding Test

## Preconditions

- S3H is deployed.
- Live `plugins/FragmentEngine/abilities.yml` has the expanded ability catalogue.
- Player has an active character.
- Player has selected a Discipline.
- Player has at least one equipped ability in `/aereth abilityloadout`.

## Test steps

1. Restart the server from the panel.
2. Join as `SpitFire720`.
3. Run `/aereth abilityloadout` and confirm at least one slot has an equipped ability.
4. Sneak + swap hands.
5. Confirm ability bind items appear in hotbar slots 5 to 8 where available.
6. Right-click the slot 1 bind item.
7. Confirm the ability activates.
8. Right-click it again during cooldown.
9. Confirm cooldown blocks repeated activation.
10. Try to drop the bind item.
11. Confirm dropping is blocked.
12. Put a normal item in hotbar slot 6 and refresh again.
13. Confirm the normal item is not overwritten.

## Expected result

- Hotbar sync succeeds.
- Ability bind items activate loadout abilities.
- Cooldowns still work.
- Targeting/effects still route.
- Non-Aereth items are not overwritten.
- No console errors.
