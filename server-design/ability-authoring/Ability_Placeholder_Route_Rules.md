# Ability Placeholder Route Rules

## Purpose

Current effect routes are temporary test routes.

They prove that activation can call an effect path, pay cost, resolve target, apply cooldown, write state, and return feedback.

They are not final Aereth ability design.

## Current placeholder routes

```text
defensive_pulse
blood_pressure
arcane_spark
focus_thread
unstable_bloom
stamina_surge
resonance_ping
placeholder
manual_design_required
```

## Route meaning during framework phase

```text
defensive_pulse       defensive/self-protection placeholder
blood_pressure        single-target PvE pressure placeholder
arcane_spark          small arcane AoE placeholder
focus_thread          precision/control/movement placeholder
unstable_bloom        anomaly/area disruption placeholder
stamina_surge         movement/physical placeholder
resonance_ping        fallback stabilising placeholder
placeholder           intentionally not implemented yet
manual_design_required ability exists only as a design stub
```

## Rule

Do not treat any current route as final.

When designing real abilities later, each ability should receive a deliberate route name only after its gameplay effect is approved.

## Future route naming

Future real routes should be explicit:

```text
vanguard_guardian_stance_apply
controller_gravity_lattice_cast
chronomancer_second_breath_resolve
```

Avoid vague names:

```text
damage1
magic_attack
cool_effect
```

## PvP rule

Until explicitly designed later, ability damage must remain PvE-only.
