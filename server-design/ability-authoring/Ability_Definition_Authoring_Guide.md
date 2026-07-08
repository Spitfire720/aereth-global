# Ability Definition Authoring Guide

## Current purpose

The ability system is currently a framework, not a final ability catalogue.

Current routes, effects, particles, and damage values are temporary test scaffolding used to prove that slots, loadout, hotbar binding, cooldowns, costs, targeting, scaling, and persistence work.

Final abilities should not be designed directly inside Java first. They should be designed in this order:

1. Ability fantasy and role.
2. Player action and target mode.
3. Slot/loadout requirements.
4. Resource cost and cooldown.
5. Intended gameplay effect.
6. Balance notes.
7. Implementation route.
8. Test checklist.

## Required fields in abilities.yml

Each ability definition must include:

```yaml
ability_id:
  display-name: Example Ability
  discipline: vanguard
  unlock-rank: 1
  cost-type: stamina
  cost-amount: 10.0
  cooldown-seconds: 20.0
  target-mode: self
  effect-route: placeholder
  description: Short player-facing explanation.
```

## Runtime fields currently read by AbilityService

The current runtime reads the core fields:

- display-name
- discipline
- unlock-rank
- cost-type
- cost-amount
- cooldown-seconds
- description

The forward-compatible fields are used by design/docs/tooling and future runtime upgrades:

- target-mode
- effect-route

## Naming rules

Ability IDs:

- lowercase only
- snake_case
- discipline prefix recommended
- no spaces
- no hyphens

Good:

```text
vanguard_guardian_stance
controller_gravity_lattice
chronomancer_second_breath
```

Bad:

```text
Guardian Stance
Vanguard-Guardian-Stance
coolAbility1
```

## Discipline ownership

An ability belongs to one Discipline. Do not design shared abilities until we explicitly add shared/mutated handling.

## Unlock ranks

Use ranks 1-4 for base Discipline slot progression:

```text
Rank 1 -> Slot 1
Rank 2 -> Slot 2
Rank 3 -> Slot 3
Rank 4 -> Slot 4
```

Rank 5 can exist as a future Ascendant/mutated design layer, but it should not be required for the current basic loadout loop.

## Authoring principle

Do not design final ability behavior until the Discipline identity pass is complete.

For now, definitions exist to prove that the system can carry future real design without breaking.
