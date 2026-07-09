# Build S5E Test: Placeholder Identity Bridge

## Local QA

Run:

```powershell
Set-Location "C:\Users\Bernardo\Desktop\Aereth global"
.\tools\powershell\fragmentengine\run-s5e-placeholder-identity-bridge-qa.ps1
```

Expected:

```text
[S5E QA] PASS
Maven BUILD SUCCESS
```

## In-game smoke test

After deploy and restart, test with PlaceholderAPI tools if available:

```text
/papi parse SpitFire720 %aereth_identity_state%
/papi parse SpitFire720 %aereth_identity_summary%
/papi parse SpitFire720 %aereth_fragment_equipped_display%
/papi parse SpitFire720 %aereth_intent_primary_display%
/papi parse SpitFire720 %aereth_intent_active_display%
```

Expected:

```text
No raw null output
No console errors
State values are readable
Existing placeholders still work
```

## Existing placeholders to recheck

```text
%aereth_character_name%
%aereth_level%
%aereth_fragment_pressure%
%aereth_intent_pressure%
%aereth_discipline_rank%
%aereth_ability_count%
```

## Failure handling

If Maven fails, do not deploy.

If PlaceholderAPI parse returns the placeholder text unchanged, verify PlaceholderAPI is installed and FragmentEngine logged expansion registration on startup.
