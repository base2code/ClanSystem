package de.base2code.clansystem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface SubCommand {
    boolean onCommand(CommandSender commandSender, String[] strings, boolean isPlayer);
    String getSubCommand();
    String syntax();
}
