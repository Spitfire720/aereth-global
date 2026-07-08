# Build S4B - Combat Integration Bundle

## Goal
Move the ability system from visual/feedback effects into a safer first combat integration pass.

S4B keeps the existing activation stack intact:

- loadout slot validation
- cooldowns
- resources
- targeting
- scaling
- hotbar activation

The bundle only upgrades the effect layer so ability routes can apply controlled PvE damage and combat tagging.

## Adds

- `AbilityCombatService`
- PvE-only damage guard
- Player-target damage block
- Area target collection for PvE entities
- Combat impact summaries
- Persistent combat tags on affected entities
- Updated `AbilityEffectService` routes using the combat service

## Combat Safety Rules

- Players are never damaged by ability combat routes.
- Caster cannot damage themselves through hostile routes.
- Damage is clamped.
- Single-target route cap: 12 damage.
- Area route cap: 8 damage per target.
- If a Bukkit damage call fails, the route returns a safe failed impact instead of crashing activation.

## Route Behavior

### defensive_pulse
Self defensive buffs only. No hostile damage.

### blood_pressure
Single target PvE pressure route. Applies glowing and controlled damage to non-player living target.

### arcane_spark
Area PvE route. Applies slow/glow and controlled area damage.

### focus_thread
Hybrid route. Gives caster speed, marks target, applies light PvE damage if valid.

### unstable_bloom
Area control route. Applies slow/confusion and light PvE damage.

### stamina_surge
Movement/self-buff route. No hostile damage.

### resonance_ping
Fallback self sustain route. No hostile damage.

## Not Added Yet

- MythicMobs skill execution
- MMOItems stat scaling
- threat tables
- real enemy AI behavior changes
- boss resistance rules
- PvP ability rules

Those belong in later S4 combat bundles.
