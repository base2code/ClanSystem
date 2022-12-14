package de.base2code.clansystem.commands.clan.sub;

import de.base2code.clansystem.ClanSystem;
import de.base2code.clansystem.commands.SubCommand;
import de.base2code.clansystem.manager.confirmations.Confirmation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClanConfirmCommand implements SubCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, String[] args, boolean isPlayer) {
        if (!(isPlayer)) {
            commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.no-console"));
            return false;
        }
        Player player = (Player) commandSender;

        Confirmation confirmation = ClanSystem.getInstance().getConfirmationManager().getConfirmation(player);
        if (confirmation == null) {
            commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.no-confirmation"));
            return false;
        }

        if (confirmation.getTime() + 5 * 60 * 1000 <= System.currentTimeMillis()) {
            commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.confirmation-timeout"));
            ClanSystem.getInstance().getConfirmationManager().removeConfirmation(player);
            return false;
        }

        confirmation.run();
        confirmation.remove();
        return true;
    }

    @Override
    public String getSubCommand() {
        return "confirm";
    }

    @Override
    public String syntax() {
        return "";
    }
}
