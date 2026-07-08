param()
$ErrorActionPreference = "Stop"

function Write-Step($Message) {
    Write-Host "[Build S3D] $Message"
}

function Read-Utf8NoBom($Path) {
    $Text = [System.IO.File]::ReadAllText((Resolve-Path $Path))
    if ($Text.Length -gt 0 -and $Text[0] -eq [char]0xFEFF) {
        $Text = $Text.Substring(1)
    }
    return $Text
}

function Write-Utf8NoBom($Path, $Text) {
    $Utf8NoBom = New-Object System.Text.UTF8Encoding($false)
    [System.IO.File]::WriteAllText((Resolve-Path $Path), $Text, $Utf8NoBom)
}

function Add-After($Text, $Anchor, $Insert) {
    if ($Text.Contains($Insert.Trim())) {
        return $Text
    }
    if (!$Text.Contains($Anchor)) {
        throw "Patch anchor not found: $Anchor"
    }
    return $Text.Replace($Anchor, $Anchor + "`r`n" + $Insert)
}

function Add-Before($Text, $Anchor, $Insert) {
    if ($Text.Contains($Insert.Trim())) {
        return $Text
    }
    if (!$Text.Contains($Anchor)) {
        throw "Patch anchor not found: $Anchor"
    }
    return $Text.Replace($Anchor, $Insert + "`r`n" + $Anchor)
}

$Root = (Get-Location).Path
$SourceRoot = Join-Path $Root "FragmentEngine_Build1_Source"
$ImplRoot = Join-Path $Root "implementation-files\FragmentEngine_Build1_Source"

if (!(Test-Path $SourceRoot)) {
    throw "Run this from the Aereth global repo root. Missing FragmentEngine_Build1_Source."
}

if (!(Test-Path (Join-Path $SourceRoot "src\main\java\live\aereth\fragmentengine\gui\AbilityLoadoutGui.java"))) {
    throw "Build S3D depends on S3C. AbilityLoadoutGui.java is missing. Apply/pass S3C first."
}

Write-Step "Copying Ability Activation service and GUI"
$FilesToCopy = @(
    "src\main\java\live\aereth\fragmentengine\service\AbilityActivationService.java",
    "src\main\java\live\aereth\fragmentengine\gui\AbilityActivationGui.java"
)

foreach ($Relative in $FilesToCopy) {
    $From = Join-Path $ImplRoot $Relative
    $To = Join-Path $SourceRoot $Relative
    if (!(Test-Path $From)) {
        throw "Missing implementation file: $From"
    }
    New-Item -ItemType Directory -Force -Path (Split-Path $To -Parent) | Out-Null
    Copy-Item $From $To -Force
    Write-Step "Copied FragmentEngine_Build1_Source\$Relative"
}

$CommandPath = Join-Path $SourceRoot "src\main\java\live\aereth\fragmentengine\command\AerethCommand.java"
$ListenerPath = Join-Path $SourceRoot "src\main\java\live\aereth\fragmentengine\listener\FragmentEngineGuiListener.java"

Write-Step "Patching AerethCommand.java"
$CommandText = Read-Utf8NoBom $CommandPath

if (!$CommandText.Contains("import live.aereth.fragmentengine.gui.AbilityActivationGui;")) {
    $CommandText = Add-After $CommandText "import live.aereth.fragmentengine.gui.AbilityLoadoutGui;" "import live.aereth.fragmentengine.gui.AbilityActivationGui;"
}
if (!$CommandText.Contains("import live.aereth.fragmentengine.service.AbilityActivationService;")) {
    $CommandText = Add-After $CommandText "import live.aereth.fragmentengine.service.AbilityService;" "import live.aereth.fragmentengine.service.AbilityActivationService;"
}

if (!$CommandText.Contains("private final AbilityActivationService abilityActivation;")) {
    $CommandText = Add-After $CommandText "    private final AbilityLoadoutGui abilityLoadoutGui;" "    private final AbilityActivationService abilityActivation;`r`n    private final AbilityActivationGui abilityActivationGui;"
}

if (!$CommandText.Contains("this.abilityActivation = new AbilityActivationService")) {
    $CommandText = Add-After $CommandText "        this.abilityLoadoutGui = new AbilityLoadoutGui(plugin, characters, disciplines, abilities);" "        this.abilityActivation = new AbilityActivationService(plugin, characters, disciplines, abilities);`r`n        this.abilityActivationGui = new AbilityActivationGui(plugin, characters, disciplines, abilities, abilityActivation);"
}

if (!$CommandText.Contains('case "abilityactivation" -> abilityActivationGui(sender);')) {
    $CommandText = Add-After $CommandText '                case "abilityloadout" -> abilityLoadout(sender);' '                case "abilityactivation" -> abilityActivationGui(sender);`r`n                case "abilityactivate" -> abilityActivate(sender, args);`r`n                case "abilitycooldowns" -> abilityCooldowns(sender, args);'
}

if (!$CommandText.Contains('/aereth abilityactivation')) {
    $CommandText = Add-After $CommandText '        sender.sendMessage(Text.color("&b/aereth abilityloadout"));' '        sender.sendMessage(Text.color("&b/aereth abilityactivation"));`r`n        sender.sendMessage(Text.color("&b/aereth abilityactivate <player> <slot>"));`r`n        sender.sendMessage(Text.color("&b/aereth abilitycooldowns <player>"));'
}

if (!$CommandText.Contains("private void abilityActivationGui(CommandSender sender)")) {
$CommandMethods = @'
    private void abilityActivationGui(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix() + Text.color("&cOnly players can open the Ability Activation GUI."));
            return;
        }
        abilityActivationGui.open(player);
    }

    private void abilityActivate(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 3, "/aereth abilityactivate <player> <slot>");
        int slot = parseInt(args[2], "slot");
        AbilityActivationService.ActivationResult result = abilityActivation.activate(Bukkit.getOfflinePlayer(args[1]), slot);
        sender.sendMessage(prefix() + Text.color("&aAbility activated: &f" + result.display()
                + " &8/ &7Slot: &f" + result.slot()
                + " &8/ &7Cooldown: &f" + result.cooldownSeconds() + "s"));
    }

    private void abilityCooldowns(CommandSender sender, String[] args) {
        requireArgs(args, 2, "/aereth abilitycooldowns <player>");
        AbilityActivationService.CooldownSummary summary = abilityActivation.cooldowns(Bukkit.getOfflinePlayer(args[1]));
        sender.sendMessage(prefix() + Text.color("&7Ability cooldowns: &b" + args[1]));
        if (summary.active().isEmpty()) {
            sender.sendMessage(Text.color("&7Active: &fnone"));
            return;
        }
        for (AbilityActivationService.ActiveCooldown cooldown : summary.active()) {
            sender.sendMessage(Text.color("&7Slot " + cooldown.slot() + ": &f" + cooldown.display()
                    + " &8/ &7Remaining: &f" + cooldown.remainingSeconds() + "s"));
        }
    }

'@
    $CommandText = Add-Before $CommandText "    private void profile(CommandSender sender, String[] args)" $CommandMethods
}

if ($CommandText.Contains('"abilityloadout"') -and !$CommandText.Contains('"abilityactivation"')) {
    $CommandText = $CommandText.Replace('"abilityloadout",', '"abilityloadout", "abilityactivation", "abilityactivate", "abilitycooldowns",')
}

if (!$CommandText.Contains('args[0].equalsIgnoreCase("abilityactivate")')) {
$TabBlock = @'
        if (args.length == 3 && args[0].equalsIgnoreCase("abilityactivate")) {
            return partial(args[2], List.of("1", "2", "3", "4"));
        }
'@
    $CommandText = Add-Before $CommandText '        if (args.length == 2 && args[0].equalsIgnoreCase("agent")) {' $TabBlock
}

Write-Utf8NoBom $CommandPath $CommandText

Write-Step "Patching FragmentEngineGuiListener.java"
$ListenerText = Read-Utf8NoBom $ListenerPath

if (!$ListenerText.Contains("import live.aereth.fragmentengine.gui.AbilityActivationGui;")) {
    $ListenerText = Add-After $ListenerText "import live.aereth.fragmentengine.gui.AbilityLoadoutGui;" "import live.aereth.fragmentengine.gui.AbilityActivationGui;"
}
if (!$ListenerText.Contains("import live.aereth.fragmentengine.service.AbilityActivationService;")) {
    $ListenerText = Add-After $ListenerText "import live.aereth.fragmentengine.service.AbilityService;" "import live.aereth.fragmentengine.service.AbilityActivationService;"
}
if (!$ListenerText.Contains("private final AbilityActivationGui abilityActivationGui;")) {
    $ListenerText = Add-After $ListenerText "    private final AbilityLoadoutGui abilityLoadoutGui;" "    private final AbilityActivationGui abilityActivationGui;"
}
if (!$ListenerText.Contains("this.abilityActivationGui = new AbilityActivationGui")) {
    $ListenerText = Add-After $ListenerText "        this.abilityLoadoutGui = new AbilityLoadoutGui(plugin, characters, disciplines, abilities);" "        AbilityActivationService abilityActivation = new AbilityActivationService(plugin, characters, disciplines, abilities);`r`n        this.abilityActivationGui = new AbilityActivationGui(plugin, characters, disciplines, abilities, abilityActivation);"
}

if (!$ListenerText.Contains("title.equals(plain(AbilityActivationGui.TITLE))")) {
$ListenerBlock = @'

        if (title.equals(plain(AbilityActivationGui.TITLE))) {
            abilityActivationGui.handleClick(player, slot);
            return;
        }
'@
    $ListenerText = Add-Before $ListenerText "    @EventHandler`r`n    public void onInventoryDrag" $ListenerBlock
}

if (!$ListenerText.Contains("plain(AbilityActivationGui.TITLE)")) {
    $ListenerText = $ListenerText.Replace("|| title.equals(plain(AbilityLoadoutGui.TITLE))", "|| title.equals(plain(AbilityLoadoutGui.TITLE))`r`n                        || title.equals(plain(AbilityActivationGui.TITLE))")
}

Write-Utf8NoBom $ListenerPath $ListenerText

Write-Step "Build S3D patch applied. Now run: cd FragmentEngine_Build1_Source; mvn clean package"
