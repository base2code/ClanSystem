package de.base2code.clansystem.database.connector;

import javax.sql.DataSource;
import java.sql.SQLException;

public interface SqlConnector {
    DataSource getPluginDataSource(Class<?> plugin) throws SQLException;
    void shutdown();
    boolean canBeReloaded();

}

