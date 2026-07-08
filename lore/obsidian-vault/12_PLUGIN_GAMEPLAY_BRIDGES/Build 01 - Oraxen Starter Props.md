# Build 01 - Oraxen Starter Props

## Purpose

This note defines the Oraxen visual layer for the Lantern Marches Build 01 starter vertical slice.

Oraxen is used for custom prop visuals, item icons, placed decorative objects, light-emitting markers, and resource-pack presentation.

## Canon boundary

Oraxen does **not** own:

- Remnant condition
- Race identity
- Fragment state
- Intent state
- Discipline state
- Title recognition
- Quest progression
- Permanent RPG stats

Those belong to FragmentEngine and related RPG systems.

## Build 01 prop set

| Prop ID | Canon object | Location usage | Gameplay purpose |
| --- | --- | --- | --- |
| `lm_registry_ledger` | Registry Ledger | Lantern's Rest | Registration desk / identity onboarding visual |
| `lm_bent_road_marker` | Bent Road Marker | The Bent Road | Route marker / starter-road signposting |
| `lm_hollowglass_shard` | Hollowglass Shard | Hollowglass Pool | Memory anomaly visual |
| `lm_lantern_waymarker` | Lantern Waymarker | Roads / crossings | Safe route readability |
| `lm_archive_crate` | Archive Crate | Quiet Ledger / outposts | Archive faction dressing |
| `lm_quiet_ledger_stamp` | Quiet Ledger Stamp | Quiet Ledger | Administrative/registry flavor |
| `lm_ash_mile_charcoal_post` | Ash Mile Charcoal Post | Ash Mile | Burnt route marker |
| `lm_mirrorfen_reed_charm` | Mirrorfen Reed Charm | Mirrorfen | Folk warding / memory weirdness visual |

## Implementation notes

- Use furniture for placed decorative objects that need rotation, barriers, light, or custom models.
- Use simple item icons for inventory-only or admin placement helper objects.
- Keep Oraxen props non-authoritative. If a prop triggers a quest, BetonQuest/FragmentEngine should own the state change.
- Build 01 should prioritize simple props with readable silhouette over complex animation.

## Deployment status

- Draft config created.
- Real textures/models required.
- Dev build syntax must be checked before live deployment.
- Live placement should wait until WorldGuard and blockout tests pass.
