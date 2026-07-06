# BetonQuest Deployment

## Draft source

```text
server-config-drafts/betonquest/lantern-marches/build1/
```

## Suggested live target

Depending on your BetonQuest version/configuration, use one of these patterns:

```text
plugins/BetonQuest/QuestPackages/aereth_lantern_marches_build1/
```

or, if your server already uses a different package structure, mirror that existing structure and name the package:

```text
aereth_lantern_marches_build1
```

## Files in the package

```text
package.yml
objectives.yml
events.yml
conditions.yml
journal.yml
items.yml
conversations/archivist_maerin.yml
conversations/road_warden_tovan.yml
```

## Deployment sequence

1. Copy the package to the live BetonQuest package folder.
2. Start server or run BetonQuest reload.
3. Watch console for YAML errors.
4. Test conversation with Archivist Maerin.
5. Test conversation with Road Warden Tovan.
6. Test journal update.
7. Test objective trigger.

## Do not connect yet

Do not connect FragmentEngine state hooks until:

- The package loads.
- Conversations open.
- Objective flow works.
- Journal entries work.
- No console errors appear.

This is called “not stapling one broken thing to another broken thing,” an ancient craft long forgotten by mankind.
