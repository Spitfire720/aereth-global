$ErrorActionPreference = "Stop"

Write-Host "[Build S4A HOTFIX v2] Applying Player Ability Experience Bundle"

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
    Write-Host "[Build S4A HOTFIX v2] Copied $RelativePath"
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

# Imports.
$Text = Add-ImportAfter $Text "import live.aereth.fragmentengine.service.AbilityActivationService;" "import live.aereth.fragmentengine.service.AbilityHotbarService;"
$Text = Add-ImportAfter $Text "import live.aereth.fragmentengine.service.AbilityHotbarService;" "import live.aereth.fragmentengine.service.AbilityPlayerExperienceService;"
$Text = Add-ImportAfter $Text "import live.aereth.fragmentengine.service.AbilityPlayerExperienceService;" "import live.aereth.fragmentengine.service.AbilityResourceService;"

# Fields.
if ($Text -notmatch 'private final AbilityHotbarService abilityHotbar;') {
    $FieldReplacement = @'
    private final AbilityActivationGui abilityActivationGui;
    private final AbilityHotbarService abilityHotbar;
    private final AbilityPlayerExperienceService abilityPlayerExperience;
'@
    $FieldReplacement = $FieldReplacement.TrimEnd()
    $Text = Replace-RegexOnce $Text '(?m)^    private final AbilityActivationGui abilityActivationGui;\r?$' $FieldReplacement 'AerethCommand S4A fields'
}

# Constructor initialisation.
if ($Text -notmatch 'this\.abilityPlayerExperience = new AbilityPlayerExperienceService') {
    $ConstructorReplacement = @'
        this.abilityActivation = new AbilityActivationService(plugin, characters, disciplines, abilities);
        this.abilityHotbar = new AbilityHotbarService(plugin, characters, disciplines, abilities, abilityActivation);
        this.abilityActivationGui = new AbilityActivationGui(plugin, characters, disciplines, abilities, abilityActivation);
        this.abilityPlayerExperience = new AbilityPlayerExperienceService(abilities);
'@
    $ConstructorReplacement = $ConstructorReplacement.TrimEnd()
    $Text = Replace-RegexOnce $Text '(?s)        this\.abilityActivation = new AbilityActivationService\(plugin, characters, disciplines, abilities\);\s+        this\.abilityActivationGui = new AbilityActivationGui\(plugin, characters, disciplines, abilities, abilityActivation\);' $ConstructorReplacement 'AerethCommand S4A constructor'
}

# Switch cases.
if ($Text -notmatch 'case "abilitysummary"') {
    $SwitchReplacement = @'
                case "abilitycooldowns" -> abilityCooldowns(sender, args);
                case "abilitysummary" -> abilitySummary(sender, args);
                case "abilityresources" -> abilityResources(sender, args);
                case "abilitysync" -> abilitySync(sender);
'@
    $SwitchReplacement = $SwitchReplacement.TrimEnd()
    $Text = Replace-RegexOnce $Text '(?m)^                case "abilitycooldowns" -> abilityCooldowns\(sender, args\);\r?$' $SwitchReplacement 'AerethCommand S4A switch cases'
}

# Help lines.
if ($Text -notmatch '/aereth abilitysummary') {
    $HelpReplacement = @'
        sender.sendMessage(Text.color("&b/aereth abilitycooldowns <player>"));
        sender.sendMessage(Text.color("&b/aereth abilitysummary <player>"));
        sender.sendMessage(Text.color("&b/aereth abilityresources <player>"));
        sender.sendMessage(Text.color("&b/aereth abilitysync"));
'@
    $HelpReplacement = $HelpReplacement.TrimEnd()
    $Text = Replace-RegexOnce $Text '(?m)^        sender\.sendMessage\(Text\.color\("&b/aereth abilitycooldowns <player>"\)\);\r?$' $HelpReplacement 'AerethCommand S4A help lines'
}

# Replace addDisciplineXp with unlock feedback + hotbar sync.
$AddDisciplineXpReplacement = @'
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
$AddDisciplineXpReplacement = $AddDisciplineXpReplacement.TrimEnd()
if ($Text -notmatch 'sendAbilityUnlockFeedback\(sender, target, after, oldRank, result\.progress\(\)\.rank\(\)\);') {
    $Text = Replace-RegexOnce $Text '(?s)    private void addDisciplineXp\(CommandSender sender, String\[\] args\) throws IOException \{.*?\r?\n    private void setDisciplineRank' ($AddDisciplineXpReplacement + "`r`n`r`n    private void setDisciplineRank") 'addDisciplineXp method'
}

# Replace setDisciplineRank with unlock feedback + hotbar sync.
$SetDisciplineRankReplacement = @'
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
$SetDisciplineRankReplacement = $SetDisciplineRankReplacement.TrimEnd()
if ($Text -notmatch 'sendAbilityUnlockFeedback\(sender, target, after, oldRank, result\.progress\(\)\.rank\(\)\);\s+syncAbilityHotbarIfOnline\(target\);\s+\}\s+private void resetDisciplineProgress') {
    $Text = Replace-RegexOnce $Text '(?s)    private void setDisciplineRank\(CommandSender sender, String\[\] args\) throws IOException \{.*?\r?\n    private void resetDisciplineProgress' ($SetDisciplineRankReplacement + "`r`n`r`n    private void resetDisciplineProgress") 'setDisciplineRank method'
}

# Replace cooldown method block and insert new commands/helper methods before profile.
if ($Text -notmatch 'private void abilitySummary\(CommandSender sender, String\[\] args\)') {
    $AbilityExperienceMethods = @'
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
            sender.sendMessage(Text.color("&8Health is only accurate while the player is online."));
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

        sender.sendMessage(prefix() + Text.color("&dAbility unlock recap for &f" + character.getString("name", target.getName())
                + " &8[Rank " + oldRank + " -> " + newRank + "]"));
        for (String line : abilityPlayerExperience.unlockLines(character, oldRank, newRank)) {
            sender.sendMessage(Text.color(line));
        }
        for (String line : abilityPlayerExperience.rankRoadmapLines(character)) {
            sender.sendMessage(Text.color(line));
        }
    }

    private void syncAbilityHotbarIfOnline(OfflinePlayer target) {
        Player player = target.getPlayer();
        if (player == null || !player.isOnline()) {
            return;
        }
        abilityHotbar.sync(player, false);
        player.sendMessage(prefix() + Text.color("&aAbility hotbar refreshed after Discipline rank change."));
    }
'@
    $AbilityExperienceMethods = $AbilityExperienceMethods.TrimEnd()
    $Text = Replace-RegexOnce $Text '(?s)    private void abilityCooldowns\(CommandSender sender, String\[\] args\) \{.*?\r?\n    private void profile' ($AbilityExperienceMethods + "`r`n`r`n    private void profile") 'ability experience command methods'
}

Set-Content -Path $CommandPath -Value $Text -Encoding UTF8
Write-Host "[Build S4A HOTFIX v2] Patched AerethCommand.java"

# Copy docs when present.
$Docs = @(
    "server-design\systems-ui\Build_S4A_Player_Ability_Experience_Bundle_HOTFIX_v2.md",
    "server-design\testing\fragmentengine\Build_S4A_Player_Ability_Experience_Bundle_HOTFIX_v2_Test.md"
)
foreach ($Doc in $Docs) {
    $From = Join-Path $Root $Doc
    if (Test-Path $From) {
        $To = Join-Path $Root $Doc
        New-Item -ItemType Directory -Force -Path (Split-Path $To -Parent) | Out-Null
    }
}

Write-Host "[Build S4A HOTFIX v2] Local patch applied. Now run mvn clean package. No deployment until BUILD SUCCESS."
