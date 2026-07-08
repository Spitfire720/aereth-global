$ErrorActionPreference = "Stop"

Write-Host "[Build S4A] Applying Player Ability Experience Bundle"

$Root = Get-Location
$SourceRoot = Join-Path $Root "FragmentEngine_Build1_Source"
$ImplRoot = Join-Path $Root "implementation-files\FragmentEngine_Build1_Source"

if (!(Test-Path $SourceRoot)) {
    throw "FragmentEngine_Build1_Source not found. Run this from C:\Users\Bernardo\Desktop\Aereth global"
}

function Copy-SourceFile {
    param(
        [Parameter(Mandatory=$true)][string]$RelativePath
    )
    $From = Join-Path $ImplRoot $RelativePath
    $To = Join-Path $SourceRoot $RelativePath
    if (!(Test-Path $From)) { throw "Missing implementation file: $From" }
    New-Item -ItemType Directory -Force -Path (Split-Path $To -Parent) | Out-Null
    Copy-Item $From $To -Force
    Write-Host "[Build S4A] Copied $RelativePath"
}

function Replace-Once {
    param(
        [Parameter(Mandatory=$true)][string]$Text,
        [Parameter(Mandatory=$true)][string]$Old,
        [Parameter(Mandatory=$true)][string]$New,
        [Parameter(Mandatory=$true)][string]$Label
    )
    if (!$Text.Contains($Old)) {
        throw "Patch anchor not found: $Label"
    }
    return $Text.Replace($Old, $New)
}

Copy-SourceFile "src\main\java\live\aereth\fragmentengine\service\AbilityPlayerExperienceService.java"

$CommandPath = Join-Path $SourceRoot "src\main\java\live\aereth\fragmentengine\command\AerethCommand.java"
if (!(Test-Path $CommandPath)) { throw "AerethCommand.java not found: $CommandPath" }

$Text = Get-Content $CommandPath -Raw

if ($Text -notmatch "AbilityPlayerExperienceService") {
    $Text = Replace-Once $Text `
        "import live.aereth.fragmentengine.service.AbilityActivationService;" `
        "import live.aereth.fragmentengine.service.AbilityActivationService;`r`nimport live.aereth.fragmentengine.service.AbilityHotbarService;`r`nimport live.aereth.fragmentengine.service.AbilityPlayerExperienceService;" `
        "AerethCommand imports"
}

if ($Text -notmatch "abilityPlayerExperience") {
    $Text = Replace-Once $Text `
        "    private final AbilityActivationGui abilityActivationGui;" `
        "    private final AbilityActivationGui abilityActivationGui;`r`n    private final AbilityHotbarService abilityHotbar;`r`n    private final AbilityPlayerExperienceService abilityPlayerExperience;" `
        "AerethCommand ability experience fields"
}

if ($Text -notmatch "new AbilityHotbarService") {
    $Text = Replace-Once $Text `
        "        this.abilityActivation = new AbilityActivationService(plugin, characters, disciplines, abilities);`r`n        this.abilityActivationGui = new AbilityActivationGui(plugin, characters, disciplines, abilities, abilityActivation);" `
        "        this.abilityActivation = new AbilityActivationService(plugin, characters, disciplines, abilities);`r`n        this.abilityHotbar = new AbilityHotbarService(plugin, characters, disciplines, abilities, abilityActivation);`r`n        this.abilityActivationGui = new AbilityActivationGui(plugin, characters, disciplines, abilities, abilityActivation);`r`n        this.abilityPlayerExperience = new AbilityPlayerExperienceService(abilities);" `
        "AerethCommand ability experience constructor"
}

if ($Text -notmatch 'case "abilitysummary"') {
    $Text = Replace-Once $Text `
        "                case ""abilitycooldowns"" -> abilityCooldowns(sender, args);`r`n                case ""abilitygui"" -> abilityGui(sender);" `
        "                case ""abilitycooldowns"" -> abilityCooldowns(sender, args);`r`n                case ""abilitysummary"" -> abilitySummary(sender, args);`r`n                case ""abilityresources"" -> abilityResources(sender, args);`r`n                case ""abilitysync"" -> abilitySync(sender);`r`n                case ""abilitygui"" -> abilityGui(sender);" `
        "AerethCommand ability experience switch cases"
}

if ($Text -notmatch '/aereth abilitysummary') {
    $Text = Replace-Once $Text `
        "        sender.sendMessage(Text.color(""&b/aereth abilitycooldowns <player>""));`r`n        sender.sendMessage(Text.color(""&b/aereth abilitygui""));" `
        "        sender.sendMessage(Text.color(""&b/aereth abilitycooldowns <player>""));`r`n        sender.sendMessage(Text.color(""&b/aereth abilitysummary <player>""));`r`n        sender.sendMessage(Text.color(""&b/aereth abilityresources <player>""));`r`n        sender.sendMessage(Text.color(""&b/aereth abilitysync""));`r`n        sender.sendMessage(Text.color(""&b/aereth abilitygui""));" `
        "AerethCommand ability experience help lines"
}

$OldAddDisciplineXp = @'
    private void addDisciplineXp(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 3, "/aereth adddisciplinexp <player> <amount>");
        long amount = parseLong(args[2], "amount");

        DisciplineService.DisciplineProgressResult result = disciplines.addDisciplineXp(Bukkit.getOfflinePlayer(args[1]), amount);

        if (result.ranksGained() > 0) {
            sender.sendMessage(prefix() + Text.color("&dDiscipline rank up. &7Ranks gained: &f" + result.ranksGained()));
        }

        sender.sendMessage(prefix() + Text.color("&aDiscipline XP added. &7Rank: &f" + result.progress().rank()
                + " &8/ &7XP: &f" + result.progress().xp() + " / " + result.progress().xpRequired()));
    }
'@
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
if ($Text.Contains($OldAddDisciplineXp)) {
    $Text = $Text.Replace($OldAddDisciplineXp, $NewAddDisciplineXp)
} elseif ($Text -notmatch "sendAbilityUnlockFeedback\(sender, target, after") {
    throw "Patch anchor not found: addDisciplineXp method"
}

$OldSetDisciplineRank = @'
    private void setDisciplineRank(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 3, "/aereth setdisciplinerank <player> <rank>");
        int rank = parseInt(args[2], "rank");

        DisciplineService.DisciplineProgressResult result = disciplines.setDisciplineRank(Bukkit.getOfflinePlayer(args[1]), rank);
        sender.sendMessage(prefix() + Text.color("&aDiscipline rank updated. &7Rank: &f" + result.progress().rank()
                + " &8/ &7Name: &f" + result.progress().rankName()));
    }
'@
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
if ($Text.Contains($OldSetDisciplineRank)) {
    $Text = $Text.Replace($OldSetDisciplineRank, $NewSetDisciplineRank)
} elseif ($Text -notmatch "sendAbilityUnlockFeedback\(sender, target, after, oldRank") {
    throw "Patch anchor not found: setDisciplineRank method"
}

if ($Text -notmatch "private void abilitySummary") {
    $OldCooldownMethod = @'
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
    if (!$Text.Contains($OldCooldownMethod)) {
        throw "Patch anchor not found: abilityCooldowns method"
    }
    $Text = $Text.Replace($OldCooldownMethod, $NewCooldownAndExperienceMethods)
}

Set-Content -Path $CommandPath -Value $Text -Encoding UTF8

Write-Host "[Build S4A] Patched AerethCommand.java"
Write-Host "[Build S4A] Added ability summary, resources, sync, unlock feedback, and hotbar refresh hooks"
Write-Host "[Build S4A] Build S4A local patch applied. Run mvn clean package next."
