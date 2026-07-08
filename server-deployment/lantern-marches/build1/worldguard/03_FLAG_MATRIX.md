# WorldGuard Flag Matrix — Build 01

This matrix defines intended behavior. Adjust only if testing shows a plugin conflict.

## General assumptions

- Staff/builders are region members or owners where needed.
- Normal players are not members.
- Block break/place should be denied in all public Build 01 regions.
- Interaction should be tested carefully because NPC, quest, Oraxen, and container behavior can conflict with blanket denial.

## Region matrix

| Region | PVP | Mob spawning | Block break/place | Explosions | Entry | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| `aereth_lm_build1_outer` | deny | deny initially | deny | deny | allow | Outer protection shell. |
| `aereth_lm_lanterns_rest` | deny | deny | deny | deny | allow | Safe hub. |
| `aereth_lm_registry_post` | deny | deny | deny | deny | allow | Tutorial/registration zone. |
| `aereth_lm_bent_road` | deny | deny initially | deny | deny | allow | Protected route. |
| `aereth_lm_hollowglass_pool` | deny | allow later | deny | deny | allow | Low-risk anomaly site. |
| `aereth_lm_hollowglass_inner` | deny | allow later | deny | deny | allow | Future trigger/danger area. |
| `aereth_lm_builder_staging` | deny | deny | deny for non-members | deny | deny for non-members | Staff-only. |

## Recommended first-pass flags

Use conservative flags first:

```text
pvp deny
creeper-explosion deny
other-explosion deny
tnt deny
enderdragon-block-damage deny
ghast-fireball deny
fire-spread deny
lava-fire deny
block-break deny
block-place deny
mob-damage deny in hub only
```

## Avoid at first

Do not immediately deny `interact`, `use`, or `chest-access` globally unless testing proves it is needed. Those flags can break NPCs, Oraxen furniture, quest interaction, and future tutorial objects. Because naturally the one flag that protects everything also breaks everything. Very elegant. Terrible, but elegant.
