$ErrorActionPreference = "Stop"

Write-Host "[Build S4B] Applying Combat Integration Bundle"

$Root = Resolve-Path (Join-Path $PSScriptRoot "..\..\..")
$ImplementationRoot = Join-Path $Root "implementation-files\FragmentEngine_Build1_Source"
$ProjectRoot = Join-Path $Root "FragmentEngine_Build1_Source"

if (!(Test-Path $ProjectRoot)) {
    throw "FragmentEngine_Build1_Source not found at: $ProjectRoot"
}

$Files = @(
    "src\main\java\live\aereth\fragmentengine\service\AbilityCombatService.java",
    "src\main\java\live\aereth\fragmentengine\service\AbilityEffectService.java"
)

foreach ($Relative in $Files) {
    $Source = Join-Path $ImplementationRoot $Relative
    $Target = Join-Path $ProjectRoot $Relative

    if (!(Test-Path $Source)) {
        throw "Missing implementation file: $Source"
    }

    New-Item -ItemType Directory -Force -Path (Split-Path $Target -Parent) | Out-Null
    Copy-Item $Source $Target -Force
    Write-Host "[Build S4B] Copied $Relative"
}

$DesignSource = Join-Path $Root "server-design\systems-ui\Build_S4B_Combat_Integration_Bundle.md"
$DesignTarget = Join-Path $Root "server-design\systems-ui\Build_S4B_Combat_Integration_Bundle.md"
if (Test-Path $DesignSource) {
    New-Item -ItemType Directory -Force -Path (Split-Path $DesignTarget -Parent) | Out-Null
}

Write-Host "[Build S4B] Local patch applied. Now run mvn clean package. No deployment until BUILD SUCCESS."
