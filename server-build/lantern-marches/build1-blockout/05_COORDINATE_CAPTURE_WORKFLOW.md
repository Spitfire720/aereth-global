# Coordinate Capture Workflow

## Capture Order

Capture regions in this order:

1. Build 01 total slice
2. Lantern's Rest safe zone
3. Registry building interior
4. The Bent Road route
5. Hollowglass Pool outer area
6. Hollowglass Pool inner anomaly
7. Optional mob edge pockets

## Method A: WorldEdit Wand

Use:

```text
//wand
```

Left-click position 1.
Right-click position 2.

Then define WorldGuard region manually:

```text
/rg define <region_id>
```

## Method B: Manual Coordinate Recording

Record both corners:

```text
pos1: X _____ Y _____ Z _____
pos2: X _____ Y _____ Z _____
```

Then use:

```text
//pos1 X,Y,Z
//pos2 X,Y,Z
/rg define <region_id>
```

## Height Rule

For starter regions, use generous vertical range:

```text
Minimum Y: -64 or terrain bottom if custom world
Maximum Y: 320 or server max height
```

For interiors, use tighter Y ranges only if needed.

## Coordinate Sheet

Use:

```text
server-build/lantern-marches/build1-blockout/capture-sheets/coordinate_capture_sheet.csv
```

## Important

When capturing, include enough space around roads and buildings for later detailing. Rebuilding regions because the protection box is 3 blocks too tight is the sort of avoidable misery that deserves a documentary.
