# FragmentEngine Integration Notes - Lantern Marches

## Purpose

Define what the starter region needs from FragmentEngine without overbuilding the entire RPG backend.

## Required reads

Suggested placeholders or API reads:

```text
player race
player level
remnant state / registered state
fragment pressure value or starter-safe equivalent
current title / recognition state
```

## Required writes or flags

These should be implemented only if the backend supports them cleanly:

```text
mark player as locally registered in Lantern Marches
mark Hollowglass event witnessed
set local recognition flag
optional starter title hint
```

## Fragment design rule

Fragments do not grant raw power directly.

In the starter region, a Fragment disturbance should change:

- What the player sees.
- Which dialogue appears.
- Which route/interaction unlocks.
- Which responsibility/consequence follows.

It should not simply give `+10 damage` because then the entire philosophical system collapses into a spreadsheet wearing a wizard hat.

## Hollowglass Pool event

### Player-facing effect

- Pool reflects stars even in daylight.
- Player sees missing registry names in reflection.
- Short wave of Hollowglass Shardlings spawns.
- After event, Archivist dialogue changes.

### Backend flag candidate

```text
lm_hollowglass_event_seen
```

### Pressure value candidate

Use a starter-safe value only. Do not expose full high-level fracture math yet.

```text
fragment_pressure_context = low_controlled_disturbance
```

## Title / recognition candidate

Do not require final Title backend for v1. Use staged implementation:

1. Dialogue recognizes player.
2. Quest flag records local recognition.
3. Later, title backend can convert this into a title.

Candidate title:

```text
Of the Lantern Road
```

## Testing checklist

- New player can complete starter flow without existing Fragment data.
- Existing player does not break if they re-enter Lantern's Rest.
- Hollowglass event cannot be infinitely farmed.
- Quest can recover if player disconnects mid-event.
- Placeholder display does not show raw null/undefined values.
