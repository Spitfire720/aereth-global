# MythicMobs Live Deployment Order

## Target live paths

Typical MythicMobs paths:

```text
plugins/MythicMobs/Mobs/
plugins/MythicMobs/Skills/
plugins/MythicMobs/Spawners/
plugins/MythicMobs/Drops/
```

Your actual server may differ. Check it before copying files, because plugin folders enjoy having opinions.

## Step 1 - Backup

```powershell
$server = "C:\PATH\TO\SERVER"
$stamp = Get-Date -Format "yyyyMMdd-HHmmss"

Copy-Item "$server\plugins\MythicMobs" "$server\backups\MythicMobs-before-lantern-build1-$stamp" -Recurse
```

## Step 2 - Copy draft files manually

Copy:

```text
server-config-drafts/mythicmobs/lantern-marches/build1/live/Skills/lantern_marches_skills.yml
```

to:

```text
plugins/MythicMobs/Skills/lantern_marches_skills.yml
```

Copy:

```text
server-config-drafts/mythicmobs/lantern-marches/build1/live/Mobs/lantern_marches_mobs.yml
```

to:

```text
plugins/MythicMobs/Mobs/lantern_marches_mobs.yml
```

Do not copy spawners until coordinates are replaced.

## Step 3 - Reload

In console:

```text
mm reload
```

or in-game as OP:

```text
/mm reload
```

## Step 4 - Manual spawn tests

```text
/mm mobs spawn LM_Roadstray 1
/mm mobs spawn LM_BentRoadDrifter 1
/mm mobs spawn LM_MirrorfenWisp 1
/mm mobs spawn LM_HollowglassShardling 1
/mm mobs spawn LM_AshMileCinderling 1
```

## Step 5 - Spawner pass

Replace all placeholders in:

```text
server-config-drafts/mythicmobs/lantern-marches/build1/live/Spawners/lantern_marches_spawners_TEMPLATE.yml
```

Then copy it to:

```text
plugins/MythicMobs/Spawners/lantern_marches_spawners.yml
```

Reload again.

## Step 6 - Non-OP combat test

Use a non-OP player with starter gear.
