param(
    [Parameter(Mandatory = $true)]
    [string]$InputPath,

    [Parameter(Mandatory = $true)]
    [string]$OutputPath
)

# Redacts IPv4 addresses and common Minecraft login address patterns before publishing logs.
# Use this only if you still want latest-tail.txt in the public mirror.
# Otherwise, exclude logs entirely. Privacy: the one boss fight nobody asked for.

if (!(Test-Path $InputPath)) {
    throw "Input log file not found: $InputPath"
}

$content = Get-Content -LiteralPath $InputPath -Raw

# Redact player login address style: Player[/1.2.3.4:12345]
$content = $content -replace '(\[/)(?:\d{1,3}\.){3}\d{1,3}(:\d+\])', '$1REDACTED-IP$2'

# Redact any remaining IPv4 address
$content = $content -replace '(?<!\d)(?:\d{1,3}\.){3}\d{1,3}(?!\d)', 'REDACTED-IP'

# Optional: redact ports after REDACTED-IP if desired
# $content = $content -replace 'REDACTED-IP:\d+', 'REDACTED-IP:REDACTED-PORT'

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputPath) | Out-Null
Set-Content -LiteralPath $OutputPath -Value $content -Encoding UTF8
