# Oraxen Starter Props Test - Lantern Marches Build 01

## Pre-test

- Oraxen version recorded.
- Server version recorded.
- Client version recorded.
- Resource pack loads.
- Test world is backed up.

## Admin item test

Run:

```text
/oraxen iteminfo lm_registry_ledger
/oraxen iteminfo lm_bent_road_marker
/oraxen iteminfo lm_hollowglass_shard
/oraxen iteminfo lm_lantern_waymarker
/oraxen iteminfo lm_archive_crate
```

Expected:

- No console errors.
- Items resolve.
- Custom model data / model info appears where supported.

## Placement test

Place each furniture item in admin test zone.

Check:

- appears correctly
- rotates correctly
- has expected light
- hitbox/barrier does not block paths badly
- can be removed by admin
- does not drop unintended duplicates
- survives relog/server restart if expected

## Non-OP test

Use non-OP account.

Check:

- player receives resource pack
- player sees models
- player cannot break protected props in WorldGuard region
- player cannot steal storage contents unless intended
- no permission errors spam chat

## Pass criteria

All Build 01 props can be viewed by a normal player and safely placed in protected areas without console errors.
