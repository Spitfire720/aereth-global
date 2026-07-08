# Build S3A - Discipline Codex UI Test

## Pre-test

Confirm S2A/S2B are already working:

```text
/aereth card
/aereth intentgui
```

Confirm the server has loaded FragmentEngine:

```text
/pl
/aereth status
```

Expected:

```text
FragmentEngine green/enabled
/aereth status returns version and system counts
```

## Test 1 - Open Discipline Codex from command

Command:

```text
/aereth disciplinegui
```

Expected:

```text
Discipline Codex opens
Header shows current character
Discipline entries are visible
Back / Clear / Refresh / Close controls are visible
```

## Test 2 - Open Discipline Codex from Character Card

Commands/actions:

```text
/aereth card
Click Discipline
```

Expected:

```text
Discipline Codex opens
No console error
```

## Test 3 - Locked selection below level 16

If character is below level 16:

```text
/aereth disciplinegui
Click any Discipline
/aereth discipline SpitFire720
```

Expected:

```text
Chat says Discipline locked / required level
Discipline remains Unformed
No console error
```

## Test 4 - Unlock test at level 16

Dev command:

```text
/aereth setlevel SpitFire720 16
/aereth disciplinegui
```

Action:

```text
Click Vanguard or another Discipline
```

Expected:

```text
Chat confirms Discipline selection
GUI refreshes
Selected Discipline appears highlighted
```

Verify:

```text
/aereth discipline SpitFire720
/aereth disciplineprogress SpitFire720
/aereth abilities SpitFire720
```

Expected:

```text
Discipline is selected
Rank is 1 / Initiate
Ability summary reads selected Discipline
```

## Test 5 - Replace Discipline

Action:

```text
/aereth disciplinegui
Click a different unlocked Discipline
/aereth discipline SpitFire720
```

Expected:

```text
New Discipline replaces old one
Rank resets to 1
Profession/remnant state update through backend
No console error
```

## Test 6 - Clear Discipline

Action:

```text
/aereth disciplinegui
Click Clear Current Discipline
/aereth discipline SpitFire720
```

Expected:

```text
Discipline becomes Unformed
Rank becomes 0 / Untrained
Remnant state becomes UNCOMMITTED
```

## Test 7 - GUI protection

Actions:

```text
Try dragging GUI items
Try shift-clicking GUI items
Try moving player inventory items while GUI is open
```

Expected:

```text
GUI items cannot be stolen
No ghost items remain after closing/reopening
No console error
```

## Test 8 - Restart persistence

Actions:

```text
Select a Discipline
Restart server
/aereth discipline SpitFire720
/aereth disciplinegui
```

Expected:

```text
Selected Discipline persists after restart
GUI shows same selected Discipline
```

## Pass condition

S3A passes when Discipline selection, replacement, clearing, command access, Character Card access, and inventory protection all work without console errors.
