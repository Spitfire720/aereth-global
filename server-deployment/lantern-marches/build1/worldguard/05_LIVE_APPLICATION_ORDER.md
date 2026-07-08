# Live Application Order

Apply WorldGuard regions in this order.

## Step 1 — Confirm plugins

```text
/version WorldGuard
/version WorldEdit
/version Paper
/plugins
```

## Step 2 — Define outer shell

Create `aereth_lm_build1_outer` first.

## Step 3 — Define hub and road

Create:

```text
aereth_lm_lanterns_rest
aereth_lm_registry_post
aereth_lm_bent_road
```

## Step 4 — Define anomaly area

Create:

```text
aereth_lm_hollowglass_pool
aereth_lm_hollowglass_inner
```

## Step 5 — Define staff area

Create:

```text
aereth_lm_builder_staging
```

## Step 6 — Apply priorities

Apply priorities before complicated flags.

## Step 7 — Apply safe flags

Start with safe hub behavior.

## Step 8 — Test before adding danger

Do not add mob spawning or danger behavior until simple movement and permissions pass.

## Step 9 — Save command log

After successful application, copy the exact commands used into:

```text
server-config-drafts/worldguard/lantern-marches/build1/live/worldguard_live_commands_applied.txt
```

Commit it later so we have a versioned deployment record.
