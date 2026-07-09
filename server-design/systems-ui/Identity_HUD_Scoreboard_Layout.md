# Identity HUD + Scoreboard Layout

## Design target

The HUD should make the player understand who they are in Aereth, not just how many numbers are screaming at them.

The layout should answer:

```text
Who am I?
What state am I in?
What pressure am I under?
What direction am I choosing?
```

## Compact scoreboard layout

```text
&b&lAERETH
&8────────────
&f%aereth_character_name%
&7Race: &f%aereth_race_display%
&7Level: &c%aereth_level% &8/ &f%aereth_phase%
&8────────────
&7Discipline: &b%aereth_discipline_display%
&7Rank: &f%aereth_discipline_rank_name%
&8────────────
&7Intent: &d%aereth_intent_primary_display%
&7State: &f%aereth_identity_state%
&7Stability: &a%aereth_identity_combined_stability%
&7Erasure: &5%aereth_erasure_pressure%
&8────────────
&aereth.live
```

## Minimal action bar

```text
&b%aereth_discipline_display% &8| &d%aereth_intent_primary_display% &8| &fState: %aereth_identity_state% &8| &5Erasure %aereth_erasure_pressure%
```

## Identity hologram layout

```text
&b%aereth_character_name%
&7Race: &f%aereth_race_display%
&7Discipline: &f%aereth_discipline_display% &8/ &7Rank: &f%aereth_discipline_rank_name%
&7Intent: &d%aereth_intent_primary_display%
&7Identity: &f%aereth_identity_summary%
```

## What not to show by default

Avoid showing these on the everyday scoreboard:

```text
All equipped fragments
All discovered fragments
All active intent slots
All ability IDs
Raw internal IDs
Diagnostics unless in a debug layout
```

Players need readable identity, not a YAML autopsy.

## Recommended player display hierarchy

1. Name and race.
2. Level and phase.
3. Discipline and rank.
4. Primary Intent.
5. Stability and Erasure pressure.
6. Short identity state.

## Recommended admin/debug hierarchy

1. All player-facing lines.
2. Equipped fragment display names.
3. Active intent display names.
4. Combined pressure.
5. Combined stability.
6. Diagnostic state.
7. Missing/cleaned state from S5D runtime hardening.
