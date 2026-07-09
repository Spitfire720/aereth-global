# Fragment + Intent Field Contract

## Principle

Fragments and Intents are identity data. They must remain independent from items, classes, abilities, and quest scripts.

Other systems may read this identity data later. They must not redefine it.

## Current Fragment fields

Runtime-consumed fields:

```yaml
fragments:
  <fragment_id>:
    display: string
    lore: string
    pressure: number
    stability-cost: number
    modifiers:
      stats:
        vitality: number
        strength: number
        dexterity: number
        intelligence: number
        willpower: number
        endurance: number
```

Required by S5C contract:

```text
display
lore
pressure
stability-cost
```

Optional current runtime field:

```text
modifiers.stats
```

Allowed design-only fields:

```text
design-status
identity-layer
access-tags
narrative-hooks
outcome-bias
notes
```

## Current Intent fields

Runtime-consumed fields:

```yaml
intents:
  <intent_id>:
    display: string
    family: string
    pressure: number
    stability-impact: number
    description: string
```

Required by S5C contract:

```text
display
family
pressure
stability-impact
description
```

Allowed design-only fields:

```text
design-status
identity-layer
compatible-outcomes
conflict-outcomes
access-tags
narrative-hooks
notes
```

## Character YAML relationship

Character state should store selected/discovered/equipped values, not full definitions.

Definition files:

```text
fragments.yml
intents.yml
```

Character state:

```yaml
fragments:
  discovered-list: []
  equipped: []
  capacity: 3
  total-pressure: 0.0
  stability: 100.0
  erasure-pressure: 0.0

intent:
  unlocked-slots: 1
  active:
    slot1: anchor
  primary: anchor
  pressure: 0.0
  stability-impact: 0.0
```

## Hard boundaries

Do not put the following into Fragment or Intent definitions:

- MMOItems item IDs.
- Oraxen model IDs as mechanics.
- BetonQuest progression ownership.
- MythicMobs combat ownership.
- Ability final effects.
- Class labels.

A Fragment can later influence access to these systems, but it does not become those systems.

## Design-status values

Recommended values:

```text
placeholder
draft
approved
runtime-test
locked
retired
```

`approved` means design-approved, not automatically live mechanic-approved.
