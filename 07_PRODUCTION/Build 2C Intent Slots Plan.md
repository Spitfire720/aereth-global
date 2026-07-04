# Build 2C - Intent Slots Plan

Status: PLANNED / IN IMPLEMENTATION

## Objective

Implement the first production version of the Intent Slot system inside FragmentEngine.

Intent is not a class system.
Intent is not an MMOItems stat.
Intent does not grant raw abilities yet.

Intent answers: what happens to the result of the player's actions?

## Scope

Build 2C adds:

- Intent definitions
- Intent slot summary
- Primary intent
- Intent pressure
- Intent stability impact
- Set intent command
- Clear intent command
- Intent list command
- PlaceholderAPI intent placeholders
- Agent schema update

## Commands

/aereth intent <player>
/aereth intentlist
/aereth setintent <player> <slot> <intentId>
/aereth clearintent <player> <slot>

Examples:

/aereth intent SpitFire720
/aereth intentlist
/aereth setintent SpitFire720 slot1 anchor
/aereth clearintent SpitFire720 slot1

## PlaceholderAPI

%aereth_intent_primary%
%aereth_intent_slots_used%
%aereth_intent_slots_max%
%aereth_intent_pressure%
%aereth_intent_stability_impact%
%aereth_intent_active%

## Starting Intent Definitions

- anchor
- fracture
- distortion
- memory
- null

## Slot Rules

Level 1-15:
- 1 slot

Level 16-35:
- 2 slots

Level 36-50:
- 3 slots

Level 51-60:
- 4 slots

Mutation instability may later unlock a fifth unstable slot.

## Design Boundary

FragmentEngine owns Intent.

Oraxen may later visualize Intent.
MMOItems must not own Intent.
MythicMobs may later react to Intent.
BetonQuest may later branch dialogue based on Intent.

## Future Work

- Intent effect hooks
- WorldFracture reactions
- Discipline interaction
- Mutation instability
- UI/HUD presentation through Oraxen/PlaceholderAPI