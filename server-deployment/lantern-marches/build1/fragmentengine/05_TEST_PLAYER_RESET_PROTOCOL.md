# Test Player Reset Protocol

## Purpose

Run the starter flow repeatedly without corrupting real player state.

## Test account rules

Use a dedicated test account or a clearly marked test UUID. Do not use Bernardo's main player state as the guinea pig unless you enjoy archaeology in playerdata folders.

## Reset checklist

Before each test run:

1. Clear BetonQuest starter tags for the player.
2. Reset only Build 01 starter flags in FragmentEngine.
3. Preserve base race identity unless testing race registration.
4. Confirm inventory is clean or controlled.
5. Teleport to Lantern's Rest spawn.
6. Confirm WorldGuard non-OP behavior.
7. Start the Registrar dialogue.

## Expected clean start state

```yaml
starter_region_id: lantern_marches
registration_status: incomplete
first_fragment_contact: none
first_intent_prompt: none
starter_intro_complete: false
```

## Expected complete state

```yaml
registration_status: lantern_marches_registered
first_fragment_contact: hollowglass_pool
first_intent_prompt: clarity
starter_intro_complete: true
```
