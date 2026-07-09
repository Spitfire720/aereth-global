# Build S5F — Identity HUD + Scoreboard Blueprint

## Purpose

S5F connects the Fragment + Intent identity framework to player-facing display surfaces without changing runtime code.

This is a blueprint layer for scoreboards, action bars, holograms, TAB layouts, NPC text, and later website/wiki display exports.

It depends on S5E Placeholder Identity Bridge.

## Scope

S5F defines:

1. Which identity placeholders belong on HUD surfaces.
2. Which values should be visible during normal gameplay.
3. Which values should remain diagnostic/admin-facing.
4. How Fragment, Intent, Discipline, Ability, Erasure, and Stability should be visually grouped.
5. How to avoid turning the HUD into a casino receipt.

## Runtime changes

None.

S5F does not patch Java, plugin.yml, config.yml, fragments.yml, intents.yml, abilities.yml, or live server files.

## Required prior build

S5E must be applied before S5F can be meaningfully used, because S5F relies on identity placeholders such as:

```text
%aereth_identity_state%
%aereth_identity_summary%
%aereth_fragment_equipped_display%
%aereth_intent_primary_display%
%aereth_intent_active_display%
```

## Player-facing HUD rules

The normal HUD should show only a compact identity snapshot:

```text
Character name
Level / phase
Race
Discipline / rank
Fragment stability
Erasure pressure
Primary Intent
Identity state
```

Do not show every active fragment, every intent slot, every stat, every resource, and every diagnostic line at once. That is not UI. That is a hostage note.

## Admin/debug surfaces

Detailed identity data belongs in:

```text
/aereth card
/aereth fragments <player>
/aereth intent <player>
/aereth identitysummary <player> if S5A command hooks are enabled later
Placeholder debug panels
```

## Visual grouping

### Character core

```text
Name
Race
Level
Phase
```

### Identity pressure

```text
Fragment pressure
Intent pressure
Combined pressure
Erasure pressure
```

### Stability

```text
Fragment stability
Intent stability impact
Combined stability
```

### Decision layer

```text
Primary Intent
Active Intent display list
Outcome Hooks
```

### Progression layer

```text
Discipline
Rank
Ability count
```

## Build result

S5F produces docs and config templates only. It is safe to commit without deployment.
