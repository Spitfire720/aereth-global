package live.aereth.fragmentengine.service;

import live.aereth.fragmentengine.util.TimeUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AbilitySlotFrameworkService {
    private static final String BASE = "abilities.loadout";
    private static final int SLOT_COUNT = 4;

    private final AbilityService abilities;

    public AbilitySlotFrameworkService(AbilityService abilities) {
        this.abilities = abilities;
    }

    public ValidationResult sanitizeLoadout(YamlConfiguration character) {
        return validate(character, true);
    }

    public ValidationResult inspect(YamlConfiguration character) {
        return validate(character, false);
    }

    public int maxSlots(YamlConfiguration character) {
        boolean selected = character.getBoolean("discipline.selected", false);
        if (!selected) {
            return 0;
        }
        int rank = Math.max(0, character.getInt("discipline.progression.rank", 0));
        return Math.max(1, Math.min(SLOT_COUNT, rank));
    }

    public String slotPath(int slot) {
        return BASE + ".slots.slot" + slot;
    }

    public String slotValue(YamlConfiguration character, int slot) {
        if (slot < 1 || slot > SLOT_COUNT) {
            return "";
        }
        return normalize(character.getString(slotPath(slot), ""));
    }

    public void equip(YamlConfiguration character, int selectedSlot, String rawAbilityId) {
        String abilityId = normalize(rawAbilityId);
        if (selectedSlot < 1 || selectedSlot > SLOT_COUNT) {
            throw new IllegalArgumentException("Ability loadout slot must be between 1 and " + SLOT_COUNT + ".");
        }
        if (selectedSlot > maxSlots(character)) {
            throw new IllegalArgumentException("Ability loadout slot " + selectedSlot + " is locked.");
        }
        if (abilityId.isBlank() || !abilities.allAbilityIds().contains(abilityId)) {
            throw new IllegalArgumentException("Unknown ability: " + rawAbilityId);
        }
        AbilityService.AbilitySummary summary = abilities.summary(character);
        if (!summary.unlocked().contains(abilityId)) {
            throw new IllegalArgumentException("Ability is locked: " + abilities.displayName(abilityId));
        }

        for (int i = 1; i <= SLOT_COUNT; i++) {
            if (abilityId.equals(slotValue(character, i))) {
                character.set(slotPath(i), null);
            }
        }
        character.set(slotPath(selectedSlot), abilityId);
        sanitizeLoadout(character);
    }

    public void clear(YamlConfiguration character, int selectedSlot) {
        if (selectedSlot < 1 || selectedSlot > SLOT_COUNT) {
            throw new IllegalArgumentException("Ability loadout slot must be between 1 and " + SLOT_COUNT + ".");
        }
        character.set(slotPath(selectedSlot), null);
        sanitizeLoadout(character);
    }

    public void writeSummary(YamlConfiguration character, ValidationResult result) {
        List<String> active = new ArrayList<>();
        for (int i = 1; i <= SLOT_COUNT; i++) {
            String ability = slotValue(character, i);
            if (!ability.isBlank()) {
                active.add(ability);
            }
        }
        character.set(BASE + ".active", active);
        character.set(BASE + ".count", active.size());
        character.set(BASE + ".max-slots", SLOT_COUNT);
        character.set(BASE + ".framework.schema", "S4C-slot-framework");
        character.set(BASE + ".framework.checked-at", TimeUtil.nowIso());
        character.set(BASE + ".framework.status", result.status());
        character.set(BASE + ".framework.issues", result.issues());
        character.set(BASE + ".framework.cleaned-count", result.cleanedCount());
        character.set(BASE + ".framework.invalid-count", result.invalidCount());
        character.set(BASE + ".framework.duplicate-count", result.duplicateCount());
        character.set(BASE + ".framework.locked-count", result.lockedCount());
    }

    private ValidationResult validate(YamlConfiguration character, boolean mutate) {
        List<String> issues = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        int maxSlots = maxSlots(character);
        int cleaned = 0;
        int invalid = 0;
        int duplicate = 0;
        int locked = 0;
        int active = 0;

        AbilityService.AbilitySummary summary = abilities.summary(character);
        String selectedDiscipline = normalize(summary.discipline());

        for (int slot = 1; slot <= SLOT_COUNT; slot++) {
            String path = slotPath(slot);
            String original = character.getString(path, "");
            String abilityId = normalize(original);

            if (original != null && !original.isBlank() && !original.equals(abilityId)) {
                issues.add("slot" + slot + ": normalized '" + original + "' -> '" + abilityId + "'");
                cleaned++;
                if (mutate) {
                    character.set(path, abilityId);
                }
            }

            if (abilityId.isBlank()) {
                if (mutate) {
                    character.set(path, null);
                }
                continue;
            }

            if (slot > maxSlots) {
                issues.add("slot" + slot + ": cleared because slot is locked at current Discipline rank");
                locked++;
                cleaned++;
                if (mutate) {
                    character.set(path, null);
                }
                continue;
            }

            if (!abilities.allAbilityIds().contains(abilityId)) {
                issues.add("slot" + slot + ": cleared unknown ability id '" + abilityId + "'");
                invalid++;
                cleaned++;
                if (mutate) {
                    character.set(path, null);
                }
                continue;
            }

            AbilityService.AbilityDefinition definition = abilities.definition(abilityId);
            if (!definition.discipline().equalsIgnoreCase(selectedDiscipline)) {
                issues.add("slot" + slot + ": cleared ability from wrong Discipline '" + definition.discipline() + "'");
                invalid++;
                cleaned++;
                if (mutate) {
                    character.set(path, null);
                }
                continue;
            }

            if (!summary.unlocked().contains(abilityId)) {
                issues.add("slot" + slot + ": cleared locked ability '" + abilityId + "'");
                locked++;
                cleaned++;
                if (mutate) {
                    character.set(path, null);
                }
                continue;
            }

            if (seen.contains(abilityId)) {
                issues.add("slot" + slot + ": cleared duplicate ability '" + abilityId + "'");
                duplicate++;
                cleaned++;
                if (mutate) {
                    character.set(path, null);
                }
                continue;
            }

            seen.add(abilityId);
            active++;
        }

        String status = issues.isEmpty() ? "clean" : mutate ? "cleaned" : "issues_detected";
        ValidationResult result = new ValidationResult(status, maxSlots, active, cleaned, invalid, duplicate, locked, List.copyOf(issues));
        if (mutate) {
            writeSummary(character, result);
        }
        return result;
    }

    private String normalize(String raw) {
        return raw == null ? "" : raw.toLowerCase(Locale.ROOT).trim().replace(" ", "_").replace("-", "_");
    }

    public record ValidationResult(
            String status,
            int maxSlots,
            int activeCount,
            int cleanedCount,
            int invalidCount,
            int duplicateCount,
            int lockedCount,
            List<String> issues
    ) {
        public boolean changed() {
            return cleanedCount > 0;
        }

        public String compactLine() {
            if (issues.isEmpty()) {
                return "Slots clean. Active " + activeCount + "/" + maxSlots + ".";
            }
            return status + ": " + issues.size() + " issue" + (issues.size() == 1 ? "" : "s") + ", cleaned " + cleanedCount + ".";
        }
    }
}
