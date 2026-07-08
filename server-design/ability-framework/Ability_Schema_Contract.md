# Ability Schema Contract

## Purpose

This document defines the current ability definition contract for the framework layer.

The current goal is not final ability design. The goal is to keep ability definitions easy to author later while preserving a stable loadout, activation, resource, targeting, and cooldown pipeline.

## Current required fields

Every ability in `abilities.yml` should have:

```yaml
ability_id:
  display-name: Guardian Stance
  discipline: vanguard
  unlock-rank: 1
  cost-type: stamina
  cost-amount: 10
  cooldown-seconds: 20
  description: "Short readable description."
```

## Forward-compatible fields

These fields may exist now but are not final design:

```yaml
  target-mode: self
  effect-route: defensive_pulse
```

They are placeholders for later authoring. Final ability behavior should be designed separately before these routes become real combat logic.

## Loadout slot contract

The loadout only stores ability IDs:

```yaml
abilities:
  loadout:
    slots:
      slot1: vanguard_guardian_stance
      slot2: vanguard_linebreaker
      slot3: null
      slot4: null
```

The slot framework enforces:

- ability id exists
- ability belongs to selected Discipline
- ability is unlocked by current Discipline rank
- ability appears only once in loadout
- locked slots do not retain ability ids

## Authoring principle

Real abilities should be designed as data first, then wired to routes. Do not treat current placeholder effects as final gameplay design.
