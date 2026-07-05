package live.aereth.fragmentengine.service;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AgentExportService {
    private final JavaPlugin plugin;
    private final StorageService storage;

    public AgentExportService(JavaPlugin plugin, StorageService storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    public void exportAll() throws IOException {
        storage.ensureFolders();
        writeManifest();
        writeSchema();
        writeStatus();
        writeDiagnostics();
    }

    private void writeManifest() throws IOException {
        File file = new File(storage.getAgentFolder(), "manifest.yml");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("plugin: FragmentEngine\n");
            writer.write("version: \"" + plugin.getDescription().getVersion() + "\"\n");
            writer.write("schema-version: 6\n");
            writer.write("exports:\n");
            writer.write("  - schema.yml\n");
            writer.write("  - latest-status.json\n");
            writer.write("  - latest-diagnostics.json\n");
        }
    }

    private void writeSchema() throws IOException {
        File file = new File(storage.getAgentFolder(), "schema.yml");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("account-folder: accounts\n");
            writer.write("character-folder: characters\n");
            writer.write("account-fields:\n");
            writer.write("  - uuid\n  - username\n  - active-slot\n  - maximum-slots\n  - slots\n");
            writer.write("character-fields:\n");
            writer.write("  - profile-id\n  - owner-uuid\n  - slot\n  - race\n  - progression\n  - stats\n  - derived\n  - fragments\n  - intent\n  - discipline\n");
            writer.write("fragment-fields:\n");
            writer.write("  - fragments.capacity\n  - fragments.discovered-list\n  - fragments.equipped\n  - fragments.total-pressure\n  - fragments.stability\n  - fragments.erasure-pressure\n");
            writer.write("intent-fields:\n");
            writer.write("  - intent.unlocked-slots\n  - intent.active\n  - intent.primary\n  - intent.pressure\n  - intent.stability-impact\n  - intent.slots-used\n");
            writer.write("discipline-fields:\n");
            writer.write("  - discipline.id\n");
            writer.write("  - discipline.display\n");
            writer.write("  - discipline.family\n");
            writer.write("  - discipline.level-required\n");
            writer.write("  - discipline.selected\n");
            writer.write("  - discipline.progression.rank\n");
            writer.write("  - discipline.progression.rank-name\n");
            writer.write("  - discipline.progression.xp\n");
            writer.write("  - discipline.progression.xp-required\n");
            writer.write("  - discipline.progression.progress-percent\n");
            writer.write("  - discipline.progression.max-rank\n");
            writer.write("discipline-passive-fields:\n");
            writer.write("  - stats.discipline-bonus\n");
            writer.write("  - derived.discipline-bonus\n");
            writer.write("  - discipline.passives.stat-summary\n");
            writer.write("  - discipline.passives.derived-summary\n");
        }
    }

    private void writeStatus() throws IOException {
        File file = new File(storage.getAgentFolder(), "latest-status.json");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("{\n");
            writer.write("  \"plugin\": \"FragmentEngine\",\n");
            writer.write("  \"version\": \"" + escape(plugin.getDescription().getVersion()) + "\",\n");
            writer.write("  \"serverVersion\": \"" + escape(Bukkit.getVersion()) + "\",\n");
            writer.write("  \"onlinePlayers\": " + Bukkit.getOnlinePlayers().size() + ",\n");
            writer.write("  \"accountFiles\": " + countFiles(storage.getAccountsFolder()) + ",\n");
            writer.write("  \"characterFiles\": " + countFiles(storage.getCharactersFolder()) + ",\n");
            writer.write("  \"placeholderApiHooked\": " + Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") + "\n");
            writer.write("}\n");
        }
    }

    private void writeDiagnostics() throws IOException {
        File file = new File(storage.getAgentFolder(), "latest-diagnostics.json");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("{\n");
            writer.write("  \"status\": \"ok\",\n");
            writer.write("  \"notes\": [\n");
            writer.write("    \"Build 3C data backend active\",\n");
            writer.write("    \"Race, progression, stats, fragments, intent, discipline identity, discipline progression, and discipline passive modifiers implemented\",\n");
            writer.write("    \"Combat execution not implemented yet\",\n");
            writer.write("    \"Discipline active abilities pending later build\"\n");
            writer.write("  ]\n");
            writer.write("}\n");
        }
    }

    private int countFiles(File folder) {
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        return files == null ? 0 : files.length;
    }

    private String escape(String input) {
        return input == null ? "" : input.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}