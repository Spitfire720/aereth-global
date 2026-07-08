# Build S4C - Ability Slot Framework Hardening

## Goal

Harden the ability slot/loadout framework before final ability design starts.

This build does not design real abilities. It makes sure the mechanical container is stable so future ability definitions can be added without rewriting the player-facing framework.

## Included changes

1. Validate ability loadout slots safely.
2. Auto-clean broken or unknown ability ids from slots.
3. Prevent duplicate or bad slot state.
4. Add slot diagnostics/debug output in the Ability Loadout GUI and character YAML.
5. Add ability schema contract documentation.
6. Add placeholder route mapping documentation.
7. Mark current effect routes as temporary test routes, not final ability design.

## Files

- `AbilitySlotFrameworkService.java`
- `AbilityActivationService.java`
- `AbilityLoadoutGui.java`
- `server-design/ability-framework/Ability_Schema_Contract.md`
- `server-design/ability-framework/Ability_Route_Mapping_PLACEHOLDER.md`

## Runtime behavior

The slot framework checks all four loadout slots and enforces the current contract:

- slot numbers are 1-4 only
- slot unlocks are based on Discipline rank
- each slot can hold one ability id
- ability ids must exist in `abilities.yml`
- abilities must belong to the selected Discipline
- abilities must be unlocked by current Discipline rank
- duplicates are removed after the first valid occurrence

The framework writes diagnostics under:

```yaml
abilities:
  loadout:
    framework:
      schema: S4C-slot-framework
      checked-at: "..."
      status: clean | cleaned | issues_detected
      issues: []
      cleaned-count: 0
      invalid-count: 0
      duplicate-count: 0
      locked-count: 0
```

## Important boundary

Current routes such as `defensive_pulse`, `blood_pressure`, and `arcane_spark` are temporary test routes. They exist to verify activation, resources, cooldowns, targeting, and slot flow.

They are not final Aereth abilities.
