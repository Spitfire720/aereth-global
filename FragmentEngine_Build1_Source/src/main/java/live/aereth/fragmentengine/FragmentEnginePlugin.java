package live.aereth.fragmentengine;

import live.aereth.fragmentengine.command.AerethCommand;
import live.aereth.fragmentengine.papi.AerethPlaceholderExpansion;
import live.aereth.fragmentengine.service.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class FragmentEnginePlugin extends JavaPlugin {
    private StorageService storageService;
    private ProgressionService progressionService;
    private StatsService statsService;
    private CharacterService characterService;
    private LegacyCommandService legacyCommandService;
    private AgentExportService agentExportService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        storageService = new StorageService(this);
        storageService.ensureFolders();

        progressionService = new ProgressionService(getConfig());
        statsService = new StatsService();
        characterService = new CharacterService(this, storageService, progressionService, statsService);
        legacyCommandService = new LegacyCommandService(characterService);
        agentExportService = new AgentExportService(this, storageService);

        getServer().getServicesManager().register(
        com.spitfire.fragmentengine.api.AerethProfileService.class,
        new live.aereth.fragmentengine.api.LegacyAerethProfileService(),
        this,
        org.bukkit.plugin.ServicePriority.Normal
);

        AerethCommand command = new AerethCommand(this, characterService, legacyCommandService, agentExportService);
        PluginCommand aereth = getCommand("aereth");
        if (aereth != null) {
            aereth.setExecutor(command);
            aereth.setTabCompleter(command);
        }

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new AerethPlaceholderExpansion(this, characterService).register();
            getLogger().info("PlaceholderAPI expansion registered: %aereth_...%");
        }

        if (getConfig().getBoolean("agent-support.export-on-startup", true)) {
            try {
                agentExportService.exportAll();
            } catch (IOException ex) {
                getLogger().warning("Could not export FragmentEngine diagnostics on startup: " + ex.getMessage());
            }
        }

        getLogger().info("FragmentEngine enabled. Reality has begun remembering.");
    }

    @Override
    public void onDisable() {
        if (agentExportService != null && getConfig().getBoolean("agent-support.export-on-shutdown", true)) {
            try {
                agentExportService.exportAll();
            } catch (IOException ex) {
                getLogger().warning("Could not export FragmentEngine diagnostics on shutdown: " + ex.getMessage());
            }
        }

        getLogger().info("FragmentEngine disabled. Memory has been sealed.");
    }
}
