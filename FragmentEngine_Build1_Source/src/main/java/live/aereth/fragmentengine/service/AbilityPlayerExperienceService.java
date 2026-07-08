package live.aereth.fragmentengine.service;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AbilityPlayerExperienceService {
    private final AbilityService abilities;
    private final AbilityProgressionPolishService polish;

    public AbilityPlayerExperienceService(AbilityService abilities) {
        this.abilities = abilities;
        this.polish = new AbilityProgressionPolishService(abilities);
    }

    public List<String> summaryLines(YamlConfiguration character, DisciplineService.DisciplineSummary discipline,
                                     DisciplineService.DisciplineProgressSummary progress) {
        AbilityService.AbilitySummary summary = abilities.summary(character);
        AbilityProgressionPolishService.ProgressionView view = polish.view(character);
        List<String> lines = new ArrayList<>();

        lines.add("&7Character: &b" + character.getString("name", "Unnamed"));
        lines.add("&7Discipline: &f" + discipline.display() + " &8(" + discipline.id() + ")");
        lines.add("&7Rank: &f" + progress.rank() + " &8/ &f" + progress.rankName()
                + " &8| &7XP: &f" + progress.xp() + " / " + progress.xpRequired());
        lines.add("&7Ability Path: &f" + view.unlockedAbilities() + "&8/&f" + view.totalAbilities()
                + " &8(" + round(view.completionPercent()) + "%)");
        lines.add("&7Unlocked: &a" + readable(summary.unlocked()));
        lines.add("&7Locked: &8" + readable(summary.locked()));
        lines.add("&7Next Reveal: &d" + view.nextLine());
        lines.add("&7Roadmap: &f" + view.unlockMap());
        return lines;
    }

    public List<String> resourceLines(AbilityResourceService.ResourceSnapshot snapshot) {
        List<String> lines = new ArrayList<>();
        lines.add("&7Stamina: &f" + snapshot.stamina().line() + " &8(+" + round(snapshot.stamina().regenPerSecond()) + "/s)");
        lines.add("&7Mana: &f" + snapshot.mana().line() + " &8(+" + round(snapshot.mana().regenPerSecond()) + "/s)");
        lines.add("&7Focus: &f" + snapshot.focus().line() + " &8(+" + round(snapshot.focus().regenPerSecond()) + "/s)");
        lines.add("&7Instability: &f" + snapshot.instability().line() + " &8(+" + round(snapshot.instability().regenPerSecond()) + "/s)");
        lines.add("&7Health: &f" + snapshot.health().line());
        return lines;
    }

    public List<String> unlockLines(YamlConfiguration character, int oldRank, int newRank) {
        String discipline = normalize(character.getString("discipline.id", character.getString("abilities.by-discipline", "unformed")));
        List<String> lines = new ArrayList<>();

        if (newRank <= oldRank) {
            return lines;
        }

        for (AbilityService.AbilityDefinition definition : polish.definitionsFor(discipline)) {
            if (definition.unlockRank() > oldRank && definition.unlockRank() <= newRank) {
                lines.add("&dAbility Unlocked: &f" + definition.display()
                        + " &8[Rank " + definition.unlockRank() + "]"
                        + " &7Cost: &f" + readableCost(definition)
                        + " &7Cooldown: &f" + round(definition.cooldownSeconds()) + "s");
            }
        }

        if (lines.isEmpty()) {
            lines.add("&7No new ability unlocks at this rank. The void applauds politely.");
        }

        return lines;
    }

    public List<String> rankRoadmapLines(YamlConfiguration character) {
        AbilityProgressionPolishService.ProgressionView view = polish.view(character);
        List<String> lines = new ArrayList<>();
        lines.add("&7Completion: &f" + round(view.completionPercent()) + "%");
        lines.add("&7Unlocked: &f" + view.unlockedAbilities() + " &8/ &f" + view.totalAbilities());
        lines.add("&7Next: &d" + view.nextLine());
        lines.add("&7Roadmap: &f" + view.unlockMap());
        return lines;
    }

    private String readable(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "none";
        }
        return String.join(", ", values);
    }

    private String readableCost(AbilityService.AbilityDefinition definition) {
        if (definition.costType() == null || definition.costType().isBlank() || definition.costType().equalsIgnoreCase("none")) {
            return "None";
        }
        return round(definition.costAmount()) + " " + definition.costType();
    }

    private String normalize(String raw) {
        return raw == null ? "unformed" : raw.toLowerCase(Locale.ROOT).trim().replace(" ", "_").replace("-", "_");
    }

    private String round(double value) {
        return String.format(Locale.US, "%.1f", value);
    }
}
