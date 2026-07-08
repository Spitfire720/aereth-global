# Live Deployment Order

## Phase 0 - backup

- Stop server if changing plugin files.
- Backup FragmentEngine data/storage.
- Backup BetonQuest packages.
- Backup PlaceholderAPI config if touched.

## Phase 1 - verify current state

Run:

```text
/plugins
/version FragmentEngine
/version PlaceholderAPI
/version BetonQuest
```

Confirm there are no console errors on startup.

## Phase 2 - command/API confirmation

Confirm which Build 01 commands already exist. Mark missing commands as dev tasks.

Do not wire BetonQuest to commands that do not exist.

## Phase 3 - placeholder confirmation

Confirm placeholders exist or mark them as dev tasks.

## Phase 4 - limited bridge test

Use one test player:

1. Set Remnant/race state.
2. Register starter region.
3. Mark Hollowglass contact.
4. Mark Intent prompt.
5. Relog.
6. Confirm values persist.

## Phase 5 - BetonQuest integration

Only after the direct test passes, connect commands/events into the starter flow.

## Phase 6 - full starter run

Run as non-OP from spawn to return-to-hub.
