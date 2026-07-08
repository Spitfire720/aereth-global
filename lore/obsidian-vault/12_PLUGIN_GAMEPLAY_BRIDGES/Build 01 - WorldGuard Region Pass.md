# Build 01 - WorldGuard Region Pass

## Status

Planned for live server blockout protection.

## Purpose

This pass turns the Lantern Marches Build 01 blockout into protected playable server space.

## Canon relationship

The regions are technical server regions, not lore borders.

Important distinction:

- `aereth_lm_lanterns_rest` is a WorldGuard safety region.
- Lantern's Rest is the in-world hub settlement.
- `aereth_lm_hollowglass_inner` is a trigger/protection zone.
- Hollowglass Pool is the lore anomaly site.

Do not let plugin region IDs become public lore names. That is how server admin residue leaks into fantasy, and nobody wants an NPC saying “welcome to aereth underscore lm underscore build one outer.”

## Build 01 region IDs

```text
aereth_lm_build1_outer
aereth_lm_lanterns_rest
aereth_lm_registry_post
aereth_lm_bent_road
aereth_lm_hollowglass_pool
aereth_lm_hollowglass_inner
aereth_lm_builder_staging
```

## Plugin ownership

| System | Owns |
| --- | --- |
| WorldGuard | Physical boundaries, flags, safe areas |
| BetonQuest | NPC dialogue and quest flow |
| MythicMobs | Encounter entities |
| Oraxen | Visual props and custom assets |
| FragmentEngine | Player RPG state, Fragment/Intent state |

## Hard boundary

WorldGuard does not own progression, Fragments, Intent, Titles, Disciplines, or lore truth. It only protects and defines spaces.
