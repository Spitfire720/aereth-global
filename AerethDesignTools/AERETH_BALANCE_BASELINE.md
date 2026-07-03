# Aereth Balance Baseline v0.1

## Current status

This is the first working balance baseline for Aereth. It is not final.

## Progression

Level cap: 60

XP formula:

XP to next level = floor(900 * level^1.9)

Phase bands:

- Level 1-15: discovery
- Level 16-35: commitment
- Level 36-50: optimization
- Level 51-60: mastery

## Discovery Phase Time Targets

Target total time from level 1 to 15:

- Story route: 391 minutes / 6.52 hours
- Balanced route: 415 minutes / 6.92 hours
- Grind route: 507 minutes / 8.45 hours

Design intent:

- Story route is fastest because it follows curated content.
- Balanced route is the recommended baseline.
- Grind route is slower but should not feel punishing.
- Early levels should be quick.
- Later discovery levels should take longer.

## Quest XP Targets

Quest rewards are calculated as a percentage of the player's current level requirement.

Recommended targets for an 8-minute quest:

- Story quest: 30-35% of level
- Side quest: 18-22% of level
- Daily quest: 8-12% of level
- Exploration quest: 14-18% of level
- Elite quest: 40-50% of level

Current level 12 test values:

- Story, 8 min: 32.20%, 32,549 XP
- Side, 8 min: 20.00%, 20,217 XP
- Daily, 8 min: 10.20%, 10,311 XP

## Combat Difficulty Targets

Use difficulty categories by mob role.

### Trivial

Use for harmless/tutorial targets.

Target:
- TTK: ~12 seconds
- Player HP loss: ~5%

### Easy

Use for regular overworld mobs.

Target:
- TTK: ~20 seconds
- Player HP loss: ~12%

Level 10 easy test:
- Mob HP: 770.20
- Raw damage per hit: 4.41
- Attack interval: 2s
- Defense: 6.50
- Simulated duration: 21.60s
- Simulated player HP loss: 13.20%

### Normal

Use for meaningful quest mobs or stronger overworld enemies.

Target:
- TTK: ~32 seconds
- Player HP loss: ~25%

Level 10 normal test:
- Mob HP: 1232.32
- Raw damage per hit: 5.74
- Attack interval: 2s
- Defense: 6.50

Level 15 normal test:
- Mob HP: 1516.16
- Raw damage per hit: 7.32
- Attack interval: 2s
- Defense: 9.75
- Simulated duration: 35s
- Simulated player HP loss: 28.14%

### Hard

Use for dangerous area mobs.

Target:
- TTK: ~48 seconds
- Player HP loss: ~45%

### Elite

Use for named enemies and small group threats.

Target:
- TTK: ~75 seconds
- Player HP loss: ~70%

### Boss

Use for bosses only.

Target:
- TTK: ~150 seconds
- Player HP loss: ~95%

## Usage Rules

Regular mobs should generally use easy difficulty.

Do not use normal difficulty for every overworld mob, or the game will feel spongey.

Normal should be reserved for mobs that are meant to matter.

Elite and boss values are placeholders until ability mechanics, healing, potions, and player skill rotations exist.

## Current Decision

Keep these systems as v0.1 working assumptions:

- XP formula
- Discovery phase time targets
- Corrected quest reward rates
- Easy mob baseline
- Normal mob baseline
- Loot budget model as abstract power budget only

Do not directly convert loot budget into damage or armor yet.
