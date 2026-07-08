# MythicMobs Starter Mobs Test - Build 01

## Test account

Use a non-OP test player.

## Pre-test

- [ ] `/mm reload` succeeds.
- [ ] No console errors.
- [ ] All mobs can be manually spawned.
- [ ] Spawners use real coordinates.
- [ ] WorldGuard regions prevent hub spawning.

## Manual mob spawn test

Run:

```text
/mm mobs spawn LM_Roadstray 1
/mm mobs spawn LM_BentRoadDrifter 1
/mm mobs spawn LM_MirrorfenWisp 1
/mm mobs spawn LM_HollowglassShardling 1
/mm mobs spawn LM_AshMileCinderling 1
```

Pass criteria:
- mob appears
- name is visible/readable
- no console errors
- skills do not spam console
- mob despawns naturally or can be killed
- loot does not break economy

## Route test

Walk:

```text
Lantern's Rest -> The Bent Road -> Mirrorfen edge -> Hollowglass Pool -> return
```

Pass criteria:
- no mobs inside Lantern's Rest
- Bent Road feels lightly dangerous
- Mirrorfen feels strange but survivable
- Hollowglass Pool feels riskier
- player can retreat
- no unavoidable death spiral

## Balance pass

Record:
- number of deaths
- time to kill per mob
- food/health loss
- armor durability loss
- whether mobs path correctly
- any stuck spawners
