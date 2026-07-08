param(
    [string]$RepoRoot = "C:\Users\Bernardo\Desktop\Aereth global"
)

$ErrorActionPreference = "Stop"
Set-Location $RepoRoot

$paths = @(
    "server-config-drafts/fragmentengine/lantern-marches/build1/live/fragmentengine_build1_contract.yml",
    "server-config-drafts/fragmentengine/lantern-marches/build1/live/command_contract_reference.txt",
    "server-config-drafts/placeholderapi/lantern-marches/build1/live/placeholder_reference.md",
    "server-design/testing/fragmentengine/FragmentEngine_Build_01_Test.md"
)

foreach ($path in $paths) {
    if (!(Test-Path $path)) {
        Write-Host "MISSING: $path" -ForegroundColor Red
    } else {
        Write-Host "OK: $path" -ForegroundColor Green
    }
}

Write-Host "\nReminder: command names in this pack are contracts until confirmed against FragmentEngine source." -ForegroundColor Yellow
