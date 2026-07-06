# Build 01 Pre-Deployment Check

## Required before live testing

- [ ] Latest `main` pulled locally.
- [ ] Server backed up.
- [ ] World backup taken or world snapshot copied.
- [ ] Plugin versions recorded.
- [ ] Operator account has WorldEdit and WorldGuard permissions.
- [ ] Test player account available.
- [ ] Server console access available.
- [ ] Restart window available if reloads fail.

## Record versions

Run in console or in-game:

```text
/version
/version WorldEdit
/version WorldGuard
/version BetonQuest
/version MythicMobs
/version Oraxen
/version PlaceholderAPI
```

Optional depending on installed stack:

```text
/version ModelEngine
/version MMOItems
/version MythicLib
/version ProtocolLib
/version Vault
```

## First build goal

Build 01 is not a finished starter zone.

It is a **testable vertical slice**:

- One safe hub.
- One road route.
- One starter quest conversation.
- One low-risk enemy.
- One anomaly location.
- One return-to-hub loop.

If someone tries to add five more systems before this works, lock them in The Black Archive until they repent.
