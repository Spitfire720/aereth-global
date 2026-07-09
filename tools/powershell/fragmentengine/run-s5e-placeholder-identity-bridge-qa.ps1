$ErrorActionPreference = "Stop"

Write-Host "[S5E QA] Placeholder Identity Bridge QA started"

$Root = Get-Location
$Expansion = Join-Path $Root "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\papi\AerethPlaceholderExpansion.java"
$Plugin = Join-Path $Root "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\FragmentEnginePlugin.java"
$Pom = Join-Path $Root "FragmentEngine_Build1_Source\pom.xml"

foreach ($File in @($Expansion, $Plugin, $Pom)) {
    if (!(Test-Path $File)) {
        throw "Required file missing: $File"
    }
    Write-Host "[S5E QA] OK file: $File"
}

foreach ($File in @($Expansion, $Plugin)) {
    $Bytes = [System.IO.File]::ReadAllBytes($File)
    if ($Bytes.Length -ge 3 -and $Bytes[0] -eq 0xEF -and $Bytes[1] -eq 0xBB -and $Bytes[2] -eq 0xBF) {
        throw "UTF-8 BOM found in $File"
    }
    Write-Host "[S5E QA] No BOM: $File"
}

$Text = Get-Content -Raw -Path $Expansion
$Markers = @(
    "identity_state",
    "identity_total_pressure",
    "identity_combined_stability",
    "identity_erasure_pressure",
    "identity_summary",
    "fragment_equipped_display",
    "fragment_discovered_count",
    "intent_primary_display",
    "intent_active_display",
    "intent_slot1_display",
    "pressureLabel",
    "stabilityLabel",
    "joinFragmentDisplay",
    "joinIntentDisplay"
)

foreach ($Marker in $Markers) {
    if ($Text -notmatch [regex]::Escape($Marker)) {
        throw "Marker missing from placeholder expansion: $Marker"
    }
    Write-Host "[S5E QA] Marker found: $Marker"
}

$PluginText = Get-Content -Raw -Path $Plugin
if ($PluginText -notmatch "AerethPlaceholderExpansion") {
    throw "FragmentEnginePlugin does not register AerethPlaceholderExpansion"
}
Write-Host "[S5E QA] Expansion registration marker found"

Write-Host "[S5E QA] Running Maven build"
Push-Location (Join-Path $Root "FragmentEngine_Build1_Source")
try {
    mvn clean package
    if ($LASTEXITCODE -ne 0) {
        throw "Maven build failed with exit code $LASTEXITCODE"
    }
}
finally {
    Pop-Location
}

Write-Host "[S5E QA] PASS"
