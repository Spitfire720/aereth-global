# WorldGuard Rollback Plan

## Before changing anything

Run:

```text
/rg list
/rg info aereth_lm_build1_outer
/rg info aereth_lm_lanterns_rest
/rg info aereth_lm_bent_road
/rg info aereth_lm_hollowglass_pool
```

Screenshot or copy output where practical.

## Soft rollback

Remove problem flags only:

```text
/rg flag <region> <flag-name>
```

WorldGuard clears a flag when the value is omitted.

Example:

```text
/rg flag aereth_lm_lanterns_rest interact
```

## Hard rollback

Remove Build 01 regions in reverse order:

```text
/rg remove aereth_lm_builder_staging
/rg remove aereth_lm_hollowglass_inner
/rg remove aereth_lm_hollowglass_pool
/rg remove aereth_lm_registry_post
/rg remove aereth_lm_bent_road
/rg remove aereth_lm_lanterns_rest
/rg remove aereth_lm_build1_outer
```

## Do not rollback by deleting the world

Deleting the world because flags are wrong is the server-admin equivalent of burning down a house because the curtains are ugly.
