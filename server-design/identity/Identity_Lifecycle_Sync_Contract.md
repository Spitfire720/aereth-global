# S5D Identity Lifecycle Sync Contract

## Source of truth

`CharacterIdentityService` remains the source of truth for identity summaries.

## Persisted YAML

S5D refreshes and saves the existing `identity.*` contract:

```text
identity.schema-version
identity.name
identity.race
identity.level
identity.phase
identity.fragments.*
identity.intent.*
identity.pressure.total
identity.stability.combined
identity.erasure.pressure
identity.diagnostics.state
identity.hooks.*
identity.last-sync-source
```

## Sync moments

Identity may be refreshed during:

```text
player join
player world change
player quit
plugin startup online-player pass
manual repair/sync calls
PlaceholderAPI reads, without guaranteed save
```

## Error behavior

Lifecycle sync must be safe.

No active character:

```text
status = no_active_character
no hard failure
```

I/O problem:

```text
status = io_error
warning in console
no player-facing crash
```

Runtime calculation problem:

```text
status = runtime_error
warning in console
no player-facing crash
```

## Boundary

The identity lifecycle contract does not give power by itself.

It is state plumbing for future systems that need Fragment + Intent context.
