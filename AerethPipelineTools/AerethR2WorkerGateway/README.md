# Aereth R2 Worker Gateway

## Values

Bucket:

```text
aerethsnaphots
```

Prefix:

```text
aereth-1e766ea2214b876ed44ec889cc404cc5/latest
```

View token:

```text
aN8i16fnXu0cdyVKzs-MIr0ozPQn3vcXf3AzQrgDUHk
```

## Deploy

```powershell
wrangler secret put VIEW_TOKEN
```

Paste this token when asked:

```text
aN8i16fnXu0cdyVKzs-MIr0ozPQn3vcXf3AzQrgDUHk
```

Then:

```powershell
wrangler deploy
```

Final manifest URL format:

```text
WORKER_URL/aereth-1e766ea2214b876ed44ec889cc404cc5/latest/manifest.json?key=aN8i16fnXu0cdyVKzs-MIr0ozPQn3vcXf3AzQrgDUHk
```
