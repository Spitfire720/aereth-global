Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Write-Host "[Build S3J] Applying ability resource costs" -ForegroundColor Cyan

$Root = (Get-Location).Path
$PackRoot = Join-Path $Root "implementation-files"
$SourceRoot = Join-Path $Root "FragmentEngine_Build1_Source"
$Utf8NoBom = New-Object System.Text.UTF8Encoding($false)

function Copy-PackFile {
    param(
        [Parameter(Mandatory=$true)][string]$RelativePath
    )

    $Source = Join-Path $PackRoot $RelativePath
    $Target = Join-Path $Root $RelativePath

    if (!(Test-Path $Source)) {
        throw "Missing pack file: $Source"
    }

    $TargetDir = Split-Path $Target -Parent
    New-Item -ItemType Directory -Force -Path $TargetDir | Out-Null
    Copy-Item $Source $Target -Force
    Write-Host "[Build S3J] Copied $RelativePath" -ForegroundColor Green
}

Copy-PackFile "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\AbilityResourceService.java"
Copy-PackFile "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\AbilityActivationService.java"
Copy-PackFile "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\AbilityHotbarService.java"
Copy-PackFile "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\gui\AbilityActivationGui.java"

Get-ChildItem $SourceRoot -Recurse -Filter "*.java" | ForEach-Object {
    $Text = [System.IO.File]::ReadAllText($_.FullName)
    if ($Text.Length -gt 0 -and $Text[0] -eq [char]0xFEFF) {
        [System.IO.File]::WriteAllText($_.FullName, $Text.Substring(1), $Utf8NoBom)
    }
}

Write-Host "[Build S3J] Build S3J local patch applied." -ForegroundColor Cyan
Write-Host "[Build S3J] Ability costs now spend stamina, mana, focus, instability, or health before cooldown/effects." -ForegroundColor Cyan
Write-Host "[Build S3J] No abilities.yml upload required." -ForegroundColor Cyan
