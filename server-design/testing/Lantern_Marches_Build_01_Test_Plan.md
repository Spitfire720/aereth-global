# Lantern Marches Build 01 Test Plan

## Test objective

Verify that the first Lantern Marches starter loop works as a playable server slice without breaking plugin boundaries or confusing the player.

## Test account setup

Use a clean player account or reset test data.

Minimum test states:

1. New player with no Aereth profile.
2. Player after registration.
3. Player after first quest completion.
4. Player who logs out mid-quest.
5. Player who dies during the road/anomaly step.

## Test 1 - Spawn and orientation

Steps:

1. Join server as clean player.
2. Confirm spawn in Lantern's Rest or intended starting point.
3. Look for visible path to The Quiet Ledger.
4. Walk to registry NPC.

Expected:

- Player does not spawn in danger.
- Hub reads as a starter settlement.
- Player can find the first NPC without external instructions.

## Test 2 - Registry interaction

Steps:

1. Talk to Archivist/Registrar NPC.
2. Accept registration dialogue.
3. Confirm quest starts.
4. Check any displayed placeholders or journal entries.

Expected:

- Dialogue opens.
- Text does not expose backend jargon.
- Quest state updates.
- No console errors.

## Test 3 - Bent Road route

Steps:

1. Follow quest toward The Bent Road.
2. Check route markers.
3. Enter road encounter zone.
4. Confirm hub protection does not extend too far.

Expected:

- Route is readable.
- Starter enemies appear only outside safe zones.
- Player can retreat.

## Test 4 - Roadstray combat

Steps:

1. Engage Roadstray.
2. Fight with starter gear.
3. Check damage, speed, and clarity.
4. Confirm drops.

Expected:

- Combat is survivable.
- Enemy is readable.
- Drops are not economy-breaking.

## Test 5 - Hollowglass disturbance

Steps:

1. Reach Hollowglass Pool.
2. Trigger anomaly objective.
3. Encounter Hollowglass Wisp or equivalent.
4. Complete investigation step.

Expected:

- The area feels different from normal wilderness.
- Objective completes reliably.
- The encounter teaches RE:FRAGMENT weirdness without overexplaining.

## Test 6 - Return loop

Steps:

1. Return to Lantern's Rest.
2. Talk to registry NPC.
3. Complete quest.
4. Confirm reward/progression hook.

Expected:

- Quest completes.
- Player understands there are bigger systems ahead.
- No Fragment or Intent system is faked by the wrong plugin.

## Test 7 - Logout/death resilience

Steps:

1. Start quest.
2. Log out during road objective.
3. Log back in.
4. Die during encounter.
5. Return to quest.

Expected:

- Quest state persists.
- Player is not soft-locked.
- Death does not duplicate rewards.
