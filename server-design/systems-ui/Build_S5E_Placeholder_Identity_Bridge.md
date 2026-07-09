# Build S5E: Placeholder Identity Bridge

## Goal

Expose the current Fragment + Intent identity framework through PlaceholderAPI in a safe, display-only way.

## Changed files

```text
FragmentEngine_Build1_Source/src/main/java/live/aereth/fragmentengine/papi/AerethPlaceholderExpansion.java
server-design/identity/Placeholder_Identity_Bridge.md
tools/powershell/fragmentengine/apply-build-s5e-placeholder-identity-bridge.ps1
tools/powershell/fragmentengine/run-s5e-placeholder-identity-bridge-qa.ps1
```

## What this build adds

```text
Identity diagnostic placeholders
Fragment display placeholders
Intent display placeholders
Pressure labels
Stability labels
Readable display-name placeholder output
Identity summary placeholder output
```

## What this build does not add

```text
No command patching
No plugin.yml changes
No config upload
No Fragment design changes
No Intent design changes
No gameplay mutation
```

## Placeholder policy

Placeholders are read-only. They are allowed to format state for UI and integration tools, but they do not own or alter state.

## Deployment

Compile and deploy the plugin jar only after Maven succeeds.
