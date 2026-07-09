$ErrorActionPreference = "Stop"

Write-Host "[Build S5E] Applying Placeholder Identity Bridge"

$Root = Get-Location
$Project = Join-Path $Root "FragmentEngine_Build1_Source"
if (!(Test-Path $Project)) {
    throw "FragmentEngine_Build1_Source not found. Run this from the Aereth global repo root."
}

$Source = Join-Path $Root "implementation-files\FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\papi\AerethPlaceholderExpansion.java"
$Target = Join-Path $Root "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\papi\AerethPlaceholderExpansion.java"

if (!(Test-Path $Source)) {
    throw "Implementation source missing: $Source"
}

New-Item -ItemType Directory -Force -Path (Split-Path $Target -Parent) | Out-Null
Copy-Item -Force $Source $Target
Write-Host "[Build S5E] Copied AerethPlaceholderExpansion.java"

$Expected = @(
    "server-design\identity\Placeholder_Identity_Bridge.md",
    "server-design\systems-ui\Build_S5E_Placeholder_Identity_Bridge.md",
    "server-design\testing\fragmentengine\Build_S5E_Placeholder_Identity_Bridge_Test.md",
    "tools\powershell\fragmentengine\run-s5e-placeholder-identity-bridge-qa.ps1"
)

foreach ($File in $Expected) {
    if (!(Test-Path (Join-Path $Root $File))) {
        throw "Expected S5E file missing after extraction: $File"
    }
    Write-Host "[Build S5E] Found $File"
}

Write-Host "[Build S5E] No command patching. No plugin.yml changes. Now run mvn clean package."
