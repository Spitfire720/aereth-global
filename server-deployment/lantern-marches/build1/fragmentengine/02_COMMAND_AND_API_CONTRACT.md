# Command and API Contract

This is the desired control surface for Build 01. Treat it as a contract to implement or confirm in FragmentEngine.

## Admin/test commands

```text
/aereth debug player <player>
/aereth debug reset-starter <player>
/aereth debug set-race <player> <race_id>
/aereth debug set-remnant <player> true
/aereth debug set-starter-flag <player> <flag> <value>
```

## Quest-safe commands

These should be usable by console or approved server systems only.

```text
/aereth starter register <player> lantern_marches
/aereth starter mark-fragment-contact <player> hollowglass_pool
/aereth starter mark-intent-prompt <player> clarity
/aereth starter complete-step <player> <step_id>
```

## Read-only commands or placeholders

```text
/aereth state get <player> race_id
/aereth state get <player> remnant_status
/aereth state get <player> registration_status
/aereth state get <player> first_fragment_contact
```

## API direction

Long term, BetonQuest should ideally call FragmentEngine through a controlled bridge/API, not raw string commands everywhere.

Build 01 may use command hooks as a temporary safe bridge if:

- commands are console-only or permission-protected,
- they validate player UUIDs,
- they write through FragmentEngine storage,
- they log changes for debugging,
- they are easy to remove when the API bridge exists.
