# Build S4D Test - Ability Definition Authoring Toolkit

## Test 1 - Apply toolkit

Run from repo root:

```powershell
.\tools\powershell\fragmentengine\apply-build-s4d-ability-definition-authoring-toolkit.ps1
```

Expected:

```text
Docs copied
Validation script copied
No Java files changed
```

## Test 2 - Validate abilities.yml contract

Run:

```powershell
.\tools\powershell\fragmentengine\check-abilities-yml-contract.ps1
```

Expected:

```text
Ability ids found
PASS if required fields exist
Warnings allowed for forward-compatible fields during transition
```

## Test 3 - Maven optional safety check

Run:

```powershell
Set-Location "C:\Users\Bernardo\Desktop\Aereth global\FragmentEngine_Build1_Source"
mvn clean package
```

Expected:

```text
BUILD SUCCESS
```

Because S4D does not touch Java, a Maven failure would indicate pre-existing local source damage, not this toolkit.
