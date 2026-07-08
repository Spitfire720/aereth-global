# Build 01 Rollback and Recovery

## Rollback principle

Rollback should restore the server to the last known stable state without deleting evidence.

Do not panic-delete files. That is how debugging becomes folklore.

## Before deployment

Create backups of:

- `plugins/WorldGuard/worlds/<world>/regions.yml`
- `plugins/BetonQuest/BetonQuest/packages/` or current package path
- `plugins/MythicMobs/Mobs/`
- `plugins/MythicMobs/Skills/`
- `plugins/Oraxen/items/`
- `plugins/Oraxen/pack/` if changed
- FragmentEngine config/state files if touched
- Any PlaceholderAPI config files changed

## Rollback order

### If WorldGuard breaks

1. Remove new child regions first.
2. Remove parent region last.
3. Restore backed-up regions file if needed.
4. Restart or reload WorldGuard safely.

### If BetonQuest breaks

1. Disable only the Lantern Marches package.
2. Reload BetonQuest.
3. Check console.
4. Restore package backup if reload still fails.

### If MythicMobs breaks

1. Remove spawners first.
2. Remove or disable Lantern Marches mob file.
3. Reload MythicMobs.
4. Restore previous mobs/skills files if needed.

### If Oraxen breaks

1. Remove newly added item config file.
2. Reload Oraxen items.
3. Rebuild/resend pack only after errors are gone.
4. Restore previous pack if clients fail to load.

### If FragmentEngine breaks

1. Stop integration calls from BetonQuest.
2. Do not delete player state.
3. Restore config only if schema mismatch is confirmed.
4. Preserve logs.

## Evidence to keep

- Console error screenshots/logs
- Exact command used
- File changed
- Time of failure
- Player account used
- Whether issue affects OP, non-OP, or both
