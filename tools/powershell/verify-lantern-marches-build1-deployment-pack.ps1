param(
    [string]$RepoRoot = "C:\Users\Bernardo\Desktop\Aereth global"
)

$required = @(
    "server-deployment\lantern-marches\build1\00_PRE_DEPLOYMENT_CHECK.md",
    "server-deployment\lantern-marches\build1\01_WORLD_BLOCKOUT_COORDINATE_CAPTURE.md",
    "server-deployment\lantern-marches\build1\02_WORLDGUARD_LIVE_SETUP.md",
    "server-deployment\lantern-marches\build1\03_BETONQUEST_DEPLOYMENT.md",
    "server-deployment\lantern-marches\build1\04_MYTHICMOBS_DEPLOYMENT.md",
    "server-deployment\lantern-marches\build1\05_ORAXEN_DEPLOYMENT.md",
    "server-deployment\lantern-marches\build1\08_TEST_RUN_PROTOCOL.md",
    "server-design\deployment\Lantern_Marches_Build_01_Live_Taskboard.md",
    "lore\obsidian-vault\12_PLUGIN_GAMEPLAY_BRIDGES\Build 01 - Live Deployment Notes.md"
)

Set-Location $RepoRoot

foreach ($path in $required) {
    if (Test-Path $path) {
        Write-Host "OK   $path" -ForegroundColor Green
    } else {
        Write-Host "MISS $path" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "Git status:" -ForegroundColor Cyan
git status --short
