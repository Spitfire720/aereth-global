# Build 01 QA Start Here

Region: The Lantern Marches  
Hub: Lantern's Rest  
Build: 01  
Purpose: First playable starter vertical slice

## Goal

Confirm that the first 30-60 minute starter slice works as one coherent server experience.

This QA pass validates:

- WorldGuard protection and region logic
- BetonQuest starter dialogue and quest tags
- MythicMobs starter encounter safety
- Oraxen starter props and resource pack behavior
- FragmentEngine state ownership and persistence
- Non-OP player journey from arrival to first meaningful progression moment

## Absolute rule

Do not mark Build 01 as ready because one system works in isolation.

Build 01 only passes when the full vertical slice works on a fresh non-OP player account without admin intervention.

## Required evidence

Capture screenshots or notes for:

1. Player spawn / arrival area
2. Registrar interaction
3. Race / Remnant registration state
4. First quest accepted
5. The Bent Road route reached
6. First anomaly interaction
7. First starter mob encounter
8. Return to Lantern's Rest
9. Quest completion state
10. Relog persistence after completion

## Test order

1. Preflight checks
2. Admin system checks
3. Non-OP clean account run
4. Reload/relog persistence checks
5. Failure and rollback checks
6. Release gate review
