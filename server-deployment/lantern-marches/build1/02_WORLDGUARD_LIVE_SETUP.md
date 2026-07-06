# WorldGuard Live Setup

Use the draft commands here:

```text
server-config-drafts/worldguard/lantern-marches/build1/worldguard_region_commands.md
```

## Recommended region priority logic

| Region | Suggested priority | Notes |
| --- | ---: | --- |
| `aereth_lanterns_rest` | 50 | Main safe hub. |
| `aereth_lanterns_rest_registry` | 70 | Nested protected intro location. |
| `aereth_bent_road` | 30 | Protected route. |
| `aereth_old_wick_post` | 40 | Semi-safe checkpoint. |
| `aereth_mirrorfen` | 20 | Anomaly route area. |
| `aereth_hollowglass_pool` | 20 | Anomaly combat/tutorial area. |
| `aereth_ash_mile` | 15 | Starter danger area. |
| `aereth_saints_crossing` | 25 | Route junction. |
| `aereth_quiet_ledger` | 25 | Archive landmark. |

## Safety flags for hub

Recommended for Lantern's Rest:

```text
/rg flag aereth_lanterns_rest pvp deny
/rg flag aereth_lanterns_rest mob-spawning deny
/rg flag aereth_lanterns_rest creeper-explosion deny
/rg flag aereth_lanterns_rest enderman-grief deny
/rg flag aereth_lanterns_rest build deny
```

Use member/owner exceptions only after region testing.

## Test after creating each region

```text
/rg info <region_id>
/rg list
/rg flag <region_id>
```

Then walk the edges in-game. If the road feels like it is protected by vibes rather than coordinates, fix the cuboid. Minecraft players will find every gap because apparently entropy has a username.
