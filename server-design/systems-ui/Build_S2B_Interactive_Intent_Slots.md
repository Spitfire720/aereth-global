# Build S2B - Interactive Intent Slots

## Status

Implementation pack prepared.

## Build Target

Upgrade the Build S2A Intent Slots GUI from a read-only status view into a player-facing assignment interface.

## Scope

Build S2B adds:

- Slot selection in the Intent Slots GUI.
- Intent assignment by clicking an Intent definition.
- Intent replacement by clicking another Intent while a slot is selected.
- Clear selected slot button.
- GUI refresh after set/clear.
- Locked-slot protection based on the existing IntentService max-slot rules.
- Continued inventory protection against dragging, stealing, or moving menu items.

## Out of Scope

Build S2B does not add:

- New Intent definitions.
- Fragment mutation logic.
- Outcome Effects.
- Combat stat buffs from Intent.
- Discipline selection.
- Admin editing for other players.

## Backend Used

The GUI uses existing FragmentEngine backend functions:

```java
IntentService.setIntent(player, slot, intentId)
IntentService.clearIntent(player, slot)
IntentService.summary(character)
```

The service already recalculates:

- intent.unlocked-slots
- intent.slots-used
- intent.primary
- intent.pressure
- intent.stability-impact

## Player Flow

1. Player runs `/aereth intentgui` or opens Intent Slots from the Character Card.
2. Player clicks an unlocked slot.
3. GUI highlights the selected slot.
4. Player clicks an Intent definition.
5. FragmentEngine saves the selection to the active character file.
6. GUI refreshes and shows updated pressure, stability impact, primary Intent, and slot contents.

## Slot Unlock Rules

The current backend exposes up to 4 Intent slots:

- Level 1: Slot 1
- Level 16: Slot 2
- Level 36: Slot 3
- Level 51: Slot 4

The GUI follows the backend rather than inventing a separate UI rule.

## Interaction Rules

- Locked slots show as barriers.
- Clicking a locked slot sends a warning.
- Clicking an Intent before selecting a slot sends a warning.
- Clicking Clear without selecting a slot sends a warning.
- Clicking Clear after selecting a slot clears that slot.
- Clicking Back returns to the Character Card.
- Clicking Close closes the inventory.
- Inventory click and drag events remain cancelled for Aereth GUIs.

## Design Notes

Intent is still a meaning/direction system, not a raw stat class.

The GUI is deliberately simple:

- Select a slot.
- Choose meaning.
- Accept pressure.
- Let later systems read the result.

That is the foundation needed before Outcome Effects and world reactions can behave intelligently.
