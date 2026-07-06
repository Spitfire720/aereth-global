# World Blockout and Coordinate Capture

## Build areas

Capture two corner coordinates for each WorldGuard region.

| Area | Purpose | Priority | Region ID |
| --- | --- | --- | --- |
| Lantern's Rest | Starter hub / safe zone | Critical | `aereth_lanterns_rest` |
| Registry Hall | Remnant registration / intro NPC | Critical | `aereth_lanterns_rest_registry` |
| The Bent Road | First route out | Critical | `aereth_bent_road` |
| Old Wick Post | Warden post / travel checkpoint | High | `aereth_old_wick_post` |
| Mirrorfen | Reflection anomaly | High | `aereth_mirrorfen` |
| Hollowglass Pool | Low-risk Fragment disturbance | High | `aereth_hollowglass_pool` |
| Ash Mile | Burnt roadside combat stretch | Medium | `aereth_ash_mile` |
| Saint's Crossing | Route junction / lore marker | Medium | `aereth_saints_crossing` |
| Quiet Ledger | Archive/record landmark | Medium | `aereth_quiet_ledger` |

## Coordinate template

```text
World name:
Area:
Region ID:
Corner 1: x=___ y=___ z=___
Corner 2: x=___ y=___ z=___
Notes:
```

## Minimum blockout standard

Lantern's Rest must include:

- Arrival point.
- Registry desk or small archive post.
- Stable NPC area.
- Road exit toward The Bent Road.
- Visual viewline toward the larger world.
- At least one safe player gathering space.

The Bent Road must include:

- Broken road geometry.
- At least one side path.
- Low-risk combat area nearby, but not inside safe travel lane.
- Clear route back to Lantern's Rest.

Mirrorfen / Hollowglass Pool must include:

- Reflection or glass-like visual language.
- Distortion markers.
- No high-risk enemy density.
- Clear test path for the first anomaly quest.
