package de.base2code.clansystem.commands.clan.sub;

import de.base2code.clansystem.ClanSystem;
import de.base2code.clansystem.commands.SubCommand;
import de.base2code.clansystem.manager.confirmations.Confirmation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClanDeleteCommand implements SubCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, String[] strings, boolean isPlayer) {
        if (!(isPlayer)) {
            commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.no-console"));
            return false;
        }
        Player player = (Player) commandSender;

        ClanSystem.getInstance().getClanManager().getClanOfUser(player.getUniqueId()).whenComplete((clan, throwable) -> {
            if (throwable != null || clan == null) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.not-in-clan"));
                if (throwable != null) {
                    throwable.printStackTrace();
                }
                return;
            }

            if (!clan.getOwner().equals(player.getUniqueId())) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.not-clan-owner"));
                return;
            }

            Confirmation confirmation = new Confirmation(player) {
                @Override
                public void run() {
                    ClanSystem.getInstance().getConfirmationManager().removeConfirmation((Player) commandSender);
                    ClanSystem.getInstance().getClanManager().deleteClan(clan);
                    player.sendMessage(ClanSystem.getInstance().getMessage("commands.clan.delete.success"));
                }
            };
            ClanSystem.getInstance().getConfirmationManager().addConfirmation(confirmation);
        });
        return true;
    }

    @Override
    public String getSubCommand() {
        return "delete";
    }

    @Override
    public String syntax() {
        return "";
    }
}
