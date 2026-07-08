$ErrorActionPreference = "Stop"

Write-Host "[Build S3K] Applying Ability Scaling + Discipline Identity"

$Root = Get-Location
$SourceRoot = Join-Path $Root "implementation-files\FragmentEngine_Build1_Source"
$TargetRoot = Join-Path $Root "FragmentEngine_Build1_Source"

if (!(Test-Path $TargetRoot)) {
    throw "FragmentEngine_Build1_Source not found. Run this from C:\Users\Bernardo\Desktop\Aereth global"
}

$Files = @(
    "src\main\java\live\aereth\fragmentengine\service\AbilityScalingService.java",
    "src\main\java\live\aereth\fragmentengine\service\AbilityResourceService.java",
    "src\main\java\live\aereth\fragmentengine\service\AbilityActivationService.java",
    "src\main\java\live\aereth\fragmentengine\service\AbilityEffectService.java",
    "src\main\java\live\aereth\fragmentengine\service\AbilityHotbarService.java",
    "src\main\java\live\aereth\fragmentengine\gui\AbilityActivationGui.java"
)

foreach ($Relative in $Files) {
    $From = Join-Path $SourceRoot $Relative
    $To = Join-Path $TargetRoot $Relative
    if (!(Test-Path $From)) {
        throw "Missing implementation file: $From"
    }
    New-Item -ItemType Directory -Force -Path (Split-Path $To -Parent) | Out-Null
    Copy-Item $From $To -Force
    Write-Host "[Build S3K] Copied $Relative"
}

$Utf8NoBom = New-Object System.Text.UTF8Encoding($false)
Get-ChildItem (Join-Path $TargetRoot "src\main\java") -Recurse -Filter "*.java" | ForEach-Object {
    $Text = [System.IO.File]::ReadAllText($_.FullName)
    if ($Text.Length -gt 0 -and $Text[0] -eq [char]0xFEFF) {
        $Text = $Text.Substring(1)
    }
    [System.IO.File]::WriteAllText($_.FullName, $Text, $Utf8NoBom)
}

Write-Host "[Build S3K] Build S3K local patch applied. Compile with: mvn clean package"
