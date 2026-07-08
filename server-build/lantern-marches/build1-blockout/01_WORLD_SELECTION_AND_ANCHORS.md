# World Selection and Coordinate Anchors

## Required Decision

Before building, choose the test world:

```text
World name: ___________________________
Seed / terrain source: ________________
Dimension: overworld / custom / other
Purpose: build test / live starter world / temporary prototype
```

## Recommended Anchor Standard

Pick one central coordinate and treat it as the starter slice origin.

```text
LANTERN_REST_CENTER = X: _____ Y: _____ Z: _____
```

Suggested layout relative to this point:

| Area | Relative Position | Suggested Distance |
| --- | --- | --- |
| Lantern's Rest | Center | 0 blocks |
| The Bent Road entrance | East/Southeast from hub | 80-120 blocks |
| Hollowglass Pool | Further along Bent Road | 220-350 blocks |
| Old Wick Post hint marker | North/Northeast optional | 120-180 blocks |
| Route outward gate | East/South/East | 350-500 blocks |

## Coordinate Naming Convention

Use lowercase region IDs for plugin config:

```text
lm_lanterns_rest_safe
lm_lanterns_rest_registry
lm_bent_road_route
lm_hollowglass_pool_outer
lm_hollowglass_pool_inner
lm_build01_total_slice
```

Use readable names in documentation:

```text
Lantern's Rest Safe Zone
Lantern's Rest Registry
The Bent Road
Hollowglass Pool Outer Ring
Hollowglass Pool Inner Anomaly
Build 01 Total Slice
```

## First Anchor Capture

In-game, stand at the intended center of Lantern's Rest and run:

```text
/tp ~ ~ ~
```

Record the exact coordinate from F3 or console output.

Do not eyeball it. Eyeballing coordinates is how block worlds become tax disputes.
