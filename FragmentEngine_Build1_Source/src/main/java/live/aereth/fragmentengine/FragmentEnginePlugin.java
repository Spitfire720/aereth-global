package live.aereth.fragmentengine;

import live.aereth.fragmentengine.command.AerethCommand;
import live.aereth.fragmentengine.listener.FragmentEngineGuiListener;
import live.aereth.fragmentengine.papi.AerethPlaceholderExpansion;
import live.aereth.fragmentengine.service.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class FragmentEnginePlugin extends JavaPlugin {
    private StorageService storageService;
    private ProgressionService progressionService;
    private RaceService raceService;
    private StatsService statsService;
    private CharacterService characterService;
    private FragmentService fragmentService;
    private IntentService intentService;
    private DisciplineService disciplineService;
    private AbilityService abilityService;
    private LegacyCommandService legacyCommandService;
    private AgentExportService agentExportService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("races.yml", false);
        saveResource("fragments.yml", false);
        saveResource("intents.yml", false);
        saveResource("disciplines.yml", false);
        saveResource("abilities.yml", false);

        storageService = new StorageService(this);
        storageService.ensureFolders();

        progressionService = new ProgressionService(getConfig());
        raceService = new RaceService(this);
        statsService = new StatsService(getConfig(), raceService);
        characterService = new CharacterService(this, storageService, progressionService, statsService, raceService);
        fragmentService = new FragmentService(this, characterService);
        intentService = new IntentService(this, characterService);
        disciplineService = new DisciplineService(this, characterService);
        abilityService = new AbilityService(this, characterService);
        legacyCommandService = new LegacyCommandService(characterService);
        agentExportService = new AgentExportService(this, storageService);

        getServer().getServicesManager().register(
                com.spitfire.fragmentengine.api.AerethProfileService.class,
                new live.aereth.fragmentengine.api.LegacyAerethProfileService(),
                this,
                org.bukkit.plugin.ServicePriority.Normal
        );

        AerethCommand command = new AerethCommand(this, characterService, fragmentService, intentService, disciplineService, abilityService, legacyCommandService, agentExportService);
        PluginCommand aereth = getCommand("aereth");
        if (aereth != null) {
            aereth.setExecutor(command);
            aereth.setTabCompleter(command);
        }
        getServer().getPluginManager().registerEvents(new FragmentEngineGuiListener(this, characterService, fragmentService, intentService, disciplineService, abilityService), this);
        getServer().getPluginManager().registerEvents(new live.aereth.fragmentengine.listener.AbilityHotbarListener(this, characterService, disciplineService, abilityService), this);


        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new AerethPlaceholderExpansion(this, characterService, fragmentService, intentService, disciplineService, abilityService).register();
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