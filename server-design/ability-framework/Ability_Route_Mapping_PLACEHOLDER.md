# Ability Route Mapping - Placeholder Contract

## Current status

Current effect routes are temporary framework test routes.

They exist to prove that ability slots, activation, costs, cooldowns, targeting, scaling, and hotbar binding work.

They are not final Aereth abilities.

## Temporary routes

```text
defensive_pulse
blood_pressure
arcane_spark
focus_thread
unstable_bloom
stamina_surge
resonance_ping
```

## Current route purpose

- `defensive_pulse`: verifies self buffs and defensive particles.
- `blood_pressure`: verifies aimed entity marking and PvE-only damage.
- `arcane_spark`: verifies small area control/damage.
- `focus_thread`: verifies focus-style target marking and caster buff.
- `unstable_bloom`: verifies area control without PvP damage.
- `stamina_surge`: verifies movement and self buff routes.
- `resonance_ping`: verifies fallback behavior.

## Later design process

When final ability design starts:

1. Design the Discipline fantasy first.
2. Define ability purpose and use-case.
3. Decide target mode.
4. Decide resource/cooldown cost.
5. Decide route/effect needs.
6. Implement specific route behavior.
7. Test in slot/loadout/hotbar/activation pipeline.

The route name should describe the mechanical effect, not just the lore name.
