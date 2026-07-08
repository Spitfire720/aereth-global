$ErrorActionPreference = "Stop"

Write-Host "[S4E QA] Ability Framework QA started"

$Root = (Get-Location).Path
$SourceRoot = "FragmentEngine_Build1_Source"
if (!(Test-Path $SourceRoot)) {
    throw "FragmentEngine_Build1_Source not found. Run this from C:\Users\Bernardo\Desktop\Aereth global"
}

$RequiredFiles = @(
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\command\AerethCommand.java",
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\gui\AbilityCodexGui.java",
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\gui\AbilityLoadoutGui.java",
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\gui\AbilityActivationGui.java",
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\listener\AbilityHotbarListener.java",
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\AbilityService.java",
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\AbilityActivationService.java",
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\AbilitySlotFrameworkService.java",
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\AbilityResourceService.java",
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\AbilityScalingService.java",
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\AbilityTargetingService.java",
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\AbilityCombatService.java",
    "FragmentEngine_Build1_Source\src\main\resources\abilities.yml"
)

foreach ($File in $RequiredFiles) {
    if (!(Test-Path $File)) {
        throw "Required ability framework file missing: $File"
    }
    Write-Host "[S4E QA] OK file: $File"
}

function Test-NoUtf8Bom {
    param([string]$Path)
    $Bytes = [System.IO.File]::ReadAllBytes((Resolve-Path $Path))
    if ($Bytes.Length -ge 3 -and $Bytes[0] -eq 0xEF -and $Bytes[1] -eq 0xBB -and $Bytes[2] -eq 0xBF) {
        throw "UTF-8 BOM found in $Path"
    }
}

Write-Host "[S4E QA] Checking for UTF-8 BOM in key source/scripts"
$BomCheckFiles = @(
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\command\AerethCommand.java",
    "tools\powershell\fragmentengine\check-abilities-yml-contract.ps1",
    "tools\powershell\fragmentengine\run-s4e-ability-framework-qa.ps1"
)
foreach ($File in $BomCheckFiles) {
    if (Test-Path $File) {
        Test-NoUtf8Bom $File
        Write-Host "[S4E QA] No BOM: $File"
    }
}

$CommandFile = "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\command\AerethCommand.java"
$CommandText = Get-Content -Raw -Path $CommandFile
$ExpectedCommands = @(
    "abilitygui",
    "abilityloadout",
    "abilityactivation",
    "abilityactivate",
    "abilitycooldowns",
    "abilitysummary",
    "abilityresources",
    "abilitysync"
)

foreach ($Command in $ExpectedCommands) {
    if ($CommandText -notmatch [regex]::Escape($Command)) {
        throw "Expected ability command not found in AerethCommand.java: $Command"
    }
    Write-Host "[S4E QA] Command string found: /aereth $Command"
}

if (Test-Path "tools\powershell\fragmentengine\check-abilities-yml-contract.ps1") {
    Write-Host "[S4E QA] Running abilities.yml contract checker"
    & "tools\powershell\fragmentengine\check-abilities-yml-contract.ps1"
    if ($LASTEXITCODE -ne 0) {
        throw "Ability contract checker failed with exit code $LASTEXITCODE"
    }
}

Write-Host "[S4E QA] Running Maven clean package"
Push-Location "FragmentEngine_Build1_Source"
try {
    mvn clean package
    if ($LASTEXITCODE -ne 0) {
        throw "Maven failed with exit code $LASTEXITCODE"
    }
} finally {
    Pop-Location
}

Write-Host "[S4E QA] PASS - Ability framework compiles and core command hooks exist."
