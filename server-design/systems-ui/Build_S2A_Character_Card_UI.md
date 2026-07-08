# Build S2A - Character Card UI

## Status

Implementation pack.

## Goal

Expose the existing FragmentEngine systems through inventory GUIs without creating quests or world content.

## Commands

```text
/aereth card
/aereth intentgui
```

## Character Card displays

- Player head / name
- Race
- Level / XP / phase
- HP / attack / defense / magic / resistance
- Fragment capacity / pressure / stability / erasure pressure
- Intent primary / slots used / pressure / stability impact
- Discipline display / selected / unlocked / rank / XP
- Abilities summary
- Remnant state / profession

## Intent Slots displays

- Active slots
- Locked slots
- Primary Intent
- Intent pressure
- Stability impact
- Known Intent definitions

## Design decision

The backend currently supports up to 4 Intent slots by level. S2A displays 4 slots because the UI should match the service layer, even if the original design sketch used 3. Fighting your own backend in the UI is how systems develop trust issues.

## Non-goals

- No quest/tutorial flow.
- No Oraxen icons.
- No Intent mutation from GUI.
- No Discipline selection from GUI.
- No world dependency.

## Build S2B follow-up

- Select target slot.
- Click Intent to assign.
- Clear selected slot.
- Persist via `IntentService.setIntent` / `clearIntent`.
- Add permission checks if public editing is restricted.
