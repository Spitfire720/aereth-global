# Build S5G Test - Identity Framework QA + Cleanup

## Objective

Confirm S5 identity framework files are present, clean, and compatible with the current Maven build.

## Work Carried Out

1. Run S5G apply script.
2. Run S5G QA script.
3. Confirm required Java files exist.
4. Confirm required docs/templates exist.
5. Confirm critical files do not contain UTF-8 BOM.
6. Confirm core identity markers exist.
7. Confirm Maven build succeeds.

## Commands

```powershell
Set-Location "C:\Users\Bernardo\Desktop\Aereth global"
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass -Force
.\tools\powershell\fragmentengine\apply-build-s5g-identity-framework-qa-cleanup.ps1
.\tools\powershell\fragmentengine\run-s5g-identity-framework-qa.ps1
```

## Expected Result

```text
[S5G QA] PASS
```

## In-Game Testing

No new runtime behavior is introduced by S5G.

Regression checks after previous deployed build:

```text
/aereth card
/aereth intent SpitFire720
/aereth fragments SpitFire720
/papi parse SpitFire720 %aereth_identity_state%
```

## Comment

This build is a lock-point. If it passes, S5 can be treated as a stable foundation for the next framework layer.
