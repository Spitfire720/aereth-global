package live.aereth.fragmentengine.service;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IntentService {
    private static final int HARD_MAX_SLOTS = 4;

    private final JavaPlugin plugin;
    private final CharacterService characters;
    private final YamlConfiguration definitions;

    public IntentService(JavaPlugin plugin, CharacterService characters) {
        this.plugin = plugin;
        this.characters = characters;
        this.definitions = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "intents.yml"));
    }

    public List<String> allIntentIds() {
        ConfigurationSection section = definitions.getConfigurationSection("intents");
        if (section == null) {
            return List.of("anchor", "fracture", "distortion", "memory", "null");
        }
        return new ArrayList<>(section.getKeys(false));
    }

    public boolean isKnownIntent(String raw) {
        String id = normalizeIntent(raw);
        return allIntentIds().contains(id);
    }

    public String displayName(String raw) {
        String id = normalizeIntent(raw);
        return definitions.getString("intents." + id + ".display", title(id));
    }

    public IntentDefinition definition(String raw) {
        String id = normalizeIntent(raw);
        return new IntentDefinition(
                id,
                displayName(id),
                definitions.getString("intents." + id + ".family", id),
                definitions.getDouble("intents." + id + ".pressure", 0.0),
                definitions.getDouble("intents." + id + ".stability-impact", 0.0),
                definitions.getString("intents." + id + ".description", "")
        );
    }

    public IntentSummary summary(YamlConfiguration character) {
        IntentSanitizationResult sanitation = sanitizeIntentState(character);
        int maxSlots = maxSlots(character);
        Map<String, String> slots = new LinkedHashMap<>();

        for (int i = 1; i <= maxSlots; i++) {
            String slotKey = slotKey(i);
            String intent = normalizeIntent(character.getString("intent.active." + slotKey, ""));
            if (!intent.isBlank() && isKnownIntent(intent)) {
                slots.put(slotKey, intent);
            }
        }

        double pressure = 0.0;
        double stabilityImpact = 0.0;
        String primary = "none";

        for (String intent : slots.values()) {
            IntentDefinition definition = definition(intent);
            pressure += definition.pressure();
            stabilityImpact += definition.stabilityImpact();
            if (primary.equals("none")) {
                primary = intent;
            }
        }

        character.set("intent.framework.last-summary-status", sanitation.changed() ? "cleaned_for_summary" : "clean");
        return new IntentSummary(maxSlots, slots.size(), primary, pressure, stabilityImpact, slots);
    }

    public IntentResult setIntent(OfflinePlayer player, String rawSlot, String rawIntent) throws IOException {
        YamlConfiguration character = activeCharacter(player);
        sanitizeIntentState(character);

        int slot = parseSlot(rawSlot);
        String slotKey = slotKey(slot);
        String intent = normalizeIntent(rawIntent);

        if (intent.equals("none") || intent.equals("clear")) {
            return clearIntent(player, rawSlot);
        }

        if (!isKnownIntent(intent)) {
            throw new IllegalArgumentException("Unknown intent: " + rawIntent);
        }

        int maxSlots = maxSlots(character);
        if (slot < 1 || slot > maxSlots) {
            throw new IllegalArgumentException("Slot " + slot + " is locked. Max slots: " + maxSlots);
        }

        // Intent slots represent active states, not inventory storage. Duplicate states are noise.
        for (int i = 1; i <= maxSlots; i++) {
            String otherKey = slotKey(i);
            if (!otherKey.equals(slotKey) && intent.equals(normalizeIntent(character.getString("intent.active." + otherKey, "")))) {
                character.set("intent.active." + otherKey, null);
            }
        }

        character.set("intent.active." + slotKey, intent);
        writeSummaryFields(character);
        saveCharacter(player, character);

        return new IntentResult(slotKey, intent, "set", summary(character));
    }

    public IntentResult clearIntent(OfflinePlayer player, String rawSlot) throws IOException {
        YamlConfiguration character = activeCharacter(player);
        sanitizeIntentState(character);

        int slot = parseSlot(rawSlot);
        String slotKey = slotKey(slot);

        int maxSlots = maxSlots(character);
        if (slot < 1 || slot > maxSlots) {
            throw new IllegalArgumentException("Slot " + slot + " is locked. Max slots: " + maxSlots);
        }

        character.set("intent.active." + slotKey, null);
        writeSummaryFields(character);
        saveCharacter(player, character);

        return new IntentResult(slotKey, "none", "cleared", summary(character));
    }

    public IntentRepairResult repair(OfflinePlayer player) throws IOException {
        YamlConfiguration character = activeCharacter(player);
        IntentSanitizationResult sanitation = sanitizeIntentState(character);
        writeSummaryFields(character);
        saveCharacter(player, character);
        return new IntentRepairResult(sanitation.changed(), sanitation.issues(), summary(character));
    }

    public IntentSanitizationResult sanitizeIntentState(YamlConfiguration character) {
        int maxSlots = maxSlots(character);
        Set<String> seen = new LinkedHashSet<>();
        List<String> issues = new ArrayList<>();
        int invalid = 0;
        int duplicate = 0;
        int locked = 0;
        int normalized = 0;
        boolean changed = false;

        if (!character.contains("intent.active")) {
            character.createSection("intent.active");
            changed = true;
            issues.add("Created missing intent.active section.");
        }

        for (int i = 1; i <= HARD_MAX_SLOTS; i++) {
            String slotKey = slotKey(i);
            String path = "intent.active." + slotKey;
            String raw = character.getString(path, "");
            String intent = normalizeIntent(raw);

            if (i > maxSlots) {
                if (!intent.isBlank()) {
                    character.set(path, null);
                    locked++;
                    changed = true;
                    issues.add(slotKey + " cleared because the slot is locked.");
                }
                continue;
            }

            if (intent.isBlank()) {
                continue;
            }

            if (!isKnownIntent(intent)) {
                character.set(path, null);
                invalid++;
                changed = true;
                issues.add(slotKey + " cleared unknown intent: " + raw);
                continue;
            }

            if (seen.contains(intent)) {
                character.set(path, null);
                duplicate++;
                changed = true;
                issues.add(slotKey + " cleared duplicate intent: " + intent);
                continue;
            }

            seen.add(intent);

            if (!intent.equals(raw)) {
                character.set(path, intent);
                normalized++;
                changed = true;
                issues.add(slotKey + " normalized to " + intent + ".");
            }
        }

        character.set("intent.framework.schema", "S5D-intent-runtime-hardening");
        character.set("intent.framework.status", changed ? "cleaned" : "clean");
        character.set("intent.framework.issues", issues);
        character.set("intent.framework.cleaned-count", invalid + duplicate + locked + normalized);
        character.set("intent.framework.invalid-count", invalid);
        character.set("intent.framework.duplicate-count", duplicate);
        character.set("intent.framework.locked-count", locked);
        character.set("intent.framework.normalized-count", normalized);

        return new IntentSanitizationResult(changed, issues, invalid, duplicate, locked, normalized);
    }

    public void writeSummaryFields(YamlConfiguration character) {
        IntentSummary summary = summary(character);
        character.set("intent.unlocked-slots", summary.maxSlots());
        character.set("intent.slots-used", summary.usedSlots());
        character.set("intent.primary", summary.primary());
        character.set("intent.pressure", summary.pressure());
        character.set("intent.stability-impact", summary.stabilityImpact());
    }

    private YamlConfiguration activeCharacter(OfflinePlayer player) {
        YamlConfiguration character = characters.getActiveCharacter(player);
        if (character == null) {
            throw new IllegalStateException("No active character.");
        }
        return character;
    }

    private void saveCharacter(OfflinePlayer player, YamlConfiguration character) throws IOException {
        int slot = character.getInt("slot", characters.getActiveSlot(player));
        characters.storage().saveCharacter(player.getUniqueId(), slot, character);
    }

    private int maxSlots(YamlConfiguration character) {
        int level = character.getInt("progression.level", 1);
        int levelBased = 1;

        if (level >= 51) {
            levelBased = 4;
        } else if (level >= 36) {
            levelBased = 3;
        } else if (level >= 16) {
            levelBased = 2;
        }

        int stored = character.getInt("intent.unlocked-slots", plugin.getConfig().getInt("intent.starting-slots", 1));
        return Math.max(1, Math.min(HARD_MAX_SLOTS, Math.max(stored, levelBased)));
    }

    private int parseSlot(String raw) {
        String clean = raw == null ? "" : raw.toLowerCase().replace("slot", "").trim();
        try {
            return Integer.parseInt(clean);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid intent slot: " + raw);
        }
    }

    private String slotKey(int slot) {
        return "slot" + slot;
    }

    private String normalizeIntent(String raw) {
        return raw == null ? "" : raw.toLowerCase().trim().replace(" ", "_").replace("-", "_");
    }

    private String title(String id) {
        if (id == null || id.isBlank()) {
            return "None";
        }
        String clean = id.replace("_", " ");
        return clean.substring(0, 1).toUpperCase() + clean.substring(1);
    }

    public record IntentDefinition(
            String id,
            String display,
            String family,
            double pressure,
            double stabilityImpact,
            String description
    ) {
    }

    public record IntentSummary(
            int maxSlots,
            int usedSlots,
            String primary,
            double pressure,
            double stabilityImpact,
            Map<String, String> slots
    ) {
    }

    public record IntentResult(
            String slot,
            String intentId,
            String status,
            IntentSummary summary
    ) {
    }

    public record IntentSanitizationResult(
            boolean changed,
            List<String> issues,
            int invalidCount,
            int duplicateCount,
            int lockedCount,
            int normalizedCount
    ) {
    }

    public record IntentRepairResult(
            boolean changed,
            List<String> changes,
            IntentSummary summary
    ) {
    }
}
