# Build S5D - Fragment + Intent Runtime Hardening

## Purpose

S5D hardens the runtime state of Fragment and Intent data.

This is not content design. It does not define final fragments, final intents, quests, trials, passives, classes, or abilities.

It ensures the character YAML state remains safe while future systems begin reading Fragment + Intent identity.

## Changes

### IntentService

Adds runtime sanitation for:

- Unknown intent IDs
- Duplicate active Intent states
- Intent states inside locked slots
- Non-normalized intent IDs
- Missing `intent.active` section

Adds framework metadata under:

```yaml
intent:
  framework:
    schema: S5D-intent-runtime-hardening
    status: clean | cleaned
    issues: []
    cleaned-count: 0
    invalid-count: 0
    duplicate-count: 0
    locked-count: 0
    normalized-count: 0
```

### FragmentService

Adds runtime sanitation for:

- Unknown discovered fragments
- Unknown equipped fragments
- Equipped fragments not present in discovered list
- Equipped fragments over capacity
- Non-normalized fragment IDs
- Legacy discovered/equipped section formats

Adds framework metadata under:

```yaml
fragments:
  framework:
    schema: S5D-fragment-runtime-hardening
    status: clean | cleaned
    issues: []
    cleaned-count: 0
    invalid-count: 0
    overflow-count: 0
    normalized-count: 0
```

## Non-goals

- No command changes.
- No GUI changes.
- No plugin.yml changes.
- No live config changes.
- No final Fragment or Intent design.
- No ability design.

## Why this exists

Fragment and Intent state is becoming a core input for:

- Character Card display
- Outcome hooks
- Dialogue gates
- World response
- Later ability rules
- Later quest conditions
- Erasure pressure diagnostics

So the state has to stop being fragile YAML soup. Humanity has committed worse crimes, but not many.
