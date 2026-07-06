# WorldGuard Region Plan - Lantern Marches

## Purpose

Define a clean region hierarchy for the Lantern Marches starter slice.

## Naming convention

```text
aereth_lm_<area>
```

Examples:

```text
aereth_lm_lanterns_rest_core
aereth_lm_bent_road
aereth_lm_mirrorfen
aereth_lm_old_wick_post
aereth_lm_hollowglass_pool
```

## Region hierarchy

```text
aereth_lantern_marches
  aereth_lm_lanterns_rest
    aereth_lm_lanterns_rest_core
    aereth_lm_registry_hall
    aereth_lm_market_row
    aereth_lm_waystone_plaza
  aereth_lm_bent_road
  aereth_lm_mirrorfen
  aereth_lm_old_wick_post
  aereth_lm_ash_mile
  aereth_lm_saints_crossing
  aereth_lm_hollowglass_pool
```

## Suggested flags

### Lantern's Rest core

```text
mob-spawning deny
pvp deny
build deny
use allow
interact allow
```

### Roads

```text
pvp deny initially
mob-spawning allow
build deny
use allow
interact allow
```

### Anomaly areas

```text
mob-spawning allow
entry allow
build deny
use allow
interact allow
```

## Region functions

| Region | Use |
| --- | --- |
| `aereth_lantern_marches` | Parent region, general rules |
| `aereth_lm_lanterns_rest_core` | Safe hub |
| `aereth_lm_registry_hall` | Character/registry onboarding |
| `aereth_lm_bent_road` | First route/tutorial area |
| `aereth_lm_mirrorfen` | Puzzle/anomaly area |
| `aereth_lm_old_wick_post` | Combat pocket |
| `aereth_lm_hollowglass_pool` | Scripted Fragment disturbance |

## Integration notes

WorldGuard does not own progression. It protects areas and provides location triggers/conditions for BetonQuest and other systems.

Do not use WorldGuard as the RPG brain. That is FragmentEngine's job, and yes, apparently we have to say this so plugins don't start wearing crowns.
