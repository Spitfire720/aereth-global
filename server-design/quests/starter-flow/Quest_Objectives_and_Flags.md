# Quest Objectives and Flags

## Naming convention

Use lowercase snake_case for internal flags.

```text
lm_<area>_<action>
```

Examples:

```text
lm_registry_registered
lm_bentroad_marker_seen
lm_mirrorfen_reflection_solved
lm_oldwick_satchel_recovered
lm_hollowglass_event_seen
```

## Core starter flags

| Flag | Meaning |
| --- | --- |
| `lm_intro_started` | Player spoke to Roadwarden |
| `lm_registry_registered` | Player completed registry intro |
| `lm_race_acknowledged` | Player race identity acknowledged in dialogue |
| `lm_remnant_acknowledged` | Player learned Remnant condition |
| `lm_bentroad_marker_seen` | Player inspected repeated marker |
| `lm_mirrorfen_reflection_solved` | Player completed reflection marker puzzle |
| `lm_oldwick_satchel_recovered` | Player recovered registry satchel |
| `lm_hollowglass_event_seen` | Player witnessed Hollowglass anomaly |
| `lm_hollowglass_survived` | Player completed controlled wave event |
| `lm_lantern_marches_known` | Player has local recognition state |

## Optional flags

| Flag | Meaning |
| --- | --- |
| `lm_saints_crossing_recorded` | Player chose to record local tradition |
| `lm_saints_crossing_preserved` | Player chose to preserve shrine/crossing |
| `lm_saints_crossing_ignored` | Player chose to ignore local belief |
| `lm_oldwick_lantern_restored` | Player restored old lantern |
| `lm_ashmile_route_cleared` | Player cleared temporary ash hazard |

## Placeholder integration candidates

These depend on current FragmentEngine and PlaceholderAPI implementation.

```text
%aereth_race%
%aereth_level%
%aereth_remnant_state%
%aereth_fragment_pressure%
%aereth_intent_primary%
%aereth_title_current%
```

## Reward philosophy

Starter rewards should introduce systems without power creep.

Valid rewards:

- XP.
- Coins/low economy currency.
- Basic food/tool/weapon.
- Route access.
- Local recognition flag.
- Lore/record entry.

Avoid:

- Permanent Discipline choice.
- Raw Fragment power increase.
- High-tier gear.
- Free teleport network unlock.
- Overexplaining all canon in one NPC dialogue, because that is how onboarding dies.
