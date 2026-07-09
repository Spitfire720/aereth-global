# Build S5G - Identity Framework QA + Cleanup

## Build Type

Framework consolidation / QA cleanup.

## Goal

Lock the S5 identity work so the project can move forward without dragging hidden YAML/state debt behind it like a little haunted suitcase.

## Phase Covered

S5A-S5F:

1. Fragment + Intent Integration Framework
2. Character Identity Card UI
3. Fragment + Intent Definition Authoring Toolkit
4. Fragment + Intent Runtime Hardening
5. Placeholder Identity Bridge
6. Identity HUD + Scoreboard Blueprint

## Runtime Changes

None.

## Files Added

```text
server-design/identity/Identity_Framework_Status_Snapshot.md
server-design/systems-ui/Build_S5G_Identity_Framework_QA_Cleanup.md
server-design/testing/fragmentengine/Build_S5G_Identity_Framework_QA_Cleanup_Test.md
tools/powershell/fragmentengine/apply-build-s5g-identity-framework-qa-cleanup.ps1
tools/powershell/fragmentengine/check-identity-framework-contract.ps1
tools/powershell/fragmentengine/run-s5g-identity-framework-qa.ps1
```

## Files Not Touched

```text
FragmentEngine_Build1_Source/src/main/java/live/aereth/fragmentengine/command/AerethCommand.java
FragmentEngine_Build1_Source/src/main/resources/plugin.yml
FragmentEngine_Build1_Source/src/main/resources/fragments.yml
FragmentEngine_Build1_Source/src/main/resources/intents.yml
FragmentEngine_Build1_Source/src/main/resources/abilities.yml
```

## QA Expectations

The QA script must confirm:

- Required S5 Java files exist.
- Required S5 docs/templates exist.
- Critical files do not contain UTF-8 BOM.
- Core identity markers exist.
- Maven build succeeds.

## Deployment

No deployment is needed for this build because it adds documentation and QA tooling only.
