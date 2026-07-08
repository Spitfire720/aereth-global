$ErrorActionPreference = "Stop"

Write-Host "[Build S4D] Applying Ability Definition Authoring Toolkit"

$Root = (Get-Location).Path
$PackRoot = Join-Path $Root "implementation-files"

# S4D pack is intentionally docs/tooling only. Files are copied from the extracted pack root if present,
# otherwise from current directory layout. This supports normal ZIP extraction into repo root.
$Mappings = @(
    @{ Source = "server-design/ability-authoring/Ability_Definition_Authoring_Guide.md"; Target = "server-design/ability-authoring/Ability_Definition_Authoring_Guide.md" },
    @{ Source = "server-design/ability-authoring/Ability_Design_Template.md"; Target = "server-design/ability-authoring/Ability_Design_Template.md" },
    @{ Source = "server-design/ability-authoring/Ability_Balance_Field_Contract.md"; Target = "server-design/ability-authoring/Ability_Balance_Field_Contract.md" },
    @{ Source = "server-design/ability-authoring/Ability_Placeholder_Route_Rules.md"; Target = "server-design/ability-authoring/Ability_Placeholder_Route_Rules.md" },
    @{ Source = "server-design/config-templates/abilities-authoring-template.yml"; Target = "server-design/config-templates/abilities-authoring-template.yml" },
    @{ Source = "server-design/systems-ui/Build_S4D_Ability_Definition_Authoring_Toolkit.md"; Target = "server-design/systems-ui/Build_S4D_Ability_Definition_Authoring_Toolkit.md" },
    @{ Source = "server-design/testing/fragmentengine/Build_S4D_Ability_Definition_Authoring_Toolkit_Test.md"; Target = "server-design/testing/fragmentengine/Build_S4D_Ability_Definition_Authoring_Toolkit_Test.md" },
    @{ Source = "tools/powershell/fragmentengine/check-abilities-yml-contract.ps1"; Target = "tools/powershell/fragmentengine/check-abilities-yml-contract.ps1" }
)

foreach ($Map in $Mappings) {
    $Source = Join-Path $Root $Map.Source
    $Target = Join-Path $Root $Map.Target

    if (!(Test-Path $Source)) {
        throw "Source file missing from extracted pack: $Source"
    }

    New-Item -ItemType Directory -Force -Path (Split-Path $Target -Parent) | Out-Null
    Copy-Item -Force $Source $Target
    Write-Host "[Build S4D] Copied $($Map.Target)"
}

Write-Host "[Build S4D] Applied. No Java/runtime files changed. Run check-abilities-yml-contract.ps1 to validate current abilities.yml."
