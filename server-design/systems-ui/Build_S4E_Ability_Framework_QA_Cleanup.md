# Build S4E - Ability Framework QA + Cleanup

## Objective

Close the current Ability Framework phase safely.

This build is intentionally not an ability design pass. Current combat/effect routes are scaffolding. The framework must remain ready for future real ability design without locking Aereth into placeholder spell behaviour.

## Scope

1. Clean leftover generated pack README files from the repository root.
2. Remove temporary implementation-files staging folder.
3. Keep server-deployment/backups local only.
4. Add local git exclude rules for generated pack junk.
5. Add a repeatable ability framework QA script.
6. Verify command hooks still exist.
7. Verify Maven build succeeds after ability framework changes.

## Not in scope

- No new abilities.
- No final balance tuning.
- No new combat design.
- No abilities.yml rewrite.
- No Java source changes.
- No plugin.yml changes.
- No live deployment.

## Current Ability Framework Layer

The ability framework currently supports:

- Ability Codex
- Ability Loadout
- Ability Activation GUI
- Hotbar binding
- Slot persistence
- Cooldown tracking
- Resource cost scaffolding
- Scaling placeholder metadata
- Targeting placeholder metadata
- Slot validation and cleanup
- Temporary PvE effect route scaffolding

## Design Principle

The framework should answer:

> Can a future designed ability be registered, equipped, activated, costed, cooled down, targeted, and persisted?

It should not answer:

> What are the final Aereth abilities?

That comes later.
