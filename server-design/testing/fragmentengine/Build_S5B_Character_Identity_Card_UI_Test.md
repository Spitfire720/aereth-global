# Build S5B Test - Character Identity Card UI

## Apply

```powershell
Set-Location "C:\Users\Bernardo\Desktop\Aereth global"

Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass -Force

.\tools\powershell\fragmentengine\apply-build-s5b-character-identity-card-ui.ps1
```

## Compile

```powershell
Set-Location "C:\Users\Bernardo\Desktop\Aereth global\FragmentEngine_Build1_Source"

mvn clean package
```

Expected:

```text
BUILD SUCCESS
```

## QA

```powershell
Set-Location "C:\Users\Bernardo\Desktop\Aereth global"

.\tools\powershell\fragmentengine\run-s5b-character-identity-card-ui-qa.ps1
```

Expected:

```text
[S5B QA] PASS
```

## In-game test

Restart after deploying the successful jar.

```text
/aereth card
```

Click these slots:

- Intent Layer
- Discipline Layer
- Ability Codex
- Ability Loadout
- Activation Pipeline
- Refresh
- Close

Expected:

- Card opens.
- Identity Diagnostic is visible.
- Fragment Layer and Intent Layer are readable.
- Ability Loadout label now matches the loadout click route.
- Activation Pipeline label now matches the activation click route.
- No console errors.
