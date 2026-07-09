param(
    [string]$Root = (Get-Location).Path
)

$ErrorActionPreference = "Stop"

function Assert-File {
    param([string]$Path)
    $Full = Join-Path $Root $Path
    if (!(Test-Path $Full)) {
        throw "Missing required file: $Path"
    }
    Write-Host "[S5G Contract] OK file: $Path"
}

function Assert-NoBom {
    param([string]$Path)
    $Full = Join-Path $Root $Path
    $Bytes = [System.IO.File]::ReadAllBytes($Full)
    if ($Bytes.Length -ge 3 -and $Bytes[0] -eq 0xEF -and $Bytes[1] -eq 0xBB -and $Bytes[2] -eq 0xBF) {
        throw "UTF-8 BOM found: $Path"
    }
    Write-Host "[S5G Contract] No BOM: $Path"
}

function Assert-Contains {
    param(
        [string]$Path,
        [string]$Pattern,
        [string]$Label
    )
    $Full = Join-Path $Root $Path
    $Text = Get-Content -Raw -Path $Full
    if ($Text -notmatch $Pattern) {
        throw "Missing marker [$Label] in $Path"
    }
    Write-Host "[S5G Contract] Marker found: $Label"
}

$JavaFiles = @(
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\CharacterService.java",
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\FragmentService.java",
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\IntentService.java",
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\gui\CharacterCardGui.java",
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\papi\AerethPlaceholderExpansion.java"
)

foreach ($Path in $JavaFiles) {
    Assert-File $Path
    Assert-NoBom $Path
}

# CharacterIdentityService was introduced in S5A. If command wiring was skipped, the service may still exist as framework support.
$IdentityService = "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\CharacterIdentityService.java"
if (Test-Path (Join-Path $Root $IdentityService)) {
    Assert-NoBom $IdentityService
    Assert-Contains $IdentityService "IdentitySummary" "IdentitySummary service record"
    Assert-Contains $IdentityService "RepairResult" "RepairResult service record"
} else {
    Write-Host "[S5G Contract] WARN: CharacterIdentityService.java not found. S5 identity still uses existing Fragment/Intent services only."
}

$Docs = @(
    "server-design\identity\Identity_Framework_Status_Snapshot.md",
    "server-design\identity\Fragment_Intent_Field_Contract.md",
    "server-design\identity\Identity_HUD_Placeholder_Map.md",
    "server-design\systems-ui\Identity_HUD_Scoreboard_Layout.md",
    "server-design\systems-ui\Build_S5G_Identity_Framework_QA_Cleanup.md",
    "server-design\testing\fragmentengine\Build_S5G_Identity_Framework_QA_Cleanup_Test.md"
)

foreach ($Path in $Docs) {
    Assert-File $Path
    Assert-NoBom $Path
}

$Templates = @(
    "server-design\config-templates\fragments-authoring-template.yml",
    "server-design\config-templates\intents-authoring-template.yml",
    "server-design\config-templates\identity-scoreboard-placeholder-layout.yml",
    "server-design\config-templates\identity-actionbar-placeholder-layout.yml",
    "server-design\config-templates\identity-hologram-placeholder-layout.yml"
)

foreach ($Path in $Templates) {
    Assert-File $Path
    Assert-NoBom $Path
}

Assert-Contains "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\FragmentService.java" "FragmentSummary" "FragmentSummary"
Assert-Contains "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\FragmentService.java" "erasurePressure" "Fragment erasure pressure"
Assert-Contains "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\IntentService.java" "IntentSummary" "IntentSummary"
Assert-Contains "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\IntentService.java" "stabilityImpact" "Intent stability impact"
Assert-Contains "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\gui\CharacterCardGui.java" "Fragment Layer|Fragments" "Character card Fragment panel"
Assert-Contains "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\gui\CharacterCardGui.java" "Intent Layer|Intent Slots" "Character card Intent panel"
Assert-Contains "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\papi\AerethPlaceholderExpansion.java" "intent_primary|intent_primary_display" "Intent placeholder bridge"
Assert-Contains "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\papi\AerethPlaceholderExpansion.java" "fragment_equipped|fragment_equipped_display" "Fragment placeholder bridge"

Write-Host "[S5G Contract] PASS"
