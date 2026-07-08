param(
    [string]$RepoRoot = "C:\Users\Bernardo\Desktop\Aereth global"
)

Set-Location $RepoRoot

$files = @(
    "server-deployment/lantern-marches/build1/worldguard/00_WORLDGUARD_START_HERE.md",
    "server-deployment/lantern-marches/build1/worldguard/01_COORDINATE_CAPTURE_SHEET.md",
    "server-config-drafts/worldguard/lantern-marches/build1/live/worldguard_live_commands_template.txt",
    "server-design/testing/worldguard/WorldGuard_Non_OP_Test.md",
    "lore/obsidian-vault/12_PLUGIN_GAMEPLAY_BRIDGES/Build 01 - WorldGuard Region Pass.md"
)

foreach ($file in $files) {
    if (Test-Path $file) {
        Write-Host "OK     $file" -ForegroundColor Green
    } else {
        Write-Host "MISSING $file" -ForegroundColor Red
    }
}
