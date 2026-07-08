param(
    [Parameter(Mandatory=$true)]
    [string]$ServerRoot,

    [string]$RepoRoot = "C:\Users\Bernardo\Desktop\Aereth global"
)

$ErrorActionPreference = "Stop"

$source = Join-Path $RepoRoot "server-config-drafts\oraxen\lantern-marches\build1\live\items\lantern_marches_props.yml"
$targetDir = Join-Path $ServerRoot "plugins\Oraxen\items\aereth"
$target = Join-Path $targetDir "lantern_marches_props.yml"

if (!(Test-Path $source)) {
    Write-Error "Source file missing: $source"
}

New-Item -ItemType Directory -Force -Path $targetDir | Out-Null
Copy-Item -Force $source $target

Write-Host "Copied Oraxen draft to: $target"
Write-Host "Next: add models/textures, then run /oraxen reload items and /oraxen reload pack."
