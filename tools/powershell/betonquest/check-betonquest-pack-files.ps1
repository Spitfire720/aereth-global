param(
    [string]$RepoRoot = "C:\Users\Bernardo\Desktop\Aereth global"
)

$ErrorActionPreference = "Stop"

$required = @(
    "server-config-drafts\betonquest\lantern-marches\build1\live\package.yml",
    "server-config-drafts\betonquest\lantern-marches\build1\live\events.yml",
    "server-config-drafts\betonquest\lantern-marches\build1\live\conditions.yml",
    "server-config-drafts\betonquest\lantern-marches\build1\live\objectives.yml",
    "server-config-drafts\betonquest\lantern-marches\build1\live\journal.yml",
    "server-config-drafts\betonquest\lantern-marches\build1\live\conversations\registrar_elian_voss.yml",
    "server-config-drafts\betonquest\lantern-marches\build1\live\conversations\archivist_maera_vale.yml",
    "server-config-drafts\betonquest\lantern-marches\build1\live\conversations\road_warden_tollen.yml",
    "server-design\testing\betonquest\BetonQuest_Starter_Flow_Test.md"
)

Set-Location $RepoRoot

$missing = @()
foreach ($file in $required) {
    if (-not (Test-Path $file)) {
        $missing += $file
    }
}

if ($missing.Count -gt 0) {
    Write-Host "Missing files:" -ForegroundColor Red
    $missing | ForEach-Object { Write-Host " - $_" -ForegroundColor Red }
    exit 1
}

Write-Host "BetonQuest Build 01 pack files found." -ForegroundColor Green
