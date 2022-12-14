package de.base2code.clansystem.configuration;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class ConfigManager {
    private final File file;
    private YamlConfiguration config;

    private final HashMap<String, String> defaults = new HashMap<>();

    public ConfigManager(File file) {
        this.file = file;
    }

    public void createIfNotExistsAndLoad() throws IOException {
        if (!file.exists()) {
            Files.createDirectories(file.toPath().getParent());
            Files.createFile(file.toPath());

            config = YamlConfiguration.loadConfiguration(file);
            setDefaults();
            save();
        } else {
            load();
            setDefaults();
            save();
        }
    }

    public void setDefaults() {
        defaults.put("clan.price", "100");

        defaults.put("clan.suffix", "§7[§b%tag%§7]");
        defaults.put("clan.team.suffix", "§7[§cT§aE§5A§eM§7]");

        for (String key : defaults.keySet()) {
            if (!config.contains(key)) {
                config.set(key, defaults.get(key));
            }
        }
    }

    public void load() throws IOException {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public YamlConfiguration getConfiguration() {
        return config;
    }

    public void save() throws IOException {
        config.save(file);
    }
}
