# Oraxen Deployment

## Draft source

```text
server-config-drafts/oraxen/lantern-marches/build1/
```

## Suggested first assets

```text
registry_ledger
bent_road_marker
hollowglass_shard
wick_lantern
quiet_ledger_seal
```

## Live target

Oraxen structures vary by setup. Suggested location:

```text
plugins/Oraxen/items/aereth_lantern_marches.yml
```

Use the draft file:

```text
server-config-drafts/oraxen/lantern-marches/build1/items/lantern_marches_props.yml
```

## Deployment sequence

1. Copy item YAML only.
2. Do not add final custom models yet unless model files exist.
3. Reload Oraxen.
4. Confirm resource pack builds.
5. Confirm no missing model spam.
6. Test each item in-game.

## Commands

```text
/oraxen reload
/oraxen pack generate
/oraxen give <player> registry_ledger
/oraxen give <player> bent_road_marker
/oraxen give <player> hollowglass_shard
```

## Rule

Oraxen is the visual layer, not the identity/progression layer.

Fragments are not Oraxen items. Intent is not an Oraxen stat. Disciplines are not Oraxen classes. The universe continues to survive because we do not let item plugins become theology.
