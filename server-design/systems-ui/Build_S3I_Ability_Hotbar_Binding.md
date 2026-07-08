# Build S3I - Ability Hotbar Binding

## Purpose

S3I adds a lightweight ability hotbar binding layer on top of the existing loadout, activation, targeting, and effect stack.

The goal is to let players activate equipped abilities without opening a GUI every time.

## Player controls

- Sneak + swap hands refreshes ability hotbar bindings.
- Right-click a bound ability item to activate that loadout slot.
- Bound ability items cannot be moved or dropped.
- The plugin never overwrites non-Aereth items in occupied hotbar slots.

## Hotbar layout

| Loadout Slot | Hotbar Slot |
|---|---|
| 1 | 5 |
| 2 | 6 |
| 3 | 7 |
| 4 | 8 |

These are zero-based inventory indexes 4 to 7 in Bukkit terms.

## Safety rules

- Existing non-Aereth items are skipped.
- Empty/locked loadout slots clear old managed bind items only.
- Cooldowns still route through AbilityActivationService.
- Existing targeting and effect services remain the source of truth.
- No PvP-specific damage is added here.

## Files

- `AbilityHotbarService.java`
- `AbilityHotbarListener.java`
- `FragmentEnginePlugin.java` registration line
