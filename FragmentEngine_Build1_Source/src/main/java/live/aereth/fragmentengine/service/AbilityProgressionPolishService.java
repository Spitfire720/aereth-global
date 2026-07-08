package live.aereth.fragmentengine.service;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AbilityProgressionPolishService {
    private final AbilityService abilities;

    public AbilityProgressionPolishService(AbilityService abilities) {
        this.abilities = abilities;
    }

    public ProgressionView view(YamlConfiguration character) {
        String discipline = normalize(character.getString("discipline.id", character.getString("abilities.by-discipline", "unformed")));
        boolean selected = character.getBoolean("discipline.selected", !discipline.equals("unformed"));
        int rank = Math.max(0, character.getInt("discipline.progression.rank", 0));
        long xp = Math.max(0L, character.getLong("discipline.progression.xp", 0L));
        long xpRequired = Math.max(0L, character.getLong("discipline.progression.xp-required", character.getLong("discipline.progression.xpRequired", 0L)));

        List<AbilityService.AbilityDefinition> definitions = definitionsFor(discipline);
        int unlocked = 0;
        AbilityService.AbilityDefinition next = null;

        for (AbilityService.AbilityDefinition definition : definitions) {
            if (selected && rank >= definition.unlockRank()) {
                unlocked++;
                continue;
            }

            if (next == null || definition.unlockRank() < next.unlockRank()) {
                next = definition;
            }
        }

        int total = definitions.size();
        double completion = total <= 0 ? 0.0 : (unlocked * 100.0) / total;
        int nextRank = next == null ? 0 : next.unlockRank();
        String nextId = next == null ? "none" : next.id();
        String nextDisplay = next == null ? "All abilities revealed" : next.display();
        int ranksAway = next == null ? 0 : Math.max(0, next.unlockRank() - rank);

        return new ProgressionView(
                discipline,
                selected,
                rank,
                xp,
                xpRequired,
                total,
                unlocked,
                total - unlocked,
                completion,
                nextRank,
                nextId,
                nextDisplay,
                ranksAway,
                unlockMap(definitions, rank, selected),
                stageLine(selected, rank, total, unlocked, next),
                nextLine(next, ranksAway)
        );
    }

    public AbilityState abilityState(YamlConfiguration character, AbilityService.AbilityDefinition definition) {
        ProgressionView view = view(character);
        boolean unlocked = view.selected() && view.rank() >= definition.unlockRank();
        int ranksAway = Math.max(0, definition.unlockRank() - view.rank());
        String milestone = unlocked
                ? "Revealed at Rank " + definition.unlockRank()
                : ranksAway == 1
                ? "Next rank unlock"
                : "Rank " + definition.unlockRank() + " unlock";
        String reveal = unlocked
                ? "This ability is available for loadout use."
                : ranksAway == 0
                ? "Select this Discipline to reveal it."
                : "Gain " + ranksAway + " Discipline rank" + (ranksAway == 1 ? "" : "s") + " to reveal.";
        String style = unlocked ? "&aUnlocked" : ranksAway == 1 ? "&eNext Unlock" : "&8Locked";

        return new AbilityState(definition.id(), definition.display(), unlocked, definition.unlockRank(), view.rank(), ranksAway, milestone, reveal, style);
    }

    public List<AbilityService.AbilityDefinition> definitionsFor(String rawDiscipline) {
        String discipline = normalize(rawDiscipline);
        List<AbilityService.AbilityDefinition> result = new ArrayList<>();
        for (AbilityService.AbilityDefinition definition : abilities.allDefinitions()) {
            if (definition.discipline().equalsIgnoreCase(discipline)) {
                result.add(definition);
            }
        }
        result.sort((a, b) -> {
            int rankCompare = Integer.compare(a.unlockRank(), b.unlockRank());
            return rankCompare != 0 ? rankCompare : a.id().compareTo(b.id());
        });
        return result;
    }

    public List<AbilityService.AbilityDefinition> unlocksAtRank(String rawDiscipline, int rank) {
        List<AbilityService.AbilityDefinition> result = new ArrayList<>();
        for (AbilityService.AbilityDefinition definition : definitionsFor(rawDiscipline)) {
            if (definition.unlockRank() == rank) {
                result.add(definition);
            }
        }
        return result;
    }

    private String unlockMap(List<AbilityService.AbilityDefinition> definitions, int rank, boolean selected) {
        if (definitions.isEmpty()) {
            return "No ability path defined.";
        }

        List<String> parts = new ArrayList<>();
        for (AbilityService.AbilityDefinition definition : definitions) {
            String prefix = selected && rank >= definition.unlockRank() ? "✓" : "R" + definition.unlockRank();
            parts.add(prefix + " " + compact(definition.display(), 16));
        }
        return String.join(" | ", parts);
    }

    private String stageLine(boolean selected, int rank, int total, int unlocked, AbilityService.AbilityDefinition next) {
        if (!selected) {
            return "No Discipline selected.";
        }
        if (total <= 0) {
            return "This Discipline has no ability path yet.";
        }
        if (unlocked >= total) {
            return "Ability path fully revealed.";
        }
        if (rank <= 0) {
            return "Commit to the Discipline to begin revealing abilities.";
        }
        return "Next reveal: " + next.display() + " at Rank " + next.unlockRank() + ".";
    }

    private String nextLine(AbilityService.AbilityDefinition next, int ranksAway) {
        if (next == null) {
            return "All ability reveals complete.";
        }
        if (ranksAway <= 0) {
            return next.display() + " is ready to reveal.";
        }
        return next.display() + " unlocks in " + ranksAway + " rank" + (ranksAway == 1 ? "" : "s") + ".";
    }

    private String compact(String text, int max) {
        if (text == null || text.isBlank()) {
            return "Unnamed";
        }
        return text.length() <= max ? text : text.substring(0, Math.max(0, max - 3)) + "...";
    }

    private String normalize(String raw) {
        return raw == null ? "unformed" : raw.toLowerCase(Locale.ROOT).trim().replace(" ", "_").replace("-", "_");
    }

    public record ProgressionView(
            String discipline,
            boolean selected,
            int rank,
            long xp,
            long xpRequired,
            int totalAbilities,
            int unlockedAbilities,
            int lockedAbilities,
            double completionPercent,
            int nextUnlockRank,
            String nextUnlockId,
            String nextUnlockDisplay,
            int ranksAway,
            String unlockMap,
            String stageLine,
            String nextLine
    ) {
    }

    public record AbilityState(
            String abilityId,
            String display,
            boolean unlocked,
            int unlockRank,
            int currentRank,
            int ranksAway,
            String milestone,
            String revealLine,
            String style
    ) {
    }
}
