# Fragment Definition Authoring Guide

## Purpose

Fragments are identity-layer definitions. They are not inventory items, gear modifiers, skills, classes, or abilities.

A Fragment represents a discovered or equipped condition that changes how the character relates to Aereth. It may affect pressure, stability, Erasure exposure, narrative access, future dialogue, future world response, or later ability interpretation.

## Current runtime behavior

The current Fragment system reads definitions from `fragments.yml` and supports:

- Fragment discovery.
- Fragment attachment.
- Fragment detachment.
- Equipped Fragment capacity.
- Total Fragment pressure.
- Stability cost.
- Erasure pressure contribution.
- Optional stat modifiers.

## Required fields

Each Fragment definition should include:

```yaml
fragments:
  fragment_id:
    display: "Readable Fragment Name"
    lore: "Short explanation of what the Fragment represents."
    pressure: 1.0
    stability-cost: 1.0
```

## Optional current fields

Fragments may include stat modifiers, but this should be used carefully. Fragments should primarily be identity pressure, access, consequence, and world-state hooks.

```yaml
    modifiers:
      stats:
        vitality: 0.0
        strength: 0.0
        dexterity: 0.0
        intelligence: 0.0
        willpower: 0.0
        endurance: 0.0
```

## Forward-compatible fields

Future-safe authoring may include fields that are not yet consumed by Java:

```yaml
    design-status: placeholder
    identity-layer: fragment
    access-tags:
      - forgotten_path
    narrative-hooks:
      - memory_echo
    outcome-bias:
      conversion: 0.0
      amplification: 0.0
      redirection: 0.0
      delay: 0.0
      suppression: 0.0
      distortion: 0.0
```

These fields are allowed as design metadata. They should not be treated as live mechanics until Java explicitly consumes them.

## Naming rules

Fragment IDs should:

- Use lowercase.
- Use underscores.
- Avoid spaces.
- Avoid hyphens unless legacy compatibility requires them.
- Be stable once released.

Good:

```text
glass_memory
ashen_oath
hollow_witness
```

Bad:

```text
Glass Memory
fragmentOne
cool_power_thing
```

## Design rules

A Fragment should answer:

1. What has the character become exposed to?
2. What pressure does this add?
3. What does this destabilize?
4. What future access or consequence could it imply?
5. What does it reveal about Aereth?

A Fragment should not be written as:

- A sword.
- A spell.
- A passive perk.
- A class bonus.
- A random stat stick.

## Balance guidance

Suggested early placeholder ranges:

```text
pressure: 0.25 to 3.00
stability-cost: 0.25 to 5.00
```

High-pressure Fragments should be rare and should imply stronger story/world consequences later.
