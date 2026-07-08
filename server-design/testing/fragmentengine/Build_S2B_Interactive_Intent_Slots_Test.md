# Build S2B - Interactive Intent Slots Test

## Preconditions

- Build S2A is already deployed and working.
- Server is restarted after deploying the S2B jar.
- Player has an active FragmentEngine character.
- FragmentEngine v1.15.0 is enabled.
- `/aereth card` and `/aereth intentgui` open their GUIs.

## Test 1 - Open Intent GUI

Command:

```text
/aereth intentgui
```

Expected:

- Intent Slots GUI opens.
- Slot buttons are visible.
- Intent definition buttons are visible.
- Back, Clear, Refresh, and Close controls are visible.

## Test 2 - Select Unlocked Slot

Action:

- Click Slot 1.

Expected:

- Slot 1 becomes selected.
- Player receives a selected-slot message.
- Top profile item shows selected slot.
- Intent definition items say they can be assigned to Slot 1.

## Test 3 - Assign Intent

Action:

- With Slot 1 selected, click an Intent definition.

Expected:

- Slot 1 updates to that Intent.
- Player receives success message.
- Primary Intent updates if this is the first active slot.
- Pressure and Stability Impact update.
- No console error appears.

Confirm with:

```text
/aereth intent SpitFire720
```

Expected:

- Slot 1 appears in the text command output.
- Primary matches the selected Intent.

## Test 4 - Replace Intent

Action:

- Select Slot 1 again.
- Click a different Intent definition.

Expected:

- Slot 1 changes to the new Intent.
- The GUI refreshes.
- `/aereth intent SpitFire720` reflects the replacement.

## Test 5 - Clear Intent

Action:

- Select Slot 1.
- Click Clear Selected Slot.

Expected:

- Slot 1 becomes empty.
- Player receives clear message.
- Pressure and Stability Impact recalculate.
- `/aereth intent SpitFire720` shows the slot as cleared or absent.

## Test 6 - Locked Slot Protection

Action:

- Click a locked slot, such as Slot 3 or Slot 4 on a lower-level character.

Expected:

- Player receives locked-slot warning.
- No YAML change occurs.
- GUI refreshes safely.
- No console error appears.

## Test 7 - No Slot Selected Protection

Action:

- Open Intent GUI.
- Click an Intent definition without selecting a slot.

Expected:

- Player receives warning to select a slot first.
- No YAML change occurs.
- GUI stays usable.

## Test 8 - Inventory Protection

Actions:

- Try to drag GUI items.
- Try shift-clicking GUI items.
- Try moving inventory items into the GUI.

Expected:

- GUI items cannot be stolen.
- GUI layout remains intact.
- No item duplication occurs.
- No console error appears.

## Test 9 - Navigation

Actions:

- Open `/aereth card`.
- Click Intent Slots.
- Click Back.
- Click Refresh.
- Click Close.

Expected:

- Character Card opens.
- Intent Slots opens.
- Back returns to Character Card.
- Refresh reloads Intent state.
- Close closes the GUI.

## Pass Criteria

Build S2B passes if:

- Players can assign, replace, and clear Intent slots.
- Locked slots cannot be edited.
- GUI protection still works.
- Text commands reflect GUI changes.
- No FragmentEngine exceptions appear in logs.
