package live.aereth.fragmentengine.service;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;

public class CharacterIdentityService {
    private final JavaPlugin plugin;
    private final CharacterService characters;
    private final FragmentService fragments;
    private final IntentService intents;

    public CharacterIdentityService(JavaPlugin plugin, CharacterService characters, FragmentService fragments, IntentService intents) {
        this.plugin = plugin;
        this.characters = characters;
        this.fragments = fragments;
        this.intents = intents;
    }

    public IdentitySummary summary(YamlConfiguration character) {
        if (character == null) {
            throw new IllegalStateException("No active character.");
        }

        fragments.ensureFragmentState(character);
        intents.writeSummaryFields(character);

        FragmentService.FragmentSummary fragmentSummary = fragments.summary(character);
        IntentService.IntentSummary intentSummary = intents.summary(character);
        IdentitySummary identity = buildSummary(character, fragmentSummary, intentSummary);
        writeIdentityFields(character, identity);
        return identity;
    }

    public IdentitySummary sync(OfflinePlayer player) throws IOException {
        YamlConfiguration character = characters.getActiveCharacter(player);
        if (character == null) {
            throw new IllegalStateException("No active character.");
        }

        IdentitySummary summary = summary(character);
        int slot = character.getInt("slot", characters.getActiveSlot(player));
        characters.storage().saveCharacter(player.getUniqueId(), slot, character);
        return summary;
    }

    public SyncResult syncSilently(OfflinePlayer player) {
        try {
            IdentitySummary summary = sync(player);
            return new SyncResult(true, "synced", summary);
        } catch (IllegalStateException ignored) {
            return new SyncResult(false, "no_active_character", null);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.WARNING, "Could not sync identity state for " + player.getName(), ex);
            return new SyncResult(false, "io_error", null);
        } catch (RuntimeException ex) {
            plugin.getLogger().log(Level.WARNING, "Could not calculate identity state for " + player.getName(), ex);
            return new SyncResult(false, "runtime_error", null);
        }
    }

    public int syncOnlinePlayersSilently() {
        int synced = 0;
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            SyncResult result = syncSilently(player);
            if (result.synced()) {
                synced++;
            }
        }
        return synced;
    }

    public RepairResult repair(OfflinePlayer player) throws IOException {
        YamlConfiguration character = characters.getActiveCharacter(player);
        if (character == null) {
            throw new IllegalStateException("No active character.");
        }

        List<String> changes = new ArrayList<>();

        fragments.ensureFragmentState(character);
        changes.addAll(cleanFragmentList(character, "fragments.discovered-list", "discovered fragment"));
        changes.addAll(cleanFragmentList(character, "fragments.equipped", "equipped fragment"));

        fragments.ensureFragmentState(character);
        changes.addAll(cleanIntentSlots(character));

        intents.writeSummaryFields(character);
        IdentitySummary identity = summary(character);
        writeIdentityFields(character, identity);

        boolean changed = !changes.isEmpty();
        if (!changed) {
            changes.add("No repair needed. Character identity framework is already clean.");
        }

        int slot = character.getInt("slot", characters.getActiveSlot(player));
        characters.storage().saveCharacter(player.getUniqueId(), slot, character);

        return new RepairResult(changed, List.copyOf(changes), identity);
    }

    public List<String> summaryLines(IdentitySummary summary) {
        List<String> lines = new ArrayList<>();
        lines.add("&7Race: &f" + summary.raceId() + " &8/ &7Level: &f" + summary.level() + " &8/ &7Phase: &f" + summary.phase());
        lines.add("&7Fragment Pressure: &f" + round(summary.fragment().totalPressure())
                + " &8/ &7Fragment Stability: &f" + round(summary.fragment().stability())
                + " &8/ &7Erasure Pressure: &f" + round(summary.fragment().erasurePressure()));
        lines.add("&7Intent Primary: &f" + intents.displayName(summary.intent().primary())
                + " &8/ &7Slots: &f" + summary.intent().usedSlots() + " / " + summary.intent().maxSlots());
        lines.add("&7Intent Pressure: &f" + round(summary.intent().pressure())
                + " &8/ &7Stability Impact: &f" + round(summary.intent().stabilityImpact()));
        lines.add("&7Total Identity Pressure: &c" + round(summary.totalPressure())
                + " &8/ &7Combined Stability: &f" + round(summary.combinedStability())
                + " &8/ &7State: &b" + summary.diagnosticState());
        lines.add("&7Hook: &fprimaryIntent=" + summary.hooks().primaryIntent()
                + " &8/ &ffragment=" + summary.hooks().primaryFragment()
                + " &8/ &faccess=" + summary.hooks().accessTier());
        lines.add("&8Framework only. These values are routing context, not final combat design.");
        return lines;
    }

    public List<String> contractLines() {
        return List.of(
                "&7Fragment identity is stored under &ffragments.*&7 and scalar legacy fields like &ffragment-stability&7.",
                "&7Intent identity is stored under &fintent.active.slotN&7 and summarized under &fintent.*&7.",
                "&7Discipline remains separate under &fdiscipline.*&7.",
                "&7Abilities consume these signals later, but do not own identity."
        );
    }

    public IdentityHooks hooks(YamlConfiguration character) {
        return summary(character).hooks();
    }

    private IdentitySummary buildSummary(YamlConfiguration character, FragmentService.FragmentSummary fragmentSummary, IntentService.IntentSummary intentSummary) {
        double totalPressure = fragmentSummary.totalPressure() + intentSummary.pressure();
        double combinedStability = clamp(fragmentSummary.stability() + intentSummary.stabilityImpact(), 0.0, 100.0);
        double erasurePressure = fragmentSummary.erasurePressure();

        String diagnosticState = diagnosticState(totalPressure, combinedStability, erasurePressure);
        String primaryFragment = fragmentSummary.equipped().isEmpty() ? "none" : fragmentSummary.equipped().get(0);
        String primaryIntent = intentSummary.primary() == null || intentSummary.primary().isBlank() ? "none" : intentSummary.primary();

        IdentityHooks hooks = new IdentityHooks(
                primaryFragment,
                primaryIntent,
                round(totalPressure),
                round(combinedStability),
                intentSummary.usedSlots() > 0,
                !fragmentSummary.equipped().isEmpty(),
                accessTier(character, totalPressure, combinedStability)
        );

        return new IdentitySummary(
                character.getString("name", "Unnamed"),
                character.getString("race.id", character.getString("race-id", "unknown")),
                character.getInt("progression.level", 1),
                character.getString("progression.phase", "discovery"),
                fragmentSummary,
                intentSummary,
                round(totalPressure),
                round(combinedStability),
                round(erasurePressure),
                diagnosticState,
                hooks
        );
    }

    private void writeIdentityFields(YamlConfiguration character, IdentitySummary summary) {
        character.set("identity.schema-version", 1);
        character.set("identity.name", summary.characterName());
        character.set("identity.race", summary.raceId());
        character.set("identity.level", summary.level());
        character.set("identity.phase", summary.phase());

        character.set("identity.fragments.equipped-count", summary.fragment().equipped().size());
        character.set("identity.fragments.discovered-count", summary.fragment().discovered().size());
        character.set("identity.fragments.pressure", summary.fragment().totalPressure());
        character.set("identity.fragments.stability", summary.fragment().stability());
        character.set("identity.fragments.erasure-pressure", summary.fragment().erasurePressure());

        character.set("identity.intent.primary", summary.intent().primary());
        character.set("identity.intent.used-slots", summary.intent().usedSlots());
        character.set("identity.intent.max-slots", summary.intent().maxSlots());
        character.set("identity.intent.pressure", summary.intent().pressure());
        character.set("identity.intent.stability-impact", summary.intent().stabilityImpact());

        character.set("identity.pressure.total", summary.totalPressure());
        character.set("identity.stability.combined", summary.combinedStability());
        character.set("identity.erasure.pressure", summary.erasurePressure());
        character.set("identity.diagnostics.state", summary.diagnosticState());
        character.set("identity.last-sync-source", "fragmentengine-runtime");

        character.set("identity.hooks.primary-fragment", summary.hooks().primaryFragment());
        character.set("identity.hooks.primary-intent", summary.hooks().primaryIntent());
        character.set("identity.hooks.total-pressure", summary.hooks().totalPressure());
        character.set("identity.hooks.combined-stability", summary.hooks().combinedStability());
        character.set("identity.hooks.has-active-intent", summary.hooks().hasActiveIntent());
        character.set("identity.hooks.has-equipped-fragments", summary.hooks().hasEquippedFragments());
        character.set("identity.hooks.access-tier", summary.hooks().accessTier());
        character.set("identity.hooks.note", "Framework routing context only. Not final ability design.");
    }

    private List<String> cleanFragmentList(YamlConfiguration character, String path, String label) {
        List<String> changes = new ArrayList<>();
        List<String> rawValues = character.getStringList(path);
        Set<String> cleaned = new LinkedHashSet<>();

        for (String raw : rawValues) {
            String id = fragments.normalize(raw);
            if (id.isBlank()) {
                changes.add("Removed blank " + label + " from " + path + ".");
                continue;
            }
            if (!fragments.exists(id)) {
                changes.add("Removed unknown " + label + ": " + raw + ".");
                continue;
            }
            if (!cleaned.add(id)) {
                changes.add("Removed duplicate " + label + ": " + id + ".");
            }
        }

        List<String> result = new ArrayList<>(cleaned);
        if (!result.equals(rawValues)) {
            character.set(path, result);
        }
        return changes;
    }

    private List<String> cleanIntentSlots(YamlConfiguration character) {
        List<String> changes = new ArrayList<>();
        IntentService.IntentSummary current = intents.summary(character);
        int maxSlots = Math.max(1, Math.min(4, current.maxSlots()));
        Set<String> seen = new LinkedHashSet<>();

        ConfigurationSection section = character.getConfigurationSection("intent.active");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                int slot = parseSlotKey(key);
                if (slot < 1 || slot > 4) {
                    character.set("intent.active." + key, null);
                    changes.add("Removed invalid intent slot key: " + key + ".");
                }
            }
        }

        for (int slot = 1; slot <= 4; slot++) {
            String key = "slot" + slot;
            String path = "intent.active." + key;
            String raw = character.getString(path, "");
            String id = normalizeIntent(raw);

            if (id.isBlank()) {
                character.set(path, null);
                continue;
            }

            if (slot > maxSlots) {
                character.set(path, null);
                changes.add("Cleared locked intent " + key + ": " + raw + ".");
                continue;
            }

            if (!intents.isKnownIntent(id)) {
                character.set(path, null);
                changes.add("Cleared unknown intent " + key + ": " + raw + ".");
                continue;
            }

            if (!seen.add(id)) {
                character.set(path, null);
                changes.add("Cleared duplicate intent " + key + ": " + id + ".");
                continue;
            }

            if (!raw.equals(id)) {
                character.set(path, id);
                changes.add("Normalized intent " + key + ": " + raw + " -> " + id + ".");
            }
        }

        return changes;
    }

    private int parseSlotKey(String key) {
        if (key == null) {
            return -1;
        }
        String clean = key.toLowerCase(Locale.ROOT).replace("slot", "").trim();
        try {
            return Integer.parseInt(clean);
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private String normalizeIntent(String raw) {
        return raw == null ? "" : raw.toLowerCase(Locale.ROOT).trim().replace(" ", "_").replace("-", "_");
    }

    private String diagnosticState(double totalPressure, double combinedStability, double erasurePressure) {
        if (combinedStability <= 25.0 || erasurePressure >= 80.0 || totalPressure >= 90.0) {
            return "critical";
        }
        if (combinedStability <= 60.0 || erasurePressure >= 45.0 || totalPressure >= 45.0) {
            return "strained";
        }
        return "stable";
    }

    private String accessTier(YamlConfiguration character, double totalPressure, double combinedStability) {
        int level = character.getInt("progression.level", 1);
        if (combinedStability <= 25.0) {
            return "unstable";
        }
        if (level >= 51 || totalPressure >= 60.0) {
            return "mastery";
        }
        if (level >= 36) {
            return "optimized";
        }
        if (level >= 16) {
            return "committed";
        }
        return "discovery";
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public record IdentitySummary(
            String characterName,
            String raceId,
            int level,
            String phase,
            FragmentService.FragmentSummary fragment,
            IntentService.IntentSummary intent,
            double totalPressure,
            double combinedStability,
            double erasurePressure,
            String diagnosticState,
            IdentityHooks hooks
    ) {
    }

    public record IdentityHooks(
            String primaryFragment,
            String primaryIntent,
            double totalPressure,
            double combinedStability,
            boolean hasActiveIntent,
            boolean hasEquippedFragments,
            String accessTier
    ) {
    }

    public record RepairResult(
            boolean changed,
            List<String> changes,
            IdentitySummary summary
    ) {
    }

    public record SyncResult(
            boolean synced,
            String status,
            IdentitySummary summary
    ) {
    }
}
