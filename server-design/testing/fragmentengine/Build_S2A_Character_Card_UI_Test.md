# Build S2A - Character Card UI Test

## Pre-check

```text
Plugin compiles.
Server starts without console errors.
A test character exists for your player.
```

## Commands

```text
/aereth card
/aereth intentgui
/aereth character SpitFire720
/aereth intent SpitFire720
/aereth discipline SpitFire720
```

## Test cases

### 1. Character Card opens

Expected:

```text
A 6-row inventory opens titled Character Card.
Player head, race, level, fragments, intent, discipline, and ability buttons appear.
```

### 2. Intent navigation

Action:

```text
Click Intent Slots inside Character Card.
```

Expected:

```text
Intent Slots GUI opens.
```

### 3. Direct Intent GUI command

Action:

```text
/aereth intentgui
```

Expected:

```text
Intent Slots GUI opens directly.
```

### 4. Click cancellation

Action:

```text
Click several menu items.
```

Expected:

```text
Items cannot be removed, moved, duplicated, or dragged.
```

### 5. Player without active character

Action:

```text
Use /aereth card on a player with no active character.
```

Expected:

```text
Player receives: No active character.
No console error.
```

## Pass condition

```text
Both GUIs open correctly.
Navigation works.
No item stealing.
No console spam.
Existing text commands still work.
```
