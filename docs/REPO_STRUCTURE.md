# Repository Structure

## Active target structure

```text
FragmentEngine_Build1_Source/
website/
07_PRODUCTION/
docs/
archive/
README.md
ROADMAP.md
CHANGELOG.md
```

## Folder responsibilities

### `FragmentEngine_Build1_Source/`

Custom Java plugin source for FragmentEngine.

Owns RPG backend state.

### `website/`

Public website and wiki app.

Should contain frontend source, public assets, and content files.

### `07_PRODUCTION/`

Versioned production build plans and live verification records.

### `docs/`

Active repo documentation.

### `archive/`

Repo-level archived material.

This is not a dumping ground for server backups or binary files.

## Do not commit

- jars
- `.env`
- server backups
- live world folders
- player data
- databases
- generated resource packs
- ModelEngine generated output
- BlueMap generated web output
- secrets