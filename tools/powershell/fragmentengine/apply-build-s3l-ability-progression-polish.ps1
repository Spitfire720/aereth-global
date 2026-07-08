$ErrorActionPreference = "Stop"

Write-Host "[Build S3L] Applying ability progression polish" -ForegroundColor Cyan

$Root = Get-Location
$SourceRoot = Join-Path $Root "implementation-files"
$ProjectRoot = Join-Path $Root "FragmentEngine_Build1_Source"

if (!(Test-Path $ProjectRoot)) {
    throw "FragmentEngine_Build1_Source not found. Run this from C:\Users\Bernardo\Desktop\Aereth global after extracting the pack."
}

$Files = @(
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\AbilityProgressionPolishService.java",
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\gui\AbilityCodexGui.java",
    "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\gui\AbilityActivationGui.java"
)

foreach ($Relative in $Files) {
    $Source = Join-Path $SourceRoot $Relative
    $Target = Join-Path $Root $Relative

    if (!(Test-Path $Source)) {
        throw "Missing implementation file: $Source"
    }

    New-Item -ItemType Directory -Force -Path (Split-Path $Target -Parent) | Out-Null
    Copy-Item $Source $Target -Force
    Write-Host "[Build S3L] Copied $Relative" -ForegroundColor DarkGray
}

$Utf8NoBom = New-Object System.Text.UTF8Encoding($false)
Get-ChildItem (Join-Path $ProjectRoot "src\main\java") -Filter "*.java" -Recurse | ForEach-Object {
    $Text = [System.IO.File]::ReadAllText($_.FullName)
    if ($Text.Length -gt 0 -and $Text[0] -eq [char]0xFEFF) {
        $Text = $Text.Substring(1)
    }
    [System.IO.File]::WriteAllText($_.FullName, $Text, $Utf8NoBom)
}

Write-Host "[Build S3L] Build S3L local patch applied." -ForegroundColor Green
Write-Host "[Build S3L] Compile with: mvn clean package" -ForegroundColor Yellow
