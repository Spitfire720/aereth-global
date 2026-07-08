# Build S3A - Discipline Codex UI

## Objective

Add a player-facing Discipline Codex GUI that makes the existing Discipline system usable from the Character Card and from a direct command.

## Scope

Build S3A includes:

- `/aereth disciplinegui`
- Discipline Codex inventory GUI
- Character Card Discipline button opens the Codex
- Clickable Discipline selection
- Locked Discipline feedback
- Current Discipline highlighting
- Clear Discipline button for testing/admin iteration
- GUI protection against item movement and dragging

Build S3A does not include:

- Discipline balancing
- Ability activation/combat integration
- Mutation selection
- Discipline respec economy
- Quest-gated Discipline unlocks
- New Discipline definitions

## Design intent

Disciplines are commitment paths. They are not item classes and not MMOItems item types.

The GUI is a player-facing entry point over the existing backend state:

- `discipline.id`
- `discipline.display`
- `discipline.family`
- `discipline.level-required`
- `discipline.selected`
- `discipline.progression.*`
- `profession`
- `remnant-state`

## UX flow

### From Character Card

1. Player runs `/aereth card`.
2. Player clicks Discipline.
3. Discipline Codex opens.

### Direct command

1. Player runs `/aereth disciplinegui`.
2. Discipline Codex opens.

### Selecting a Discipline

1. Player clicks an unlocked Discipline.
2. Plugin calls `DisciplineService.setDiscipline(player, disciplineId)`.
3. Character YAML is saved by the service.
4. GUI refreshes.
5. Chat confirms selection.

### Locked Discipline

1. Player clicks a locked Discipline.
2. Backend rejects selection if player level is too low.
3. Chat explains required level.
4. GUI refreshes.

## Slot layout

```text
Top row: border + Codex header
Rows 2-5: Discipline entries
Bottom row: Back / Clear / Refresh / Close
```

Control slots:

```text
45 = Back to Character Card
48 = Clear Current Discipline
49 = Refresh
53 = Close
```

Discipline slots:

```text
10,11,12,13,14,15,16
19,20,21,22,23,24,25
28,29,30,31,32,33,34
37,38,39,40,41,42,43
```

This supports up to 28 Disciplines, enough for the current 22 base Disciplines.

## Visual states

```text
Selected: Nether Star, purple title
Unlocked: family material, cyan title
Locked: Barrier, dark title
Clear button: Red Dye if selected, Gray Dye if not selected
```

## Family materials

```text
martial      -> Iron Sword
defensive    -> Shield
support      -> Golden Apple
arcane       -> Enchanted Book
summoning    -> Totem of Undying
construction -> Anvil
aereth       -> Echo Shard
unknown      -> Knowledge Book
```

## Backend dependency

S3A depends on existing `DisciplineService` methods. The service already owns validation, persistence, and progression recalculation.

The GUI must not duplicate level validation. It may visually display locked state, but final authority remains in `DisciplineService.setDiscipline(...)`.

## Acceptance criteria

- `/aereth disciplinegui` opens the Discipline Codex.
- Character Card Discipline button opens the Discipline Codex.
- Clicking an unlocked Discipline selects it.
- `/aereth discipline <player>` reflects the selected Discipline.
- `/aereth abilities <player>` reflects the selected Discipline/rank state.
- Clicking locked Disciplines does not corrupt state.
- Clear button resets Discipline through `DisciplineService.clearDiscipline(...)`.
- GUI items cannot be moved or stolen.
- No console errors occur during normal clicks.
