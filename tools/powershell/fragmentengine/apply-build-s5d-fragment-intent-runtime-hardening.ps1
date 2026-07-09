$ErrorActionPreference = "Stop"

Write-Host "[Build S5D] Applying Fragment + Intent Runtime Hardening"

$Root = (Get-Location).Path
$Sources = @(
    @{
        Source = "implementation-files\FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\IntentService.java"
        Target = "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\IntentService.java"
    },
    @{
        Source = "implementation-files\FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\FragmentService.java"
        Target = "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\FragmentService.java"
    }
)

foreach ($Pair in $Sources) {
    $Source = Join-Path $Root $Pair.Source
    $Target = Join-Path $Root $Pair.Target

    if (!(Test-Path $Source)) {
        throw "Missing implementation file: $Source"
    }

    New-Item -ItemType Directory -Force -Path (Split-Path $Target -Parent) | Out-Null
    Copy-Item -Force $Source $Target

    $Bytes = [System.IO.File]::ReadAllBytes($Target)
    if ($Bytes.Length -ge 3 -and $Bytes[0] -eq 0xEF -and $Bytes[1] -eq 0xBB -and $Bytes[2] -eq 0xBF) {
        $Bytes = $Bytes[3..($Bytes.Length - 1)]
        [System.IO.File]::WriteAllBytes($Target, $Bytes)
        Write-Host "[Build S5D] Removed BOM from $($Pair.Target)"
    }

    Write-Host "[Build S5D] Copied $($Pair.Target)"
}

Write-Host "[Build S5D] No command patching. No plugin.yml changes. Now run mvn clean package."
