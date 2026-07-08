# Build S3D Test - Ability Activation Foundation

## Prerequisites

- Build S3C applied and live.
- Character has active Discipline.
- At least one unlocked ability is equipped in Ability Loadout.

Suggested dev setup:

```text
/aereth setlevel SpitFire720 16
/aereth setdiscipline SpitFire720 vanguard
/aereth adddisciplinexp SpitFire720 1000
/aereth abilityloadout
```

Equip Guardian Stance in slot 1.

## Test 1 - Activation GUI opens

Command:

```text
/aereth abilityactivation
```

Expected:

```text
Ability Activation GUI opens.
Loadout slots are visible.
Equipped ability appears in slot 1.
Back, refresh, close buttons work.
```

## Test 2 - Activate equipped ability

Click equipped slot 1 in the Activation GUI.

Expected:

```text
Chat says ability activated.
GUI refreshes.
Slot shows cooldown state.
No console errors.
```

## Test 3 - Cooldown blocks repeated activation

Immediately click the same slot again.

Expected:

```text
Chat says ability is on cooldown.
Remaining seconds are displayed.
No duplicate activation state corruption.
```

## Test 4 - Cooldown command

Command:

```text
/aereth abilitycooldowns SpitFire720
```

Expected:

```text
The active cooldown is listed.
Slot, ability display, and remaining seconds are shown.
```

## Test 5 - Empty slot blocked

Click an empty unlocked slot.

Expected:

```text
Chat says the slot is empty.
No YAML corruption.
```

## Test 6 - Locked slot blocked

Use a low-rank character and click a locked activation slot.

Expected:

```text
Locked slot cannot activate.
No console error.
```

## Test 7 - Persistence

After activation, inspect character YAML or use later diagnostics.

Expected fields:

```yaml
abilities:
  cooldowns:
  activation:
    last:
```
