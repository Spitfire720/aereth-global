# MythicMobs Starter Mob Spec - Lantern Marches

## Purpose

Draft specifications for the first Lantern Marches enemies.

## File placement suggestion

```text
plugins/MythicMobs/Mobs/Aereth/LanternMarches.yml
plugins/MythicMobs/Skills/Aereth/LanternMarchesSkills.yml
plugins/MythicMobs/DropTables/Aereth/LanternMarchesDrops.yml
```

## Mob implementation order

1. Roadstray.
2. Wick-Touched.
3. Mirrorfen Wisp.
4. Hollowglass Shardling.
5. Ashling.
6. Ledgerless mini-mob.

## Roadstray draft

- Base entity: wolf, fox, or custom model later.
- Level range: 1-4.
- Health: low.
- Damage: low.
- Skills: short lunge, road particle.

## Wick-Touched draft

- Base entity: zombie / husk.
- Level range: 3-7.
- Health: low-mid starter.
- Skills: weak blind or smoke effect.

## Mirrorfen Wisp draft

- Base entity: allay / vex / custom invisible armorstand logic depending server version.
- Level range: 4-8.
- Skills: small ranged pulse, glow flicker.

## Hollowglass Shardling draft

- Base entity: silverfish / endermite / custom model later.
- Level range: 5-9.
- Skills: short teleport/flicker, glass particle burst.

## Design constraints

- Do not make starter enemies over-mechanical.
- Avoid instant death, heavy CC, or confusing mechanics in first 30 minutes.
- Use particles to teach anomaly categories.
- Custom models can come later. First priority is playable behavior.
