# Oraxen Live Deployment Order - Build 01

## 0. Record versions

Run:

```text
/version Oraxen
/version
```

Record results in the deployment log.

## 1. Backup

Back up:

```text
/plugins/Oraxen/items/
/plugins/Oraxen/pack/
/plugins/Oraxen/settings.yml
```

## 2. Copy draft item file

Copy:

```text
server-config-drafts/oraxen/lantern-marches/build1/live/items/lantern_marches_props.yml
```

Into something like:

```text
/plugins/Oraxen/items/aereth/lantern_marches_props.yml
```

Adjust the exact target folder to match your live Oraxen structure.

## 3. Add assets

Prepare matching texture/model assets for the model paths referenced in the YAML.

Do not commit large or generated resource-pack binaries to the main repo.

## 4. Reload

Run:

```text
/oraxen reload items
/oraxen reload pack
```

If that fails, stop and rollback. Do not continue with placement testing.

## 5. Test admin inventory/item info

Run:

```text
/oraxen iteminfo lm_registry_ledger
/oraxen iteminfo lm_lantern_waymarker
/oraxen iteminfo lm_hollowglass_shard
```

Also use the Oraxen inventory GUI if available on your build.

## 6. Test placement

Place furniture only in the admin test zone first.

Check:

- rotation
- barrier/hitbox
- breaking behavior
- drops
- light emission
- resource pack appearance
- no console errors

## 7. Place in Lantern Marches

Only after admin test passes:

- Registry Ledger in Lantern's Rest registry area
- Lantern Waymarkers along the Bent Road
- Hollowglass Shards around Hollowglass Pool
- Archive Crates around Quiet Ledger / Old Wick Post

## 8. Non-OP test

Use a non-OP account and confirm players cannot break protected props unless intended.
