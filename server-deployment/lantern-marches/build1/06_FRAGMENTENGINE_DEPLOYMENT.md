# FragmentEngine Deployment Notes

FragmentEngine owns Aereth RPG state.

For Build 01, FragmentEngine should only be integrated after WorldGuard, BetonQuest, and basic MythicMobs tests pass.

## Build 01 integration goal

Minimum useful hooks:

- Detect player entering Lantern's Rest for first time.
- Mark starter registration state.
- Track first anomaly investigation state.
- Track if player has encountered Hollowglass Pool.
- Expose PlaceholderAPI values for future dialogue/display.

## Do not implement yet unless backend is ready

Avoid deep Fragment/Intent mechanical changes during Build 01. This slice is about proving the world, quest, and region loop.

## Required conceptual lock

Fragments:

- Do not grant raw power directly.
- Increase responsibility.
- Change perception before mechanics.
- Are not MMOItems or Oraxen items.

Intent:

- Is meaning/direction.
- Is not a physical item stat.

Disciplines:

- Unlock later.
- Are not starter classes.

## First placeholder values

Potential PlaceholderAPI values later:

```text
%aereth_region%
%aereth_remnant_state%
%aereth_fragment_pressure%
%aereth_first_anomaly_seen%
%aereth_starter_registration%
```
