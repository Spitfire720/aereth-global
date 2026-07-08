# BetonQuest Deployment Order

## Step 1 - Create package folder

Target live server path depends on your server layout. Common location:

```text
plugins/BetonQuest/QuestPackages/lantern_marches_build1/
```

Copy only `package.yml` first.

## Step 2 - Reload safely

Use console, not panic clicking in-game like a goblin with permissions.

```text
/bq reload
```

Check console for parse errors.

## Step 3 - Add conversations

Copy:

```text
conversations/registrar_elian_voss.yml
conversations/archivist_maera_vale.yml
conversations/road_warden_tollen.yml
```

Reload again.

## Step 4 - Add events and conditions

Copy:

```text
events.yml
conditions.yml
journal.yml
```

Reload again.

## Step 5 - Add objectives last

Copy:

```text
objectives.yml
```

Replace all placeholder coordinates before live use.

## Step 6 - Test as OP

Use an admin account to trigger each NPC manually.

## Step 7 - Test as non-OP

Use a fresh account or reset test tags.

## Step 8 - Freeze Build 01

Once passed, record:

```text
- Date
- Server version
- BetonQuest version
- Package file list
- Test account used
- Issues found
- Fixes made
```
