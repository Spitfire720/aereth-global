param(
    [string]$RepoRoot = "C:\Users\Bernardo\Desktop\Aereth global"
)

$path = Join-Path $RepoRoot "server-deployment\lantern-marches\build1\qa\build1_release_summary.md"

$content = @"
# Lantern Marches Build 01 Release Summary

Generated: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")

## Decision

- [ ] Failed
- [ ] Needs fixes
- [ ] Passed with known minor issues
- [ ] Passed and ready for Build 02

## Systems

| System | Status | Notes |
| --- | --- | --- |
| WorldGuard | Pending |  |
| BetonQuest | Pending |  |
| MythicMobs | Pending |  |
| Oraxen | Pending |  |
| FragmentEngine | Pending |  |
| PlaceholderAPI | Pending |  |
| Full non-OP test | Pending |  |

## Critical issues

- None recorded.

## Minor issues

- None recorded.

## Build 02 readiness

- [ ] Ready
- [ ] Not ready
"@

$content | Set-Content -Path $path -Encoding UTF8
Write-Host "Created release summary: $path"
