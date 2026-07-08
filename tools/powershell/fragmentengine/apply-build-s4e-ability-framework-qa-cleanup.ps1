$ErrorActionPreference = "Stop"

Write-Host "[Build S4E] Applying Ability Framework QA + Cleanup"

$Root = (Get-Location).Path
$ExpectedRootName = "Aereth global"
if ((Split-Path $Root -Leaf) -ne $ExpectedRootName) {
    Write-Host "[WARN] Current folder is not named '$ExpectedRootName': $Root"
    Write-Host "[WARN] Continue only if this is your project root. Humans do love making folders into riddles."
}

$Docs = @(
    "server-design\systems-ui\Build_S4E_Ability_Framework_QA_Cleanup.md",
    "server-design\testing\fragmentengine\Build_S4E_Ability_Framework_QA_Cleanup_Test.md",
    "tools\powershell\fragmentengine\run-s4e-ability-framework-qa.ps1"
)

foreach ($Doc in $Docs) {
    if (!(Test-Path $Doc)) {
        throw "Missing expected S4E file after extraction: $Doc"
    }
    Write-Host "[Build S4E] Found $Doc"
}

Write-Host "[Build S4E] Removing generated root README pack files"
Get-ChildItem -Path . -File -Filter "README_AERETH_FRAGMENTENGINE_BUILD_*.md" -ErrorAction SilentlyContinue | ForEach-Object {
    Write-Host "[Build S4E] Removing $($_.Name)"
    Remove-Item -Force $_.FullName
}

if (Test-Path "implementation-files") {
    Write-Host "[Build S4E] Removing implementation-files staging folder"
    Remove-Item "implementation-files" -Recurse -Force
}

$InfoExclude = ".git\info\exclude"
if (Test-Path ".git") {
    if (!(Test-Path $InfoExclude)) {
        New-Item -ItemType File -Force -Path $InfoExclude | Out-Null
    }

    $ExcludeEntries = @(
        "README_AERETH_FRAGMENTENGINE_BUILD_*.md",
        "implementation-files/",
        "server-deployment/backups/"
    )

    $Existing = Get-Content -Path $InfoExclude -ErrorAction SilentlyContinue
    foreach ($Entry in $ExcludeEntries) {
        if ($Existing -notcontains $Entry) {
            Add-Content -Path $InfoExclude -Value $Entry
            Write-Host "[Build S4E] Added local git exclude: $Entry"
        }
    }
}

Write-Host "[Build S4E] Cleanup applied. Run .\tools\powershell\fragmentengine\run-s4e-ability-framework-qa.ps1 next."
