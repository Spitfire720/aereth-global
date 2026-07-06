# FragmentEngine Hooks Draft - Lantern Marches Build 01

This is not live code. It defines what the starter flow should eventually ask FragmentEngine to do.

## Required checks

### On first registry

Check:

- Does player have Aereth profile?
- Does player have race selected?
- Does player have Remnant condition initialized?

If no profile:

- Send player to character creation flow.
- Do not fake identity inside BetonQuest.

If profile exists:

- Add or confirm starter story flag: `lm_registered`

## Suggested backend flags

```text
aereth.region.lantern_marches.entered
aereth.story.lm.registered
aereth.story.lm.bent_road_seen
aereth.story.lm.hollowglass_trace_seen
aereth.story.lm.build1_complete
```

## Do not implement as

- scoreboard-only permanent truth
- Oraxen item
- MythicMobs drop
- LuckPerms rank
- cosmetic title

## Future hook

When the first real Fragment tutorial is implemented, Hollowglass Pool can become the first place where the player perceives a Fragment consequence, not receives a raw power upgrade.
