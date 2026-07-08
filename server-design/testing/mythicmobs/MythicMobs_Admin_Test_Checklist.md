# MythicMobs Admin Test Checklist

## Reload test

```text
/mm reload
```

Expected:
- no syntax errors
- no missing skill references
- no invalid mob type errors

## Spawn test

- [ ] LM_Roadstray
- [ ] LM_BentRoadDrifter
- [ ] LM_MirrorfenWisp
- [ ] LM_HollowglassShardling
- [ ] LM_AshMileCinderling

## Spawner test

- [ ] spawner activates when player approaches
- [ ] spawner respects max mob count
- [ ] mobs leash/reset correctly
- [ ] mobs do not enter Lantern's Rest
- [ ] mobs do not spawn below terrain or inside walls

## Performance test

Watch:
- TPS
- entity count
- console spam
- chunk loading issues
