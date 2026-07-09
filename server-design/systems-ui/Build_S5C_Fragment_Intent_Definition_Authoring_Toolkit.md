# Build S5C: Fragment + Intent Definition Authoring Toolkit

## UI/system note

S5C is not a visible UI build. It supports future identity UI and world reactions by making Fragment and Intent definition data safer to expand.

## Added tooling

- Fragment definition guide.
- Intent definition guide.
- Field contract.
- Config templates.
- Contract checker.
- QA script.

## Expected developer flow

1. Draft Fragment or Intent in the template file.
2. Review it against the authoring guide.
3. Copy approved definitions into runtime config only when ready.
4. Run the contract checker.
5. Run Maven/QA before deployment.

## Why this exists

Fragment and Intent are core Aereth identity systems. They should not become scattered lore notes or random YAML blobs.

This build keeps future content structured before the server accumulates twelve versions of reality duct-taped together in different folders.
