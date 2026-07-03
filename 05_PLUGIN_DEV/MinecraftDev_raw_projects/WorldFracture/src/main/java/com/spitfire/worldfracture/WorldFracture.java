package com.spitfire.worldfracture;

import com.spitfire.fragmentengine.FragmentEngine;
import com.spitfire.fragmentengine.state.PlayerState;
import com.spitfire.fragmentengine.state.PlayerStateManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * WorldFracture
 *
 * World-facing consequences of FragmentEngine state.
 *
 * RULES:
 * - NO player state mutation
 * - READ-ONLY access to FragmentEngine
 * - World changes only
 */
public class WorldFracture extends JavaPlugin implements Listener {

    private PlayerStateManager stateManager;

    @Override
    public void onEnable() {

        // Ensure FragmentEngine is present
        FragmentEngine fragmentEngine =
                (FragmentEngine) Bukkit.getPluginManager().getPlugin("FragmentEngine");

        if (fragmentEngine == null) {
            getLogger().severe("FragmentEngine not found! Disabling WorldFracture.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.stateManager = fragmentEngine.getStateManager();

        // Register listeners
        Bukkit.getPluginManager().registerEvents(this, this);

        getLogger().info("WorldFracture enabled and linked to FragmentEngine.");
    }

    @Override
    public void onDisable() {
        getLogger().info("WorldFracture disabled.");
    }

    /* ==========================================================
       EVENT HOOKS
       ========================================================== */

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        PlayerState state = stateManager.getState(event.getPlayer().getUniqueId());

        if (state == null) {
            getLogger().warning("PlayerState not loaded for " + event.getPlayer().getName());
            return;
        }

        getLogger().info("==== WorldFracture Debug ====");
        getLogger().info("Player: " + event.getPlayer().getName());
        getLogger().info("Stability: " + state.getStability());
        getLogger().info("Instability: " + state.getInstability());
        getLogger().info("Identity: " + state.getIdentityState());
        getLogger().info("Fragments: " + state.getFragments());
        getLogger().info("============================");
    }
}
