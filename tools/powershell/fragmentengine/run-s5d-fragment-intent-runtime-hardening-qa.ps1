$ErrorActionPreference = "Stop"

Write-Host "[S5D QA] Fragment + Intent Runtime Hardening QA started"

$Root = (Get-Location).Path
$Intent = Join-Path $Root "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\IntentService.java"
$Fragment = Join-Path $Root "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\FragmentService.java"

$Files = @($Intent, $Fragment)
foreach ($File in $Files) {
    if (!(Test-Path $File)) {
        throw "Missing file: $File"
    }

    Write-Host "[S5D QA] OK file: $File"

    $Bytes = [System.IO.File]::ReadAllBytes($File)
    if ($Bytes.Length -ge 3 -and $Bytes[0] -eq 0xEF -and $Bytes[1] -eq 0xBB -and $Bytes[2] -eq 0xBF) {
        throw "UTF-8 BOM found in $File"
    }

    Write-Host "[S5D QA] No BOM: $File"
}

$IntentText = Get-Content -Raw $Intent
$FragmentText = Get-Content -Raw $Fragment

$Markers = @(
    @{ Name = "Intent runtime schema"; Text = $IntentText; Marker = "S5D-intent-runtime-hardening" },
    @{ Name = "Intent sanitizer"; Text = $IntentText; Marker = "sanitizeIntentState" },
    @{ Name = "Intent repair record"; Text = $IntentText; Marker = "IntentRepairResult" },
    @{ Name = "Duplicate intent cleanup"; Text = $IntentText; Marker = "duplicate-count" },
    @{ Name = "Fragment runtime schema"; Text = $FragmentText; Marker = "S5D-fragment-runtime-hardening" },
    @{ Name = "Fragment sanitizer"; Text = $FragmentText; Marker = "sanitizeFragmentState" },
    @{ Name = "Fragment repair record"; Text = $FragmentText; Marker = "FragmentRepairResult" },
    @{ Name = "Fragment overflow cleanup"; Text = $FragmentText; Marker = "overflow-count" }
)

foreach ($Entry in $Markers) {
    if ($Entry.Text -notmatch [regex]::Escape($Entry.Marker)) {
        throw "Missing marker [$($Entry.Name)]: $($Entry.Marker)"
    }
    Write-Host "[S5D QA] Marker found: $($Entry.Name)"
}

Write-Host "[S5D QA] Running Maven build"

Push-Location "FragmentEngine_Build1_Source"
try {
    mvn clean package
    if ($LASTEXITCODE -ne 0) {
        throw "Maven build failed."
    }
}
finally {
    Pop-Location
}

Write-Host "[S5D QA] PASS"
