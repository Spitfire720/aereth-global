param()

$ErrorActionPreference = "Stop"
$Root = Get-Location
$Utf8NoBom = New-Object System.Text.UTF8Encoding($false)

Write-Host "[Build S3H] Applying ability balance and definition expansion"

$Source = Join-Path $Root "implementation-files\FragmentEngine_Build1_Source\src\main\resources\abilities.yml"
$Destination = Join-Path $Root "FragmentEngine_Build1_Source\src\main\resources\abilities.yml"

if (!(Test-Path $Source)) {
    throw "Missing source file: $Source"
}

if (Test-Path $Destination) {
    $BackupDir = Join-Path $Root "server-deployment\backups\fragmentengine\local-config"
    New-Item -ItemType Directory -Force -Path $BackupDir | Out-Null
    $Stamp = Get-Date -Format "yyyyMMdd-HHmmss"
    Copy-Item $Destination (Join-Path $BackupDir "abilities-before-s3h-$Stamp.yml") -Force
}

$Parent = Split-Path $Destination -Parent
New-Item -ItemType Directory -Force -Path $Parent | Out-Null
$Text = [System.IO.File]::ReadAllText((Resolve-Path $Source))
if ($Text.Length -gt 0 -and $Text[0] -eq [char]0xFEFF) {
    $Text = $Text.Substring(1)
}
[System.IO.File]::WriteAllText($Destination, $Text, $Utf8NoBom)

Write-Host "[Build S3H] Copied expanded abilities.yml into source resources"
Write-Host "[Build S3H] IMPORTANT: live server reads plugins/FragmentEngine/abilities.yml, so upload that config separately after backing it up."
Write-Host "[Build S3H] Build S3H local patch applied."
