$ErrorActionPreference = "Stop"

Write-Host "[Build S4C] Applying Ability Slot Framework Hardening"

$Root = (Get-Location).Path
$PackRoot = Join-Path $Root "implementation-files"
$SourceRoot = Join-Path $Root "FragmentEngine_Build1_Source"

if (!(Test-Path $SourceRoot)) {
    throw "FragmentEngine_Build1_Source not found. Run this from C:\Users\Bernardo\Desktop\Aereth global"
}

function Copy-ImplFile {
    param(
        [Parameter(Mandatory=$true)][string]$RelativePath
    )

    $From = Join-Path $PackRoot (Join-Path "FragmentEngine_Build1_Source" $RelativePath)
    $To = Join-Path $SourceRoot $RelativePath

    if (!(Test-Path $From)) {
        throw "Missing implementation file: $From"
    }

    $Dir = Split-Path $To -Parent
    New-Item -ItemType Directory -Force -Path $Dir | Out-Null

    $Text = [System.IO.File]::ReadAllText($From)
    $Utf8NoBom = New-Object System.Text.UTF8Encoding($false)
    [System.IO.File]::WriteAllText($To, $Text, $Utf8NoBom)

    Write-Host "[Build S4C] Copied $RelativePath"
}

Copy-ImplFile "src\main\java\live\aereth\fragmentengine\service\AbilitySlotFrameworkService.java"
Copy-ImplFile "src\main\java\live\aereth\fragmentengine\service\AbilityActivationService.java"
Copy-ImplFile "src\main\java\live\aereth\fragmentengine\gui\AbilityLoadoutGui.java"

Write-Host "[Build S4C] Local files copied. Now run mvn clean package. No deployment until BUILD SUCCESS."
