# Build 01 - BetonQuest Start Here

## Current phase

Lantern Marches Build 01 has reached the quest-flow preparation stage.

Do this only after:

1. Lantern's Rest rough blockout exists.
2. The Bent Road rough blockout exists.
3. Hollowglass Pool rough blockout exists.
4. WorldGuard regions are applied and tested.
5. Non-OP movement and interaction tests pass.

## Deployment principle

Deploy in this order:

1. Create package folder only.
2. Add package.yml with no active objective triggers.
3. Add NPC conversations.
4. Add simple tag events.
5. Add one objective at a time.
6. Test with a fresh player profile.
7. Only then connect later FragmentEngine hooks.

Do not connect MythicMobs combat steps yet unless WorldGuard and spawn safety are confirmed.

## Starter questline

Name: `After RE:FRAGMENT`

Internal package ID:

```text
lantern_marches_build1
```

Primary flags/tags:

```text
aereth_started
remnant_registered
quiet_ledger_seen
bent_road_assigned
bent_road_reached
hollowglass_investigated
lm_intro_complete
```

## NPCs

```text
Registrar Elian Voss - registration / Remnant acknowledgement
Archivist Maera Vale - Quiet Ledger / RE:FRAGMENT explanation
Road Warden Tollen - Bent Road assignment
```

## Required coordinates before live deployment

```text
LANTERNS_REST_REGISTRY_NPC = TBD
QUIET_LEDGER_LOCATION = TBD
BENT_ROAD_MARKER = TBD
HOLLOWGLASS_POOL_CENTER = TBD
RETURN_REGISTRY_POINT = TBD
```

## Success condition

A fresh non-OP test player can complete the loop:

```text
Spawn / enter Lantern's Rest -> Register -> Quiet Ledger -> Bent Road -> Hollowglass Pool -> Return -> Completion tag
```
