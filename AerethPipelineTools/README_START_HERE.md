# Aereth Pipeline Tools

Put this whole folder inside your Aereth project folder.

Final structure:

```text
<Aereth project folder>/
  AerethPipelineTools/
    AerethSnapshotAgent/
    AerethR2WorkerGateway/
    README_START_HERE.md
```

## Confirmed values

R2 bucket:

```text
aerethsnaphots
```

Snapshot prefix:

```text
aereth-1e766ea2214b876ed44ec889cc404cc5/latest
```

Worker view token:

```text
aN8i16fnXu0cdyVKzs-MIr0ozPQn3vcXf3AzQrgDUHk
```

Keep the view token private. It is not your Cloudflare key, but it controls read access to the sanitized snapshot.

## What this does

```text
Godlike SFTP
→ Snapshot Agent on your PC
→ Cloudflare R2
→ Worker Gateway
→ ChatGPT reads final manifest URL
```
