# Blockout Acceptance Test

## Test Profile

Use a normal survival/adventure player account, not only OP/admin.

```text
Tester: _____________________
World: ______________________
Date: _______________________
Build commit: _______________
```

## Test 1 - Arrival Clarity

Start at Lantern's Rest center.

Pass if:

- The player immediately understands this is a safe hub.
- There is a visible central landmark.
- There is a visible road exit.
- The route toward The Bent Road is visually obvious.

Fail if:

- The player spins around lost.
- All exits look equally important.
- The hub feels like a random village.

## Test 2 - Travel Flow

Walk from Lantern's Rest to Hollowglass Pool without flying.

Pass if:

- Route is readable.
- Travel time feels short enough for intro gameplay.
- There are 1-2 visual hooks along the way.
- The player reaches Hollowglass Pool without needing coordinates.

Target walking time:

```text
3-6 minutes from hub to anomaly site
```

## Test 3 - Anomaly Readability

Approach Hollowglass Pool.

Pass if:

- The area clearly feels different from normal terrain.
- The pool or shard feature is visually central.
- There is enough space for a small encounter.
- There is enough space for an NPC/objective interaction.

## Test 4 - WorldGuard Readiness

After region selection, confirm:

- All main regions can be defined.
- Lantern's Rest is protected.
- Hollowglass Pool has inner/outer region separation.
- There is room for future mob spawn controls.

## Test 5 - Build Scope Control

Pass if the team did not accidentally build half the continent.

The correct Build 01 result is ugly-playable. Pretty-unplayable is not a win, no matter how many lanterns were emotionally placed.
