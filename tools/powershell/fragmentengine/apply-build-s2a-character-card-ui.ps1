param(
    [string]$RepoRoot = (Get-Location).Path
)

$ErrorActionPreference = "Stop"

function Write-Step($message) {
    Write-Host "[Build S2A] $message" -ForegroundColor Cyan
}

function Copy-ImplementationFile($relativePath) {
    $source = Join-Path $RepoRoot ("implementation-files\" + $relativePath)
    $target = Join-Path $RepoRoot $relativePath
    $targetDir = Split-Path $target -Parent
    New-Item -ItemType Directory -Force -Path $targetDir | Out-Null
    Copy-Item -Force $source $target
    Write-Step "Copied $relativePath"
}

function Add-Once([string]$content, [string]$needle, [string]$insertAfter) {
    if ($content.Contains($needle)) {
        return $content
    }
    if (-not $content.Contains($insertAfter)) {
        throw "Could not find insertion point: $insertAfter"
    }
    return $content.Replace($insertAfter, $insertAfter + "`r`n" + $needle)
}

Write-Step "Copying new GUI/listener classes"
Copy-ImplementationFile "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\gui\GuiItem.java"
Copy-ImplementationFile "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\gui\CharacterCardGui.java"
Copy-ImplementationFile "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\gui\IntentSlotsGui.java"
Copy-ImplementationFile "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\listener\FragmentEngineGuiListener.java"

$pluginPath = Join-Path $RepoRoot "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\FragmentEnginePlugin.java"
$commandPath = Join-Path $RepoRoot "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\command\AerethCommand.java"

Write-Step "Patching FragmentEnginePlugin.java"
$plugin = Get-Content $pluginPath -Raw
$plugin = Add-Once $plugin "import live.aereth.fragmentengine.listener.FragmentEngineGuiListener;" "import live.aereth.fragmentengine.command.AerethCommand;"

$registerLine = "        getServer().getPluginManager().registerEvents(new FragmentEngineGuiListener(this, characterService, fragmentService, intentService, disciplineService, abilityService), this);"
if (-not $plugin.Contains($registerLine)) {
    $anchor = @"
        if (aereth != null) {
            aereth.setExecutor(command);
            aereth.setTabCompleter(command);
        }
"@
    $replacement = $anchor + "`r`n" + $registerLine + "`r`n"
    if (-not $plugin.Contains($anchor)) {
        throw "Could not find command registration block in FragmentEnginePlugin.java"
    }
    $plugin = $plugin.Replace($anchor, $replacement)
}
Set-Content -Path $pluginPath -Value $plugin -NoNewline

Write-Step "Patching AerethCommand.java"
$command = Get-Content $commandPath -Raw
$command = Add-Once $command "import live.aereth.fragmentengine.gui.CharacterCardGui;" "import live.aereth.fragmentengine.service.ProgressionService;"
$command = Add-Once $command "import live.aereth.fragmentengine.gui.IntentSlotsGui;" "import live.aereth.fragmentengine.gui.CharacterCardGui;"
$command = Add-Once $command "import org.bukkit.entity.Player;" "import org.bukkit.configuration.file.YamlConfiguration;"

$fieldBlock = @"
    private final CharacterCardGui characterCardGui;
    private final IntentSlotsGui intentSlotsGui;
"@
if (-not $command.Contains("private final CharacterCardGui characterCardGui;")) {
    $command = $command.Replace("    private final AgentExportService agentExport;", "    private final AgentExportService agentExport;`r`n" + $fieldBlock.TrimEnd())
}

$ctorBlock = @"
        this.characterCardGui = new CharacterCardGui(plugin, characters, fragments, intents, disciplines, abilities);
        this.intentSlotsGui = new IntentSlotsGui(plugin, characters, intents);
"@
if (-not $command.Contains("this.characterCardGui = new CharacterCardGui")) {
    $command = $command.Replace("        this.agentExport = agentExport;", "        this.agentExport = agentExport;`r`n" + $ctorBlock.TrimEnd())
}

if (-not $command.Contains('case "card" -> card(sender);')) {
    $command = $command.Replace('                case "status" -> status(sender);', '                case "status" -> status(sender);' + "`r`n" + '                case "card" -> card(sender);')
}
if (-not $command.Contains('case "intentgui" -> intentGui(sender);')) {
    $command = $command.Replace('                case "intent" -> intent(sender, args);', '                case "intent" -> intent(sender, args);' + "`r`n" + '                case "intentgui" -> intentGui(sender);')
}

if (-not $command.Contains('&b/aereth card')) {
    $command = $command.Replace('        sender.sendMessage(Text.color("&b/aereth status"));', '        sender.sendMessage(Text.color("&b/aereth status"));' + "`r`n" + '        sender.sendMessage(Text.color("&b/aereth card"));')
}
if (-not $command.Contains('&b/aereth intentgui')) {
    $command = $command.Replace('        sender.sendMessage(Text.color("&b/aereth intent <player>"));', '        sender.sendMessage(Text.color("&b/aereth intent <player>"));' + "`r`n" + '        sender.sendMessage(Text.color("&b/aereth intentgui"));')
}

$cardMethods = @"

    private void card(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix() + Text.color("&cOnly players can open the Character Card GUI."));
            return;
        }
        characterCardGui.open(player);
    }

    private void intentGui(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix() + Text.color("&cOnly players can open the Intent Slots GUI."));
            return;
        }
        intentSlotsGui.open(player);
    }
"@
if (-not $command.Contains("private void card(CommandSender sender)")) {
    $command = $command.Replace("    private void profile(CommandSender sender, String[] args) {", $cardMethods + "`r`n    private void profile(CommandSender sender, String[] args) {")
}

if (-not $command.Contains('"card", "intentgui"')) {
    $command = $command.Replace('List.of("status", "profile",', 'List.of("status", "card", "intentgui", "profile",')
}

Set-Content -Path $commandPath -Value $command -NoNewline

Write-Step "Build S2A patch applied. Now run: cd FragmentEngine_Build1_Source; mvn clean package"
