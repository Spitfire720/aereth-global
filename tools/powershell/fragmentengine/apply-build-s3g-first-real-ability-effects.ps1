$ErrorActionPreference = "Stop"

Write-Host "[Build S3G] Applying First Real Ability Effects"

$Root = (Get-Location).Path
$SourceRoot = Join-Path $Root "FragmentEngine_Build1_Source"
$ImplRoot = Join-Path $Root "implementation-files"
$Utf8NoBom = New-Object System.Text.UTF8Encoding($false)

if (!(Test-Path $SourceRoot)) {
    throw "FragmentEngine_Build1_Source not found. Run this script from C:\Users\Bernardo\Desktop\Aereth global."
}

$RequiredExisting = @(
    "src\main\java\live\aereth\fragmentengine\service\AbilityActivationService.java",
    "src\main\java\live\aereth\fragmentengine\service\AbilityEffectService.java",
    "src\main\java\live\aereth\fragmentengine\service\AbilityTargetingService.java",
    "src\main\java\live\aereth\fragmentengine\gui\AbilityActivationGui.java"
)

foreach ($Relative in $RequiredExisting) {
    $Target = Join-Path $SourceRoot $Relative
    if (!(Test-Path $Target)) {
        throw "Required S3F file missing: $Relative. Apply/pass S3F before S3G."
    }
}

$FilesToCopy = @(
    "src\main\java\live\aereth\fragmentengine\service\AbilityEffectService.java",
    "src\main\java\live\aereth\fragmentengine\gui\AbilityActivationGui.java"
)

foreach ($Relative in $FilesToCopy) {
    $Source = Join-Path $ImplRoot (Join-Path "FragmentEngine_Build1_Source" $Relative)
    $Target = Join-Path $SourceRoot $Relative

    if (!(Test-Path $Source)) {
        throw "Implementation file missing: $Source"
    }

    New-Item -ItemType Directory -Force -Path (Split-Path $Target -Parent) | Out-Null
    Copy-Item $Source $Target -Force
    Write-Host "[Build S3G] Copied $Relative"
}

# Strip BOM from Java files, because invisible bytes are still apparently a gameplay mechanic.
Get-ChildItem (Join-Path $SourceRoot "src\main\java") -Recurse -Filter "*.java" | ForEach-Object {
    $Path = $_.FullName
    $Text = [System.IO.File]::ReadAllText($Path)
    if ($Text.Length -gt 0 -and $Text[0] -eq [char]0xFEFF) {
        $Text = $Text.Substring(1)
    }
    [System.IO.File]::WriteAllText($Path, $Text, $Utf8NoBom)
}

Write-Host "[Build S3G] Patch applied. Run: cd FragmentEngine_Build1_Source; mvn clean package"
