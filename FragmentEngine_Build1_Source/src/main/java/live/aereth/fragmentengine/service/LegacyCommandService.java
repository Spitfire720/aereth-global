package live.aereth.fragmentengine.service;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LegacyCommandService {
    private final CharacterService characterService;

    public LegacyCommandService(CharacterService characterService) {
        this.characterService = characterService;
    }

    public void activity(OfflinePlayer player, String activityType, double amount, String source) throws IOException {
        YamlConfiguration character = requireActive(player);
        String key = activityType.toUpperCase(Locale.ROOT);
        double current = character.getDouble("activities." + key, 0.0);
        character.set("activities." + key, current + amount);
        character.set("activity-sources." + key + "." + source, amount);
        save(player, character);
    }

    public void echo(OfflinePlayer player, String echoId, String scope, String source) throws IOException {
        YamlConfiguration character = requireActive(player);
        List<String> echoes = character.getStringList("echoes");
        if (!echoes.contains(echoId)) {
            echoes.add(echoId);
        }
        character.set("echoes", echoes);
        character.set("echo-meta." + echoId + ".scope", scope);
        character.set("echo-meta." + echoId + ".source", source);
        save(player, character);
    }

    public void attach(OfflinePlayer player, String fragmentId) throws IOException {
        YamlConfiguration character = requireActive(player);

        List<String> attached = character.getStringList("fragments.attached");
        if (!attached.contains(fragmentId)) {
            attached.add(fragmentId);
        }
        character.set("fragments.attached", attached);

        List<String> discovered = character.getStringList("fragments.discovered-list");
        if (!discovered.contains(fragmentId)) {
            discovered.add(fragmentId);
        }
        character.set("fragments.discovered-list", discovered);

        save(player, character);
    }

    public void erasure(OfflinePlayer player, double amount, String source) throws IOException {
        YamlConfiguration character = requireActive(player);
        double current = character.getDouble("erasure", 0.0);
        double next = current + amount;
        character.set("erasure", next);
        character.set("fragments.erasure-pressure", next);
        character.set("erasure-sources." + source, character.getDouble("erasure-sources." + source, 0.0) + amount);
        characterService.stats().applyStatsAndDerived(character);
        save(player, character);
    }

    private YamlConfiguration requireActive(OfflinePlayer player) {
        YamlConfiguration character = characterService.getActiveCharacter(player);
        if (character == null) {
            throw new IllegalStateException("No active character.");
        }
        return character;
    }

    private void save(OfflinePlayer player, YamlConfiguration character) throws IOException {
        int slot = character.getInt("slot", characterService.getActiveSlot(player));
        characterService.storage().saveCharacter(player.getUniqueId(), slot, character);
    }
}
