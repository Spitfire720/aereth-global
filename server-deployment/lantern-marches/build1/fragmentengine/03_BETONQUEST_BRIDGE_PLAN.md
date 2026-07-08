# BetonQuest Bridge Plan

## Purpose

BetonQuest should guide the player through the starter flow and request FragmentEngine state changes at key moments.

## Bridge points

| Starter moment | BetonQuest action | FragmentEngine state |
| --- | --- | --- |
| Player speaks to Registrar | dialogue/objective | check `race_id` and `remnant_status` |
| Registration accepted | event call | set `registration_status = lantern_marches_registered` |
| Archivist explains RE:FRAGMENT | dialogue | no permanent RPG write unless tutorial flag needed |
| Player reaches Hollowglass Pool | location objective | set `first_fragment_contact = hollowglass_pool` |
| Intent explanation shown | dialogue/objective | set `first_intent_prompt = clarity` |
| Starter loop complete | final event | set `starter_intro_complete = true` |

## BetonQuest should not

- generate race identity,
- assign Fragments directly,
- own permanent progression,
- override FragmentEngine state if it already exists,
- hide errors silently.

## Practical command placeholder pattern

Use this style only after commands are confirmed:

```text
console command: aereth starter register %player% lantern_marches
console command: aereth starter mark-fragment-contact %player% hollowglass_pool
console command: aereth starter mark-intent-prompt %player% clarity
```

## Error handling

If FragmentEngine state is missing, the NPC should say something like:

> Your record is incomplete. Return to the registry post before taking the road.

Not exactly Shakespeare, but better than letting the player fall through the universe because one flag forgot to exist.
