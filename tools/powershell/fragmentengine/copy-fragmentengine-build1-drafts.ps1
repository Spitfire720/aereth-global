param(
    [string]$RepoRoot = "C:\Users\Bernardo\Desktop\Aereth global",
    [string]$ServerRoot = "C:\Path\To\Your\Server"
)

$ErrorActionPreference = "Stop"
Set-Location $RepoRoot

Write-Host "This script does NOT copy files by default." -ForegroundColor Yellow
Write-Host "FragmentEngine Build 01 files are integration contracts, not guaranteed live configs." -ForegroundColor Yellow
Write-Host "Review command/API compatibility before copying anything to: $ServerRoot" -ForegroundColor Yellow

Write-Host "\nDraft files:"
Get-ChildItem "server-config-drafts/fragmentengine/lantern-marches/build1/live" -Recurse -File | Select-Object FullName
Get-ChildItem "server-config-drafts/placeholderapi/lantern-marches/build1/live" -Recurse -File | Select-Object FullName
