package live.aereth.fragmentengine.service;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class FragmentService {
    private final JavaPlugin plugin;
    private final CharacterService characters;
    private final YamlConfiguration fragments;

    public FragmentService(JavaPlugin plugin, CharacterService characters) {
        this.plugin = plugin;
        this.characters = characters;
        this.fragments = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "fragments.yml"));
    }

    public List<String> allFragmentIds() {
        ConfigurationSection section = fragments.getConfigurationSection("fragments");
        if (section == null) {
            return List.of();
        }
        return new ArrayList<>(section.getKeys(false));
    }

    public boolean exists(String fragmentId) {
        return fragments.contains("fragments." + normalize(fragmentId));
    }

    public String normalize(String fragmentId) {
        if (fragmentId == null || fragmentId.isBlank()) {
            return "";
        }
        return fragmentId.toLowerCase(Locale.ROOT).trim().replace(" ", "_");
    }

    public String displayName(String fragmentId) {
        String id = normalize(fragmentId);
        return fragments.getString("fragments." + id + ".display", id);
    }

    public String lore(String fragmentId) {
        String id = normalize(fragmentId);
        return fragments.getString("fragments." + id + ".lore", "");
    }

    public double pressure(String fragmentId) {
        String id = normalize(fragmentId);
        return fragments.getDouble("fragments." + id + ".pressure", 0.0);
    }

    public double stabilityCost(String fragmentId) {
        String id = normalize(fragmentId);
        return fragments.getDouble("fragments." + id + ".stability-cost", 0.0);
    }

    public FragmentResult discoverFragment(OfflinePlayer player, String fragmentId) throws IOException {
        String id = requireFragment(fragmentId);
        YamlConfiguration character = requireActive(player);
        ensureFragmentState(character);

        List<String> discovered = discovered(character);
        boolean changed = addUnique(discovered, id);
        character.set("fragments.discovered-list", discovered);
        save(player, character);
        return new FragmentResult(id, changed, "discovered");
    }

    public FragmentResult attachFragment(OfflinePlayer player, String fragmentId) throws IOException {
        String id = requireFragment(fragmentId);
        YamlConfiguration character = requireActive(player);
        ensureFragmentState(character);

        List<String> discovered = discovered(character);
        addUnique(discovered, id);
        character.set("fragments.discovered-list", discovered);

        List<String> equipped = equipped(character);
        if (equipped.contains(id)) {
            recalculate(character);
            save(player, character);
            return new FragmentResult(id, false, "already-equipped");
        }

        int capacity = character.getInt("fragments.capacity", plugin.getConfig().getInt("fragments.starting-capacity", 3));
        if (equipped.size() >= capacity) {
            throw new IllegalStateException("No free fragment slots. Capacity: " + capacity);
        }

        equipped.add(id);
        character.set("fragments.equipped", equipped);
        recalculate(character);
        save(player, character);
        return new FragmentResult(id, true, "attached");
    }

    public FragmentResult detachFragment(OfflinePlayer player, String fragmentId) throws IOException {
        String id = requireFragment(fragmentId);
        YamlConfiguration character = requireActive(player);
        ensureFragmentState(character);

        List<String> equipped = equipped(character);
        boolean changed = equipped.remove(id);
        character.set("fragments.equipped", equipped);
        recalculate(character);
        save(player, character);
        return new FragmentResult(id, changed, changed ? "detached" : "not-equipped");
    }

    public FragmentSummary summary(YamlConfiguration character) {
        ensureFragmentState(character);
        return new FragmentSummary(
                character.getInt("fragments.capacity", plugin.getConfig().getInt("fragments.starting-capacity", 3)),
                discovered(character),
                equipped(character),
                character.getDouble("fragments.total-pressure", 0.0),
                character.getDouble("fragments.stability", character.getDouble("fragment-stability", 100.0)),
                character.getDouble("fragments.erasure-pressure", character.getDouble("erasure", 0.0))
        );
    }

    public void ensureFragmentState(YamlConfiguration character) {
        int configuredCapacity = plugin.getConfig().getInt("fragments.starting-capacity", 3);
        character.set("fragments.capacity", character.getInt("fragments.capacity", configuredCapacity));

        if (!character.contains("fragments.discovered-list")) {
            character.set("fragments.discovered-list", toList(character, "fragments.discovered"));
        }
        if (!character.contains("fragments.equipped") || character.getConfigurationSection("fragments.equipped") != null) {
            character.set("fragments.equipped", toList(character, "fragments.equipped"));
        }

        recalculate(character);
    }

    public void recalculate(YamlConfiguration character) {
        List<String> equipped = equipped(character);

        double totalPressure = 0.0;
        double stabilityCost = 0.0;
        clearFragmentBonuses(character);

        for (String id : equipped) {
            totalPressure += pressure(id);
            stabilityCost += stabilityCost(id);
            applyFragmentStatBonuses(character, id);
        }

        double baseErasure = character.getDouble("erasure", 0.0);
        double pressureToErasure = plugin.getConfig().getDouble("fragments.pressure-to-erasure-rate", 0.5);
        double stabilityPenaltyRate = plugin.getConfig().getDouble("fragments.pressure-stability-penalty-rate", 1.0);
        double baseStability = character.getDouble("fragment-stability", 100.0);

        character.set("fragments.total-pressure", round(totalPressure));
        character.set("fragments.stability-cost", round(stabilityCost));
        character.set("fragments.erasure-pressure", round(baseErasure + totalPressure * pressureToErasure));
        character.set("fragments.stability", round(clamp(baseStability - stabilityCost * stabilityPenaltyRate, 0.0, 100.0)));

        characters.stats().applyStatsAndDerived(character);
    }

    private void applyFragmentStatBonuses(YamlConfiguration character, String fragmentId) {
        ConfigurationSection stats = fragments.getConfigurationSection("fragments." + normalize(fragmentId) + ".modifiers.stats");
        if (stats == null) {
            return;
        }
        for (String stat : stats.getKeys(false)) {
            double current = character.getDouble("stats.fragment-bonus." + stat, 0.0);
            character.set("stats.fragment-bonus." + stat, round(current + stats.getDouble(stat, 0.0)));
        }
    }

    private void clearFragmentBonuses(YamlConfiguration character) {
        character.set("stats.fragment-bonus.vitality", 0.0);
        character.set("stats.fragment-bonus.strength", 0.0);
        character.set("stats.fragment-bonus.dexterity", 0.0);
        character.set("stats.fragment-bonus.intelligence", 0.0);
        character.set("stats.fragment-bonus.willpower", 0.0);
        character.set("stats.fragment-bonus.endurance", 0.0);
    }

    private List<String> discovered(YamlConfiguration character) {
        return toList(character, "fragments.discovered-list");
    }

    private List<String> equipped(YamlConfiguration character) {
        return toList(character, "fragments.equipped");
    }

    private List<String> toList(YamlConfiguration character, String path) {
        Set<String> values = new LinkedHashSet<>();
        List<String> direct = character.getStringList(path);
        for (String value : direct) {
            if (value != null && !value.isBlank()) {
                values.add(normalize(value));
            }
        }
        ConfigurationSection section = character.getConfigurationSection(path);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                values.add(normalize(key));
            }
        }
        return new ArrayList<>(values);
    }

    private boolean addUnique(List<String> list, String value) {
        if (list.contains(value)) {
            return false;
        }
        list.add(value);
        return true;
    }

    private String requireFragment(String fragmentId) {
        String id = normalize(fragmentId);
        if (!exists(id)) {
            throw new IllegalArgumentException("Unknown fragment: " + fragmentId);
        }
        return id;
    }

    private YamlConfiguration requireActive(OfflinePlayer player) {
        YamlConfiguration character = characters.getActiveCharacter(player);
        if (character == null) {
            throw new IllegalStateException("No active character.");
        }
        return character;
    }

    private void save(OfflinePlayer player, YamlConfiguration character) throws IOException {
        int slot = character.getInt("slot", characters.getActiveSlot(player));
        characters.storage().saveCharacter(player.getUniqueId(), slot, character);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public record FragmentResult(String fragmentId, boolean changed, String status) {}
    public record FragmentSummary(int capacity, List<String> discovered, List<String> equipped, double totalPressure, double stability, double erasurePressure) {}
}