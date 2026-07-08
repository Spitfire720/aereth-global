# BetonQuest Starter Flow Test - Build 01

## Preconditions

- WorldGuard regions pass.
- NPCs exist or temporary NPC command triggers are available.
- BetonQuest package loads without console errors.
- Test player has no Build 01 tags.

## Reset test player

Record whatever reset command is used here:

```text
TBD
```

## Test path

| Step | Action | Expected result | Pass/Fail |
| --- | --- | --- | --- |
| 1 | Talk to Registrar Elian Voss | Dialogue opens | |
| 2 | Choose registration option | `remnant_registered` tag applies | |
| 3 | Journal updates | Quiet Ledger objective appears | |
| 4 | Reach Quiet Ledger location | Objective completes | |
| 5 | Talk to Archivist Maera Vale | RE:FRAGMENT / Remnant dialogue available | |
| 6 | Accept road assignment | Bent Road objective starts | |
| 7 | Reach Bent Road marker | Objective completes | |
| 8 | Reach Hollowglass Pool | Hollowglass objective completes | |
| 9 | Return to Lantern's Rest registry | Intro completion fires | |
| 10 | Check tags | `lm_intro_complete` present | |

## Non-OP test requirements

The player must be able to complete the full flow without:

- OP permissions
- creative mode
- manual teleports
- admin-only NPC interaction
- bypassing protected regions

## Fail conditions

- Console YAML parse error.
- Objective fires early.
- Player gets stuck with no journal direction.
- NPC repeats wrong state.
- Completion reward grants identity/power outside FragmentEngine.
