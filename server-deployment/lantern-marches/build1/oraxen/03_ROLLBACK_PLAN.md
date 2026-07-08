# Oraxen Rollback Plan - Build 01

## When to rollback

Rollback if:

- Oraxen reload fails.
- Resource pack generation fails.
- Players cannot join due to pack errors.
- Furniture placement causes console spam.
- Props are invisible or broken across clients.

## Rollback steps

1. Stop placing new props.
2. Remove the copied live file:

```text
/plugins/Oraxen/items/aereth/lantern_marches_props.yml
```

3. Restore the previous backed-up Oraxen items folder if needed.
4. Run:

```text
/oraxen reload items
/oraxen reload pack
```

5. Restart server if reload does not clear the issue.
6. Record what failed.

## Do not rollback

Do not rollback FragmentEngine, BetonQuest, WorldGuard, or MythicMobs for an Oraxen visual issue unless testing proves cross-plugin damage.
