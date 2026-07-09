# Build S5B - Character Identity Card UI

## Goal

S5B improves the Character Card so it reads like the main identity dashboard for Aereth instead of a pile of useful but slightly confused buttons, because apparently menus also need therapy now.

This is still framework work. It does not design final abilities, final Fragment behaviour, final Intent consequences, or final Outcome Effects.

## Adds

- Clear Fragment Layer panel.
- Clear Intent Layer panel.
- Clear Discipline Layer panel.
- Ability Codex panel.
- Ability Loadout panel.
- Activation Pipeline panel.
- Identity Diagnostic panel.
- Erasure Context panel.
- Outcome Hooks placeholder panel.
- Better labels matching the existing click routes.

## Important

The existing GUI listener already routes Character Card clicks as:

- slot 22 -> Intent Slots
- slot 24 -> Discipline Codex
- slot 28 -> Ability Codex
- slot 30 -> Ability Loadout
- slot 32 -> Ability Activation

S5B makes the visible labels match those routes.

## Does not touch

- AerethCommand.java
- plugin.yml
- abilities.yml
- fragments.yml
- intents.yml
- live server files

## Identity Diagnostic Rules

Display-only for now:

- Critical if erasure pressure is high, stability is very low, or total pressure is very high.
- Strained if erasure pressure, pressure, or stability are concerning.
- Unformed if the character has no equipped Fragment and no active Intent.
- Coherent otherwise.

These are UI diagnostics only. They do not apply penalties.
