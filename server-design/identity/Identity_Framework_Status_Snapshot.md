# Identity Framework Status Snapshot

## Status

S5 identity framework is considered structurally complete once S5G QA passes.

## Current Identity Layers

```text
Character Core
Race / Remnant state
Fragment state
Intent state
Discipline state
Ability output layer
Placeholder output layer
HUD / scoreboard blueprint layer
```

## Boundaries

Fragments are not MMOItems items.

Intent is not an MMOItems stat.

Discipline is not a class plugin system.

Abilities are not the identity source of truth. They are an output layer that can later read identity state.

PlaceholderAPI is not gameplay logic. It is a display bridge.

HUD and scoreboard layouts are not progression logic. They are presentation rules.

## Runtime State Expectations

Character YAML should safely support:

```yaml
fragments:
  capacity: 3
  discovered-list: []
  equipped: []
  total-pressure: 0.0
  stability-cost: 0.0
  stability: 100.0
  erasure-pressure: 0.0

intent:
  unlocked-slots: 1
  active: {}
  slots-used: 0
  primary: none
  pressure: 0.0
  stability-impact: 0.0
```

## Output Expectations

The identity framework should provide:

- Character card visibility.
- Fragment and Intent summaries.
- PlaceholderAPI values for external displays.
- HUD and scoreboard design templates.
- Authoring contracts for future Fragment and Intent definitions.

## Next Phase Recommendation

After S5G passes, the project should move to one of these:

1. **S6A: Character Creation Bridge Hardening**
   - Connect identity framework expectations to character creation.
   - Ensure new characters start with clean Fragment and Intent state.

2. **S6A: World Reaction Framework**
   - Let regions, quests, dialogue, and environmental systems read identity state.
   - Keep it framework-only before making major content.

Recommended next step: **Character Creation Bridge Hardening**, because world reaction logic should not assume messy character birth records. Humanity made birth certificates and still got YAML wrong. We can do better.
