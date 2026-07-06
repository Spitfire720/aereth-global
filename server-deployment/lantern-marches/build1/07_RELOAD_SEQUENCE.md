# Safe Reload Sequence

## Preferred order

```text
1. Stop server.
2. Copy files.
3. Start server.
4. Check console.
5. Reload only the specific plugin if needed.
```

## If testing live without restart

Use this order:

```text
/worldguard reload
/bq reload
/mm reload
/oraxen reload
```

Do not reload all plugins at once.

## After every reload

Check console for:

- YAML parse errors.
- Missing MythicMobs skills.
- Unknown mobs.
- Missing Oraxen materials/models.
- BetonQuest package errors.
- Placeholder errors.

## Test checkpoint

After each plugin deployment:

```text
1. Plugin loads.
2. No console error spam.
3. Commands work.
4. Minimal in-game test passes.
5. Commit notes updated.
```

The goal is boring progress. Boring is good. Boring means the server is not currently eating itself.
