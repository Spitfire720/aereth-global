$ErrorActionPreference = "Stop"

Write-Host "[Build S4A HOTFIX] Applying Player Ability Experience Bundle"

$Root = Get-Location
$SourceRoot = Join-Path $Root "FragmentEngine_Build1_Source"
$ImplRoot = Join-Path $Root "implementation-files\FragmentEngine_Build1_Source"

if (!(Test-Path $SourceRoot)) {
    throw "FragmentEngine_Build1_Source not found. Run this from C:\Users\Bernardo\Desktop\Aereth global"
}

function Copy-SourceFile {
    param([Parameter(Mandatory=$true)][string]$RelativePath)
    $From = Join-Path $ImplRoot $RelativePath
    $To = Join-Path $SourceRoot $RelativePath
    if (!(Test-Path $From)) { throw "Missing implementation file: $From" }
    New-Item -ItemType Directory -Force -Path (Split-Path $To -Parent) | Out-Null
    Copy-Item $From $To -Force
    Write-Host "[Build S4A HOTFIX] Copied $RelativePath"
}

function Replace-RegexOnce {
    param(
        [Parameter(Mandatory=$true)][string]$Text,
        [Parameter(Mandatory=$true)][string]$Pattern,
        [Parameter(Mandatory=$true)][string]$Replacement,
        [Parameter(Mandatory=$true)][string]$Label
    )
    if ($Text -notmatch $Pattern) {
        throw "Patch anchor not found: $Label"
    }
    return [regex]::Replace($Text, $Pattern, $Replacement, 1)
}

function Add-ImportAfter {
    param(
        [Parameter(Mandatory=$true)][string]$Text,
        [Parameter(Mandatory=$true)][string]$ExistingImport,
        [Parameter(Mandatory=$true)][string]$NewImport
    )
    if ($Text.Contains($NewImport)) { return $Text }
    if (!$Text.Contains($ExistingImport)) { throw "Import anchor not found: $ExistingImport" }
    return $Text.Replace($ExistingImport, $ExistingImport + "`r`n" + $NewImport)
}

Copy-SourceFile "src\main\java\live\aereth\fragmentengine\service\AbilityPlayerExperienceService.java"

$CommandPath = Join-Path $SourceRoot "src\main\java\live\aereth\fragmentengine\command\AerethCommand.java"
if (!(Test-Path $CommandPath)) { throw "AerethCommand.java not found: $CommandPath" }

$Text = Get-Content $CommandPath -Raw

# Imports. Keep this line-based because it survives CRLF/LF differences.
$Text = Add-ImportAfter $Text "import live.aereth.fragmentengine.service.AbilityActivationService;" "import live.aereth.fragmentengine.service.AbilityHotbarService;"
$Text = Add-ImportAfter $Text "import live.aereth.fragmentengine.service.AbilityHotbarService;" "import live.aereth.fragmentengine.service.AbilityPlayerExperienceService;"
$Text = Add-ImportAfter $Text "import live.aereth.fragmentengine.service.AbilityPlayerExperienceService;" "import live.aereth.fragmentengine.service.AbilityResourceService;"

# Fields.
if ($Text -notmatch "private final AbilityHotbarService abilityHotbar;") {
    $Text = Replace-RegexOnce $Text `
        "(?m)^    private final AbilityActivationGui abilityActivationGui;\r?$" `
        "    private final AbilityActivationGui abilityActivationGui;`r`n    private final AbilityHotbarService abilityHotbar;`r`n    private final AbilityPlayerExperienceService abilityPlayerExperience;" `
        "AerethCommand S4A fields"
}

# Constructor initialisation.
if ($Text -notmatch "this\.abilityPlayerExperience = new AbilityPlayerExperienceService") {
    $Pattern = "(?s)        this\.abilityActivation = new AbilityActivationService\(plugin, characters, disciplines, abilities\);\s+        this\.abilityActivationGui = new AbilityActivationGui\(plugin, characters, disciplines, abilities, abilityActivation\);"
    $Replacement = "        this.abilityActivation = new AbilityActivationService(plugin, characters, disciplines, abilities);`r`n        this.abilityHotbar = new AbilityHotbarService(plugin, characters, disciplines, abilities, abilityActivation);`r`n        this.abilityActivationGui = new AbilityActivationGui(plugin, characters, disciplines, abilities, abilityActivation);`r`n        this.abilityPlayerExperience = new AbilityPlayerExperienceService(abilities);"
    $Text = Replace-RegexOnce $Text $Pattern $Replacement "AerethCommand S4A constructor"
}

# Switch cases.
if ($Text -notmatch 'case "abilitysummary"') {
    $Pattern = "(?m)^                case \"abilitycooldowns\" -> abilityCooldowns\(sender, args\);\r?$"
    $Replacement = "                case \"abilitycooldowns\" -> abilityCooldowns(sender, args);`r`n                case \"abilitysummary\" -> abilitySummary(sender, args);`r`n                case \"abilityresources\" -> abilityResources(sender, args);`r`n                case \"abilitysync\" -> abilitySync(sender);"
    $Text = Replace-RegexOnce $Text $Pattern $Replacement "AerethCommand S4A switch cases"
}

# Help lines.
if ($Text -notmatch '/aereth abilitysummary') {
    $Pattern = "(?m)^        sender\.sendMessage\(Text\.color\(\"&b/aereth abilitycooldowns <player>\"\)\);\r?$"
    $Replacement = "        sender.sendMessage(Text.color(\"&b/aereth abilitycooldowns <player>\"));`r`n        sender.sendMessage(Text.color(\"&b/aereth abilitysummary <player>\"));`r`n        sender.sendMessage(Text.color(\"&b/aereth abilityresources <player>\"));`r`n        sender.sendMessage(Text.color(\"&b/aereth abilitysync\"));"
    $Text = Replace-RegexOnce $Text $Pattern $Replacement "AerethCommand S4A help lines"
}

$NewAddDisciplineXp = @'
    private void addDisciplineXp(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 3, "/aereth adddisciplinexp <player> <amount>");
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        long amount = parseLong(args[2], "amount");

        YamlConfiguration before = characters.getActiveCharacter(target);
        int oldRank = before == null ? 0 : disciplines.progress(before).rank();

        DisciplineService.DisciplineProgressResult result = disciplines.addDisciplineXp(target, amount);
        YamlConfiguration after = characters.getActiveCharacter(target);

        if (result.ranksGained() > 0) {
            sender.sendMessage(prefix() + Text.color("&dDiscipline rank up. &7Ranks gained: &f" + result.ranksGained()));
        }

        sender.sendMessage(prefix() + Text.color("&aDiscipline XP added. &7Rank: &f" + result.progress().rank()
                + " &8/ &7XP: &f" + result.progress().xp() + " / " + result.progress().xpRequired()));

        sendAbilityUnlockFeedback(sender, target, after, oldRank, result.progress().rank());
        syncAbilityHotbarIfOnline(target);
    }
'@

if ($Text -notmatch "sendAbilityUnlockFeedback\(sender, target, after") {
    $Pattern = "(?s)    private void addDisciplineXp\(CommandSender sender, String\[\] args\) throws IOException \{.*?\r?\n    private void setDisciplineRank"
    $Replacement = $NewAddDisciplineXp + "`r`n    private void setDisciplineRank"
    $Text = Replace-RegexOnce $Text $Pattern $Replacement "addDisciplineXp method"
}

$NewSetDisciplineRank = @'
    private void setDisciplineRank(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 3, "/aereth setdisciplinerank <player> <rank>");
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        int rank = parseInt(args[2], "rank");

        YamlConfiguration before = characters.getActiveCharacter(target);
        int oldRank = before == null ? 0 : disciplines.progress(before).rank();

        DisciplineService.DisciplineProgressResult result = disciplines.setDisciplineRank(target, rank);
        YamlConfiguration after = characters.getActiveCharacter(target);

        sender.sendMessage(prefix() + Text.color("&aDiscipline rank updated. &7Rank: &f" + result.progress().rank()
                + " &8/ &7Name: &f" + result.progress().rankName()));

        sendAbilityUnlockFeedback(sender, target, after, oldRank, result.progress().rank());
        syncAbilityHotbarIfOnline(target);
    }
'@

if ($Text -notmatch "sendAbilityUnlockFeedback\(sender, target, after, oldRank") {
    $Pattern = "(?s)    private void setDisciplineRank\(CommandSender sender, String\[\] args\) throws IOException \{.*?\r?\n    private void resetDisciplineProgress"
    $Replacement = $NewSetDisciplineRank + "`r`n    private void resetDisciplineProgress"
    $Text = Replace-RegexOnce $Text $Pattern $Replacement "setDisciplineRank method"
}

$NewCooldownAndExperienceMethods = @'
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

    private void abilitySummary(CommandSender sender, String[] args) {
        requireArgs(args, 2, "/aereth abilitysummary <player>");
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        YamlConfiguration character = characters.getActiveCharacter(target);
        if (character == null) {
            sender.sendMessage(prefix() + Text.color("&cNo active character."));
            return;
        }

        DisciplineService.DisciplineSummary discipline = disciplines.summary(character);
        DisciplineService.DisciplineProgressSummary progress = disciplines.progress(character);
        sender.sendMessage(prefix() + Text.color("&7Ability Summary: &b" + character.getString("name", target.getName())));
        for (String line : abilityPlayerExperience.summaryLines(character, discipline, progress)) {
            sender.sendMessage(Text.color(line));
        }
        sender.sendMessage(Text.color("&8Use /aereth abilityresources " + args[1] + " for current pools."));
    }

    private void abilityResources(CommandSender sender, String[] args) {
        requireArgs(args, 2, "/aereth abilityresources <player>");
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        YamlConfiguration character = characters.getActiveCharacter(target);
        if (character == null) {
            sender.sendMessage(prefix() + Text.color("&cNo active character."));
            return;
        }

        Player online = target.getPlayer();
        AbilityResourceService.ResourceSnapshot snapshot = abilityActivation.resourceSnapshot(character, online);
        sender.sendMessage(prefix() + Text.color("&7Ability Resources: &b" + character.getString("name", target.getName())));
        for (String line : abilityPlayerExperience.resourceLines(snapshot)) {
            sender.sendMessage(Text.color(line));
        }
        if (online == null || !online.isOnline()) {
            sender.sendMessage(Text.color("&8Health is only exact while the player is online."));
        }
    }

    private void abilitySync(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix() + Text.color("&cOnly players can sync their ability hotbar."));
            return;
        }
        abilityHotbar.sync(player, true);
    }

    private void sendAbilityUnlockFeedback(CommandSender sender, OfflinePlayer target, YamlConfiguration character, int oldRank, int newRank) {
        if (character == null || newRank <= oldRank) {
            return;
        }

        List<String> lines = abilityPlayerExperience.unlockLines(character, oldRank, newRank);
        sender.sendMessage(prefix() + Text.color("&dAbility unlock check: &7Rank &f" + oldRank + " &8-> &f" + newRank));
        for (String line : lines) {
            sender.sendMessage(Text.color(line));
        }

        Player online = target.getPlayer();
        if (online != null && online.isOnline() && online != sender) {
            online.sendMessage(prefix() + Text.color("&dYour Discipline rank revealed new ability information."));
            for (String line : lines) {
                online.sendMessage(Text.color(line));
            }
        }
    }

    private void syncAbilityHotbarIfOnline(OfflinePlayer target) {
        Player online = target.getPlayer();
        if (online == null || !online.isOnline()) {
            return;
        }
        abilityHotbar.sync(online, false);
        online.sendMessage(prefix() + Text.color("&7Ability hotbar refreshed after progression change."));
    }
'@

if ($Text -notmatch "private void abilitySummary") {
    $Pattern = "(?s)    private void abilityCooldowns\(CommandSender sender, String\[\] args\) \{.*?\r?\n    private void profile"
    $Replacement = $NewCooldownAndExperienceMethods + "`r`n    private void profile"
    $Text = Replace-RegexOnce $Text $Pattern $Replacement "abilityCooldowns experience methods block"
}

Set-Content -Path $CommandPath -Value $Text -Encoding UTF8

Write-Host "[Build S4A HOTFIX] Patched AerethCommand.java"
Write-Host "[Build S4A HOTFIX] Added ability summary, resources, sync, unlock feedback, and hotbar refresh hooks"
Write-Host "[Build S4A HOTFIX] Run mvn clean package next. No deployment until BUILD SUCCESS, because apparently we learn from our little disasters."
