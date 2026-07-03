$ErrorActionPreference = "Stop"

Set-Location $PSScriptRoot

Write-Host "Running safe Aereth snapshot..." -ForegroundColor Cyan
.\snapshot.ps1

Write-Host "Publishing latest snapshot to Cloudflare Pages..." -ForegroundColor Cyan
wrangler pages deploy .\latest --project-name aereth-snapshot --branch main

Write-Host "Aereth snapshot published to Pages:" -ForegroundColor Green
Write-Host "https://aereth-snapshot.pages.dev/manifest.json"
