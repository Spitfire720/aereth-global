# WorldGuard Region Plan - Build 01

## Region Naming

| Region ID | Display Name | Priority | Purpose |
| --- | --- | ---: | --- |
| lm_build01_total_slice | Build 01 Total Slice | 1 | Parent/general test area |
| lm_lanterns_rest_safe | Lantern's Rest Safe Zone | 20 | Hub protection |
| lm_lanterns_rest_registry | Lantern's Rest Registry | 30 | Registry interior |
| lm_bent_road_route | The Bent Road | 10 | Guided route |
| lm_hollowglass_pool_outer | Hollowglass Pool Outer | 15 | Anomaly site |
| lm_hollowglass_pool_inner | Hollowglass Pool Inner | 25 | Core anomaly |
| lm_hollowglass_spawn_edges | Hollowglass Spawn Edges | 12 | Later controlled starter mobs |

## Initial Flag Suggestions

### Lantern's Rest Safe Zone

```text
passthrough deny
build deny
mob-spawning deny
pvp deny
creeper-explosion deny
enderman-grief deny
fire-spread deny
greeting &bEntering Lantern's Rest
greeting-title &6Lantern's Rest
```

### Bent Road

```text
pvp deny
creeper-explosion deny
enderman-grief deny
fire-spread deny
greeting &7The Bent Road remembers where it used to lead.
```

### Hollowglass Pool Outer

```text
pvp deny
creeper-explosion deny
enderman-grief deny
fire-spread deny
greeting &bThe air bends around Hollowglass Pool.
```

### Hollowglass Pool Inner

```text
pvp deny
mob-spawning deny initially, until MythicMobs spawns are controlled
greeting &3Your reflection moves half a second late.
```

## Parent/Child Structure

Recommended:

```text
lm_lanterns_rest_safe parent -> lm_build01_total_slice
lm_lanterns_rest_registry parent -> lm_lanterns_rest_safe
lm_bent_road_route parent -> lm_build01_total_slice
lm_hollowglass_pool_outer parent -> lm_build01_total_slice
lm_hollowglass_pool_inner parent -> lm_hollowglass_pool_outer
```

## Live Command Template

See:

```text
server-config-drafts/worldguard/lantern-marches/build1/blockout/worldguard_region_commands_template.txt
```
