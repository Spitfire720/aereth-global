# Race Registration Bridge

## Purpose

The Registrar NPC should acknowledge the player's selected race and confirm their Remnant condition without creating those values itself.

## Flow

```text
Player joins after character creation
FragmentEngine has race_id + remnant_status
Registrar reads/checks state
Registrar completes registration
FragmentEngine records registration_status
BetonQuest advances dialogue
```

## Allowed launch race IDs

- `continuant`
- `sylvae`
- `delver`
- `vireborn`

## Failure handling

If no race exists:

```text
Your record is incomplete. The registry cannot bind a name to a blank page.
```

Then route the player back to character creation or admin/debug resolution.
