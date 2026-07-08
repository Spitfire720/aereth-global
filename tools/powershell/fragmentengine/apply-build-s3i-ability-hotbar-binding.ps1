Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Write-Host "[Build S3I] Applying ability hotbar binding" -ForegroundColor Cyan

$Root = (Get-Location).Path
$PackRoot = Join-Path $Root "implementation-files"
$SourceRoot = Join-Path $Root "FragmentEngine_Build1_Source"
$JavaRoot = Join-Path $SourceRoot "src\main\java\live\aereth\fragmentengine"
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
    Write-Host "[Build S3I] Copied $RelativePath" -ForegroundColor Green
}

function Read-NoBom {
    param([Parameter(Mandatory=$true)][string]$Path)
    $Resolved = (Resolve-Path $Path).Path
    $Text = [System.IO.File]::ReadAllText($Resolved)
    if ($Text.Length -gt 0 -and $Text[0] -eq [char]0xFEFF) {
        $Text = $Text.Substring(1)
    }
    return $Text
}

function Write-NoBom {
    param(
        [Parameter(Mandatory=$true)][string]$Path,
        [Parameter(Mandatory=$true)][string]$Text
    )
    $Resolved = (Resolve-Path $Path).Path
    [System.IO.File]::WriteAllText($Resolved, $Text, $Utf8NoBom)
}

Copy-PackFile "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\AbilityHotbarService.java"
Copy-PackFile "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\listener\AbilityHotbarListener.java"

$PluginPath = Join-Path $JavaRoot "FragmentEnginePlugin.java"
if (!(Test-Path $PluginPath)) {
    throw "Missing FragmentEnginePlugin.java at $PluginPath"
}

$PluginText = Read-NoBom $PluginPath
$Registration = "        getServer().getPluginManager().registerEvents(new live.aereth.fragmentengine.listener.AbilityHotbarListener(this, characterService, disciplineService, abilityService), this);"
$Anchor = "        getServer().getPluginManager().registerEvents(new FragmentEngineGuiListener(this, characterService, fragmentService, intentService, disciplineService, abilityService), this);"

if (!$PluginText.Contains($Registration)) {
    if (!$PluginText.Contains($Anchor)) {
        throw "Could not find GUI listener registration anchor in FragmentEnginePlugin.java"
    }
    $PluginText = $PluginText.Replace($Anchor, $Anchor + [Environment]::NewLine + $Registration)
    Write-Host "[Build S3I] Patched FragmentEnginePlugin.java listener registration" -ForegroundColor Green
} else {
    Write-Host "[Build S3I] FragmentEnginePlugin.java already contains hotbar listener registration" -ForegroundColor Yellow
}

Write-NoBom $PluginPath $PluginText

Get-ChildItem $SourceRoot -Recurse -Filter "*.java" | ForEach-Object {
    $Text = [System.IO.File]::ReadAllText($_.FullName)
    if ($Text.Length -gt 0 -and $Text[0] -eq [char]0xFEFF) {
        [System.IO.File]::WriteAllText($_.FullName, $Text.Substring(1), $Utf8NoBom)
    }
}

Write-Host "[Build S3I] Build S3I local patch applied." -ForegroundColor Cyan
Write-Host "[Build S3I] Sneak + swap hands will refresh ability hotbar bindings. Right-click bound items to activate." -ForegroundColor Cyan
