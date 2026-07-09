$ErrorActionPreference = "Stop"

Write-Host "[Build S5G] Applying Identity Framework QA + Cleanup"

$Root = (Get-Location).Path

$Required = @(
    "tools\powershell\fragmentengine\check-identity-framework-contract.ps1",
    "tools\powershell\fragmentengine\run-s5g-identity-framework-qa.ps1",
    "server-design\identity\Identity_Framework_Status_Snapshot.md",
    "server-design\systems-ui\Build_S5G_Identity_Framework_QA_Cleanup.md",
    "server-design\testing\fragmentengine\Build_S5G_Identity_Framework_QA_Cleanup_Test.md"
)

foreach ($Path in $Required) {
    if (!(Test-Path $Path)) {
        throw "Required S5G file missing after extraction: $Path"
    }
    Write-Host "[Build S5G] Found $Path"
}

# Keep the repository clean from generated pack/staging junk.
Get-ChildItem -Path $Root -Filter "README_AERETH_FRAGMENTENGINE_BUILD_*.md" -File -ErrorAction SilentlyContinue | ForEach-Object {
    Write-Host "[Build S5G] Removing generated root README: $($_.Name)"
    Remove-Item -Force $_.FullName
}

$StagingFolders = @(
    "implementation-files",
    "s5a_pack",
    "s5b_pack",
    "s5c_pack",
    "s5d_pack",
    "s5e_pack",
    "s5f_pack",
    "s5g_pack"
)

foreach ($Folder in $StagingFolders) {
    if (Test-Path $Folder) {
        Write-Host "[Build S5G] Removing staging folder: $Folder"
        Remove-Item -Recurse -Force $Folder
    }
}

$ExcludePath = ".git\info\exclude"
if (Test-Path ".git") {
    if (!(Test-Path $ExcludePath)) {
        New-Item -ItemType File -Force -Path $ExcludePath | Out-Null
    }

    $ExcludeText = Get-Content -Raw -Path $ExcludePath
    $Entries = @(
        "README_AERETH_FRAGMENTENGINE_BUILD_*.md",
        "implementation-files/",
        "s5a_pack/",
        "s5b_pack/",
        "s5c_pack/",
        "s5d_pack/",
        "s5e_pack/",
        "s5f_pack/",
        "s5g_pack/",
        "server-deployment/backups/",
        "server-deployment/logs/"
    )

    foreach ($Entry in $Entries) {
        if ($ExcludeText -notmatch [regex]::Escape($Entry)) {
            Add-Content -Path $ExcludePath -Value $Entry
            Write-Host "[Build S5G] Added local git exclude: $Entry"
        }
    }
}

Write-Host "[Build S5G] Applied. Now run .\tools\powershell\fragmentengine\run-s5g-identity-framework-qa.ps1"
