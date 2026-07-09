$ErrorActionPreference = "Stop"

Write-Host "[S5G QA] Identity Framework QA started"

$Root = (Get-Location).Path

& ".\tools\powershell\fragmentengine\check-identity-framework-contract.ps1" -Root $Root

$SourceRoot = Join-Path $Root "FragmentEngine_Build1_Source"
if (!(Test-Path $SourceRoot)) {
    throw "FragmentEngine_Build1_Source not found."
}

Write-Host "[S5G QA] Running Maven build"
Push-Location $SourceRoot
try {
    mvn clean package
    if ($LASTEXITCODE -ne 0) {
        throw "Maven build failed."
    }
} finally {
    Pop-Location
}

$Jar = Join-Path $SourceRoot "target\FragmentEngine-1.15.0.jar"
if (!(Test-Path $Jar)) {
    throw "Expected jar not found after Maven build: $Jar"
}

Write-Host "[S5G QA] PASS"
