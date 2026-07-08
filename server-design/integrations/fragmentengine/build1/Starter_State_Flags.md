# Starter State Flags

## FragmentEngine persistent flags

| Flag | Initial | Complete | Trigger |
| --- | --- | --- | --- |
| `registration_status` | `incomplete` | `lantern_marches_registered` | Registrar completion |
| `first_fragment_contact` | `none` | `hollowglass_pool` | Hollowglass Pool objective |
| `first_intent_prompt` | `none` | `clarity` | Archivist/Fragment tutorial |
| `starter_intro_complete` | `false` | `true` | Return to Lantern's Rest |

## BetonQuest tags

| Tag | Purpose |
| --- | --- |
| `lm_bq_started_after_refragment` | quest started |
| `lm_bq_spoke_registrar` | registrar step complete |
| `lm_bq_spoke_archivist` | lore explanation complete |
| `lm_bq_checked_bent_road` | exploration step complete |
| `lm_bq_returned_to_lanterns_rest` | starter loop returned |

## Reset rule

Reset test player state in this order:

1. BetonQuest tags.
2. FragmentEngine Build 01 starter flags.
3. Inventory/location if needed.
4. Never wipe unrelated RPG data without backup.
