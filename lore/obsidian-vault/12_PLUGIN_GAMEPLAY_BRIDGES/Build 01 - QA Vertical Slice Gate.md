# Build 01 - QA Vertical Slice Gate

## Purpose

This note links the Lantern Marches Build 01 QA gate back to the Obsidian source-of-truth layer.

Build 01 is not considered complete when the configs exist. It is complete only when the first playable starter slice passes the full non-OP journey test.

## Systems under test

- [[Plugin Boundary Map]]
- WorldGuard protection layer
- BetonQuest starter flow
- MythicMobs starter encounters
- Oraxen starter props
- FragmentEngine player-state ownership
- PlaceholderAPI display bridge

## Canon checks

The starter slice must preserve these lore rules:

1. Aereth is the world/server/setting.
2. RE:FRAGMENT is the world-changing event/condition.
3. All player characters are Remnants as an existential condition.
4. Fragments do not grant raw power directly.
5. Fragments alter perception, responsibility, access, consequence, pressure, and world interaction.
6. Race, Profession, Discipline, Title, Fragment, Intent, and Outcome Effect are separate layers.
7. The Lantern Marches are the starter subregion between Valterra and Lyssara influence.
8. Lantern's Rest is the starter hub.

## QA source files

Repo paths:

- `server-deployment/lantern-marches/build1/qa/00_QA_START_HERE.md`
- `server-deployment/lantern-marches/build1/qa/01_RELEASE_GATE_CHECKLIST.md`
- `server-deployment/lantern-marches/build1/qa/03_FULL_VERTICAL_SLICE_RUNBOOK.md`
- `server-design/testing/full-slice/Lantern_Marches_Build_01_Full_Slice_Test.md`
- `server-design/release-gates/Build_01_Release_Gate.md`

## Release lock

Do not start Build 02 as implementation work until Build 01 has either:

- passed with known minor issues, or
- passed cleanly.

Critical blockers must become fix tickets first.
