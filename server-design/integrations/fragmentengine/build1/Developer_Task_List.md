# Developer Task List - FragmentEngine Build 01

## Required checks

- [ ] Confirm FragmentEngine command namespace.
- [ ] Confirm storage format for player state.
- [ ] Confirm race_id exists after character creation.
- [ ] Confirm starter flags can be added safely.
- [ ] Confirm console-only command bridge can be permission locked.
- [ ] Confirm placeholders exist or create them.
- [ ] Confirm relog persistence.

## Potential code tasks

- [ ] Add `/aereth debug player <player>`.
- [ ] Add `/aereth debug reset-starter <player>`.
- [ ] Add `/aereth starter register <player> lantern_marches`.
- [ ] Add `/aereth starter mark-fragment-contact <player> hollowglass_pool`.
- [ ] Add `/aereth starter mark-intent-prompt <player> clarity`.
- [ ] Add read-only PlaceholderAPI expansion values.
- [ ] Add debug logging for starter state writes.

## Avoid

- [ ] Do not add starter data to unrelated progression files.
- [ ] Do not let BetonQuest tags become permanent identity.
- [ ] Do not create race data twice.
