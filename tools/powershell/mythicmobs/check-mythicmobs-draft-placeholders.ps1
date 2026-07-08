param(
    [string]$RepoRoot = "C:\Users\Bernardo\Desktop\Aereth global"
)

$path = Join-Path $RepoRoot "server-config-drafts\mythicmobs\lantern-marches\build1\live"

Write-Host "Checking MythicMobs draft placeholders in $path"

if (!(Test-Path $path)) {
    Write-Error "Path not found: $path"
    exit 1
}

$matches = Select-String -Path (Join-Path $path "*.yml") -Pattern "PLACEHOLDER" -Recurse -ErrorAction SilentlyContinue

if ($matches) {
    Write-Host "Found placeholders:" -ForegroundColor Yellow
    $matches | ForEach-Object {
        Write-Host "$($_.Path):$($_.LineNumber) $($_.Line)"
    }
    exit 2
}

Write-Host "No placeholders found." -ForegroundColor Green
