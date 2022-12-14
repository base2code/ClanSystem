package de.base2code.clansystem.commands.clan.sub;

import de.base2code.clansystem.ClanSystem;
import de.base2code.clansystem.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClanLeaveCommand implements SubCommand {
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

            if (clan.getOwner().equals(player.getUniqueId())) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.cant-leave-clan-because-owner"));
                return;
            }

            ClanSystem.getInstance().getClanManager().leaveClan(player.getUniqueId());
            player.sendMessage(ClanSystem.getInstance().getMessage("commands.clan.leave.success"));
        });
        return true;
    }

    @Override
    public String getSubCommand() {
        return "leave";
    }

    @Override
    public String syntax() {
        return "";
    }
}
