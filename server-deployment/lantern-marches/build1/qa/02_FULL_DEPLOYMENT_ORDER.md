# Build 01 Full Deployment Order

Use this only after individual system packs are committed and reviewed.

## Before touching live server

1. Confirm server is stopped or in maintenance mode if needed.
2. Create a full plugin config backup.
3. Create a world backup or schematic backup of affected areas.
4. Confirm plugin versions:
   - WorldGuard
   - BetonQuest
   - MythicMobs
   - Oraxen
   - PlaceholderAPI
   - FragmentEngine
5. Confirm test player account exists.

## Deployment order

### 1. WorldGuard

Deploy first because it protects the physical region.

- Apply parent regions
- Apply child regions
- Apply flags
- Test OP and non-OP access

### 2. FragmentEngine contract

Confirm commands, permissions, placeholders, and state storage before quest wiring.

- Confirm Remnant registration command
- Confirm race registration command
- Confirm Fragment tutorial state command
- Confirm Intent tutorial state command
- Confirm test reset command

### 3. PlaceholderAPI

Deploy display bridge only after FragmentEngine confirms actual exposed placeholders.

- Reload PlaceholderAPI
- Test placeholder output
- Confirm fallback text does not leak debug values

### 4. BetonQuest

Deploy dialogue and quest flow.

- Copy package
- Reload package
- Check console errors
- Test conversations as admin
- Test fresh non-OP flow

### 5. MythicMobs

Deploy mobs after protected zones and quest route exist.

- Copy skills
- Copy mobs
- Reload MythicMobs
- Spawn manually
- Add spawners only after manual tests pass

### 6. Oraxen

Deploy visual props after basic flow works.

- Copy item config
- Reload items
- Reload pack
- Send pack to test player
- Test iteminfo
- Place props in admin area first
- Place final props in Lantern's Rest / Bent Road / Hollowglass Pool

## Do not do this

- Do not deploy all plugins at once.
- Do not test only as OP.
- Do not assume generated config names match live plugin APIs.
- Do not use BetonQuest as permanent RPG state.
- Do not spawn mobs before WorldGuard passes.
