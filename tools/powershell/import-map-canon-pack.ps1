param(
    [string]$RepoPath = "C:\Users\Bernardo\Desktop\Aereth global"
)

Set-Location $RepoPath

Write-Host "Importing Aereth Map Canon Pack into repo:" $RepoPath

# This script assumes it is run from the extracted pack root, or that the pack files were copied into repo root.
# If you extracted the ZIP directly into the repo root, you can ignore this script and just run git status.

git status --short

Write-Host "Expected paths:"
Write-Host "- lore\obsidian-vault\02_WORLD_ATLAS\Maps"
Write-Host "- website\public\assets\maps"
Write-Host "- docs\visual-direction\maps"

Write-Host "Verify:"
Test-Path "lore\obsidian-vault\02_WORLD_ATLAS\Maps\Map Index.md"
Test-Path "website\public\assets\maps\aereth_macro_world_map_v1.png"
Test-Path "website\public\assets\maps\aereth_political_cultural_atlas_v1.png"
