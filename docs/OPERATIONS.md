# Aereth Operations

## Deployment process

1. Patch source locally.
2. Run Maven build.
3. Confirm target jar exists.
4. Upload jar to server plugins folder.
5. Remove old FragmentEngine jar.
6. Purge `.paper-remapped` if needed.
7. Full server restart.
8. Run live test commands.
9. Run `/aereth agent export`.
10. Run snapshot publisher.
11. Verify snapshot in GitHub.
12. Write live verification doc.
13. Commit source and docs.

## Maven build

```powershell
mvn clean package
```

## Restart rule

Full restart only.

No reload for core plugin builds.

Reload is where bugs go to dress up as features.

## Snapshot verification

After export and snapshot publisher, verify:

- plugin list has expected jar version
- agent manifest has expected version
- schema version matches build target
- latest status JSON is current
- diagnostics mention correct build

## Passed build definition

A build is only passed when:

- local build succeeds
- server loads correct jar
- live commands work
- PlaceholderAPI works where applicable
- agent export works
- snapshot confirms live state
- GitHub has source and verification docs