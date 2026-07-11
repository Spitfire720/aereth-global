# Build S5D - Identity Lifecycle Sync

## Build goal

Persist the canonical Character Identity Framework during normal runtime lifecycle events.

S5A calculates identity. S5B displays it. S5C exposes it through PlaceholderAPI. S5D saves the current identity contract into the active character YAML so downstream systems have stable state to read.

## Design rule

Runtime sync should not create gameplay design.

It only keeps these existing systems aligned:

```text
CharacterService
FragmentService
IntentService
CharacterIdentityService
PlaceholderAPI bridge
Character Card UI
```

## What changes

### CharacterIdentityService

Adds safe persistence methods:

```java
IdentitySummary sync(OfflinePlayer player)
SyncResult syncSilently(OfflinePlayer player)
int syncOnlinePlayersSilently()
```

`sync(player)` calculates the identity summary, writes the `identity.*` fields, and saves the active character file.

### IdentitySyncListener

Adds lifecycle hooks:

```text
PlayerJoinEvent
PlayerChangedWorldEvent
PlayerQuitEvent
```

Join sync is delayed by 20 ticks so account/character state has time to settle after login.

### FragmentEnginePlugin

Registers the listener and performs startup sync for any online players.

## Why this matters

Later systems can read stable identity fields from YAML without recalculating or guessing:

- BetonQuest conditions
- NPC dialogue branches
- scoreboards
- menus
- region ambience
- diagnostics
- future ability routing previews

## Boundary

S5D is not final ability design.

It does not add spells, damage logic, passive classes, final stats, enemy behavior, or balancing rules.
