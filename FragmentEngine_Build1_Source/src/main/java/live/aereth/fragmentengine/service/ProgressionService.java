package live.aereth.fragmentengine.service;

import org.bukkit.configuration.file.FileConfiguration;

public class ProgressionService {
    private final FileConfiguration config;

    public ProgressionService(FileConfiguration config) {
        this.config = config;
    }

    public int getLevelCap() {
        return config.getInt("progression.level-cap", 60);
    }

    public int statPointsPerLevel() {
        return config.getInt("progression.stat-points-per-level", 2);
    }

    public int intentPointsPerLevel() {
        return config.getInt("progression.intent-points-per-level", 1);
    }

    public long xpRequiredForLevel(int level) {
        if (level >= getLevelCap()) {
            return 0L;
        }
        return (long) Math.floor(900.0 * Math.pow(level, 1.9));
    }

    public long cumulativeXpToReachLevel(int level) {
        if (level <= 1) {
            return 0L;
        }
        long total = 0L;
        for (int i = 1; i < level; i++) {
            total += xpRequiredForLevel(i);
        }
        return total;
    }

    public String phaseForLevel(int level) {
        if (level >= 1 && level <= 15) {
            return "discovery";
        }
        if (level <= 35) {
            return "commitment";
        }
        if (level <= 50) {
            return "optimization";
        }
        return "mastery";
    }

    public LevelResult addXp(int currentLevel, long currentXp, long currentTotalXp, long amount) {
        int oldLevel = Math.max(1, currentLevel);
        int level = oldLevel;
        long xp = Math.max(0L, currentXp) + Math.max(0L, amount);
        long totalXp = Math.max(0L, currentTotalXp) + Math.max(0L, amount);

        while (level < getLevelCap()) {
            long required = xpRequiredForLevel(level);
            if (required <= 0 || xp < required) {
                break;
            }
            xp -= required;
            level++;
        }

        if (level >= getLevelCap()) {
            level = getLevelCap();
            xp = 0L;
        }

        int levelsGained = Math.max(0, level - oldLevel);
        return new LevelResult(level, xp, totalXp, phaseForLevel(level), oldLevel, levelsGained, levelsGained > 0);
    }

    public record LevelResult(int level, long xp, long totalXp, String phase, int oldLevel, int levelsGained, boolean leveledUp) {}
}