# Build S4E - Ability Framework QA + Cleanup Test

## Local checks

Run:

```powershell
.\tools\powershell\fragmentengine\run-s4e-ability-framework-qa.ps1
```

Expected:

- Required source files found.
- No UTF-8 BOM in checked Java/PowerShell files.
- Ability command strings found in AerethCommand.java.
- abilities.yml contract checker runs.
- Maven clean package succeeds.

## In-game smoke test, only if jar was already deployed separately

Commands:

```text
/aereth abilitygui
/aereth abilityloadout
/aereth abilityactivation
/aereth abilitysummary SpitFire720
/aereth abilityresources SpitFire720
/aereth abilitysync
/aereth abilityactivate SpitFire720 1
/aereth abilitycooldowns SpitFire720
```

Expected:

- Menus open.
- Slot framework stays valid.
- Bad slot state does not crash activation.
- Hotbar sync does not overwrite normal unmanaged items.
- Ability activation still follows cooldown/resource/target pipeline.
- Console has no FragmentEngine errors.

## Commit check

After QA passes:

```powershell
git status --short
```

Expected tracked changes should be S4E docs/scripts only, plus local untracked backups ignored or left alone.
