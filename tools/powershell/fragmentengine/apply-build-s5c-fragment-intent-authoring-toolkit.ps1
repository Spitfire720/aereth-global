$ErrorActionPreference = "Stop"

Write-Host "[Build S5C] Applying Fragment + Intent Definition Authoring Toolkit"

$Root = (Get-Location).Path

$Expected = @(
    "server-design\identity\Fragment_Definition_Authoring_Guide.md",
    "server-design\identity\Intent_Definition_Authoring_Guide.md",
    "server-design\identity\Fragment_Intent_Field_Contract.md",
    "server-design\config-templates\fragments-authoring-template.yml",
    "server-design\config-templates\intents-authoring-template.yml",
    "server-design\systems-ui\Build_S5C_Fragment_Intent_Definition_Authoring_Toolkit.md",
    "server-design\testing\fragmentengine\Build_S5C_Fragment_Intent_Definition_Authoring_Toolkit_Test.md",
    "tools\powershell\fragmentengine\check-fragment-intent-yml-contract.ps1",
    "tools\powershell\fragmentengine\run-s5c-fragment-intent-authoring-qa.ps1"
)

foreach ($File in $Expected) {
    if (!(Test-Path $File)) {
        throw "Missing expected S5C file: $File. Extract the pack into the repo root, not a random nesting swamp."
    }
    Write-Host "[Build S5C] Found $File"
}

Write-Host "[Build S5C] Applied. Run .\tools\powershell\fragmentengine\run-s5c-fragment-intent-authoring-qa.ps1 next."
