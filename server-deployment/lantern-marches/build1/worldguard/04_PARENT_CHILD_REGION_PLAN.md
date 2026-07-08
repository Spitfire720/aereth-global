# Parent / Child Region Plan

WorldGuard priorities should be used instead of pretending all rectangles are equal. They are not. Tiny rectangle tyranny is real.

## Structure

```text
aereth_lm_build1_outer                 priority 10
├── aereth_lm_lanterns_rest            priority 50
│   └── aereth_lm_registry_post        priority 70
├── aereth_lm_bent_road                priority 40
├── aereth_lm_hollowglass_pool         priority 45
│   └── aereth_lm_hollowglass_inner    priority 80
└── aereth_lm_builder_staging          priority 90
```

## Why this structure works

- The outer shell keeps the test area protected.
- Lantern's Rest overrides outer behavior as a safe hub.
- Registry Post can receive special quest/talk/interact behavior later.
- Bent Road stays protected while allowing travel.
- Hollowglass Pool can later allow controlled MythicMobs spawns without affecting the hub.
- Hollowglass Inner can later become a quest trigger/interact zone.
- Builder Staging remains staff-only.

## Priority guide

| Priority | Meaning |
| --- | --- |
| 10 | Broad region shell |
| 40–50 | Main subregions |
| 70–80 | Specific gameplay zones |
| 90+ | Staff-only or special override |
