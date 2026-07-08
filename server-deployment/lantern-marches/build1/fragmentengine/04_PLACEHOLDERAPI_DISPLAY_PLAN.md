# PlaceholderAPI Display Plan

## Purpose

Expose safe, read-only state for dialogue, scoreboards, debug panels, or admin checks.

## Proposed placeholders

```text
%aereth_race%
%aereth_race_id%
%aereth_remnant_status%
%aereth_starter_region%
%aereth_registration_status%
%aereth_first_fragment_contact%
%aereth_first_intent_prompt%
%aereth_starter_intro_complete%
```

## Display rules

- These values are read-only mirrors.
- They do not create state.
- They do not replace FragmentEngine storage.
- They should have safe fallback text.

## Suggested fallback text

| Placeholder | Fallback |
| --- | --- |
| race | `Unregistered` |
| remnant status | `Unconfirmed` |
| starter region | `Unknown` |
| registration status | `Incomplete` |
| first fragment contact | `None recorded` |

## Build 01 use

Use these for admin/debug only first. Public UI can come later after the starter flow is stable.
