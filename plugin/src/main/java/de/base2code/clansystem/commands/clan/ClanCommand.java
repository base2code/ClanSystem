package de.base2code.clansystem.commands.clan;

import de.base2code.clansystem.ClanSystem;
import de.base2code.clansystem.commands.SubCommand;
import de.base2code.clansystem.commands.clan.sub.*;
import de.base2code.clansystem.utils.Debug;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ClanCommand implements CommandExecutor {
    private final Map<String, SubCommand> subcommands = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length != 0) {
            if (subcommands.containsKey(strings[0])) {
                Debug.debug(getClass(), "Found subcommand: " + strings[0]);
                boolean isPlayer = commandSender instanceof org.bukkit.entity.Player;
                String[] args = new String[strings.length - 1];

                SubCommand subCommand = subcommands.get(strings[0]);
                System.arraycopy(strings, 1, args, 0, strings.length - 1);
                return subCommand.onCommand(commandSender, args, isPlayer);
            }
        }

        // Send help message
        commandSender.sendMessage(ClanSystem.getInstance().getMessage("commands.clan.help.general"));
        for (SubCommand subCommand : subcommands.values()) {
            sendHelp(commandSender, subCommand);
        }
        return false;
    }

    public ClanCommand() {
        addSubCommands(
                new ClanCreateCommand(),
                new ClanInfoCommand(),
                new ClanLeaveCommand(),
                new ClanDeleteCommand(),
                new ClanConfirmCommand(),
                new ClanKickCommand(),
                new ClanPromoteCommand(),
                new ClanDemoteCommand(),
                new ClanInviteCommand(),
                new ClanAcceptCommand(),
                new ClanRejectCommand()
        );
    }

    public void addSubCommands(SubCommand... subCommands) {
        for (SubCommand subCommand : subCommands) {
            Debug.debug(getClass(), "Registering subcommand " + subCommand.getSubCommand());
            subcommands.put(subCommand.getSubCommand(), subCommand);
        }
    }

    public static void sendHelp(CommandSender commandSender, SubCommand subCommand) {
        if (subCommand == null) return;
        commandSender.sendMessage(ClanSystem.getInstance().getMessage("commands.clan.help." + subCommand.getSubCommand()));
        commandSender.sendMessage(ClanSystem.getInstance().getMessage("commands.clan.syntax") + "/clan " + subCommand.getSubCommand() + " " + subCommand.syntax());
    }
}
