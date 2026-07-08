# Build 01 Release Gate Checklist

## Gate status

Status options:

- Not Started
- Blocked
- In Testing
- Failed
- Passed
- Ready for Build 02

Current status: Not Started

## Gate A - Repo readiness

- [ ] Latest main pulled locally
- [ ] All Build 01 packs committed and pushed
- [ ] No unrelated working tree changes
- [ ] Server config drafts match live plugin versions
- [ ] Backup created before live copy

## Gate B - WorldGuard

- [ ] Parent region exists for Lantern Marches
- [ ] Lantern's Rest protected
- [ ] The Bent Road protected or partially protected as intended
- [ ] Hollowglass Pool anomaly zone protected
- [ ] Non-OP cannot break/place in protected areas
- [ ] Non-OP can move through intended route
- [ ] Admin bypass confirmed

## Gate C - BetonQuest

- [ ] Package loads without errors
- [ ] Registrar Elian Voss conversation opens
- [ ] Archivist Maera Vale conversation opens
- [ ] Road Warden Tollen conversation opens
- [ ] Quest objectives progress correctly
- [ ] Journal entries are readable
- [ ] Quest tags are applied once only
- [ ] Reset protocol works for test account

## Gate D - MythicMobs

- [ ] Skills reload cleanly
- [ ] Mobs reload cleanly
- [ ] Roadstray spawns manually
- [ ] Bent Road Drifter spawns manually
- [ ] Mirrorfen Wisp spawns manually
- [ ] Hollowglass Shardling spawns manually
- [ ] Ash Mile Cinderling spawns manually
- [ ] No mob one-shots a fresh player
- [ ] Drops are either disabled or controlled

## Gate E - Oraxen

- [ ] Items reload cleanly
- [ ] Pack reloads cleanly
- [ ] Pack sends to test player
- [ ] Registry Ledger iteminfo works
- [ ] Bent Road Marker iteminfo works
- [ ] Hollowglass Shard iteminfo works
- [ ] Lantern Waymarker iteminfo works
- [ ] No missing model warnings that block play

## Gate F - FragmentEngine

- [ ] Test player can be registered as Remnant
- [ ] Race identity state is stored by FragmentEngine, not BetonQuest
- [ ] Starter Fragment tutorial state is stored by FragmentEngine
- [ ] Intent tutorial state is stored by FragmentEngine
- [ ] State persists after relog
- [ ] Placeholders display expected values or fallbacks

## Gate G - Full non-OP slice

- [ ] Fresh non-OP account can begin
- [ ] Player understands basic situation after first NPCs
- [ ] Player can follow route without admin help
- [ ] Player encounters low-risk anomaly content
- [ ] Player fights starter mob safely
- [ ] Player returns and completes starter step
- [ ] Player ends with first meaningful progression state

## Release decision

Build 01 release decision:

- [ ] Failed
- [ ] Needs fixes
- [ ] Passed with known minor issues
- [ ] Passed and ready for Build 02

Reviewer:

Date:

Notes:
