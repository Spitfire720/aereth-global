param(
    [string]$RepoRoot = "C:\Users\Bernardo\Desktop\Aereth global"
)

$ErrorActionPreference = "Stop"
$path = Join-Path $RepoRoot "server-config-drafts\oraxen\lantern-marches\build1\live\items\lantern_marches_props.yml"

if (!(Test-Path $path)) {
    Write-Error "Missing Oraxen draft file: $path"
}

$content = Get-Content $path -Raw
$bad = @("TODO", "PLACEHOLDER", "REPLACE_ME", "<world>", "<x>", "<y>", "<z>")

foreach ($token in $bad) {
    if ($content -match [regex]::Escape($token)) {
        Write-Warning "Found placeholder token: $token"
    }
}

Write-Host "Checked: $path"
Write-Host "Manual review still required because YAML is apparently where optimism goes to die."
