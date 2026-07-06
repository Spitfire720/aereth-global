# Lantern Marches Build 01 Taskboard

Status: Draft  
Region: The Lantern Marches  
Hub: Lantern's Rest  
Build target: first playable starter loop, levels 1-5 foundation  

## Build goal

Create the first playable server slice of Aereth:

- A stable starter hub.
- A registry interaction.
- A short road journey.
- A low-risk anomaly encounter.
- A starter enemy.
- A return-and-reward loop.
- Clear hooks into race, Remnant condition, Fragment state, and RE:FRAGMENT.

## Milestone 1 - Physical blockout

### LM-B01-001 - Lantern's Rest rough layout

Create a rough in-world blockout with:

- central square
- registry/archive building
- road gate toward The Bent Road
- trader corner
- small shrine/outpost ruin
- low wall or boundary markers
- visible distant road exit

Acceptance:

- Player can spawn safely.
- Player can visually identify where to go.
- Hub feels like a frontier settlement, not a giant capital.
- No final detailing required.

### LM-B01-002 - The Quiet Ledger interior

Create the first registry/archive room.

Required elements:

- ledger desk
- map wall
- shelves/records
- one Archivist NPC position
- one interactable ledger prop
- one exit back to Lantern's Rest

Acceptance:

- The room supports the first conversation.
- Player path is obvious.
- The ledger prop can later be linked to Oraxen/BetonQuest.

### LM-B01-003 - The Bent Road route

Block out a short route from Lantern's Rest toward Hollowglass Pool.

Required elements:

- broken road sections
- old road markers
- two safe pull-off spots
- one small enemy encounter pocket
- one visible anomaly clue

Acceptance:

- Route takes 2-4 minutes to walk at normal speed.
- Player cannot easily get lost.
- Environment teaches that the world is broken but survivable.

## Milestone 2 - Protection and regions

### LM-B01-004 - WorldGuard region plan

Create or prepare the following regions:

- `lm_lanterns_rest`
- `lm_quiet_ledger`
- `lm_bent_road`
- `lm_hollowglass_pool`
- `lm_mirrorfen_edge`
- `lm_ash_mile_locked`
- `lm_full_region`

Acceptance:

- Hub is safe.
- Road allows controlled starter combat.
- Locked or future areas are blocked or warned.
- PVP is disabled in starter areas.

## Milestone 3 - Quest skeleton

### LM-B01-005 - BetonQuest starter flow

Implement the draft starter package as the first quest chain:

- `lm_001_register_remnant`
- `lm_002_walk_bent_road`
- `lm_003_first_disturbance`
- `lm_004_return_to_ledger`

Acceptance:

- Player can start and complete each stage.
- Quest journal updates correctly.
- Quest does not assume final FragmentEngine UI yet.

## Milestone 4 - Starter mobs

### LM-B01-006 - Roadstray enemy

Implement Roadstray as the first starter hostile.

Acceptance:

- Low damage.
- Clear readable behavior.
- Does not overwhelm new players.
- Drops no economy-breaking loot.

### LM-B01-007 - Hollowglass Wisp

Implement Hollowglass Wisp as a low-risk anomaly mob.

Acceptance:

- More magical/weird than Roadstray.
- Teaches that anomalies are not normal monsters.
- Can be used in the Hollowglass Pool encounter.

## Milestone 5 - Oraxen starter assets

### LM-B01-008 - Starter props

Add draft Oraxen entries for:

- registry ledger
- bent road marker
- hollowglass shard
- lantern march waystone
- sealed ash gate marker

Acceptance:

- Items are defined as visual props or tools.
- None of them define RPG identity.
- No Fragment becomes an Oraxen item.

## Milestone 6 - Test pass

### LM-B01-009 - Clean player test

Run the first 15-minute loop on a clean player.

Acceptance:

- No console errors.
- No soft locks.
- No missing quest stages.
- No plugin ownership boundary violations.
