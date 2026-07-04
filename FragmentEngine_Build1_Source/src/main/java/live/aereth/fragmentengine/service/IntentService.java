package live.aereth.fragmentengine.service;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class IntentService {
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

        return new IntentSummary(maxSlots, slots.size(), primary, pressure, stabilityImpact, slots);
    }

    public IntentResult setIntent(OfflinePlayer player, String rawSlot, String rawIntent) throws IOException {
        YamlConfiguration character = activeCharacter(player);
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

        character.set("intent.active." + slotKey, intent);
        writeSummaryFields(character);
        saveCharacter(player, character);

        return new IntentResult(slotKey, intent, "set", summary(character));
    }

    public IntentResult clearIntent(OfflinePlayer player, String rawSlot) throws IOException {
        YamlConfiguration character = activeCharacter(player);
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
        return Math.max(1, Math.max(stored, levelBased));
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
}