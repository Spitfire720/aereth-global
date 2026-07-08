# Build S3H Test - Ability Balance + Definition Expansion

## Pre-check
1. Apply the S3H pack.
2. Run `mvn clean package`.
3. Back up live `plugins/FragmentEngine/abilities.yml`.
4. Upload the new `abilities.yml` to the live plugin data folder.
5. Restart the server.

## In-game Smoke Test
Run:

```text
/aereth abilitygui
/aereth abilityloadout
/aereth abilityactivation
```

Expected:
- Current Discipline shows four abilities across rank 1-4.
- Locked/unlocked state follows Discipline rank.
- Existing loadout flow still works.
- Existing activation/effect/cooldown flow still works.

## Rank Test
Run:

```text
/aereth setdisciplinerank SpitFire720 1
/aereth abilitygui
/aereth setdisciplinerank SpitFire720 4
/aereth abilitygui
```

Expected:
- Rank 1 shows only rank 1 unlocked.
- Rank 4 shows all four abilities unlocked for the selected Discipline.

## Regression Checks
- `/aereth abilityloadout` still opens.
- `/aereth abilityactivation` still opens.
- `/aereth abilityactivate SpitFire720 1` still activates an equipped unlocked ability.
- `/aereth abilitycooldowns SpitFire720` still lists active cooldowns.
- No console errors.
