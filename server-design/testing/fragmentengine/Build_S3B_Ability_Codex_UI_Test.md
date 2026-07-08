# Build S3B - Ability Codex UI Test

## Preconditions

- Build S2B works.
- Build S3A works.
- Player has an active FragmentEngine character.
- Server is restarted after uploading the new jar.

## Compile test

```powershell
Set-Location "C:\Users\Bernardo\Desktop\Aereth global\FragmentEngine_Build1_Source"
mvn clean package
```

Expected:

```text
BUILD SUCCESS
```

## In-game test

Run:

```text
/aereth abilitygui
```

Expected:

- Ability Codex opens.
- No console error.
- Items cannot be dragged or stolen.

## Character Card navigation

Run:

```text
/aereth card
```

Click the Abilities tile.

Expected:

- Ability Codex opens.

## Discipline Codex navigation

Run:

```text
/aereth disciplinegui
```

Click Open Ability Codex.

Expected:

- Ability Codex opens.

## Rank and unlock test

Run:

```text
/aereth setlevel SpitFire720 16
/aereth disciplinegui
```

Select a Discipline.

Then run:

```text
/aereth abilitygui
/aereth abilities SpitFire720
```

Expected:

- Ability Codex shows selected Discipline.
- Rank is shown.
- Locked/unlocked abilities match `/aereth abilities`.

## XP progression test

Run:

```text
/aereth adddisciplinexp SpitFire720 1000
/aereth abilitygui
/aereth disciplineprogress SpitFire720
```

Expected:

- Discipline progress changes.
- Rank updates if threshold is met.
- Ability unlock state updates if rank increased.
