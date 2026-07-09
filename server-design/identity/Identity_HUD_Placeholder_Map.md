# Identity HUD Placeholder Map

## Purpose

This document maps Aereth identity concepts to PlaceholderAPI values for scoreboards, action bars, holograms, NPC dialogue, and later website exports.

S5F assumes S5E has added the identity bridge placeholders.

## Core character placeholders

```text
%aereth_character_name%
%aereth_character_slot%
%aereth_race%
%aereth_race_display%
%aereth_level%
%aereth_phase%
%aereth_xp%
%aereth_xp_required%
%aereth_xp_progress_percent%
```

## Fragment placeholders

```text
%aereth_fragment_capacity%
%aereth_fragment_slots_used%
%aereth_fragment_slots_free%
%aereth_fragment_pressure%
%aereth_fragment_stability%
%aereth_fragment_equipped%
%aereth_fragment_equipped_display%
%aereth_fragment_discovered%
%aereth_fragment_discovered_display%
```

## Intent placeholders

```text
%aereth_intent_primary%
%aereth_intent_primary_display%
%aereth_intent_slots%
%aereth_intent_slots_used%
%aereth_intent_pressure%
%aereth_intent_stability_impact%
%aereth_intent_active%
%aereth_intent_active_display%
%aereth_intent_slot_1%
%aereth_intent_slot_2%
%aereth_intent_slot_3%
%aereth_intent_slot_4%
%aereth_intent_slot_1_display%
%aereth_intent_slot_2_display%
%aereth_intent_slot_3_display%
%aereth_intent_slot_4_display%
```

## Identity bridge placeholders

```text
%aereth_identity_state%
%aereth_identity_summary%
%aereth_identity_combined_pressure%
%aereth_identity_combined_stability%
%aereth_identity_fragment_pressure%
%aereth_identity_intent_pressure%
%aereth_identity_diagnostic%
```

## Discipline placeholders

```text
%aereth_discipline%
%aereth_discipline_display%
%aereth_discipline_family%
%aereth_discipline_rank%
%aereth_discipline_rank_name%
%aereth_discipline_xp%
%aereth_discipline_xp_required%
%aereth_discipline_progress_percent%
```

## Ability placeholders

```text
%aereth_abilities_unlocked%
%aereth_abilities_locked%
%aereth_abilities_active%
%aereth_ability_count%
```

## Erasure and stability placeholders

```text
%aereth_erasure%
%aereth_erasure_pressure%
%aereth_stability%
%aereth_fragment_stability%
%aereth_identity_combined_stability%
```

## Player-facing recommended set

Use this minimal set first:

```text
%aereth_character_name%
%aereth_race_display%
%aereth_level%
%aereth_phase%
%aereth_discipline_display%
%aereth_discipline_rank_name%
%aereth_intent_primary_display%
%aereth_identity_state%
%aereth_identity_combined_stability%
%aereth_erasure_pressure%
```

## Debug recommended set

Use this set for admin/debug layouts:

```text
%aereth_identity_summary%
%aereth_identity_combined_pressure%
%aereth_fragment_equipped_display%
%aereth_intent_active_display%
%aereth_intent_slot_1_display%
%aereth_intent_slot_2_display%
%aereth_intent_slot_3_display%
%aereth_intent_slot_4_display%
%aereth_identity_diagnostic%
```
