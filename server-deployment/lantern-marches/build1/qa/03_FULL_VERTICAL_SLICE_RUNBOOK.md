# Build 01 Full Vertical Slice Runbook

## Test account

Use a clean non-OP player account.

Record:

- Player name:
- UUID if needed:
- Date:
- Server build:
- Plugin versions:

## Start state

The player should have:

- No OP
- No admin permissions
- No prior BetonQuest tags for this flow
- No prior FragmentEngine tutorial state unless intentionally testing resume behavior
- Default survival/adventure mode as intended

## Route

### Step 1 - Arrival

Expected:

- Player starts in or near Lantern's Rest.
- Player can identify a safe settlement.
- Player cannot grief protected structures.

Evidence:

- Screenshot of spawn/arrival.

### Step 2 - Registry

Expected:

- Player can speak to Registrar Elian Voss.
- Dialogue explains Remnant condition without lore dumping.
- FragmentEngine, not BetonQuest, stores the RPG identity state.

Pass criteria:

- Dialogue opens.
- Quest state begins.
- Player receives next instruction.

### Step 3 - Archive introduction

Expected:

- Player meets Archivist Maera Vale or equivalent archive NPC.
- RE:FRAGMENT is introduced as the world-changing condition.
- The Erasure / Unmaking / Veiling can be mentioned as cultural terms, not separate events.

Pass criteria:

- No contradictory lore.
- Player receives a clear reason to follow The Bent Road.

### Step 4 - The Bent Road

Expected:

- Player can follow road markers.
- Route feels safe but not sterile.
- Road Warden Tollen or route NPC supports navigation.

Pass criteria:

- Player reaches intended route point without admin teleport.
- Route signs/markers are readable.

### Step 5 - First anomaly

Expected:

- Player reaches Hollowglass Pool or low-risk anomaly site.
- The anomaly shows that Fragments change perception/access/consequence, not raw power.

Pass criteria:

- Player receives tutorial hint.
- No permanent state breaks.

### Step 6 - First combat

Expected:

- Starter mob encounter is low-risk.
- Player can survive with basic tools/gear.
- Mob reinforces the region's tone.

Pass criteria:

- No one-shot damage.
- No broken drops.
- Mob despawns or resets cleanly.

### Step 7 - Return and completion

Expected:

- Player returns to Lantern's Rest.
- Quest completion triggers correctly.
- Player ends with first meaningful progression state.

Pass criteria:

- Quest completion tag set.
- FragmentEngine state persists.
- Player knows where to go next.

### Step 8 - Relog test

Expected:

- Player logs out and back in.
- FragmentEngine state persists.
- BetonQuest state persists or resumes as intended.
- Oraxen pack remains loaded or prompts correctly.

Pass criteria:

- No reset unless intentionally reset by admin.

## Final judgment

- [ ] Pass
- [ ] Pass with minor issues
- [ ] Fail

Critical issues found:

Minor issues found:

Fix tickets created:
