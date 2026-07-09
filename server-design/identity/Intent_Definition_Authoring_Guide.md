# Intent Definition Authoring Guide

## Purpose

Intents define character direction. They are not stats, classes, abilities, passives, or dialogue flags.

An Intent represents what the character is trying to impose on reality, preserve, seek, deny, distort, or understand.

## Current runtime behavior

The current Intent system reads definitions from `intents.yml` and supports:

- Known Intent IDs.
- Display names.
- Families.
- Pressure values.
- Stability impact values.
- Descriptions.
- Active Intent slots.
- Primary Intent selection based on active slots.
- Intent summary output.

## Required fields

Each Intent definition should include:

```yaml
intents:
  intent_id:
    display: "Readable Intent Name"
    family: "identity_family"
    pressure: 1.0
    stability-impact: -1.0
    description: "Short explanation of the Intent."
```

## Forward-compatible fields

Future-safe authoring may include fields that are not yet consumed by Java:

```yaml
    design-status: placeholder
    identity-layer: intent
    compatible-outcomes:
      - conversion
      - amplification
    conflict-outcomes:
      - suppression
    access-tags:
      - oathbound_dialogue
    narrative-hooks:
      - mercy_choice
```

These are planning fields only until Java explicitly consumes them.

## Naming rules

Intent IDs should:

- Use lowercase.
- Use underscores.
- Avoid spaces.
- Be short and readable.
- Represent a direction, not a skill.

Good:

```text
anchor
mercy
hunger
defiance
clarity
```

Bad:

```text
fireball
sword_damage
xp_boost
```

## Family guidance

Intent family should group the philosophical or behavioral direction of the Intent.

Example families:

```text
preservation
consumption
judgment
rejection
knowledge
concealment
distortion
memory
absence
```

Families are not classes. They are interpretation groups.

## Balance guidance

Suggested placeholder ranges:

```text
pressure: 0.25 to 3.00
stability-impact: -5.00 to +2.00
```

A positive stability impact should be rare and should not become a free optimization stat. Stability is a consequence layer, not a min-max toy.

## Design questions

Each Intent should answer:

1. What does this character want reality to do?
2. What pressure does that create?
3. Does it stabilize or destabilize the character?
4. What outcomes should it naturally bias later?
5. What dialogue/world responses could notice it?
