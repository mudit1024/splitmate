$ErrorActionPreference = "Stop"

$status = git status --porcelain

if ($status) {
    Write-Host ""
    Write-Host "ERROR: Working tree is not clean." -ForegroundColor Red
    Write-Host "Commit or otherwise clean your changes before switching branches." -ForegroundColor Yellow
    Write-Host ""
    git status
    exit 1
}

if (-not $args[0]) {
    Write-Host "Usage: .\.githooks\safe-switch.ps1 <branch>" -ForegroundColor Yellow
    exit 1
}

$branch = $args[0]

Write-Host "Working tree clean. Switching to '$branch'..." -ForegroundColor Green

git switch $branch

if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}
