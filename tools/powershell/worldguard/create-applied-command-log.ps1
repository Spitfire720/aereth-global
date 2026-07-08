param(
    [string]$RepoRoot = "C:\Users\Bernardo\Desktop\Aereth global"
)

Set-Location $RepoRoot

$target = "server-config-drafts/worldguard/lantern-marches/build1/live/worldguard_live_commands_applied.txt"
$template = "server-config-drafts/worldguard/lantern-marches/build1/live/worldguard_live_commands_template.txt"

if (!(Test-Path $template)) {
    throw "Template not found: $template"
}

if (Test-Path $target) {
    Write-Host "Applied command log already exists: $target" -ForegroundColor Yellow
    exit 0
}

Copy-Item $template $target
Write-Host "Created applied command log from template: $target" -ForegroundColor Green
Write-Host "Now edit it with the exact commands actually used on the live server." -ForegroundColor Cyan
