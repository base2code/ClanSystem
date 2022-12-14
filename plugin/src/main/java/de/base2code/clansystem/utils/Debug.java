package de.base2code.clansystem.utils;

import de.base2code.clansystem.ClanSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.concurrent.CompletableFuture;

public class Debug {
    private static boolean debugMode = false;
    private static final File DEBUG_FILE = new File(ClanSystem.getInstance().getDataFolder(), "debug.log");

    public Debug() {
        // Simple file check to enable debug mode
        debugMode = Files.exists(Paths.get("debug"));
    }

    public static void debug(String className, String str) {
        str = "[" + className + "] " + str;
        if (debugMode) {
            // Workaround to print debug messages to console
            ClanSystem.getInstance().getLogger().info("[DEBUG] " + str);
        }
        // Append to debug file
        try {
            Files.write(DEBUG_FILE.toPath(), (str + "\n").getBytes(),
                    Files.exists(DEBUG_FILE.toPath())
                            ? java.nio.file.StandardOpenOption.APPEND :
                            java.nio.file.StandardOpenOption.CREATE
            );
        } catch (IOException e) {
            ClanSystem.getInstance().getLogger().severe("Error while writing to debug.log");
            e.printStackTrace();
        }
    }

    public static void debug(Class className, String str) {
        debug(className.getName(), str);
    }

    @Deprecated
    public static void debug(String str) {
        debug("", str);
    }

    public CompletableFuture<String> uploadDebug() {
        return CompletableFuture.supplyAsync(() -> {
            HasteClient hasteClient = new HasteClient();

            String logCode = "";
            try {
                StringBuilder sb = new StringBuilder();
                for (String line : Files.readAllLines(Paths.get("logs/latest.log"))) {
                    sb.append(line).append("\n");
                }
                String logurl = "Uploading...";
                if (sb.toString().length() > 100000) {
                    logurl = String.valueOf(TransferCliClient.postToTransfer(sb.toString()));
                    logCode = logurl.replace(TransferCliClient.TRANSFER_URL, "-t-");
                } else {
                    logurl = hasteClient.post(sb.toString(), true);
                    logCode = logurl.replace("https://haste.base2code.dev/raw/", "");
                }
            } catch (Exception e) {
                e.printStackTrace();
                logCode = "Could not upload logs";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Uploaded at: ").append(System.currentTimeMillis()).append("\r\n");
            sb.append("Uploaded at: ").append(new Date(System.currentTimeMillis())).append("\r\n");

            sb.append("Java Version: ").append(System.getProperty("java.version")).append("\r\n");
            sb.append("Java Vendor: ").append(System.getProperty("java.vendor")).append("\r\n");
            sb.append("Java Home: ").append(System.getProperty("java.home")).append("\r\n");

            sb.append("OS Name: ").append(System.getProperty("os.name")).append("\r\n");
            sb.append("OS Version: ").append(System.getProperty("os.version")).append("\r\n");
            sb.append("OS Architecture: ").append(System.getProperty("os.arch")).append("\r\n");

            sb.append("User Name: ").append(System.getProperty("user.name")).append("\r\n");
            sb.append("User Home: ").append(System.getProperty("user.home")).append("\r\n");

            try {
                sb.append("Database Class: ").append(ClanSystem.getInstance().getDataSource().getClass().getName()).append("\r\n");
            } catch (Exception ignored) {}

            sb.append("Plugin Version: ").append(ClanSystem.getInstance().getDescription().getVersion()).append("\r\n");

            sb.append("Log Code: 781A-").append(logCode).append("\r\n");

            sb.append("-----------------------------------------------------\r\n");
            sb.append("Debug Log: \r\n");
            for (String s : getDebugLog().join()) {
                sb.append(s).append("\r\n");
            }

            String url = null;
            try {
                url = hasteClient.post(sb.toString(), true);
                return url;
            } catch (IOException e) {
                e.printStackTrace();
                return "Error while uploading debug log: " + e.getMessage();
            }
        }, ClanSystem.getExecutorService());
    }

    public CompletableFuture<String[]> getDebugLog() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Files.readAllLines(DEBUG_FILE.toPath()).toArray(new String[0]);
            } catch (IOException e) {
                return new String[]{"Could not get debug log!"};
            }
        });
    }
}
