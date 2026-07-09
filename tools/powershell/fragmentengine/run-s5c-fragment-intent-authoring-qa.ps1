$ErrorActionPreference = "Stop"

Write-Host "[S5C QA] Fragment + Intent authoring QA started"

$Root = (Get-Location).Path

$Files = @(
    "server-design\identity\Fragment_Definition_Authoring_Guide.md",
    "server-design\identity\Intent_Definition_Authoring_Guide.md",
    "server-design\identity\Fragment_Intent_Field_Contract.md",
    "server-design\config-templates\fragments-authoring-template.yml",
    "server-design\config-templates\intents-authoring-template.yml",
    "server-design\systems-ui\Build_S5C_Fragment_Intent_Definition_Authoring_Toolkit.md",
    "server-design\testing\fragmentengine\Build_S5C_Fragment_Intent_Definition_Authoring_Toolkit_Test.md",
    "tools\powershell\fragmentengine\check-fragment-intent-yml-contract.ps1",
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\FragmentService.java",
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\IntentService.java",
    "FragmentEngine_Build1_Source\src\main\resources\fragments.yml",
    "FragmentEngine_Build1_Source\src\main\resources\intents.yml"
)

foreach ($File in $Files) {
    $Full = Join-Path $Root $File
    if (!(Test-Path $Full)) {
        throw "Missing QA file: $Full"
    }
    Write-Host "[S5C QA] OK file: $File"
}

Write-Host "[S5C QA] Checking UTF-8 BOM in S5C scripts/docs"
$BomCheck = @(
    "tools\powershell\fragmentengine\check-fragment-intent-yml-contract.ps1",
    "tools\powershell\fragmentengine\run-s5c-fragment-intent-authoring-qa.ps1",
    "tools\powershell\fragmentengine\apply-build-s5c-fragment-intent-authoring-toolkit.ps1"
)

foreach ($File in $BomCheck) {
    $Full = Join-Path $Root $File
    $Bytes = [System.IO.File]::ReadAllBytes($Full)
    if ($Bytes.Length -ge 3 -and $Bytes[0] -eq 0xEF -and $Bytes[1] -eq 0xBB -and $Bytes[2] -eq 0xBF) {
        throw "UTF-8 BOM found in $File"
    }
    Write-Host "[S5C QA] No BOM: $File"
}

$Markers = @(
    "Fragments are identity-layer definitions",
    "Intents define character direction",
    "Fragments and Intents are identity data",
    "design-status",
    "outcome-bias",
    "compatible-outcomes"
)

$DocText = ""
$DocText += Get-Content -Raw "server-design\identity\Fragment_Definition_Authoring_Guide.md"
$DocText += Get-Content -Raw "server-design\identity\Intent_Definition_Authoring_Guide.md"
$DocText += Get-Content -Raw "server-design\identity\Fragment_Intent_Field_Contract.md"

foreach ($Marker in $Markers) {
    if ($DocText -notlike "*$Marker*") {
        throw "S5C marker missing: $Marker"
    }
    Write-Host "[S5C QA] Marker found: $Marker"
}

Write-Host "[S5C QA] Running Fragment + Intent contract checker"
.\tools\powershell\fragmentengine\check-fragment-intent-yml-contract.ps1

Write-Host "[S5C QA] Running Maven build"
Push-Location "FragmentEngine_Build1_Source"
try {
    mvn clean package
    if ($LASTEXITCODE -ne 0) {
        throw "Maven build failed."
    }
} finally {
    Pop-Location
}

Write-Host "[S5C QA] PASS"
