package de.base2code.clansystem.database.connector;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.mariadb.jdbc.MariaDbDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MariaDbConnector implements SqlConnector {
    private Class<?> plugin;
    private final SqlConfiguration sqlConfiguration;
    private final Logger logger;

    public MariaDbConnector(SqlConfiguration configuration, Logger logger) {
        this.logger = logger;
        this.sqlConfiguration = configuration;
    }

    private final Map<Class<?>, HikariDataSource> dataPools = new HashMap<>();

    public DataSource getPluginDataSource(Class<?> plugin) throws SQLException {
        this.plugin = plugin;
        if (dataPools.containsKey(plugin)) {
            return dataPools.get(plugin);
        }

        Properties props = new Properties();
        props.setProperty("dataSourceClassName", MariaDbDataSource.class.getName());
        props.setProperty("dataSource.serverName", sqlConfiguration.getHost());
        props.setProperty("dataSource.portNumber", sqlConfiguration.getPort());
        props.setProperty("dataSource.user", sqlConfiguration.getUsername());
        props.setProperty("dataSource.password", sqlConfiguration.getPassword());
        props.setProperty("dataSource.databaseName", sqlConfiguration.getDatabase());

        HikariConfig config = new HikariConfig(props);

        config.setMinimumIdle(3);
        config.setMaximumPoolSize(30);

        HikariDataSource dataSource = new HikariDataSource(config);

        try (Connection conn = dataSource.getConnection()) {
            conn.isValid(5 * 1000);
        } catch (SQLException e) {
            logger.warning("Invalid data for data source. Could not connect.\n");
            e.printStackTrace();
            throw e;
        }


        return dataSource;
    }

    public void shutdown() {
        if (dataPools.containsKey(plugin)) {
            dataPools.get(plugin).close();
            dataPools.remove(plugin);
            logger.info("Closed Connection for " + plugin.getName());
        } else {
            logger.info("Datasource didnt close, because it doesnt exist");
        }
    }

    @Override
    public boolean canBeReloaded() {
        return true;
    }
}

