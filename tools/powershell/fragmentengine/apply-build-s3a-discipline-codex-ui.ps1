param(
    [string]$RepoRoot = (Get-Location).Path
)

$ErrorActionPreference = "Stop"
$nl = [Environment]::NewLine

function Write-Step($Message) {
    Write-Host "[Build S3A] $Message" -ForegroundColor Cyan
}

function Copy-ImplementationFile($RelativePath) {
    $source = Join-Path $RepoRoot (Join-Path "implementation-files" $RelativePath)
    $target = Join-Path $RepoRoot $RelativePath
    if (!(Test-Path $source)) {
        throw "Missing implementation file: $source"
    }
    New-Item -ItemType Directory -Force -Path (Split-Path $target -Parent) | Out-Null
    Copy-Item $source $target -Force
    Write-Step "Copied $RelativePath"
}

Write-Step "Copying Discipline Codex GUI and updated listener/card classes"

Copy-ImplementationFile "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\gui\DisciplineCodexGui.java"
Copy-ImplementationFile "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\gui\CharacterCardGui.java"
Copy-ImplementationFile "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\listener\FragmentEngineGuiListener.java"

$commandPath = Join-Path $RepoRoot "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine\command\AerethCommand.java"
if (!(Test-Path $commandPath)) {
    throw "AerethCommand.java not found: $commandPath"
}

Write-Step "Patching AerethCommand.java"
$content = Get-Content $commandPath -Raw

if ($content -notmatch 'import live\.aereth\.fragmentengine\.gui\.DisciplineCodexGui;') {
    $replacement = 'import live.aereth.fragmentengine.gui.IntentSlotsGui;' + $nl + 'import live.aereth.fragmentengine.gui.DisciplineCodexGui;' + $nl
    $content = $content -replace 'import live\.aereth\.fragmentengine\.gui\.IntentSlotsGui;\r?\n', $replacement
}

if ($content -notmatch 'private final DisciplineCodexGui disciplineCodexGui;') {
    $replacement = 'private final IntentSlotsGui intentSlotsGui;' + $nl + '    private final DisciplineCodexGui disciplineCodexGui;' + $nl
    $content = $content -replace 'private final IntentSlotsGui intentSlotsGui;\r?\n', $replacement
}

if ($content -notmatch 'this\.disciplineCodexGui = new DisciplineCodexGui') {
    $replacement = 'this.intentSlotsGui = new IntentSlotsGui(plugin, characters, intents);' + $nl + '        this.disciplineCodexGui = new DisciplineCodexGui(plugin, characters, disciplines);'
    $content = $content -replace 'this\.intentSlotsGui = new IntentSlotsGui\(plugin, characters, intents\);', $replacement
}

if ($content -notmatch 'case "disciplinegui" -> disciplineGui\(sender\);') {
    $replacement = 'case "disciplinegui" -> disciplineGui(sender);' + $nl + '                case "discipline" -> discipline(sender, args);'
    $content = $content -replace 'case "discipline" -> discipline\(sender, args\);', $replacement
}

if ($content -notmatch '/aereth disciplinegui') {
    $replacement = 'sender.sendMessage(Text.color("&b/aereth disciplinegui"));' + $nl + '        sender.sendMessage(Text.color("&b/aereth discipline <player>"));'
    $content = $content -replace 'sender\.sendMessage\(Text\.color\("&b/aereth discipline <player>"\)\);', $replacement
}

if ($content -notmatch 'private void disciplineGui\(CommandSender sender\)') {
    $intentGuiMethod = @'
    private void intentGui(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix() + Text.color("&cOnly players can open the Intent Slots GUI."));
            return;
        }
        intentSlotsGui.open(player);
    }
'@

    $replacement = @'
    private void intentGui(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix() + Text.color("&cOnly players can open the Intent Slots GUI."));
            return;
        }
        intentSlotsGui.open(player);
    }

    private void disciplineGui(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix() + Text.color("&cOnly players can open the Discipline Codex GUI."));
            return;
        }
        disciplineCodexGui.open(player);
    }
'@

    if ($content.Contains($intentGuiMethod)) {
        $content = $content.Replace($intentGuiMethod, $replacement)
    } else {
        $methodInsert = @'

    private void disciplineGui(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix() + Text.color("&cOnly players can open the Discipline Codex GUI."));
            return;
        }
        disciplineCodexGui.open(player);
    }
'@
        $content = $content -replace '(?s)(private void intentGui\(CommandSender sender\) \{.*?intentSlotsGui\.open\(player\);\r?\n    \})', ('$1' + $methodInsert)
    }
}

if ($content -notmatch '"disciplinegui"') {
    $content = $content -replace '"discipline", "disciplinelist"', '"disciplinegui", "discipline", "disciplinelist"'
} elseif ($content -notmatch '"status", "card", "intentgui", "disciplinegui"') {
    $content = $content -replace '"status", "card", "intentgui",', '"status", "card", "intentgui", "disciplinegui",'
}

Set-Content $commandPath $content -Encoding UTF8

Write-Step "Build S3A patch applied. Now run: cd FragmentEngine_Build1_Source; mvn clean package"
