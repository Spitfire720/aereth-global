$ErrorActionPreference = "Stop"

$TOOLS = "C:\Users\Bernardo\Desktop\Aereth global\AerethPipelineTools"
$SNAPSHOT_AGENT = "$TOOLS\AerethSnapshotAgent"
$GITMIRROR = "C:\Users\Bernardo\Desktop\Aereth global\AerethSnapshotGit"

Set-Location $SNAPSHOT_AGENT

Write-Host "Running safe Aereth snapshot..." -ForegroundColor Cyan
.\snapshot.ps1

Write-Host "Publishing latest snapshot to Cloudflare Pages..." -ForegroundColor Cyan
wrangler pages deploy .\latest --project-name aereth-snapshot --branch main

Write-Host "Copying sanitized snapshot to GitHub mirror..." -ForegroundColor Cyan

robocopy ".\latest" "$GITMIRROR" /MIR /XD ".git" /XF ".gitignore" "README.md"
$RoboExit = $LASTEXITCODE
if ($RoboExit -gt 7) {
    throw "Robocopy failed with exit code $RoboExit"
}

Set-Location $GITMIRROR

$Forbidden = @(
    "*.db", "*.sqlite", "*.sqlite3", "*.mv.db", "*.h2.db",
    "*.jar", "*.zip",
    "*token*", "*secret*", "*credential*", "*password*"
)

foreach ($pattern in $Forbidden) {
    $bad = Get-ChildItem . -Recurse -File -Filter $pattern -ErrorAction SilentlyContinue
    if ($bad.Count -gt 0) {
        Write-Host "SAFETY BLOCK: forbidden files found in GitHub mirror." -ForegroundColor Red
        $bad | Select-Object -First 50 | ForEach-Object { Write-Host $_.FullName -ForegroundColor Yellow }
        exit 2
    }
}

git add -A

$Changes = git status --porcelain
if ($Changes) {
    $Stamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    git commit -m "Update snapshot $Stamp"
    git push origin main
    Write-Host "GitHub snapshot updated." -ForegroundColor Green
} else {
    Write-Host "No GitHub changes to publish." -ForegroundColor Yellow
}

Write-Host "Final GitHub raw manifest:" -ForegroundColor Green
$GHUSER = gh api user --jq .login
Write-Host "https://raw.githubusercontent.com/$GHUSER/aereth-snapshot/main/manifest.json"
