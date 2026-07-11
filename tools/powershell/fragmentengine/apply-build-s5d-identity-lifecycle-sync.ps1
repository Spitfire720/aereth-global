param(
    [string]$Root = (Get-Location).Path
)

$ErrorActionPreference = "Stop"

Write-Host "[Build S5D] Applying Identity Lifecycle Sync"

$PluginPath = Join-Path $Root "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\FragmentEnginePlugin.java"
$IdentityPath = Join-Path $Root "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\CharacterIdentityService.java"
$ListenerPath = Join-Path $Root "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\listener\IdentitySyncListener.java"

$IdentitySource = Join-Path $Root "implementation-files\FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\service\CharacterIdentityService.java"
$ListenerSource = Join-Path $Root "implementation-files\FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\listener\IdentitySyncListener.java"

foreach ($Path in @($PluginPath, $IdentitySource, $ListenerSource)) {
    if (!(Test-Path $Path)) {
        throw "Required file not found: $Path"
    }
}

$BackupDir = Join-Path $Root "server-deployment\backups\s5d-identity-lifecycle-sync"
New-Item -ItemType Directory -Force -Path $BackupDir | Out-Null
Copy-Item -Force $PluginPath (Join-Path $BackupDir "FragmentEnginePlugin.java.bak")
if (Test-Path $IdentityPath) {
    Copy-Item -Force $IdentityPath (Join-Path $BackupDir "CharacterIdentityService.java.bak")
}
if (Test-Path $ListenerPath) {
    Copy-Item -Force $ListenerPath (Join-Path $BackupDir "IdentitySyncListener.java.bak")
}

$Utf8NoBom = New-Object System.Text.UTF8Encoding($false)

New-Item -ItemType Directory -Force -Path (Split-Path $IdentityPath -Parent) | Out-Null
Copy-Item -Force $IdentitySource $IdentityPath
$IdentityText = Get-Content -Raw -Path $IdentityPath
[System.IO.File]::WriteAllText((Resolve-Path $IdentityPath), $IdentityText, $Utf8NoBom)
Write-Host "[Build S5D] Replaced CharacterIdentityService.java"

New-Item -ItemType Directory -Force -Path (Split-Path $ListenerPath -Parent) | Out-Null
Copy-Item -Force $ListenerSource $ListenerPath
$ListenerText = Get-Content -Raw -Path $ListenerPath
[System.IO.File]::WriteAllText((Resolve-Path $ListenerPath), $ListenerText, $Utf8NoBom)
Write-Host "[Build S5D] Added IdentitySyncListener.java"

$PluginText = Get-Content -Raw -Path $PluginPath

if (!$PluginText.Contains("private CharacterIdentityService characterIdentityService;")) {
    $PluginText = $PluginText.Replace(
        "    private IntentService intentService;`r`n",
        "    private IntentService intentService;`r`n    private CharacterIdentityService characterIdentityService;`r`n"
    )
    $PluginText = $PluginText.Replace(
        "    private IntentService intentService;`n",
        "    private IntentService intentService;`n    private CharacterIdentityService characterIdentityService;`n"
    )
}

if (!$PluginText.Contains("characterIdentityService = new CharacterIdentityService")) {
    $PluginText = $PluginText.Replace(
        "        intentService = new IntentService(this, characterService);`r`n",
        "        intentService = new IntentService(this, characterService);`r`n        characterIdentityService = new CharacterIdentityService(this, characterService, fragmentService, intentService);`r`n"
    )
    $PluginText = $PluginText.Replace(
        "        intentService = new IntentService(this, characterService);`n",
        "        intentService = new IntentService(this, characterService);`n        characterIdentityService = new CharacterIdentityService(this, characterService, fragmentService, intentService);`n"
    )
}

if ($PluginText.Contains("new AerethPlaceholderExpansion(this, characterService, fragmentService, intentService, disciplineService, abilityService).register();")) {
    $PluginText = $PluginText.Replace(
        "new AerethPlaceholderExpansion(this, characterService, fragmentService, intentService, disciplineService, abilityService).register();",
        "new AerethPlaceholderExpansion(this, characterService, fragmentService, intentService, disciplineService, abilityService, characterIdentityService).register();"
    )
}

$IdentityRegisterLine = "        getServer().getPluginManager().registerEvents(new live.aereth.fragmentengine.listener.IdentitySyncListener(this, characterIdentityService), this);"

if (!$PluginText.Contains("IdentitySyncListener")) {
    if ($PluginText.Contains("        getServer().getPluginManager().registerEvents(new live.aereth.fragmentengine.listener.AbilityHotbarListener(this, characterService, disciplineService, abilityService), this);`r`n")) {
        $PluginText = $PluginText.Replace(
            "        getServer().getPluginManager().registerEvents(new live.aereth.fragmentengine.listener.AbilityHotbarListener(this, characterService, disciplineService, abilityService), this);`r`n",
            "        getServer().getPluginManager().registerEvents(new live.aereth.fragmentengine.listener.AbilityHotbarListener(this, characterService, disciplineService, abilityService), this);`r`n$IdentityRegisterLine`r`n"
        )
    } elseif ($PluginText.Contains("        getServer().getPluginManager().registerEvents(new live.aereth.fragmentengine.listener.AbilityHotbarListener(this, characterService, disciplineService, abilityService), this);`n")) {
        $PluginText = $PluginText.Replace(
            "        getServer().getPluginManager().registerEvents(new live.aereth.fragmentengine.listener.AbilityHotbarListener(this, characterService, disciplineService, abilityService), this);`n",
            "        getServer().getPluginManager().registerEvents(new live.aereth.fragmentengine.listener.AbilityHotbarListener(this, characterService, disciplineService, abilityService), this);`n$IdentityRegisterLine`n"
        )
    } elseif ($PluginText.Contains("        getServer().getPluginManager().registerEvents(new FragmentEngineGuiListener(this, characterService, fragmentService, intentService, disciplineService, abilityService), this);`r`n")) {
        $PluginText = $PluginText.Replace(
            "        getServer().getPluginManager().registerEvents(new FragmentEngineGuiListener(this, characterService, fragmentService, intentService, disciplineService, abilityService), this);`r`n",
            "        getServer().getPluginManager().registerEvents(new FragmentEngineGuiListener(this, characterService, fragmentService, intentService, disciplineService, abilityService), this);`r`n$IdentityRegisterLine`r`n"
        )
    } else {
        throw "S5D patch failed: could not find listener registration anchor in FragmentEnginePlugin.java"
    }
}

if (!$PluginText.Contains("characterIdentityService.syncOnlinePlayersSilently();")) {
    if ($PluginText.Contains($IdentityRegisterLine + "`r`n")) {
        $PluginText = $PluginText.Replace(
            $IdentityRegisterLine + "`r`n",
            $IdentityRegisterLine + "`r`n        characterIdentityService.syncOnlinePlayersSilently();`r`n"
        )
    } elseif ($PluginText.Contains($IdentityRegisterLine + "`n")) {
        $PluginText = $PluginText.Replace(
            $IdentityRegisterLine + "`n",
            $IdentityRegisterLine + "`n        characterIdentityService.syncOnlinePlayersSilently();`n"
        )
    }
}

if (!$PluginText.Contains("private CharacterIdentityService characterIdentityService;")) {
    throw "S5D patch failed: missing CharacterIdentityService field."
}
if (!$PluginText.Contains("characterIdentityService = new CharacterIdentityService(this, characterService, fragmentService, intentService);")) {
    throw "S5D patch failed: missing CharacterIdentityService initialization."
}
if (!$PluginText.Contains("IdentitySyncListener")) {
    throw "S5D patch failed: IdentitySyncListener not registered."
}
if (!$PluginText.Contains("characterIdentityService.syncOnlinePlayersSilently();")) {
    throw "S5D patch failed: startup sync call missing."
}

[System.IO.File]::WriteAllText((Resolve-Path $PluginPath), $PluginText, $Utf8NoBom)
Write-Host "[Build S5D] Patched FragmentEnginePlugin.java"

Write-Host "[Build S5D] Local patch applied. Now run mvn clean package. No deployment until BUILD SUCCESS."
