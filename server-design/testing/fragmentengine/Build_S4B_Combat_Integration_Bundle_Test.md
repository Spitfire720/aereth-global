# Build S4B - Combat Integration Bundle Test

## Compile

```powershell
Set-Location "C:\Users\Bernardo\Desktop\Aereth global\FragmentEngine_Build1_Source"
mvn clean package
```

Expected:

```text
BUILD SUCCESS
```

## Deploy

Use safe copy only after compile succeeds.

```powershell
Set-Location "C:\Users\Bernardo\Desktop\Aereth global"

$Jar = "C:\Users\Bernardo\Desktop\Aereth global\FragmentEngine_Build1_Source\target\FragmentEngine-1.15.0.jar"

if (!(Test-Path $Jar)) {
    throw "Jar not found. Maven failed or target is missing. Do not deploy."
}

rclone copyto $Jar "godlike:./plugins/FragmentEngine-1.15.0.jar"
rclone lsf "godlike:./plugins" --files-only --include "FragmentEngine*.jar"
```

Restart from Godlike panel.

## In-game Test

Prepare an equipped ability:

```text
/aereth abilityloadout
/aereth abilityactivation
/aereth abilitysync
```

Spawn or find a non-player living entity.

Run:

```text
/aereth abilityactivate SpitFire720 1
/aereth abilitycooldowns SpitFire720
```

Also test hotbar:

```text
Sneak + swap hands
Right-click bound ability item while aiming at a mob
```

## Expected

- Ability activates.
- Resource cost still applies.
- Cooldown still applies.
- Targeting still resolves.
- Scaling still affects potency/duration/radius.
- PvE target can take controlled damage.
- Player targets do not take damage.
- Area effects affect non-player living entities only.
- No console errors.

## Safety Check

Aim at another player and activate a hostile-looking ability.

Expected:

- No player damage.
- Activation does not crash.
- Detail/status should mention blocked or no valid PvE damage.
