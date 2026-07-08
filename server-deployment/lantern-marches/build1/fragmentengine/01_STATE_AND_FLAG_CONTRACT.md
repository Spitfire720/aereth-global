# State and Flag Contract

## Persistent FragmentEngine state

These values belong in FragmentEngine or its own storage layer.

| State | Type | Build 01 use |
| --- | --- | --- |
| `remnant_status` | persistent identity | Confirms all player characters are Remnants |
| `race_id` | persistent identity | Continuant, Sylvae, Delver, Vireborn at launch |
| `registration_status` | persistent starter state | Marks Lantern's Rest registration |
| `first_fragment_contact` | persistent tutorial state | Marks first contact with low-risk anomaly |
| `first_intent_prompt` | persistent tutorial state | Marks first Intent explanation seen |
| `starter_region_id` | persistent/current context | `lantern_marches` |

## BetonQuest-owned temporary quest flags

These are allowed to live in BetonQuest because they describe quest flow, not identity.

| Quest flag | Purpose |
| --- | --- |
| `lm_bq_started_after_refragment` | player has started the intro flow |
| `lm_bq_spoke_registrar` | dialogue step complete |
| `lm_bq_spoke_archivist` | lore explanation complete |
| `lm_bq_checked_bent_road` | exploration objective complete |
| `lm_bq_returned_to_lanterns_rest` | starter loop complete |

## Forbidden shortcuts

Do not do this:

```text
race = BetonQuest tag
fragment = Oraxen item
intent = MMOItems stat
title = LuckPerms rank
remnant_status = scoreboard line only
```

Scoreboards, permissions, and placeholders can mirror state. They do not own state.
