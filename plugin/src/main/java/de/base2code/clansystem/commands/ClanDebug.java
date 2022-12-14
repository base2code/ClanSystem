package de.base2code.clansystem.commands;

import de.base2code.clansystem.utils.Debug;
import org.bukkit.command.CommandExecutor;

public class ClanDebug implements CommandExecutor {
    @Override
    public boolean onCommand(org.bukkit.command.CommandSender commandSender, org.bukkit.command.Command command, String label, String[] args) {
        if (!commandSender.hasPermission("clansystem.debug")) {
            commandSender.sendMessage("You don't have permission to use this command!");
            return false;
        }
        commandSender.sendMessage("§aGathering information and uploading...");
        new Debug().uploadDebug().whenComplete((url, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                commandSender.sendMessage("§cAn error occurred while uploading the debug log: " + throwable.getMessage());
            } else {
                commandSender.sendMessage("§aDebug log uploaded to " + url);
            }
        });
        return true;
    }
}
