$ErrorActionPreference = "Stop"

Write-Host "[S5F QA] Identity HUD + Scoreboard Blueprint QA started"

$Root = (Get-Location).Path

$Expected = @(
    "server-design\systems-ui\Build_S5F_Identity_HUD_Scoreboard_Blueprint.md",
    "server-design\systems-ui\Identity_HUD_Scoreboard_Layout.md",
    "server-design\identity\Identity_HUD_Placeholder_Map.md",
    "server-design\config-templates\identity-scoreboard-placeholder-layout.yml",
    "server-design\config-templates\identity-actionbar-placeholder-layout.yml",
    "server-design\config-templates\identity-hologram-placeholder-layout.yml",
    "server-design\testing\fragmentengine\Build_S5F_Identity_HUD_Scoreboard_Blueprint_Test.md",
    "tools\powershell\fragmentengine\apply-build-s5f-identity-hud-scoreboard-blueprint.ps1",
    "tools\powershell\fragmentengine\run-s5f-identity-hud-scoreboard-blueprint-qa.ps1"
)

foreach ($File in $Expected) {
    if (!(Test-Path $File)) {
        throw "Missing expected file: $File"
    }
    Write-Host "[S5F QA] OK file: $File"
}

function Assert-NoBom($Path) {
    $Bytes = [System.IO.File]::ReadAllBytes((Resolve-Path $Path))
    if ($Bytes.Length -ge 3 -and $Bytes[0] -eq 0xEF -and $Bytes[1] -eq 0xBB -and $Bytes[2] -eq 0xBF) {
        throw "UTF-8 BOM found in $Path"
    }
    Write-Host "[S5F QA] No BOM: $Path"
}

Assert-NoBom "tools\powershell\fragmentengine\apply-build-s5f-identity-hud-scoreboard-blueprint.ps1"
Assert-NoBom "tools\powershell\fragmentengine\run-s5f-identity-hud-scoreboard-blueprint-qa.ps1"

$CombinedText = ""
foreach ($File in $Expected) {
    if ($File.EndsWith(".md") -or $File.EndsWith(".yml")) {
        $CombinedText += "`n" + (Get-Content -Raw -Path $File)
    }
}

$Markers = @(
    "%aereth_identity_state%",
    "%aereth_identity_summary%",
    "%aereth_identity_combined_pressure%",
    "%aereth_identity_combined_stability%",
    "%aereth_fragment_equipped_display%",
    "%aereth_intent_primary_display%",
    "%aereth_intent_active_display%"
)

foreach ($Marker in $Markers) {
    if ($CombinedText -notmatch [regex]::Escape($Marker)) {
        throw "Missing placeholder marker in S5F docs/templates: $Marker"
    }
    Write-Host "[S5F QA] Marker found: $Marker"
}

$PapiPath = "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\papi\AerethPlaceholderExpansion.java"
if (Test-Path $PapiPath) {
    $PapiText = Get-Content -Raw -Path $PapiPath
    $RuntimeMarkers = @(
        "identity_state",
        "identity_summary",
        "fragment_equipped_display",
        "intent_primary_display",
        "intent_active_display"
    )

    foreach ($Marker in $RuntimeMarkers) {
        if ($PapiText -notmatch [regex]::Escape($Marker)) {
            throw "S5E placeholder marker not found in AerethPlaceholderExpansion.java: $Marker. Apply S5E before finalizing S5F."
        }
        Write-Host "[S5F QA] Runtime placeholder marker found: $Marker"
    }
} else {
    Write-Host "[S5F QA] WARN: AerethPlaceholderExpansion.java not found. Skipping runtime marker check."
}

Write-Host "[S5F QA] PASS"
