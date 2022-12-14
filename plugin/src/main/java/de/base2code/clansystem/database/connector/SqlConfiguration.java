package de.base2code.clansystem.database.connector;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SqlConfiguration {
    private final String type;
    private final String host;
    private final String port;
    private final String database;
    private final String username;
    private final String password;

    public SqlConfiguration() {
        this.type = "mariadb";
        this.host = "localhost";
        this.port = "3306";
        this.database = "clansystem";
        this.username = "root";
        this.password = "";
    }

    public SqlConfiguration(String type, String host, String port, String database, String username, String password) {
        this.type = type;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public static SqlConfiguration loadFromFile(File file) throws IOException {
        if (!file.exists()) {
            SqlConfiguration configuration = new SqlConfiguration();

            Files.createDirectories(file.getParentFile().toPath());
            Files.createFile(file.toPath());
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set("type", configuration.getType());
            config.set("host", configuration.getHost());
            config.set("port", configuration.getPort());
            config.set("database", configuration.getDatabase());
            config.set("username", configuration.getUsername());
            config.set("password", configuration.getPassword());
            config.save(file);

            return configuration;
        } else {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            if (config.getString("type") == null) {
                config.set("type", "mysql");
                config.save(file);
            }
            if (config.getString("type").equals("h2")) {
                config.set("type", "mysql");
                config.save(file);
            }
            return new SqlConfiguration(
                    config.getString("type"),
                    config.getString("host"),
                    config.getString("port"),
                    config.getString("database"),
                    config.getString("username"),
                    config.getString("password"));
        }
    }

    public String getType() {
        return type;
    }

    public String getHost() {
        return this.host;
    }

    public String getPort() {
        return this.port;
    }

    public String getDatabase() {
        return this.database;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }


}
