# Plugin Cleanup Recovery

Status: RECOVERED

Date: 2026-07-04

## What Happened

An aggressive Oraxen/MMOItems cleanup caused file permission and config loading issues on the server.

Observed failures included:
- Oraxen config permission errors
- Oraxen channel initialization zip file closed errors
- MMOItems config access errors
- Temporary plugin loading instability after .paper-remapped corruption

## Recovery Action

The following recovery approach restored server stability:

- Quarantined broken Oraxen config folder
- Regenerated Oraxen config folder
- Quarantined broken MMOItems config folder
- Regenerated MMOItems config folder
- Purged Paper .paper-remapped cache
- Restarted server cleanly

## Confirmed Stable After Recovery

- FragmentEngine 1.10.0 enabled
- Oraxen 1.211.0 enabled
- MythicMobs 5.9.5 enabled
- MythicLib 1.7.1-SNAPSHOT enabled
- MMOItems 6.10.1-SNAPSHOT enabled
- WorldFracture 1.7.0 enabled
- AerethCreatorFragmentBridge 1.0.1 enabled
- Server reached Done successfully

## Remaining Non-Blocking Noise

- Oraxen compiled version still lacks default assets
- Oraxen reports missing default/demo textures
- MMOItems reports missing demo skills/stats from generated sample templates
- ModelEngine race preview models still need hitboxes

## New Rule

Do not bulk-overwrite Oraxen or MMOItems YAML files on the live server.

Future cleanup must be done one file group at a time, followed by a restart and log check.
