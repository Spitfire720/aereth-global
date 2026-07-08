# Lantern Marches Build 01 Full Slice Test

## Objective

Validate that a new player can complete the Build 01 starter experience from Lantern's Rest through the first anomaly/combat loop and back to a stable progression state.

## Test type

Full vertical slice acceptance test.

## Preconditions

- WorldGuard regions deployed and tested
- BetonQuest package deployed and reloads cleanly
- MythicMobs starter mobs deployed and manually tested
- Oraxen starter props deployed and pack tested
- FragmentEngine integration commands/placeholders verified or safely stubbed
- Test account is non-OP

## Test script

1. Log in as clean non-OP test player.
2. Confirm spawn/arrival in Lantern's Rest.
3. Attempt to break/place blocks in protected area. Expected: denied.
4. Speak to Registrar Elian Voss.
5. Confirm Remnant/race registration state.
6. Speak to Archivist Maera Vale.
7. Follow route toward The Bent Road.
8. Speak to Road Warden Tollen if present.
9. Reach Hollowglass Pool or assigned anomaly point.
10. Trigger anomaly objective.
11. Encounter and defeat or survive starter mob.
12. Return to Lantern's Rest.
13. Complete quest step.
14. Log out and back in.
15. Confirm state persists.

## Pass criteria

- No admin intervention required.
- No critical console errors.
- Player receives understandable direction.
- Player cannot grief protected zones.
- Starter mobs are survivable.
- Quest state and FragmentEngine state persist.

## Fail criteria

- Player gets stuck without instructions.
- Quest state breaks.
- Backend state disappears after relog.
- Mobs are lethal beyond starter expectations.
- Console repeatedly throws plugin errors.

## Result

- [ ] Pass
- [ ] Fail
- [ ] Pass with issues

Notes:
