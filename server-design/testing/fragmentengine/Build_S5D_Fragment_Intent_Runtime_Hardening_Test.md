# Build S5D Test - Fragment + Intent Runtime Hardening

## Local apply

```powershell
Set-Location "C:\Users\Bernardo\Desktop\Aereth global"

Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass -Force

.\tools\powershell\fragmentengine\apply-build-s5d-fragment-intent-runtime-hardening.ps1
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

## QA script

```powershell
Set-Location "C:\Users\Bernardo\Desktop\Aereth global"

.\tools\powershell\fragmentengine\run-s5d-fragment-intent-runtime-hardening-qa.ps1
```

Expected:

```text
[S5D QA] PASS
```

## In-game smoke test

After deployment and restart:

```text
/aereth card
/aereth intent SpitFire720
/aereth fragments SpitFire720
/aereth intentgui
```

Expected:

- Character Card opens.
- Intent summary still works.
- Fragment summary still works.
- Intent GUI still works.
- No console errors.
- Existing fragments and intents remain readable.

## Negative state tests

Optional, only on a local test character YAML backup:

1. Add an unknown intent ID to `intent.active.slot1`.
2. Add a duplicate intent ID to another active slot.
3. Add an unknown fragment ID to `fragments.equipped`.
4. Add more equipped fragments than capacity.
5. Open `/aereth card` or run related summaries.

Expected:

- Unknown/duplicate/overflow state is cleaned or ignored safely.
- Framework metadata records the issue.
- No crash.
