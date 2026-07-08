# FragmentEngine Build 01 Test

## Test objective

Confirm the starter RPG state writes correctly and persists.

## Pre-test

- [ ] WorldGuard regions pass.
- [ ] BetonQuest starter package loads.
- [ ] Test player has valid race state.
- [ ] FragmentEngine has no startup errors.

## Direct state test

1. Run player debug check.
2. Confirm race_id exists.
3. Mark registration complete.
4. Mark Hollowglass contact.
5. Mark Intent prompt.
6. Relog player.
7. Confirm all values persist.

## Starter flow test

1. Reset test player starter state.
2. Enter Lantern's Rest as non-OP.
3. Speak to Registrar.
4. Speak to Archivist.
5. Travel The Bent Road.
6. Trigger Hollowglass Pool contact.
7. Return to Lantern's Rest.
8. Confirm starter flow complete.
9. Relog.
10. Confirm FragmentEngine and BetonQuest still agree.

## Pass criteria

- No console errors.
- No missing race state.
- No duplicate registration writes.
- No state lost after relog.
- No quest progression blocked by RPG state mismatch.
