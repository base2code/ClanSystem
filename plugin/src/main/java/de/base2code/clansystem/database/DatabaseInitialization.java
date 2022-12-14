package de.base2code.clansystem.database;

import com.google.common.io.Resources;
import de.base2code.clansystem.ClanSystem;
import de.base2code.clansystem.database.connector.MariaDbConnector;
import de.base2code.clansystem.database.connector.SqlConfiguration;
import de.base2code.clansystem.database.connector.SqlConnector;

import javax.sql.DataSource;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DatabaseInitialization {
    public SqlConnector initialize() throws SQLException, IOException {
        getPluginLogger().info("Initializing SQL connection...");

        SqlConfiguration configuration = SqlConfiguration.loadFromFile(new File(ClanSystem.getInstance().getDataFolder() + "/sql.yml"));

        SqlConnector connector = null;
        if (configuration.getType().equalsIgnoreCase("mysql") || configuration.getType().equalsIgnoreCase("mariadb")) {
            connector = new MariaDbConnector(configuration, getPluginLogger());
        } else if (configuration.getType().equalsIgnoreCase("h2")) {

            getPluginLogger().info("H2 is not fully supported yet. Please use another database.");
            throw new SQLException("H2 is not fully supported yet. Please use another database.");
            /*H2Connector connector = new H2Connector(configuration, getPluginLogger());
            getPluginLogger().info("\n\nIMPORTANT: H2 is not currently compatible with reloads!\n");
            return connector;*/
        } else {
            throw new SQLException("Invalid SQL type: " + configuration.getType());
        }

        return connector;
    }

    private Logger getPluginLogger() {
        return ClanSystem.getInstance().getLogger();
    }

    public static void createTables(DataSource dataSource) throws IOException {
        ArrayList<String> patches = new ArrayList<>();

        CodeSource src = ClanSystem.getInstance().getClass().getProtectionDomain().getCodeSource();
        if (src != null) {
            URL jar = src.getLocation();
            ZipInputStream zip = new ZipInputStream(jar.openStream());
            ZipEntry entry = null;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.getName().startsWith("database/tables/")) {
                    patches.add(entry.getName());
                }
            }
        }

        Collections.sort(patches);

        for (String patch : patches) {
            URL url = Resources.getResource(ClanSystem.getInstance().getClass(),"/" + patch);
            String text = Resources.toString(url, StandardCharsets.UTF_8);
            if (text.isEmpty()) continue;
            ClanSystem.getInstance().getLogger().info("Executing SQL patch /" + patch);
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(text)) {
                ps.executeUpdate();
            } catch (SQLException e) {
                ClanSystem.getInstance().getLogger().warning("Error executing SQL patch: " + patch);
                ClanSystem.getInstance().getLogger().warning(e.getMessage());
            }
        }
    }
}
