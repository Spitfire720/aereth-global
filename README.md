# Aereth Global

Aereth is a Minecraft MMORPG-style server project built around custom progression, Fragments, Intent, Disciplines, Erasure, and a public website/wiki experience.

This repository is the active versioned workspace for code, production documentation, website source, and build verification.

## Active structure

```text
FragmentEngine_Build1_Source/   # Custom Aereth RPG backend plugin
website/                        # Public website and wiki app
07_PRODUCTION/                  # Build plans and live verification records
docs/                           # Active project documentation
archive/                        # Archived repo material only
README.md                       # Repository entry point
ROADMAP.md                      # Current technical roadmap
CHANGELOG.md                    # Build/release history
```

## Source of truth split

| Area | Source of truth |
| --- | --- |
| Code and versioned build records | GitHub |
| Current planning, canon, decisions | Notion v2 |
| Live server state | `aereth-snapshot` repo |
| Binaries, backups, exports, images, models | Drive / server storage |
| Lore graph / Obsidian-style thinking | Obsidian layer under World Bible |

## Current backend baseline

- Build 2A: Progression, race, stats foundation. Passed.
- Build 2B: Fragments backend. Passed.
- Build 2C: Intent Slots backend. Passed.
- Build 3A: Discipline identity backend. Passed.
- Build 3B: Discipline progression backend. Passed.
- Build 3C: Discipline passive modifiers. Passed.
- Build 3D: Discipline ability framework. Planned / in progress.

## Current priority

1. Finish Build 3D.
2. Keep repo structure clean.
3. Align `website/` with the public website and wiki plan.
4. Fix Obsidian/lore graph after the repo is stable.

## Hard rules

- Do not commit jars, generated resource packs, live backups, player data, databases, or secrets.
- Do not treat Oraxen, MythicMobs, MMOItems, or BetonQuest as the owner of player identity.
- FragmentEngine owns player RPG state.
- Notion owns current planning and canon decisions.
- GitHub owns code and versioned production records.