Write-Host "[Build S5B] Applying Character Identity Card UI"

$Root = (Get-Location).Path

$Source = Join-Path $Root "implementation-files\FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\gui\CharacterCardGui.java"
$Target = Join-Path $Root "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\gui\CharacterCardGui.java"

if (!(Test-Path $Source)) {
    throw "Implementation source missing: $Source"
}

if (!(Test-Path (Split-Path $Target -Parent))) {
    throw "Target source folder missing: $(Split-Path $Target -Parent)"
}

$Bytes = [System.IO.File]::ReadAllBytes((Resolve-Path $Source))
if ($Bytes.Length -ge 3 -and $Bytes[0] -eq 0xEF -and $Bytes[1] -eq 0xBB -and $Bytes[2] -eq 0xBF) {
    throw "Source CharacterCardGui.java has UTF-8 BOM. Refusing to copy."
}

Copy-Item -Force $Source $Target

$Text = Get-Content -Raw -Path $Target

$Required = @(
    "Identity Diagnostic",
    "Ability Loadout",
    "Activation Pipeline",
    "Outcome Hooks",
    "Fragment Layer",
    "Intent Layer"
)

foreach ($Needle in $Required) {
    if ($Text -notmatch [regex]::Escape($Needle)) {
        throw "CharacterCardGui.java missing expected text: $Needle"
    }
}

$Utf8NoBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText((Resolve-Path $Target), $Text, $Utf8NoBom)

Write-Host "[Build S5B] Copied CharacterCardGui.java"
Write-Host "[Build S5B] No command patching. No plugin.yml changes. Now run mvn clean package."
