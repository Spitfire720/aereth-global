# MythicMobs Deployment

## Draft source

```text
server-config-drafts/mythicmobs/lantern-marches/build1/
```

## Suggested live target

```text
plugins/MythicMobs/Mobs/Aereth/LanternMarches/roadstray.yml
plugins/MythicMobs/Mobs/Aereth/LanternMarches/hollowglass_wisp.yml
plugins/MythicMobs/Skills/Aereth/LanternMarches/lantern_marches_skills.yml
plugins/MythicMobs/Spawners/Aereth/LanternMarches/lantern_marches_spawners.yml
```

If your live server has an existing naming convention, follow that. Consistency beats artistic improvisation, sadly.

## First mob to test

Start with:

```text
Roadstray
```

Then:

```text
HollowglassWisp
```

## Console commands

```text
/mm reload
/mm mobs list
/mm mobs spawn Roadstray 1
/mm mobs spawn HollowglassWisp 1
```

## Balance expectation

Build 01 mobs should be annoying, not lethal.

- Roadstray = low-risk distorted roadside enemy.
- Hollowglass Wisp = anomaly marker enemy, more atmospheric than dangerous.

If a fresh player dies before understanding the server premise, that is not difficulty. That is onboarding malpractice wearing fantasy boots.
