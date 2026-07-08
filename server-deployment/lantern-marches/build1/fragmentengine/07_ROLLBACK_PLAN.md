# FragmentEngine Rollback Plan

## If placeholders fail

- Remove placeholder usage from scoreboards/dialogue.
- Keep quest flow running with plain text.
- Do not reset player RPG state just because display failed.

## If BetonQuest bridge commands fail

- Disable only the bridge events.
- Keep dialogue accessible.
- Remove progression completion rewards tied to FragmentEngine until fixed.

## If player state corrupts

1. Stop testing immediately.
2. Backup the corrupted data before touching it.
3. Restore the latest known-good FragmentEngine data snapshot.
4. Re-run with a dedicated test player only.

## If server fails to start

1. Remove the latest changed config/code.
2. Restore backup.
3. Start server.
4. Read console before making another change, because guessing at boot errors is how admins become philosophers.
