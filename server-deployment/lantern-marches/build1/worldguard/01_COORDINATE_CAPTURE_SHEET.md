# WorldGuard Coordinate Capture Sheet

Use this sheet while standing in-game. Capture clean cuboids with WorldEdit selections, then record the two corners here.

## Server/world

| Field | Value |
| --- | --- |
| Server name | `TODO` |
| Test world name | `TODO_WORLD_NAME` |
| Date captured | `TODO_DATE` |
| Captured by | `TODO_NAME` |
| Minecraft version | `TODO` |
| Paper build | `TODO` |
| WorldGuard version | `TODO` |
| WorldEdit version | `TODO` |

## Region coordinates

### Outer Build 01 Area

| Field | Value |
| --- | --- |
| Region ID | `aereth_lm_build1_outer` |
| Purpose | Container for the whole test slice |
| Pos 1 | `x: TODO, y: TODO, z: TODO` |
| Pos 2 | `x: TODO, y: TODO, z: TODO` |
| Notes | Include hub, road, Hollowglass Pool, and staging buffer. |

### Lantern's Rest

| Field | Value |
| --- | --- |
| Region ID | `aereth_lm_lanterns_rest` |
| Purpose | Safe starter hub |
| Pos 1 | `x: TODO, y: TODO, z: TODO` |
| Pos 2 | `x: TODO, y: TODO, z: TODO` |
| Notes | Include gates, square, spawn arrival, and outer walls. |

### Registry Post

| Field | Value |
| --- | --- |
| Region ID | `aereth_lm_registry_post` |
| Purpose | Remnant registration / character identity onboarding |
| Pos 1 | `x: TODO, y: TODO, z: TODO` |
| Pos 2 | `x: TODO, y: TODO, z: TODO` |
| Notes | Should sit inside Lantern's Rest with higher priority. |

### The Bent Road

| Field | Value |
| --- | --- |
| Region ID | `aereth_lm_bent_road` |
| Purpose | Protected starter route from hub to anomaly edge |
| Pos 1 | `x: TODO, y: TODO, z: TODO` |
| Pos 2 | `x: TODO, y: TODO, z: TODO` |
| Notes | Long road cuboid. Use multiple child regions later if road bends too much. |

### Hollowglass Pool

| Field | Value |
| --- | --- |
| Region ID | `aereth_lm_hollowglass_pool` |
| Purpose | Low-risk Fragment disturbance site |
| Pos 1 | `x: TODO, y: TODO, z: TODO` |
| Pos 2 | `x: TODO, y: TODO, z: TODO` |
| Notes | Exterior anomaly area. Mobs may be allowed later. |

### Hollowglass Inner

| Field | Value |
| --- | --- |
| Region ID | `aereth_lm_hollowglass_inner` |
| Purpose | Inner anomaly interaction / future trigger zone |
| Pos 1 | `x: TODO, y: TODO, z: TODO` |
| Pos 2 | `x: TODO, y: TODO, z: TODO` |
| Notes | Higher priority than Hollowglass Pool. Used for future quest triggers. |

### Builder Staging

| Field | Value |
| --- | --- |
| Region ID | `aereth_lm_builder_staging` |
| Purpose | Staff-only staging / command block-free test area |
| Pos 1 | `x: TODO, y: TODO, z: TODO` |
| Pos 2 | `x: TODO, y: TODO, z: TODO` |
| Notes | Keep hidden from normal player path. |

## Capture commands

Use WorldEdit selection:

```text
//wand
//pos1
//pos2
//expand vert
```

Then define the region:

```text
/rg define <region_id>
```

Use `/rg info <region_id>` after creation to confirm coordinates.
