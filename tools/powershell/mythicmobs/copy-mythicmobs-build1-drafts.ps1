param(
    [Parameter(Mandatory=$true)]
    [string]$ServerRoot,

    [string]$RepoRoot = "C:\Users\Bernardo\Desktop\Aereth global"
)

$source = Join-Path $RepoRoot "server-config-drafts\mythicmobs\lantern-marches\build1\live"
$target = Join-Path $ServerRoot "plugins\MythicMobs"

if (!(Test-Path $source)) {
    Write-Error "Draft source not found: $source"
    exit 1
}

if (!(Test-Path $target)) {
    Write-Error "MythicMobs target not found: $target"
    exit 1
}

$stamp = Get-Date -Format "yyyyMMdd-HHmmss"
$backupRoot = Join-Path $ServerRoot "backups"
New-Item -ItemType Directory -Force -Path $backupRoot | Out-Null

$backup = Join-Path $backupRoot "MythicMobs-before-lantern-build1-$stamp"
Copy-Item $target $backup -Recurse -Force

Write-Host "Backup created: $backup"

New-Item -ItemType Directory -Force -Path (Join-Path $target "Mobs") | Out-Null
New-Item -ItemType Directory -Force -Path (Join-Path $target "Skills") | Out-Null
New-Item -ItemType Directory -Force -Path (Join-Path $target "Spawners") | Out-Null
New-Item -ItemType Directory -Force -Path (Join-Path $target "Drops") | Out-Null

Copy-Item (Join-Path $source "Mobs\lantern_marches_mobs.yml") (Join-Path $target "Mobs\lantern_marches_mobs.yml") -Force
Copy-Item (Join-Path $source "Skills\lantern_marches_skills.yml") (Join-Path $target "Skills\lantern_marches_skills.yml") -Force

Write-Host "Copied mobs and skills. Spawners are intentionally not copied by this script." -ForegroundColor Yellow
Write-Host "Run /mm reload in-game or mm reload in console."
