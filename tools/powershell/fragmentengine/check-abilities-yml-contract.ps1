param(
    [string]$AbilitiesPath = "FragmentEngine_Build1_Source\src\main\resources\abilities.yml"
)

Write-Host "[Ability Contract] Checking $AbilitiesPath"

if (!(Test-Path $AbilitiesPath)) {
    throw "abilities.yml not found at: $AbilitiesPath"
}

$Text = Get-Content -Raw -Path $AbilitiesPath

$RequiredFields = @(
    "display-name",
    "discipline",
    "unlock-rank",
    "cost-type",
    "cost-amount",
    "cooldown-seconds",
    "description"
)

$AbilityIds = [regex]::Matches($Text, "(?m)^  ([A-Za-z0-9_\-]+):\s*$") | ForEach-Object {
    $_.Groups[1].Value
}

if ($AbilityIds.Count -eq 0) {
    throw "No ability IDs found. Expected YAML entries under abilities:"
}

Write-Host "[Ability Contract] Ability IDs found: $($AbilityIds.Count)"

$Errors = New-Object System.Collections.Generic.List[string]
$Warnings = New-Object System.Collections.Generic.List[string]

foreach ($Id in $AbilityIds) {
    $StartPattern = "(?m)^  $([regex]::Escape($Id)):\s*$"
    $StartMatch = [regex]::Match($Text, $StartPattern)

    if (!$StartMatch.Success) {
        continue
    }

    $Start = $StartMatch.Index
    $NextMatch = [regex]::Match($Text.Substring($Start + $StartMatch.Length), "(?m)^  [A-Za-z0-9_\-]+:\s*$")

    if ($NextMatch.Success) {
        $Block = $Text.Substring($Start, $StartMatch.Length + $NextMatch.Index)
    } else {
        $Block = $Text.Substring($Start)
    }

    foreach ($Field in $RequiredFields) {
        if ($Block -notmatch "(?m)^    $([regex]::Escape($Field)):\s*.+$") {
            $Errors.Add("$Id missing required field: $Field")
        }
    }

    if ($Block -notmatch "(?m)^    target-mode:\s*.+$") {
        $Warnings.Add("$Id missing forward-compatible field: target-mode")
    }

    if ($Block -notmatch "(?m)^    effect-route:\s*.+$") {
        $Warnings.Add("$Id missing forward-compatible field: effect-route")
    }

    if ($Block -notmatch "(?m)^    design-status:\s*.+$") {
        $Warnings.Add("$Id missing forward-compatible field: design-status")
    }
}

if ($Errors.Count -gt 0) {
    Write-Host ""
    Write-Host "[FAIL] Required field errors:"
    foreach ($ErrorItem in $Errors) {
        Write-Host " - $ErrorItem"
    }
    exit 1
}

Write-Host "[PASS] Required ability fields exist."

if ($Warnings.Count -gt 0) {
    Write-Host ""
    Write-Host "[WARN] Forward-compatible fields missing:"
    foreach ($WarningItem in $Warnings) {
        Write-Host " - $WarningItem"
    }
} else {
    Write-Host "[PASS] Forward-compatible fields exist."
}

Write-Host ""
Write-Host "[Ability Contract] Done."
