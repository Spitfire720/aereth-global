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
            writer.write("schema-version: 3\n");
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
            writer.write("  - profile-id\n  - owner-uuid\n  - slot\n  - race\n  - progression\n  - stats\n  - derived\n  - fragments\n  - intent\n");
            writer.write("fragment-fields:\n");
            writer.write("  - fragments.capacity\n  - fragments.discovered-list\n  - fragments.equipped\n  - fragments.total-pressure\n  - fragments.stability\n  - fragments.erasure-pressure\n");
            writer.write("intent-fields:\n");
            writer.write("  - intent.unlocked-slots\n  - intent.active\n  - intent.primary\n  - intent.pressure\n  - intent.stability-impact\n  - intent.slots-used\n");
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
            writer.write("    \"Build 2C data backend active\",\n");
            writer.write("    \"Race, progression, stats, fragments, and intent foundations implemented\",\n");
            writer.write("    \"Combat execution not implemented yet\",\n");
            writer.write("    \"Disciplines pending Build 3A\"\n");
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