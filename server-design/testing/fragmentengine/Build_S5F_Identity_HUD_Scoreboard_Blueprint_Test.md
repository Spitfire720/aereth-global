# Build S5F Test — Identity HUD + Scoreboard Blueprint

## Type

Docs/tooling QA.

S5F has no Java patch and no deployment requirement.

## Preconditions

S5E Placeholder Identity Bridge should be applied first.

## Local QA

Run:

```powershell
Set-Location "C:\Users\Bernardo\Desktop\Aereth global"

.\tools\powershell\fragmentengine\run-s5f-identity-hud-scoreboard-blueprint-qa.ps1
```

Expected:

```text
[S5F QA] PASS
```

## Manual review

Check that the templates include:

```text
%aereth_identity_state%
%aereth_identity_summary%
%aereth_identity_combined_pressure%
%aereth_identity_combined_stability%
%aereth_fragment_equipped_display%
%aereth_intent_primary_display%
%aereth_intent_active_display%
```

## In-game placeholder tests

After S5E is deployed and the server is restarted:

```text
/papi parse SpitFire720 %aereth_identity_state%
/papi parse SpitFire720 %aereth_identity_summary%
/papi parse SpitFire720 %aereth_identity_combined_pressure%
/papi parse SpitFire720 %aereth_identity_combined_stability%
/papi parse SpitFire720 %aereth_fragment_equipped_display%
/papi parse SpitFire720 %aereth_intent_primary_display%
/papi parse SpitFire720 %aereth_intent_active_display%
```

Expected:

```text
Values resolve, not raw placeholder text.
```

## Failure interpretation

If raw placeholders are returned, S5E is not deployed, PlaceholderAPI is not loaded, or the expansion did not register.

If values resolve but look ugly, fix display formatting later. Do not invent runtime state just to make a scoreboard line prettier. That way lies madness and several YAML crimes.
