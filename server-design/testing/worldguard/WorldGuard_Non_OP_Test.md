# WorldGuard Non-OP Test — Lantern Marches Build 01

Run this after applying regions and flags.

## Test account conditions

| Condition | Required |
| --- | --- |
| OP | No |
| Creative mode | No |
| Region owner/member | No |
| Staff permissions | No |
| Gamemode | Survival or Adventure |

## Test 1 — Hub safety

Location: `aereth_lm_lanterns_rest`

Expected:

- Player can enter and walk around.
- Player cannot break blocks.
- Player cannot place blocks.
- Player cannot PVP.
- Hostile mobs do not spawn naturally.
- Explosions do not damage terrain.

Pass/fail:

```text
Result:
Issues:
Screenshots/video:
```

## Test 2 — Registry Post

Location: `aereth_lm_registry_post`

Expected:

- Player can enter.
- Player can stand near NPC/interact object positions.
- Block break/place denied.
- No accidental entry denial.
- No interaction-deny errors yet.

Pass/fail:

```text
Result:
Issues:
Screenshots/video:
```

## Test 3 — Bent Road traversal

Location: `aereth_lm_bent_road`

Expected:

- Player can walk from Lantern's Rest toward Hollowglass Pool.
- Road is protected from break/place.
- No invisible region wall blocks progression.
- No unexpected damage or mob spawning during first pass.

Pass/fail:

```text
Result:
Issues:
Screenshots/video:
```

## Test 4 — Hollowglass Pool access

Location: `aereth_lm_hollowglass_pool`

Expected:

- Player can enter.
- Player cannot grief terrain.
- Area feels accessible but less safe visually.
- No protected-region spam appears in chat.

Pass/fail:

```text
Result:
Issues:
Screenshots/video:
```

## Test 5 — Builder Staging denial

Location: `aereth_lm_builder_staging`

Expected:

- Non-OP player cannot enter.
- Staff can enter after added as member/owner.

Pass/fail:

```text
Result:
Issues:
Screenshots/video:
```

## Pass criteria

Build 01 WorldGuard pass is complete only when:

- Hub is safe.
- Road is traversable.
- Hollowglass Pool is accessible.
- Non-OP grief prevention works.
- Staff can still build where intended.
- No major permission spam appears.
