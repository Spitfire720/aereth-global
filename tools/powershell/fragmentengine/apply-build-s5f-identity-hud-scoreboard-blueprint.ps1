$ErrorActionPreference = "Stop"

Write-Host "[Build S5F] Applying Identity HUD + Scoreboard Blueprint"

$Root = (Get-Location).Path

$Expected = @(
    "server-design\systems-ui\Build_S5F_Identity_HUD_Scoreboard_Blueprint.md",
    "server-design\systems-ui\Identity_HUD_Scoreboard_Layout.md",
    "server-design\identity\Identity_HUD_Placeholder_Map.md",
    "server-design\config-templates\identity-scoreboard-placeholder-layout.yml",
    "server-design\config-templates\identity-actionbar-placeholder-layout.yml",
    "server-design\config-templates\identity-hologram-placeholder-layout.yml",
    "server-design\testing\fragmentengine\Build_S5F_Identity_HUD_Scoreboard_Blueprint_Test.md",
    "tools\powershell\fragmentengine\run-s5f-identity-hud-scoreboard-blueprint-qa.ps1"
)

foreach ($File in $Expected) {
    if (!(Test-Path $File)) {
        throw "Missing expected S5F file: $File. Extract the pack into the repo root first."
    }
    Write-Host "[Build S5F] Found $File"
}

$ExcludePath = ".git\info\exclude"
if (Test-Path ".git") {
    if (!(Test-Path $ExcludePath)) {
        New-Item -ItemType File -Force -Path $ExcludePath | Out-Null
    }

    $ExcludeText = Get-Content -Raw -Path $ExcludePath
    $Entries = @(
        "s5f_pack/",
        "README_AERETH_FRAGMENTENGINE_BUILD_S5F*.md"
    )

    foreach ($Entry in $Entries) {
        if ($ExcludeText -notmatch [regex]::Escape($Entry)) {
            Add-Content -Path $ExcludePath -Value $Entry
            Write-Host "[Build S5F] Added local git exclude: $Entry"
        }
    }
}

Write-Host "[Build S5F] Blueprint applied. No Java changes. Run .\tools\powershell\fragmentengine\run-s5f-identity-hud-scoreboard-blueprint-qa.ps1 next."
