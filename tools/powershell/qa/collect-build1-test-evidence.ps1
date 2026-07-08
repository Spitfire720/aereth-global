param(
    [string]$RepoRoot = "C:\Users\Bernardo\Desktop\Aereth global",
    [string]$TestName = "build1-test"
)

$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$target = Join-Path $RepoRoot "server-deployment\lantern-marches\build1\qa\evidence\$timestamp-$TestName"

New-Item -ItemType Directory -Force -Path $target | Out-Null

$summary = @"
# Build 01 Test Evidence

Created: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")
Test name: $TestName

## Evidence checklist

- [ ] Screenshots copied here
- [ ] Console log excerpts copied here
- [ ] Plugin versions recorded
- [ ] Test result template filled
- [ ] Issues converted into tickets

## Notes

"@

$summary | Set-Content -Path (Join-Path $target "README.md") -Encoding UTF8

Write-Host "Created evidence folder: $target"
