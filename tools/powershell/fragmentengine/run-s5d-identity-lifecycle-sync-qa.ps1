param(
    [string]$Root = (Get-Location).Path
)

$ErrorActionPreference = "Stop"

Write-Host "[S5D QA] Checking Identity Lifecycle Sync"

function Fail([string]$Message) {
    Write-Host "[S5D QA] FAIL: $Message" -ForegroundColor Red
    exit 1
}

function Assert-File([string]$Path, [string]$Label) {
    if (!(Test-Path $Path)) {
        Fail "Missing file: $Label -> $Path"
    }
    Write-Host "[S5D QA] OK - $Label exists"
}

function Assert-NoBom([string]$Path) {
    $Bytes = [System.IO.File]::ReadAllBytes($Path)
    if ($Bytes.Length -ge 3 -and $Bytes[0] -eq 0xEF -and $Bytes[1] -eq 0xBB -and $Bytes[2] -eq 0xBF) {
        Fail "UTF-8 BOM found in $Path"
    }
}

function Assert-Contains([string]$Text, [string]$Needle, [string]$Label) {
    if (!$Text.Contains($Needle)) {
        Fail "Missing expected content: $Label"
    }
    Write-Host "[S5D QA] OK - $Label"
}

$PluginPath = Join-Path $Root "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\FragmentEnginePlugin.java"
$IdentityPath = Join-Path $Root "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\CharacterIdentityService.java"
$ListenerPath = Join-Path $Root "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\listener\IdentitySyncListener.java"
$BuildDoc = Join-Path $Root "server-design\systems-ui\Build_S5D_Identity_Lifecycle_Sync.md"
$ContractDoc = Join-Path $Root "server-design\identity\Identity_Lifecycle_Sync_Contract.md"
$TestDoc = Join-Path $Root "server-design\testing\fragmentengine\Build_S5D_Identity_Lifecycle_Sync_Test.md"
$JarPath = Join-Path $Root "FragmentEngine_Build1_Source\target\FragmentEngine-1.15.0.jar"

foreach ($Pair in @(
    @($PluginPath, "FragmentEnginePlugin.java"),
    @($IdentityPath, "CharacterIdentityService.java"),
    @($ListenerPath, "IdentitySyncListener.java"),
    @($BuildDoc, "S5D build doc"),
    @($ContractDoc, "S5D contract doc"),
    @($TestDoc, "S5D test doc")
)) {
    Assert-File $Pair[0] $Pair[1]
    Assert-NoBom $Pair[0]
}

Assert-File $JarPath "compiled FragmentEngine jar"

$Plugin = Get-Content -Raw -Path $PluginPath
$Identity = Get-Content -Raw -Path $IdentityPath
$Listener = Get-Content -Raw -Path $ListenerPath
$BuildDocText = Get-Content -Raw -Path $BuildDoc
$ContractDocText = Get-Content -Raw -Path $ContractDoc
$TestDocText = Get-Content -Raw -Path $TestDoc

Assert-Contains $Plugin "private CharacterIdentityService characterIdentityService;" "plugin has identity service field"
Assert-Contains $Plugin "characterIdentityService = new CharacterIdentityService(this, characterService, fragmentService, intentService);" "plugin initializes identity service"
Assert-Contains $Plugin "IdentitySyncListener" "plugin registers IdentitySyncListener"
Assert-Contains $Plugin "characterIdentityService.syncOnlinePlayersSilently();" "plugin runs startup online-player identity sync"

Assert-Contains $Identity "public IdentitySummary sync(OfflinePlayer player)" "identity service exposes sync(player)"
Assert-Contains $Identity "public SyncResult syncSilently(OfflinePlayer player)" "identity service exposes silent sync"
Assert-Contains $Identity "public int syncOnlinePlayersSilently()" "identity service exposes startup online sync"
Assert-Contains $Identity "characters.storage().saveCharacter" "identity sync persists character YAML"
Assert-Contains $Identity "record SyncResult" "identity service has SyncResult record"
Assert-Contains $Identity "identity.last-sync-source" "identity writes sync source marker"

Assert-Contains $Listener "class IdentitySyncListener" "identity sync listener class exists"
Assert-Contains $Listener "PlayerJoinEvent" "join event sync exists"
Assert-Contains $Listener "PlayerChangedWorldEvent" "world-change event sync exists"
Assert-Contains $Listener "PlayerQuitEvent" "quit event sync exists"
Assert-Contains $Listener "identity.syncSilently" "listener uses safe silent sync"

Assert-Contains $BuildDocText "lifecycle" "S5D build doc describes lifecycle sync"
Assert-Contains $ContractDocText "identity.*" "S5D contract doc describes identity YAML persistence"
Assert-Contains $TestDocText "BUILD SUCCESS" "S5D test doc includes compile pass criteria"

Write-Host "[S5D QA] PASS"
