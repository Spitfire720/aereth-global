# Build S3C - Ability Loadout UI Test

## Objective

Confirm that players can equip unlocked abilities into loadout slots through a GUI and that the selection persists.

## Preconditions

- FragmentEngine S3B is already deployed.
- `/aereth abilitygui` opens Ability Codex.
- Player has an active character.
- Player has a selected Discipline.

## Test commands

```text
/aereth setlevel SpitFire720 16
/aereth disciplinegui
/aereth abilitygui
/aereth abilityloadout
```

For rank progression testing:

```text
/aereth adddisciplinexp SpitFire720 1000
/aereth abilityloadout
```

## Test cases

### 1. Open Ability Loadout

Run:

```text
/aereth abilityloadout
```

Expected:

```text
Ability Loadout GUI opens.
No command help dump.
No console error.
```

### 2. Select a loadout slot

Click Loadout Slot 1.

Expected:

```text
Slot 1 becomes selected.
Player receives selected-slot message.
Menu refreshes.
```

### 3. Equip unlocked ability

Click an unlocked ability.

Expected:

```text
Ability appears in selected loadout slot.
Ability appears marked as equipped.
Character YAML updates under abilities.loadout.slots.slot1.
```

### 4. Block locked ability

Click a locked ability.

Expected:

```text
Ability is not equipped.
Player receives locked ability message.
No YAML loadout change.
```

### 5. Clear selected slot

Click Clear Selected Slot.

Expected:

```text
Selected slot becomes empty.
abilities.loadout.active updates.
abilities.loadout.count updates.
```

### 6. Prevent duplicate equips

Equip the same ability into Slot 1, then select Slot 2 and equip the same ability.

Expected:

```text
Ability moves to Slot 2.
Slot 1 clears.
Ability is not duplicated.
```

### 7. GUI protection

Try to drag, shift-click, or steal items.

Expected:

```text
GUI items cannot be moved into player inventory.
```

## Pass condition

Build S3C passes when:

```text
/aereth abilityloadout opens
Abilities can be equipped
Locked abilities are blocked
Slots persist to YAML
Clear works
Duplicate prevention works
GUI protection holds
No console errors
```
