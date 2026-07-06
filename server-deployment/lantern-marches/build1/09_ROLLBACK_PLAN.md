# Rollback Plan

## Before deployment

Back up:

```text
plugins/BetonQuest/
plugins/MythicMobs/
plugins/Oraxen/
plugins/WorldGuard/
world folders / server world
```

## If BetonQuest breaks

1. Remove package folder:

```text
plugins/BetonQuest/QuestPackages/aereth_lantern_marches_build1/
```

2. Reload or restart.
3. Confirm no package errors.

## If MythicMobs breaks

Remove only the Aereth Lantern Marches files:

```text
plugins/MythicMobs/Mobs/Aereth/LanternMarches/
plugins/MythicMobs/Skills/Aereth/LanternMarches/
plugins/MythicMobs/Spawners/Aereth/LanternMarches/
```

Then:

```text
/mm reload
```

## If Oraxen breaks

Remove or comment out:

```text
plugins/Oraxen/items/aereth_lantern_marches.yml
```

Then regenerate/reload after confirming existing Oraxen pack still works.

## If WorldGuard regions are wrong

Use:

```text
/rg remove <region_id>
```

or redefine with a corrected selection.

## Golden rule

Rollback the last changed system only. Do not randomly delete half the server like a panicked raccoon with filesystem access.
