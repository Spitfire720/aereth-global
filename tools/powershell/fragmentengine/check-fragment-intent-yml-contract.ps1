param(
    [string]$FragmentsPath = "FragmentEngine_Build1_Source\src\main\resources\fragments.yml",
    [string]$IntentsPath = "FragmentEngine_Build1_Source\src\main\resources\intents.yml"
)

$ErrorActionPreference = "Stop"

function Get-TwoSpaceIds {
    param([string]$Text)

    $Ids = New-Object System.Collections.Generic.List[string]
    $Matches = [regex]::Matches($Text, '(?m)^  ([A-Za-z0-9_-]+):\s*$')
    foreach ($Match in $Matches) {
        $Ids.Add($Match.Groups[1].Value)
    }
    return $Ids
}

function Get-Block {
    param(
        [string]$Text,
        [string]$Id
    )

    $StartPattern = '(?m)^  ' + [regex]::Escape($Id) + ':\s*$'
    $StartMatch = [regex]::Match($Text, $StartPattern)
    if (!$StartMatch.Success) {
        return ""
    }

    $Start = $StartMatch.Index
    $AfterStart = $Start + $StartMatch.Length
    $Remaining = $Text.Substring($AfterStart)
    $NextMatch = [regex]::Match($Remaining, '(?m)^  [A-Za-z0-9_-]+:\s*$')

    if ($NextMatch.Success) {
        return $Text.Substring($Start, $StartMatch.Length + $NextMatch.Index)
    }

    return $Text.Substring($Start)
}

function Test-RequiredFields {
    param(
        [string]$Kind,
        [string]$Text,
        [string[]]$RequiredFields
    )

    $Ids = Get-TwoSpaceIds -Text $Text
    if ($Ids.Count -eq 0) {
        throw "No $Kind IDs found. Expected entries under top-level section."
    }

    Write-Host "[Contract] $Kind IDs found: $($Ids.Count)"

    $Errors = New-Object System.Collections.Generic.List[string]

    foreach ($Id in $Ids) {
        $Block = Get-Block -Text $Text -Id $Id
        foreach ($Field in $RequiredFields) {
            $Pattern = '(?m)^    ' + [regex]::Escape($Field) + ':\s*.+'
            if ($Block -notmatch $Pattern) {
                $Errors.Add("$Kind '$Id' missing required field: $Field")
            }
        }
    }

    if ($Errors.Count -gt 0) {
        Write-Host ""
        Write-Host "[FAIL] $Kind required field errors:"
        foreach ($ErrorItem in $Errors) {
            Write-Host " - $ErrorItem"
        }
        exit 1
    }

    Write-Host "[PASS] $Kind required fields exist."
}

Write-Host "[Contract] Checking Fragment + Intent YAML contracts"

if (!(Test-Path $FragmentsPath)) {
    throw "fragments.yml not found at: $FragmentsPath"
}
if (!(Test-Path $IntentsPath)) {
    throw "intents.yml not found at: $IntentsPath"
}

$FragmentsText = Get-Content -Raw -Path $FragmentsPath
$IntentsText = Get-Content -Raw -Path $IntentsPath

if ($FragmentsText -notmatch '(?m)^fragments:\s*$') {
    throw "fragments.yml missing top-level 'fragments:' section."
}
if ($IntentsText -notmatch '(?m)^intents:\s*$') {
    throw "intents.yml missing top-level 'intents:' section."
}

Test-RequiredFields -Kind "Fragment" -Text $FragmentsText -RequiredFields @("display", "lore", "pressure", "stability-cost")
Test-RequiredFields -Kind "Intent" -Text $IntentsText -RequiredFields @("display", "family", "pressure", "stability-impact", "description")

Write-Host ""
Write-Host "[Contract] Optional design-only fields are not required yet."
Write-Host "[Contract] Done."
