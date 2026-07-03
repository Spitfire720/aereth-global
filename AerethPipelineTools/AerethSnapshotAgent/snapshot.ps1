param(
    [switch]$NoUpload,
    [switch]$VerboseRclone
)

$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ConfigPath = Join-Path $ScriptDir "snapshot-config.ps1"
$FilterPath = Join-Path $ScriptDir "exclude-list.txt"

if (!(Test-Path $ConfigPath)) {
    Write-Host "Missing snapshot-config.ps1. Copy snapshot-config.example.ps1 to snapshot-config.ps1 and edit it." -ForegroundColor Red
    exit 1
}

. $ConfigPath

if (!(Get-Command rclone -ErrorAction SilentlyContinue)) {
    throw "Missing required command: rclone"
}

function Remove-IfExists($Path) {
    if (Test-Path $Path) {
        Remove-Item $Path -Recurse -Force -ErrorAction SilentlyContinue
    }
}

function Assert-NoSensitiveFiles($RootPath) {
    $dangerNamePatterns = @(
        "*.db", "*.sqlite", "*.sqlite3", "*.mv.db", "*.h2.db",
        "*.jar", "*.zip",
        "*keystore*", "*token*", "*secret*", "*credential*", "*password*"
    )

    $badFiles = @()
    foreach ($pattern in $dangerNamePatterns) {
        $badFiles += Get-ChildItem $RootPath -Recurse -File -Filter $pattern -ErrorAction SilentlyContinue
    }

    $badPathFragments = @(
        ".paper-remapped", "cache", ".cache", "storage", "storages",
        "data", "database", "databases", "backup", "backups",
        "libs", "lib", "tmp", "temp", "sessions", "transactions",
        "accounts", "players", "characters", "PlayerData", "playerdata",
        "ClaimData", "stats", "advancements", "generated",
        "resource pack", "resource-pack", "resourcepack", "resourcepack_host",
        "pack", "packs"
    )

    $allFiles = Get-ChildItem $RootPath -Recurse -File -ErrorAction SilentlyContinue
    foreach ($file in $allFiles) {
        $relative = $file.FullName.Replace($RootPath, "")
        foreach ($fragment in $badPathFragments) {
            if ($relative -like "*\$fragment\*") {
                $badFiles += $file
                break
            }
        }
    }

    $badFiles = $badFiles | Sort-Object FullName -Unique

    if ($badFiles.Count -gt 0) {
        Write-Host ""
        Write-Host "SAFETY BLOCK: sensitive or large files are still in the snapshot. Upload cancelled." -ForegroundColor Red
        Write-Host "First matches:" -ForegroundColor Yellow
        $badFiles | Select-Object -First 80 | ForEach-Object {
            Write-Host (" - " + $_.FullName.Replace($RootPath, "").TrimStart("\")) -ForegroundColor Yellow
        }
        exit 2
    }
}

$Timestamp = Get-Date -Format "yyyy-MM-dd_HH-mm-ss"
$ArchiveSnapshot = Join-Path $ArchiveDir $Timestamp

New-Item -ItemType Directory -Force -Path $LocalRoot, $RawDir, $LatestDir, $ArchiveDir | Out-Null

Write-Host "== Aereth SAFE snapshot started: $Timestamp ==" -ForegroundColor Cyan

Remove-IfExists $RawDir
Remove-IfExists $LatestDir
New-Item -ItemType Directory -Force -Path $RawDir, $LatestDir | Out-Null

$RemotePath = "$GodlikeRemote$ServerRoot"
$rcloneFlags = @("--filter-from", $FilterPath, "--create-empty-src-dirs", "--fast-list", "--timeout", "45s")
if ($VerboseRclone) { $rcloneFlags += "-vv" } else { $rcloneFlags += "-v" }

Write-Host "Pulling sanitized files from $RemotePath ..." -ForegroundColor Yellow
& rclone copy $RemotePath $RawDir @rcloneFlags

$dangerDirs = @(
    ".paper-remapped", ".cache", "cache", "storage", "storages", "data",
    "database", "databases", "backup", "backups", "libs", "lib", "tmp",
    "temp", "sessions", "transactions", "accounts", "players", "characters",
    "PlayerData", "playerdata", "ClaimData", "stats", "advancements",
    "generated", "resource pack", "resource-pack", "resourcepack",
    "resourcepack_host", "pack", "packs"
)

foreach ($dirName in $dangerDirs) {
    Get-ChildItem $RawDir -Recurse -Directory -ErrorAction SilentlyContinue |
        Where-Object { $_.Name -ieq $dirName } |
        ForEach-Object { Remove-IfExists $_.FullName }
}

$dangerFilePatterns = @(
    "*.db", "*.sqlite", "*.sqlite3", "*.mv.db", "*.h2.db",
    "*.jar", "*.zip", "*keystore*", "*token*", "*secret*", "*credential*", "*password*"
)

foreach ($pattern in $dangerFilePatterns) {
    Get-ChildItem $RawDir -Recurse -File -Filter $pattern -ErrorAction SilentlyContinue |
        Remove-Item -Force -ErrorAction SilentlyContinue
}

Copy-Item "$RawDir\*" $LatestDir -Recurse -Force -ErrorAction SilentlyContinue

$LogsDir = Join-Path $LatestDir "logs"
$LatestLog = Join-Path $LogsDir "latest.log"
if (Test-Path $LatestLog) {
    Get-Content $LatestLog -Tail 400 | Set-Content (Join-Path $LogsDir "latest-tail.txt") -Encoding UTF8
    Remove-Item $LatestLog -Force -ErrorAction SilentlyContinue
}

$TreePath = Join-Path $LatestDir "file-tree.txt"
Get-ChildItem $LatestDir -Recurse -File |
    ForEach-Object { $_.FullName.Replace($LatestDir, "").TrimStart("\") } |
    Sort-Object |
    Set-Content $TreePath -Encoding UTF8

$PluginListPath = Join-Path $LatestDir "plugin-list.txt"
try {
    & rclone lsf "$GodlikeRemote$ServerRoot/plugins" --files-only --include "*.jar" --timeout 45s |
        Sort-Object |
        Set-Content $PluginListPath -Encoding UTF8
} catch {
    "Could not list plugin jars: $($_.Exception.Message)" | Set-Content $PluginListPath -Encoding UTF8
}

Assert-NoSensitiveFiles $LatestDir

$Files = Get-ChildItem $LatestDir -Recurse -File
$Manifest = [ordered]@{
    project = "Aereth"
    generated_at_local = (Get-Date).ToString("s")
    generated_at_utc = (Get-Date).ToUniversalTime().ToString("s") + "Z"
    godlike_remote = $GodlikeRemote
    server_root = $ServerRoot
    public_base_url = $PublicBaseUrl
    snapshot_prefix = $SnapshotPrefix
    file_count = $Files.Count
    important_urls = [ordered]@{
        manifest = "$PublicBaseUrl/$SnapshotPrefix/manifest.json"
        file_tree = "$PublicBaseUrl/$SnapshotPrefix/file-tree.txt"
        latest_log_tail = "$PublicBaseUrl/$SnapshotPrefix/logs/latest-tail.txt"
        plugin_list = "$PublicBaseUrl/$SnapshotPrefix/plugin-list.txt"
    }
    excluded_note = "Databases, secrets, player data, world folders, backups, jars, generated packs, runtime data, and caches are intentionally excluded."
}
$Manifest | ConvertTo-Json -Depth 8 | Set-Content (Join-Path $LatestDir "manifest.json") -Encoding UTF8

Copy-Item $LatestDir $ArchiveSnapshot -Recurse -Force

$ZipPath = Join-Path $LocalRoot "latest.zip"
if (Test-Path $ZipPath) { Remove-Item $ZipPath -Force }
Compress-Archive -Path "$LatestDir\*" -DestinationPath $ZipPath -Force

if (!$NoUpload) {
    Write-Host "Uploading SAFE snapshot to R2: $R2Remote/$SnapshotPrefix ..." -ForegroundColor Yellow
    & rclone sync $LatestDir "$R2Remote/$SnapshotPrefix" -v --delete-excluded --s3-no-check-bucket
    Write-Host "Published:"
    Write-Host "$PublicBaseUrl/$SnapshotPrefix/manifest.json"
}

$Snapshots = Get-ChildItem $ArchiveDir -Directory | Sort-Object Name -Descending
if ($Snapshots.Count -gt $KeepLocalSnapshots) {
    $Snapshots | Select-Object -Skip $KeepLocalSnapshots | Remove-Item -Recurse -Force
}

Write-Host "SAFE snapshot done." -ForegroundColor Green
