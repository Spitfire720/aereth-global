# FragmentEngine Build 01 - Start Here

This deployment folder exists to prevent the usual plugin mess where every system quietly starts pretending to be the main RPG brain. FragmentEngine owns Aereth player state. Everyone else gets a leash.

## Goal

Connect Lantern Marches Build 01 to FragmentEngine without breaking architecture boundaries.

## Before touching live

Confirm:

- Server starts cleanly.
- FragmentEngine loads with no console errors.
- Character creation plugin loads.
- PlaceholderAPI loads if display hooks are being tested.
- BetonQuest package can run without FragmentEngine state writes first.
- A test player account is ready.

## Files in this pack

- `01_STATE_AND_FLAG_CONTRACT.md`
- `02_COMMAND_AND_API_CONTRACT.md`
- `03_BETONQUEST_BRIDGE_PLAN.md`
- `04_PLACEHOLDERAPI_DISPLAY_PLAN.md`
- `05_TEST_PLAYER_RESET_PROTOCOL.md`
- `06_LIVE_DEPLOYMENT_ORDER.md`
- `07_ROLLBACK_PLAN.md`
- `08_DEPLOYMENT_LOG_TEMPLATE.md`

## Non-negotiable

Do not store canon state in temporary quest tags unless it is purely quest progress.
