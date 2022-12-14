package de.base2code.clansystem.commands.clan.sub;

import de.base2code.clansystem.ClanSystem;
import de.base2code.clansystem.commands.SubCommand;
import de.base2code.clansystem.commands.clan.ClanCommand;
import de.base2code.clansystem.manager.ClanInviteManager;
import de.base2code.clansystem.manager.objects.ClanPermissions;
import de.base2code.clansystem.utils.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ClanInviteCommand implements SubCommand {
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

            if (!ClanPermissions.INVITE.hasPermission(clan, player.getUniqueId()).join()) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.no-clan-permissions"));
                return;
            }

            if (strings.length != 1) {
                ClanCommand.sendHelp(commandSender, this);
                return;
            }

            String targetName = strings[0];
            UUID target = UUIDFetcher.getUUID(targetName);
            if (target == null) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.player-not-found"));
                return;
            }


            ClanSystem.getInstance().getClanManager().getClanOfUser(target).whenComplete((clan1, throwable1) -> {
                if (throwable1 != null || clan1 != null) {
                    commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.player-already-in-clan"));
                    if (throwable1 != null) {
                        throwable1.printStackTrace();
                    }
                    return;
                }

                ClanSystem.getInstance().getClanInviteManager().addInvite(clan.getId(), target);
                Player targetPlayer = Bukkit.getPlayer(target);
                if (targetPlayer != null && targetPlayer.isOnline()) {
                    ClanSystem.getInstance().getClanInviteManager().sendMessage(clan.getId(), targetPlayer);
                }

                commandSender.sendMessage(ClanSystem.getInstance().getMessage("commands.clan.invite.success")
                        .replace("%player%", targetName));

            });
        });

        return false;
    }

    @Override
    public String getSubCommand() {
        return "invite";
    }

    @Override
    public String syntax() {
        return "<Spieler>";
    }
}
