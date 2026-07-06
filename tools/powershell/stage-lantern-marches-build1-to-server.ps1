param(
    [Parameter(Mandatory=$true)]
    [string]$RepoRoot,

    [Parameter(Mandatory=$true)]
    [string]$ServerRoot,

    [switch]$CopyBetonQuest,
    [switch]$CopyMythicMobs,
    [switch]$CopyOraxen,
    [switch]$DryRun
)

$ErrorActionPreference = "Stop"

function Copy-AerethItem {
    param(
        [string]$Source,
        [string]$Destination
    )

    if (!(Test-Path $Source)) {
        Write-Host "Missing source: $Source" -ForegroundColor Red
        return
    }

    Write-Host "COPY: $Source -> $Destination"

    if (!$DryRun) {
        New-Item -ItemType Directory -Force -Path (Split-Path $Destination) | Out-Null
        Copy-Item -Recurse -Force $Source $Destination
    }
}

Write-Host "Aereth Lantern Marches Build 01 staging" -ForegroundColor Cyan
Write-Host "Repo:   $RepoRoot"
Write-Host "Server: $ServerRoot"

$stageRoot = Join-Path $ServerRoot "_aereth_staging\lantern-marches-build1"

if (!$DryRun) {
    New-Item -ItemType Directory -Force -Path $stageRoot | Out-Null
}

# Always stage deployment docs.
Copy-AerethItem `
    -Source (Join-Path $RepoRoot "server-deployment\lantern-marches\build1") `
    -Destination (Join-Path $stageRoot "deployment-docs")

if ($CopyBetonQuest) {
    Copy-AerethItem `
        -Source (Join-Path $RepoRoot "server-config-drafts\betonquest\lantern-marches\build1") `
        -Destination (Join-Path $stageRoot "betonquest\aereth_lantern_marches_build1")
}

if ($CopyMythicMobs) {
    Copy-AerethItem `
        -Source (Join-Path $RepoRoot "server-config-drafts\mythicmobs\lantern-marches\build1") `
        -Destination (Join-Path $stageRoot "mythicmobs")
}

if ($CopyOraxen) {
    Copy-AerethItem `
        -Source (Join-Path $RepoRoot "server-config-drafts\oraxen\lantern-marches\build1") `
        -Destination (Join-Path $stageRoot "oraxen")
}

Write-Host "Done. Review staging folder before copying anything live:" -ForegroundColor Green
Write-Host $stageRoot
