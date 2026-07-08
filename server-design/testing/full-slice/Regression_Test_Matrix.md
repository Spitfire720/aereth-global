# Build 01 Regression Test Matrix

Run this after any fix to Build 01 systems.

| Change type | Must retest |
| --- | --- |
| WorldGuard region/flag change | Non-OP movement, break/place, NPC interaction areas |
| BetonQuest dialogue change | Conversation start, objective progression, tags, journal |
| MythicMobs mob stat change | Manual spawn, combat survivability, drops, despawn |
| Oraxen item/model change | Reload items, pack send, iteminfo, client display |
| FragmentEngine command/state change | Registration, state read, relog persistence, placeholders |
| PlaceholderAPI change | Scoreboard/UI/dialogue placeholder output |
| Build/terrain change | Route readability, protected region coverage, mob spawn safety |

## Minimum regression after any change

- [ ] Server console clean after reload
- [ ] Non-OP can still start quest
- [ ] Non-OP can still complete route
- [ ] Relog persistence still works

## Notes

Regression testing is boring. So is not burning your server down. Choose your boredom.
