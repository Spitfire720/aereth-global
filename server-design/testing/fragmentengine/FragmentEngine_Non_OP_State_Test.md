# FragmentEngine Non-OP State Test

## Purpose

Confirm a normal player can complete the starter flow without admin permissions.

## Test account

Player:
UUID:
Race:

## Steps

- [ ] Join as non-OP.
- [ ] Confirm no admin/debug commands are available.
- [ ] Start Registrar dialogue.
- [ ] Complete registration.
- [ ] Trigger Hollowglass objective.
- [ ] Return to hub.
- [ ] Relog.
- [ ] Confirm dialogue no longer loops incorrectly.

## Fail conditions

- Player can run admin/debug state commands.
- Player cannot progress because a FragmentEngine flag is missing.
- Quest completes but FragmentEngine state does not update.
- State updates but BetonQuest does not progress.
