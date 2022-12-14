package de.base2code.clansystem;

import de.base2code.clansystem.commands.ClanDebug;
import de.base2code.clansystem.commands.clan.ClanCommand;
import de.base2code.clansystem.configuration.ConfigManager;
import de.base2code.clansystem.configuration.MessageConfig;
import de.base2code.clansystem.database.DatabaseInitialization;
import de.base2code.clansystem.database.connector.SqlConnector;
import de.base2code.clansystem.listener.PlayerChatListener;
import de.base2code.clansystem.listener.PlayerJoinListener;
import de.base2code.clansystem.manager.ClanInviteManager;
import de.base2code.clansystem.manager.ClanManager;
import de.base2code.clansystem.manager.confirmations.ConfirmationManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

public class ClanSystem extends JavaPlugin {

    private static ClanSystem instance;

    private SqlConnector sqlConnector;
    private DataSource dataSource;
    private MessageConfig messageConfig;
    private ConfigManager configManager;
    private ClanManager clanManager;
    private ConfirmationManager confirmationManager;
    private ClanInviteManager clanInviteManager;

    private Economy economy = null;

    private static final ExecutorService executorService = java.util.concurrent.Executors.newCachedThreadPool();

    @Override
    public void onEnable() {
        instance = this;

        getDataFolder().mkdirs();

        getLogger().info("ClanSystem wird geladen...");

        getLogger().info("Loading /clandebug");
        getCommand("clandebug").setExecutor(new ClanDebug());

        getLogger().info("Initializing SQL connection...");
        try {
            sqlConnector = new DatabaseInitialization().initialize();
            dataSource = sqlConnector.getPluginDataSource(getClass());
            DatabaseInitialization.createTables(dataSource);
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Could not initialize SQL connection", e);
        }

        getLogger().info("Loading config file...");
        configManager = new ConfigManager(new File(getDataFolder().toString(), "config.yml"));
        try {
            configManager.createIfNotExistsAndLoad();
        } catch (IOException e) {
            throw new RuntimeException("Could not load config file", e);
        }

        getLogger().info("Loading messages...");
        messageConfig = new MessageConfig(new File(getDataFolder().toString(), "messages.yml"));
        try {
            messageConfig.createIfNotExistsAndLoad();
        } catch (IOException e) {
            throw new RuntimeException("Could not load messages file", e);
        }

        getLogger().info("Loading clan manager...");
        clanManager = new ClanManager();

        getLogger().info("Loading confirmation manager...");
        confirmationManager = new ConfirmationManager();

        getLogger().info("Loading clan invite manager...");
        clanInviteManager = new ClanInviteManager();

        getLogger().info("Loading commands");
        getCommand("clan").setExecutor(new ClanCommand());

        getLogger().info("Loading listeners...");
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerChatListener(), this);
        pluginManager.registerEvents(new PlayerJoinListener(), this);

        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().info("Vault not found! To use economy while creating a clan you have to enable Vault!");
        } else {
            getLogger().info("Vault found! Attempting to load!");
            RegisteredServiceProvider<Economy> economyRegisteredServiceProvider = getServer().getServicesManager().getRegistration(Economy.class);
            if (economyRegisteredServiceProvider == null) {
                getLogger().info("Vault found, but economy service not found! In order to use economy in cases you need to install Vault and an Economy plugin!");
            } else {
                economy = economyRegisteredServiceProvider.getProvider();
                if (economy == null) {
                    getLogger().info("Vault found, but economy service not found! In order to use economy in cases you need to install Vault and an Economy plugin!");
                } else {
                    getLogger().info("Vault found! Economy service found! ClanSystem can add money to players! (" + economy.getName() + ")");
                }
            }
        }

        getLogger().info("ClanSystem enabled!");
    }

    public static ClanSystem getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling ClanSystem...");

        getLogger().info("Shutting down executor service...");
        try {
            executorService.shutdown();
        } catch (Exception e) {
            getLogger().info("Error while shutting down executor service!");
        }

        getLogger().info("Closing SQL connection...");
        try {
            sqlConnector.shutdown();
        } catch (Exception e) {
            getLogger().info("Error while closing SQL connection!");
        }

        getLogger().info("ClanSystem disabled!");
    }

    public String getMessage(String key) {
        return messageConfig.getMessage(key);
    }

    public ClanManager getClanManager() {
        return clanManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Economy getEconomy() {
        return economy;
    }

    public YamlConfiguration getConfig() {
        return configManager.getConfiguration();
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public ConfirmationManager getConfirmationManager() {
        return confirmationManager;
    }

    public ClanInviteManager getClanInviteManager() {
        return clanInviteManager;
    }
}
