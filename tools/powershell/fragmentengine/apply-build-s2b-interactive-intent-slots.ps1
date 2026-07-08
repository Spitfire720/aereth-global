# Aereth FragmentEngine Build S2B - Interactive Intent Slots
# Run from repo root:
#   .\tools\powershell\fragmentengine\apply-build-s2b-interactive-intent-slots.ps1

$ErrorActionPreference = "Stop"

$RepoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..\..")
$ImplementationRoot = Join-Path $RepoRoot "implementation-files\FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine"
$TargetRoot = Join-Path $RepoRoot "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine"

Write-Host "[Build S2B] Applying interactive Intent Slots patch" -ForegroundColor Cyan

$Files = @(
    @{
        Source = Join-Path $ImplementationRoot "gui\IntentSlotsGui.java"
        Target = Join-Path $TargetRoot "gui\IntentSlotsGui.java"
    },
    @{
        Source = Join-Path $ImplementationRoot "listener\FragmentEngineGuiListener.java"
        Target = Join-Path $TargetRoot "listener\FragmentEngineGuiListener.java"
    }
)

foreach ($File in $Files) {
    if (!(Test-Path $File.Source)) {
        throw "Missing implementation file: $($File.Source)"
    }

    $TargetDir = Split-Path $File.Target -Parent
    New-Item -ItemType Directory -Force -Path $TargetDir | Out-Null
    Copy-Item $File.Source $File.Target -Force
    Write-Host "[Build S2B] Copied $($File.Target.Replace($RepoRoot.Path + '\', ''))" -ForegroundColor Green
}

Write-Host "[Build S2B] Patch applied. Now run: cd FragmentEngine_Build1_Source; mvn clean package" -ForegroundColor Green
