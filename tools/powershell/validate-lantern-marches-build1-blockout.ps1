param(
    [string]$RepoRoot = "C:\Users\Bernardo\Desktop\Aereth global"
)

$ErrorActionPreference = "Stop"

Set-Location $RepoRoot

$required = @(
    "server-build\lantern-marches\build1-blockout\00_BLOCKOUT_START_HERE.md",
    "server-build\lantern-marches\build1-blockout\capture-sheets\coordinate_capture_sheet.csv",
    "server-build\lantern-marches\build1-blockout\capture-sheets\blockout_anchor_points.csv",
    "server-config-drafts\worldguard\lantern-marches\build1\blockout\worldguard_region_commands_template.txt",
    "server-config-drafts\worldguard\lantern-marches\build1\blockout\worldguard_region_placeholders.yml",
    "server-config-drafts\worldedit\lantern-marches\build1\blockout\schematic_checkpoint_plan.md",
    "server-design\testing\Lantern_Marches_Build_01_Blockout_Test.md",
    "server-design\build-standards\Aereth_Blockout_Standards.md",
    "lore\obsidian-vault\12_PLUGIN_GAMEPLAY_BRIDGES\Build 01 - Blockout Execution Notes.md"
)

$missing = @()
foreach ($path in $required) {
    if (-not (Test-Path $path)) {
        $missing += $path
    }
}

if ($missing.Count -gt 0) {
    Write-Host "Missing required files:" -ForegroundColor Red
    $missing | ForEach-Object { Write-Host " - $_" -ForegroundColor Red }
    exit 1
}

Write-Host "Lantern Marches Build 01 blockout pack files are present." -ForegroundColor Green
Write-Host "Next: fill coordinate_capture_sheet.csv after in-game blockout." -ForegroundColor Cyan
