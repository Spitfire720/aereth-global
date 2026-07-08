$ErrorActionPreference = "Stop"

$Root = (Get-Location).Path
$AbilitiesPath = Join-Path $Root "FragmentEngine_Build1_Source/src/main/resources/abilities.yml"

if (!(Test-Path $AbilitiesPath)) {
    throw "abilities.yml not found at $AbilitiesPath. Run this from repo root: C:\Users\Bernardo\Desktop\Aereth global"
}

$Text = Get-Content -Raw -Path $AbilitiesPath
$Lines = Get-Content -Path $AbilitiesPath

$RequiredFields = @(
    "display-name",
    "discipline",
    "unlock-rank",
    "cost-type",
    "cost-amount",
    "cooldown-seconds",
    "description"
)

$ForwardFields = @(
    "target-mode",
    "effect-route"
)

$KnownCostTypes = @("none", "stamina", "mana", "focus", "instability", "health", "hp", "energy", "arcane", "fragment", "pressure")
$KnownTargetModes = @("self", "aimed_entity", "aimed_location", "forward_line", "area")
$KnownRoutes = @("defensive_pulse", "blood_pressure", "arcane_spark", "focus_thread", "unstable_bloom", "stamina_surge", "resonance_ping", "placeholder", "manual_design_required")

$AbilityIds = New-Object System.Collections.Generic.List[string]
$Warnings = New-Object System.Collections.Generic.List[string]
$Errors = New-Object System.Collections.Generic.List[string]

# Basic ability-id detection under top-level abilities section: two-space indent keys.
foreach ($Line in $Lines) {
    if ($Line -match '^  ([a-z0-9_]+):\s*$') {
        $Id = $Matches[1]
        if ($Id -ne "settings") {
            $AbilityIds.Add($Id)
        }
    }
}

if ($AbilityIds.Count -eq 0) {
    $Errors.Add("No ability ids found under abilities: section.")
}

$DuplicateIds = $AbilityIds | Group-Object | Where-Object { $_.Count -gt 1 }
foreach ($Dup in $DuplicateIds) {
    $Errors.Add("Duplicate ability id found: $($Dup.Name)")
}

foreach ($Id in $AbilityIds) {
    $Pattern = "(?ms)^  $([regex]::Escape($Id)):\s*`r?`n(?<block>(?:    .+`r?`n?)*)"
    $Match = [regex]::Match($Text, $Pattern)
    if (!$Match.Success) {
        $Errors.Add("Could not read block for ability: $Id")
        continue
    }

    $Block = $Match.Groups["block"].Value

    foreach ($Field in $RequiredFields) {
        if ($Block -notmatch "(?m)^    $([regex]::Escape($Field)):\s*.+") {
            $Errors.Add("$Id missing required field: $Field")
        }
    }

    foreach ($Field in $ForwardFields) {
        if ($Block -notmatch "(?m)^    $([regex]::Escape($Field)):\s*.+") {
            $Warnings.Add("$Id missing forward-compatible field: $Field")
        }
    }

    if ($Block -match "(?m)^    cost-type:\s*['\"]?(?<value>[A-Za-z0-9_ -]+)['\"]?\s*$") {
        $Value = $Matches.value.ToLower().Trim().Replace(" ", "_").Replace("-", "_")
        if ($KnownCostTypes -notcontains $Value) {
            $Warnings.Add("$Id uses non-standard cost-type: $Value")
        }
    }

    if ($Block -match "(?m)^    target-mode:\s*['\"]?(?<value>[A-Za-z0-9_ -]+)['\"]?\s*$") {
        $Value = $Matches.value.ToLower().Trim().Replace(" ", "_").Replace("-", "_")
        if ($KnownTargetModes -notcontains $Value) {
            $Warnings.Add("$Id uses non-standard target-mode: $Value")
        }
    }

    if ($Block -match "(?m)^    effect-route:\s*['\"]?(?<value>[A-Za-z0-9_ -]+)['\"]?\s*$") {
        $Value = $Matches.value.ToLower().Trim().Replace(" ", "_").Replace("-", "_")
        if ($KnownRoutes -notcontains $Value) {
            $Warnings.Add("$Id uses non-standard effect-route: $Value")
        }
    }
}

Write-Host "[Ability Contract Check] abilities.yml: $AbilitiesPath"
Write-Host "[Ability Contract Check] Ability ids found: $($AbilityIds.Count)"

if ($Warnings.Count -gt 0) {
    Write-Host ""
    Write-Host "Warnings:" -ForegroundColor Yellow
    foreach ($Warning in $Warnings) {
        Write-Host " - $Warning" -ForegroundColor Yellow
    }
}

if ($Errors.Count -gt 0) {
    Write-Host ""
    Write-Host "Errors:" -ForegroundColor Red
    foreach ($Err in $Errors) {
        Write-Host " - $Err" -ForegroundColor Red
    }
    throw "Ability contract check failed with $($Errors.Count) error(s)."
}

Write-Host ""
Write-Host "[Ability Contract Check] PASS. No required-field errors found." -ForegroundColor Green
