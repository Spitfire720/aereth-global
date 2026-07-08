# Build S4C Test - Ability Slot Framework Hardening

## Compile

```powershell
Set-Location "C:\Users\Bernardo\Desktop\Aereth global\FragmentEngine_Build1_Source"
mvn clean package
```

Expected:

```text
BUILD SUCCESS
```

## Runtime test

Restart from the hosting panel after deploying the jar.

Run:

```text
/aereth abilityloadout
/aereth abilityactivation
/aereth abilitysync
/aereth abilityactivate SpitFire720 1
```

Expected:

- Ability Loadout opens.
- Slot Diagnostics item appears.
- Valid slots show clean status.
- Ability Activation still works.
- Hotbar sync still works.
- Cooldowns/resources/scaling still work.
- No console errors.

## Broken-slot test

Manually edit a test character YAML or use an existing broken state if present:

```yaml
abilities:
  loadout:
    slots:
      slot1: vanguard_guardian_stance
      slot2: fake_ability_id
      slot3: vanguard_guardian_stance
      slot4: reaver_blood_pressure
```

Open:

```text
/aereth abilityloadout
```

Expected:

- Unknown ability is cleared.
- Duplicate ability is cleared.
- Wrong Discipline ability is cleared.
- Summary is rebuilt.
- Diagnostics shows cleaned issues.
- Character YAML gains `abilities.loadout.framework` metadata.

## Locked-slot test

Set rank down:

```text
/aereth setdisciplinerank SpitFire720 1
/aereth abilityloadout
```

Expected:

- Slots above current rank are cleared if they had data.
- Slot Diagnostics explains locked clears.
- Slot 1 remains usable.
