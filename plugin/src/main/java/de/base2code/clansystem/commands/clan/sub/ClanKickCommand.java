package de.base2code.clansystem.commands.clan.sub;

import de.base2code.clansystem.ClanSystem;
import de.base2code.clansystem.commands.SubCommand;
import de.base2code.clansystem.commands.clan.ClanCommand;
import de.base2code.clansystem.manager.confirmations.Confirmation;
import de.base2code.clansystem.manager.objects.Clan;
import de.base2code.clansystem.manager.objects.ClanPermissions;
import de.base2code.clansystem.utils.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ClanKickCommand implements SubCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, String[] strings, boolean isPlayer) {
        if (!(isPlayer)) {
            commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.no-console"));
            return false;
        }
        Player player = (Player) commandSender;

        if (strings.length != 1) {
            ClanCommand.sendHelp(commandSender, this);
            return false;
        }

        ClanSystem.getInstance().getClanManager().getClanOfUser(player.getUniqueId()).whenComplete((clan, throwable) -> {
            if (throwable != null || clan == null) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.not-in-clan"));
                if (throwable != null) {
                    throwable.printStackTrace();
                }
                return;
            }

            if (!ClanPermissions.KICK.hasPermission(clan, player.getUniqueId()).join()) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.not-clan-permissions"));
                return;
            }

            UUID target = UUIDFetcher.getUUID(strings[0]);
            if (target == null) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.player-not-found"));
                return;
            }

            Clan targetClan = ClanSystem.getInstance().getClanManager().getClanOfUser(target).join();
            if (targetClan == null || clan.getId() != targetClan.getId()) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.player-not-in-clan"));
                return;
            }

            ClanSystem.getInstance().getClanManager().leaveClan(target);
            player.sendMessage(ClanSystem.getInstance().getMessage("commands.clan.kick.success").replace("%player%", strings[0]));
        });
        return true;
    }

    @Override
    public String getSubCommand() {
        return "kick";
    }

    @Override
    public String syntax() {
        return "<Spieler>";
    }
}
