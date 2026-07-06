param(
    [string]$Repo = "C:\Users\Bernardo\Desktop\Aereth global"
)

Set-Location $Repo

Write-Host "Checking Lantern Marches Build 01 pack..." -ForegroundColor Cyan

$paths = @(
    "server-design/build-tickets/Lantern_Marches_Build_01_Taskboard.md",
    "server-design/implementation-checklists/Lantern_Marches_Build_01_Checklist.md",
    "server-design/testing/Lantern_Marches_Build_01_Test_Plan.md",
    "server-config-drafts/worldguard/lantern-marches/build1/worldguard_region_commands.md",
    "server-config-drafts/betonquest/lantern-marches/build1/package.yml",
    "server-config-drafts/betonquest/lantern-marches/build1/conversations/archivist_maerin.yml",
    "server-config-drafts/mythicmobs/lantern-marches/build1/mobs/roadstray.yml",
    "server-config-drafts/mythicmobs/lantern-marches/build1/mobs/hollowglass_wisp.yml",
    "server-config-drafts/oraxen/lantern-marches/build1/items/lantern_marches_props.yml",
    "lore/obsidian-vault/12_PLUGIN_GAMEPLAY_BRIDGES/Build 01 - Lantern Marches Implementation.md"
)

$missing = @()

foreach ($path in $paths) {
    if (Test-Path $path) {
        Write-Host "OK   $path" -ForegroundColor Green
    } else {
        Write-Host "MISS $path" -ForegroundColor Red
        $missing += $path
    }
}

Write-Host ""
git status --short

if ($missing.Count -gt 0) {
    Write-Host ""
    Write-Host "Missing files detected." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Lantern Marches Build 01 pack appears present." -ForegroundColor Green
