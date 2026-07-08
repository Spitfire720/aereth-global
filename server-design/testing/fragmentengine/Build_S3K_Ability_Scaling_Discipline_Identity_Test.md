# Build S3K Test - Ability Scaling + Discipline Identity

## Commands
```text
/aereth abilityactivation
/aereth setlevel SpitFire720 1
/aereth setdisciplinerank SpitFire720 1
/aereth abilityactivation
/aereth setlevel SpitFire720 40
/aereth setdisciplinerank SpitFire720 4
/aereth abilityactivation
/aereth abilityactivate SpitFire720 1
/aereth abilitycooldowns SpitFire720
```

## Expected
- Ability Activation GUI shows Role and Power Scale.
- Scaled cooldown differs from base cooldown.
- Scaled cost appears in GUI/hotbar lore.
- Activation writes abilities.activation.last.scaling.
- Effects feel stronger/longer at higher level/rank.
- Cooldown still blocks repeat activation.
- Hotbar binds still activate abilities.
- No console errors.
