# Build 01 - FragmentEngine Integration

## Canon purpose

This note locks how the Lantern Marches starter vertical slice talks to FragmentEngine.

FragmentEngine is the RPG brain of Aereth. It owns persistent player identity and state:

- Remnant condition
- Race identity
- Fragment state
- Intent state
- progression values
- Titles
- Discipline state later

Other plugins may display, trigger, or react to this state, but they do not own it.

## Build 01 gameplay purpose

Build 01 only needs the first starter-region integration:

1. Confirm player is a Remnant.
2. Confirm selected race exists.
3. Mark starter registration complete.
4. Mark first Fragment disturbance seen.
5. Mark first Intent tutorial prompt seen.
6. Expose safe placeholders for quest text and debug testing.

## Current starter flow relationship

```text
Character Creation -> FragmentEngine state
Lantern's Rest Registrar -> BetonQuest dialogue reads/checks state
Starter disturbance -> BetonQuest objective requests tutorial flag
Hollowglass Pool -> FragmentEngine marks first anomaly contact
Quest journal -> BetonQuest display layer
Player identity -> FragmentEngine only
```

## Hard boundaries

- Fragments are not MMOItems.
- Intent is not an MMOItems stat.
- Race is not a BetonQuest tag.
- Titles are not cosmetic plugin badges.
- Oraxen props do not define progression.
- MythicMobs enemies do not define player identity.
- BetonQuest dialogue does not permanently own RPG state.

## Implementation status

This pack defines the integration contract. Actual code/API implementation must be checked against the live FragmentEngine source before any live commands are added.

## Release gate

The starter flow cannot be considered playable until a non-OP test player can:

- enter Lantern's Rest,
- pass registration,
- see their Remnant/race state reflected correctly,
- trigger the Hollowglass tutorial state,
- receive the correct BetonQuest progress,
- relog without losing RPG state.
