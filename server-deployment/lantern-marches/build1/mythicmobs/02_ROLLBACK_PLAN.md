# MythicMobs Rollback Plan

## Fast rollback

Remove these live files:

```text
plugins/MythicMobs/Mobs/lantern_marches_mobs.yml
plugins/MythicMobs/Skills/lantern_marches_skills.yml
plugins/MythicMobs/Spawners/lantern_marches_spawners.yml
plugins/MythicMobs/Drops/lantern_marches_drops.yml
```

Then run:

```text
/mm reload
```

## Full rollback

Restore the MythicMobs plugin folder backup made before deployment.

## Rollback triggers

Rollback if:

- `/mm reload` reports syntax errors.
- Mobs spawn invisible or broken.
- Mobs crash console.
- Spawners spawn inside protected hub.
- Non-OP player is killed before understanding the starter flow.
- Combat creates server TPS issues.
