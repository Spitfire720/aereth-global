$ErrorActionPreference = "Stop"

$RepoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..\..")
$SourceRoot = Join-Path $RepoRoot "implementation-files\FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine"
$TargetRoot = Join-Path $RepoRoot "FragmentEngine_Build1_Source\src\main\java\live\aereth\fragmentengine"
$Utf8NoBom = New-Object System.Text.UTF8Encoding($false)

function Read-NoBom([string]$Path) {
    $Text = [System.IO.File]::ReadAllText($Path)
    if ($Text.Length -gt 0 -and $Text[0] -eq [char]0xFEFF) {
        $Text = $Text.Substring(1)
    }
    return $Text
}

function Write-NoBom([string]$Path, [string]$Text) {
    [System.IO.File]::WriteAllText($Path, $Text, $Utf8NoBom)
}

function Copy-SourceFile([string]$RelativePath) {
    $Source = Join-Path $SourceRoot $RelativePath
    $Target = Join-Path $TargetRoot $RelativePath
    New-Item -ItemType Directory -Force -Path (Split-Path $Target -Parent) | Out-Null
    Write-NoBom $Target (Read-NoBom $Source)
    Write-Host "[Build S3B] Copied $RelativePath" -ForegroundColor Green
}

function Add-After([string]$Text, [string]$Needle, [string]$Anchor, [string]$Replacement) {
    if ($Text.Contains($Needle)) { return $Text }
    if (!$Text.Contains($Anchor)) { throw "Anchor not found while patching AerethCommand.java: $Anchor" }
    return $Text.Replace($Anchor, $Replacement)
}

Write-Host "[Build S3B] Copying Ability Codex GUI and updated GUI listener" -ForegroundColor Cyan
Copy-SourceFile "gui\AbilityCodexGui.java"
Copy-SourceFile "gui\DisciplineCodexGui.java"
Copy-SourceFile "listener\FragmentEngineGuiListener.java"

$CommandPath = Join-Path $TargetRoot "command\AerethCommand.java"
if (!(Test-Path $CommandPath)) { throw "AerethCommand.java not found: $CommandPath" }

Write-Host "[Build S3B] Patching AerethCommand.java" -ForegroundColor Cyan
$Command = Read-NoBom $CommandPath

$Command = Add-After $Command "import live.aereth.fragmentengine.gui.DisciplineCodexGui;" "import live.aereth.fragmentengine.gui.IntentSlotsGui;" "import live.aereth.fragmentengine.gui.IntentSlotsGui;`r`nimport live.aereth.fragmentengine.gui.DisciplineCodexGui;"
$Command = Add-After $Command "import live.aereth.fragmentengine.gui.AbilityCodexGui;" "import live.aereth.fragmentengine.gui.DisciplineCodexGui;" "import live.aereth.fragmentengine.gui.DisciplineCodexGui;`r`nimport live.aereth.fragmentengine.gui.AbilityCodexGui;"

$Command = Add-After $Command "private final DisciplineCodexGui disciplineCodexGui;" "private final IntentSlotsGui intentSlotsGui;" "private final IntentSlotsGui intentSlotsGui;`r`n    private final DisciplineCodexGui disciplineCodexGui;"
$Command = Add-After $Command "private final AbilityCodexGui abilityCodexGui;" "private final DisciplineCodexGui disciplineCodexGui;" "private final DisciplineCodexGui disciplineCodexGui;`r`n    private final AbilityCodexGui abilityCodexGui;"

$Command = Add-After $Command "this.disciplineCodexGui = new DisciplineCodexGui" "this.intentSlotsGui = new IntentSlotsGui(plugin, characters, intents);" "this.intentSlotsGui = new IntentSlotsGui(plugin, characters, intents);`r`n        this.disciplineCodexGui = new DisciplineCodexGui(plugin, characters, disciplines);"
$Command = Add-After $Command "this.abilityCodexGui = new AbilityCodexGui" "this.disciplineCodexGui = new DisciplineCodexGui(plugin, characters, disciplines);" "this.disciplineCodexGui = new DisciplineCodexGui(plugin, characters, disciplines);`r`n        this.abilityCodexGui = new AbilityCodexGui(plugin, characters, disciplines, abilities);"

$Command = Add-After $Command "case `"disciplinegui`" -> disciplineGui(sender);" "case `"discipline`" -> discipline(sender, args);" "case `"disciplinegui`" -> disciplineGui(sender);`r`n                case `"discipline`" -> discipline(sender, args);"
$Command = Add-After $Command "case `"abilitygui`" -> abilityGui(sender);" "case `"abilitylist`" -> abilityList(sender);" "case `"abilitygui`" -> abilityGui(sender);`r`n                case `"abilitylist`" -> abilityList(sender);"

$Command = Add-After $Command "&b/aereth disciplinegui" "sender.sendMessage(Text.color(`"&b/aereth discipline <player>`"));" "sender.sendMessage(Text.color(`"&b/aereth disciplinegui`"));`r`n        sender.sendMessage(Text.color(`"&b/aereth discipline <player>`"));"
$Command = Add-After $Command "&b/aereth abilitygui" "sender.sendMessage(Text.color(`"&b/aereth abilitylist`"));" "sender.sendMessage(Text.color(`"&b/aereth abilitygui`"));`r`n        sender.sendMessage(Text.color(`"&b/aereth abilitylist`"));"

if (!$Command.Contains("private void disciplineGui(CommandSender sender)")) {
$DisciplineMethod = @'
    private void disciplineGui(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix() + Text.color("&cOnly players can open the Discipline Codex GUI."));
            return;
        }
        disciplineCodexGui.open(player);
    }

'@
    $Command = Add-After $Command "private void disciplineGui(CommandSender sender)" "    private void profile(CommandSender sender, String[] args)" ($DisciplineMethod + "    private void profile(CommandSender sender, String[] args)")
}

if (!$Command.Contains("private void abilityGui(CommandSender sender)")) {
$AbilityMethod = @'
    private void abilityGui(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix() + Text.color("&cOnly players can open the Ability Codex GUI."));
            return;
        }
        abilityCodexGui.open(player);
    }

'@
    $Command = Add-After $Command "private void abilityGui(CommandSender sender)" "    private void profile(CommandSender sender, String[] args)" ($AbilityMethod + "    private void profile(CommandSender sender, String[] args)")
}

# Add abilitygui to tab completion list when the first-argument list is present.
if (!$Command.Contains('"abilitygui"')) {
    $Command = $Command.Replace('"abilitylist"', '"abilitygui", "abilitylist"')
}

Write-NoBom $CommandPath $Command

Get-ChildItem (Join-Path $TargetRoot "") -Recurse -Filter "*.java" | ForEach-Object {
    Write-NoBom $_.FullName (Read-NoBom $_.FullName)
}

Write-Host "[Build S3B] Build S3B patch applied. Now run: cd FragmentEngine_Build1_Source; mvn clean package" -ForegroundColor Green
