# Build S5C Test Plan

## Local validation

Run:

```powershell
Set-Location "C:\Users\Bernardo\Desktop\Aereth global"

.\tools\powershell\fragmentengine\run-s5c-fragment-intent-authoring-qa.ps1
```

Expected:

```text
[S5C QA] PASS
```

## Manual checks

Confirm these files exist:

```text
server-design/identity/Fragment_Definition_Authoring_Guide.md
server-design/identity/Intent_Definition_Authoring_Guide.md
server-design/identity/Fragment_Intent_Field_Contract.md
server-design/config-templates/fragments-authoring-template.yml
server-design/config-templates/intents-authoring-template.yml
tools/powershell/fragmentengine/check-fragment-intent-yml-contract.ps1
tools/powershell/fragmentengine/run-s5c-fragment-intent-authoring-qa.ps1
```

## Contract checker

Run:

```powershell
.\tools\powershell\fragmentengine\check-fragment-intent-yml-contract.ps1
```

Expected:

- Runtime `fragments.yml` found.
- Runtime `intents.yml` found.
- Fragment IDs found.
- Intent IDs found.
- Required fields pass.
- Warnings are allowed for forward-compatible design-only fields.

## Build check

The QA script runs Maven. Expected:

```text
BUILD SUCCESS
```

## Deployment

No deployment is required for S5C because it does not change Java runtime or live config files.
