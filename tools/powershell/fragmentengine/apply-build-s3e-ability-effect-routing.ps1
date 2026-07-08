param()

$ErrorActionPreference = "Stop"

Write-Host "[Build S3E] Applying Ability Effect Routing patch"

$RepoRoot = (Get-Location).Path
$SourceRoot = Join-Path $RepoRoot "FragmentEngine_Build1_Source"
$ImplementationRoot = Join-Path $RepoRoot "implementation-files\FragmentEngine_Build1_Source"
$Utf8NoBom = New-Object System.Text.UTF8Encoding($false)

function Copy-ImplementationFile {
    param(
        [Parameter(Mandatory=$true)][string]$RelativePath
    )

    $From = Join-Path $ImplementationRoot $RelativePath
    $To = Join-Path $SourceRoot $RelativePath

    if (!(Test-Path $From)) {
        throw "Implementation file missing: $From"
    }

    $ToDir = Split-Path $To -Parent
    New-Item -ItemType Directory -Force -Path $ToDir | Out-Null
    Copy-Item $From $To -Force
    Write-Host "[Build S3E] Copied $RelativePath"
}

$RequiredS3DFile = Join-Path $SourceRoot "src\main\java\live\aereth\fragmentengine\gui\AbilityActivationGui.java"
if (!(Test-Path $RequiredS3DFile)) {
    throw "S3D AbilityActivationGui.java is missing. Apply S3D before S3E."
}

Copy-ImplementationFile "src\main\java\live\aereth\fragmentengine\service\AbilityEffectService.java"
Copy-ImplementationFile "src\main\java\live\aereth\fragmentengine\service\AbilityActivationService.java"
Copy-ImplementationFile "src\main\java\live\aereth\fragmentengine\gui\AbilityActivationGui.java"

Get-ChildItem "$SourceRoot\src\main\java" -Recurse -Filter "*.java" | ForEach-Object {
    $Text = [System.IO.File]::ReadAllText($_.FullName)
    if ($Text.Length -gt 0 -and $Text[0] -eq [char]0xFEFF) {
        $Text = $Text.Substring(1)
    }
    [System.IO.File]::WriteAllText($_.FullName, $Text, $Utf8NoBom)
}

Write-Host "[Build S3E] Patch applied. Run: cd FragmentEngine_Build1_Source; mvn clean package"
