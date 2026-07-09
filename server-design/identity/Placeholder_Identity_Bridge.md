# Placeholder Identity Bridge

Build: S5E
System: FragmentEngine
Scope: PlaceholderAPI output layer for character identity state

## Purpose

The Placeholder Identity Bridge exposes Fragment, Intent, Discipline, Ability, and identity diagnostics through `%aereth_...%` placeholders.

This is a display and integration layer only. It must not own progression, mutate character YAML, or create gameplay behavior.

## Runtime source of truth

The bridge reads:

- Character YAML through `CharacterService`
- Fragment summaries through `FragmentService`
- Intent summaries through `IntentService`
- Discipline summaries through `DisciplineService`
- Ability summaries through `AbilityService`

It does not define Fragment meaning, Intent meaning, Discipline identity, or ability behavior.

## New identity placeholders

```text
%aereth_identity_state%
%aereth_identity_state_display%
%aereth_identity_total_pressure%
%aereth_identity_combined_stability%
%aereth_identity_erasure_pressure%
%aereth_identity_pressure_label%
%aereth_identity_stability_label%
%aereth_identity_summary%
%aereth_identity_remnant_state%
%aereth_identity_profession%
%aereth_identity_fragment_level%
%aereth_identity_existence_strain%
```

## New Fragment display placeholders

```text
%aereth_fragment_pressure_label%
%aereth_fragment_stability_label%
%aereth_fragment_equipped_display%
%aereth_fragment_discovered_display%
%aereth_fragment_discovered_count%
```

## New Intent display placeholders

```text
%aereth_intent_primary_display%
%aereth_intent_slots_free%
%aereth_intent_pressure_label%
%aereth_intent_stability_label%
%aereth_intent_active_display%
%aereth_intent_slot1%
%aereth_intent_slot2%
%aereth_intent_slot3%
%aereth_intent_slot4%
%aereth_intent_slot1_display%
%aereth_intent_slot2_display%
%aereth_intent_slot3_display%
%aereth_intent_slot4_display%
```

## Identity state labels

The bridge currently classifies identity display state as:

```text
unformed
stable
strained
critical
```

This is a diagnostic display label. It is not a final lore verdict and must not be treated as a permanent mechanical category.

## No gameplay ownership

PlaceholderAPI must remain an output bridge.

It may show:

- UI text
- Scoreboard values
- Hologram values
- Debug text
- Website-exported state later

It must not:

- Add or remove Fragments
- Add or remove Intents
- Change Discipline state
- Spend resources
- Trigger abilities
- Own progression

Because letting a scoreboard mutate character identity would be the kind of software sin that makes future developers chew cables.
