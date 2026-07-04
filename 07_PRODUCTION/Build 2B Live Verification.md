# Build 2B Live Verification

Status: PASSED

Date: 2026-07-04

## Confirmed Live Stack

- Paper 1.20.6
- Java 21
- FragmentEngine 1.10.0
- Oraxen 1.211.0
- CommandAPI 11.2.0
- MMOItems 6.10.1-SNAPSHOT
- MythicLib 1.7.1-SNAPSHOT
- MythicMobs 5.9.5
- ModelEngine 4.0.9
- BetonQuest 2.2.1
- WorldFracture 1.7.0
- AerethCreatorFragmentBridge 1.0.1

## Locked Decision

Oraxen is pinned to 1.211.0 for the current Paper 1.20.6 + Java 21 server.

Do not use Oraxen 1.217.0 on this stack. That version caused a Java class version mismatch because part of it required a newer Java runtime.

## System Ownership

FragmentEngine owns:
- player identity
- race
- progression
- stats
- fragments
- intent
- titles
- erasure pressure
- stability
- PlaceholderAPI Aereth values
- agent export

Oraxen owns:
- textures
- models
- glyphs
- resource pack visuals
- visual representation of Aereth systems

MMOItems + MythicLib own:
- physical gear
- weapons
- armor
- relics
- item stats
- materials

Fragments are not MMOItems items.
Intent is not an MMOItems stat.
Oraxen does not define progression.

## Current Noise / Cleanup Required

- Oraxen compiled version has missing default/demo assets.
- MMOItems default/demo templates reference missing skills/stats.
- ModelEngine race preview models are missing hitboxes.

These are cleanup issues, not deployment blockers.

## Next Milestone

1. Clean Oraxen default/demo content.
2. Clean MMOItems default/demo content.
3. Start Build 2C: Intent Slots.
