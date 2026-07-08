# After Blockout Next Steps

Once the blockout passes acceptance testing, continue in this order:

## 1. Update Coordinates in Draft Configs

Update:

```text
server-config-drafts/worldguard/lantern-marches/build1/blockout/worldguard_region_commands_template.txt
server-build/lantern-marches/build1-blockout/capture-sheets/coordinate_capture_sheet.csv
```

## 2. Commit Blockout Documentation

Commit coordinate sheets and updated plans.

Do not commit `.schem` files unless we explicitly decide the repo should track schematics.

## 3. Deploy WorldGuard Regions Live

Apply WorldGuard regions first.

## 4. Run WorldGuard Test

Confirm:

- Entry messages
- PvP disabled where intended
- Mob spawning disabled in hub
- Players cannot grief hub if survival permissions are active

## 5. Only Then Start Plugin Layer

Next plugin order:

1. BetonQuest package skeleton
2. MythicMobs Roadstray draft
3. Oraxen starter props
4. FragmentEngine state hooks
5. Placeholder/API display hooks

## Blocking Rule

If the physical space is not readable, do not add quests yet.

Quests do not fix bad geography. They just give players dialogue boxes while they remain lost, which is basically bureaucracy with particles.
