# WorldGuard Region Commands - Lantern Marches Build 01

These are draft commands. Select the correct cuboids in-game before running each `rg define`.

## Parent region

```mcfunction
/rg define lm_full_region
/rg flag lm_full_region pvp deny
/rg flag lm_full_region creeper-explosion deny
/rg flag lm_full_region enderman-grief deny
/rg flag lm_full_region build deny
```

## Lantern's Rest safe hub

```mcfunction
/rg define lm_lanterns_rest
/rg setparent lm_lanterns_rest lm_full_region
/rg flag lm_lanterns_rest pvp deny
/rg flag lm_lanterns_rest mob-spawning deny
/rg flag lm_lanterns_rest damage-animals deny
/rg flag lm_lanterns_rest chest-access deny
/rg flag lm_lanterns_rest interact allow
/rg priority lm_lanterns_rest 50
```

## The Quiet Ledger

```mcfunction
/rg define lm_quiet_ledger
/rg setparent lm_quiet_ledger lm_lanterns_rest
/rg flag lm_quiet_ledger mob-spawning deny
/rg flag lm_quiet_ledger pvp deny
/rg flag lm_quiet_ledger interact allow
/rg priority lm_quiet_ledger 75
```

## The Bent Road

```mcfunction
/rg define lm_bent_road
/rg setparent lm_bent_road lm_full_region
/rg flag lm_bent_road pvp deny
/rg flag lm_bent_road mob-spawning allow
/rg flag lm_bent_road creeper-explosion deny
/rg priority lm_bent_road 30
```

## Hollowglass Pool

```mcfunction
/rg define lm_hollowglass_pool
/rg setparent lm_hollowglass_pool lm_full_region
/rg flag lm_hollowglass_pool pvp deny
/rg flag lm_hollowglass_pool mob-spawning allow
/rg flag lm_hollowglass_pool entry allow
/rg priority lm_hollowglass_pool 40
```

## Mirrorfen edge, future locked

```mcfunction
/rg define lm_mirrorfen_edge
/rg setparent lm_mirrorfen_edge lm_full_region
/rg flag lm_mirrorfen_edge entry deny
/rg flag lm_mirrorfen_edge entry-deny-message The Mirrorfen is not safe yet.
```

## Ash Mile, future locked

```mcfunction
/rg define lm_ash_mile_locked
/rg setparent lm_ash_mile_locked lm_full_region
/rg flag lm_ash_mile_locked entry deny
/rg flag lm_ash_mile_locked entry-deny-message The Ash Mile is sealed for now.
```
