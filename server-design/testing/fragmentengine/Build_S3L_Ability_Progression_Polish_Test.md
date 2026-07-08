# Build S3L - Ability Progression Polish Test

## Compile test

```powershell
Set-Location "C:\Users\Bernardo\Desktop\Aereth global\FragmentEngine_Build1_Source"
mvn clean package
```

Expected:

```text
BUILD SUCCESS
```

## Live test

Restart the server after deploying the jar.

Run:

```text
/aereth abilitygui
/aereth abilityactivation
/aereth setdisciplinerank SpitFire720 1
/aereth abilitygui
/aereth setdisciplinerank SpitFire720 4
/aereth abilitygui
/aereth abilityactivation
```

Expected:

```text
Ability Codex shows completion percentage
Ability Codex shows next reveal
Ability Codex shows rank roadmap
Locked abilities show ranks-away guidance
Rank 4 shows full path revealed for current Discipline
Ability Activation GUI shows progression roadmap panel
Activation, resources, scaling, targeting, and cooldowns still work
No console errors
```

## Regression checks

```text
/aereth abilityloadout
/aereth abilityactivation
Sneak + swap hands
Right-click a hotbar-bound ability
```

Expected:

```text
Loadout still works
Hotbar activation still works
Resources still spend
Cooldown still applies
Effects still route
```
