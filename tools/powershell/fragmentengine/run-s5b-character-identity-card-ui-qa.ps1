Write-Host "[S5B QA] Character Identity Card UI QA started"

$Root = (Get-Location).Path
$Card = Join-Path $Root "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\gui\CharacterCardGui.java"
$Listener = Join-Path $Root "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\listener\FragmentEngineGuiListener.java"
$PomDir = Join-Path $Root "FragmentEngine_Build1_Source"

$Files = @($Card, $Listener)
foreach ($File in $Files) {
    if (!(Test-Path $File)) {
        throw "Missing required file: $File"
    }
    Write-Host "[S5B QA] OK file: $File"
}

foreach ($Path in $Files) {
    $Bytes = [System.IO.File]::ReadAllBytes((Resolve-Path $Path))
    if ($Bytes.Length -ge 3 -and $Bytes[0] -eq 0xEF -and $Bytes[1] -eq 0xBB -and $Bytes[2] -eq 0xBF) {
        throw "UTF-8 BOM found in $Path"
    }
    Write-Host "[S5B QA] No BOM: $Path"
}

$CardText = Get-Content -Raw -Path $Card
$RequiredCardText = @(
    "Identity Diagnostic",
    "Ability Loadout",
    "Activation Pipeline",
    "Outcome Hooks",
    "Fragment Layer",
    "Intent Layer",
    "diagnosticState",
    "materialForState"
)

foreach ($Needle in $RequiredCardText) {
    if ($CardText -notmatch [regex]::Escape($Needle)) {
        throw "CharacterCardGui.java missing expected marker: $Needle"
    }
    Write-Host "[S5B QA] Marker found: $Needle"
}

$ListenerText = Get-Content -Raw -Path $Listener
$ListenerMarkers = @(
    "slot == 22",
    "slot == 24",
    "slot == 28",
    "slot == 30",
    "slot == 32"
)

foreach ($Needle in $ListenerMarkers) {
    if ($ListenerText -notmatch [regex]::Escape($Needle)) {
        throw "FragmentEngineGuiListener.java missing expected routing marker: $Needle"
    }
    Write-Host "[S5B QA] Listener route found: $Needle"
}

Write-Host "[S5B QA] Running Maven build"
Push-Location $PomDir
try {
    mvn clean package
    if ($LASTEXITCODE -ne 0) {
        throw "Maven build failed."
    }
}
finally {
    Pop-Location
}

Write-Host "[S5B QA] PASS"
