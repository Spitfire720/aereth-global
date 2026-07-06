# Lantern Marches Build 01 Checklist

## Repo checks

- [ ] Pack extracted into repo root.
- [ ] Files are under `server-design/` and `server-config-drafts/`.
- [ ] No live server secrets committed.
- [ ] No jars committed.
- [ ] No generated resource pack ZIP committed.
- [ ] No player data committed.

## World build checks

- [ ] Lantern's Rest blockout exists.
- [ ] Quiet Ledger room exists.
- [ ] Bent Road route exists.
- [ ] Hollowglass Pool encounter area exists.
- [ ] Future areas are visibly blocked or unsafe.
- [ ] Starter route has readable player direction.

## Plugin boundary checks

- [ ] FragmentEngine owns RPG state.
- [ ] BetonQuest only coordinates story/dialogue/objectives.
- [ ] MythicMobs only owns enemies and encounter behavior.
- [ ] Oraxen only owns visuals/items/props.
- [ ] WorldGuard only owns region flags/protection.
- [ ] MMOItems does not define race, Fragment, Intent, Discipline, or Title.
- [ ] Titles are not cosmetic badges.
- [ ] Fragments are not items.

## Quest checks

- [ ] Player can register at The Quiet Ledger.
- [ ] Player is sent to The Bent Road.
- [ ] Player reaches Hollowglass Pool.
- [ ] Player encounters starter anomaly.
- [ ] Player returns to Lantern's Rest.
- [ ] Player receives first progression hook.
- [ ] Quest can be repeated only if intended.
- [ ] Quest does not break on logout.

## Mob checks

- [ ] Roadstray spawns in intended area only.
- [ ] Hollowglass Wisp spawns in intended area only.
- [ ] Mobs do not spawn inside safe hub.
- [ ] Mobs are not too fast for starter players.
- [ ] Drops are safe and temporary.

## Test result

Tester:
Date:
Server version:
Plugin versions:

Result:
- [ ] Passed
- [ ] Needs fixes
- [ ] Failed

Notes:
