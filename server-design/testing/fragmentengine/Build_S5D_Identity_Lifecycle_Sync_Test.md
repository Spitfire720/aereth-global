# Build S5D - Identity Lifecycle Sync Test

## Objective

Confirm FragmentEngine persists identity state safely during runtime lifecycle events and still compiles.

## Automated QA

Run:

```powershell
.\tools\powershell\fragmentengine\run-s5d-identity-lifecycle-sync-qa.ps1
```

Expected:

```text
[S5D QA] PASS
```

## Compile test

Run:

```powershell
Set-Location "C:\Users\Bernardo\Desktop\Aereth global\FragmentEngine_Build1_Source"
mvn clean package
```

Expected:

```text
BUILD SUCCESS
```

## Manual server test

After deploying the jar and restarting the server:

1. Join the server.
2. Open `/aereth card`.
3. Change world or rejoin.
4. Pull the active character YAML from the server.
5. Confirm `identity.*` fields exist and are refreshed.

Relevant fields:

```text
identity.pressure.total
identity.stability.combined
identity.diagnostics.state
identity.hooks.primary-fragment
identity.hooks.primary-intent
identity.hooks.access-tier
identity.last-sync-source
```

## Pass criteria

- Maven returns `BUILD SUCCESS`.
- S5D QA returns `PASS`.
- Server starts without FragmentEngine errors.
- Joining does not throw console errors.
- Quitting does not throw console errors.
- Active character YAML contains refreshed identity fields.
- Existing `/aereth card` and PlaceholderAPI identity placeholders still work.
