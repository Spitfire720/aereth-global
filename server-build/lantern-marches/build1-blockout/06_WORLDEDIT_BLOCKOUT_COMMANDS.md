# WorldEdit Blockout Commands

These are optional helper commands for rough shaping only.

## Basic Tools

```text
//wand
//pos1
//pos2
//set <block>
//replace <from> <to>
//copy
//paste
//undo
//redo
```

## Save Schematic Checkpoints

Stand near the structure, select it, then:

```text
//copy
//schem save lanterns_rest_blockout_v1
```

Recommended schematic names:

```text
lanterns_rest_blockout_v1
bent_road_blockout_v1
hollowglass_pool_blockout_v1
lantern_marches_build01_total_slice_v1
```

## Rough Road Creation

Use brush or manual placement. If using brushes:

```text
/br sphere coarse_dirt 3
/mask grass_block,dirt,stone
```

Then manually refine with:

```text
path_block
cobblestone
mossy_cobblestone
stone_bricks
cracked_stone_bricks
```

## Quick Ruin Materials

```text
cracked_stone_bricks
mossy_stone_bricks
cobblestone_wall
spruce_fence
chain
lantern
soul_lantern
blackstone
polished_deepslate
blue_stained_glass
cyan_stained_glass
```

## Safety Rule

After any major `//set` or `//replace`, check the area before continuing.

WorldEdit can save hours or delete your afternoon. It is not a tool, it is a loaded crossbow with autocomplete.
