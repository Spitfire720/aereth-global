# WorldGuard Region Commands Draft - Lantern Marches

This is not final because exact coordinates depend on the build.

## Parent region

```text
/rg define aereth_lantern_marches
/rg flag aereth_lantern_marches build deny
/rg flag aereth_lantern_marches pvp deny
```

## Lantern's Rest core

```text
/rg define aereth_lm_lanterns_rest_core
/rg setparent aereth_lm_lanterns_rest_core aereth_lantern_marches
/rg flag aereth_lm_lanterns_rest_core mob-spawning deny
/rg flag aereth_lm_lanterns_rest_core pvp deny
/rg flag aereth_lm_lanterns_rest_core use allow
/rg flag aereth_lm_lanterns_rest_core interact allow
```

## Bent Road

```text
/rg define aereth_lm_bent_road
/rg setparent aereth_lm_bent_road aereth_lantern_marches
/rg flag aereth_lm_bent_road mob-spawning allow
/rg flag aereth_lm_bent_road pvp deny
```

## Hollowglass Pool

```text
/rg define aereth_lm_hollowglass_pool
/rg setparent aereth_lm_hollowglass_pool aereth_lantern_marches
/rg flag aereth_lm_hollowglass_pool mob-spawning allow
/rg flag aereth_lm_hollowglass_pool pvp deny
```

## Reminder

Use the wooden axe selection tool after the region is built. Do not copy these as final commands until coordinates exist. We are not summoning WorldGuard rectangles into the void for sport.
