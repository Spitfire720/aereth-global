# Build 01 WorldGuard Pass — Start Here

## Objective

Protect the first playable Lantern Marches blockout so it can safely support testing.

This pass does not add quests, mobs, Oraxen props, or final builds. It only establishes safe boundaries, interaction rules, route protections, anomaly danger zones, and rollback notes.

## Build 01 regions covered

| Region | Purpose | Priority |
| --- | --- | --- |
| `aereth_lm_build1_outer` | Outer container for Build 01 test area | 10 |
| `aereth_lm_lanterns_rest` | Main starter hub | 50 |
| `aereth_lm_registry_post` | Character/Remnant registry area | 70 |
| `aereth_lm_bent_road` | Protected travel route | 40 |
| `aereth_lm_hollowglass_pool` | Low-risk Fragment anomaly area | 45 |
| `aereth_lm_hollowglass_inner` | Inner anomaly danger/interact zone | 80 |
| `aereth_lm_builder_staging` | Staff-only staging / schematic checkpoint area | 90 |

## Non-negotiables

- Lantern's Rest must be safe for non-OP players.
- The Bent Road must be traversable without permission errors.
- Hollowglass Pool must feel less safe than the hub but not lethal during the first tutorial.
- Players must not be able to grief, place blocks, break roads, or open protected containers.
- Staff must be able to build inside all regions.
- Region names must remain lowercase and stable so BetonQuest, MythicMobs, and internal documentation can reference them later.

## Work order

1. Confirm test world name.
2. Capture region cuboids in-game.
3. Fill coordinate sheet.
4. Replace placeholders in the command template.
5. Apply regions one by one.
6. Apply flags one by one.
7. Run OP/admin test.
8. Run non-OP test.
9. Save final command log.
10. Commit final filled command file later as `worldguard_live_commands_applied.txt`.
