package de.base2code.clansystem.commands.clan.sub;

import de.base2code.clansystem.ClanSystem;
import de.base2code.clansystem.commands.SubCommand;
import de.base2code.clansystem.commands.clan.ClanCommand;
import de.base2code.clansystem.manager.confirmations.Confirmation;
import de.base2code.clansystem.manager.objects.ClanPermissionRole;
import de.base2code.clansystem.utils.UUIDFetcher;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ClanPromoteCommand implements SubCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, String[] args, boolean isPlayer) {
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

            if (args.length != 1) {
                ClanCommand.sendHelp(commandSender, this);
                return;
            }

            String targetName = args[0];
            UUID targetUUID = UUIDFetcher.getUUID(targetName);

            if (targetUUID == null) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.player-not-found"));
                return;
            }

            ClanSystem.getInstance().getClanManager().getClanOfUser(targetUUID).whenComplete((clan1, throwable1) -> {
                if (throwable1 != null || clan1 == null) {
                    commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.not-in-clan"));
                    if (throwable1 != null) {
                        throwable1.printStackTrace();
                    }
                    return;
                }

                if (clan.getId() != clan1.getId()) {
                    commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.player-not-in-clan"));
                    return;
                }

                ClanPermissionRole currentRole = ClanSystem.getInstance().getClanManager().getPermissionRoleInClan(clan, targetUUID).join();
                if (currentRole == ClanPermissionRole.OWNER) {
                    commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.cant-promote-owner"));
                    return;
                } else if (currentRole == ClanPermissionRole.MODERATOR) {
                    commandSender.sendMessage(ClanSystem.getInstance().getMessage("commands.clan.promote.pending.owner").replace("%player%", targetName));
                    Confirmation confirmation = new Confirmation(player) {
                        @Override
                        public void run() {
                            ClanSystem.getInstance().getClanManager().setPermissionRoleInClan(clan, ((Player) commandSender).getUniqueId(), ClanPermissionRole.MEMBER);
                            ClanSystem.getInstance().getClanManager().setPermissionRoleInClan(clan, targetUUID, ClanPermissionRole.OWNER);
                            commandSender.sendMessage(ClanSystem.getInstance().getMessage("commands.clan.promote.success").replace("%player%", targetName));
                        }
                    };
                    ClanSystem.getInstance().getConfirmationManager().addConfirmation(confirmation);
                } else if (currentRole == ClanPermissionRole.MEMBER) {
                    commandSender.sendMessage(ClanSystem.getInstance().getMessage("commands.clan.promote.pending.moderator").replace("%player%", targetName));
                    Confirmation confirmation = new Confirmation(player) {
                        @Override
                        public void run() {
                            ClanSystem.getInstance().getClanManager().setPermissionRoleInClan(clan, targetUUID, ClanPermissionRole.MODERATOR);
                            commandSender.sendMessage(ClanSystem.getInstance().getMessage("commands.clan.promote.success"));
                        }
                    };
                    ClanSystem.getInstance().getConfirmationManager().addConfirmation(confirmation);
                }
            });
        });

        return true;
    }

    @Override
    public String getSubCommand() {
        return "promote";
    }

    @Override
    public String syntax() {
        return "<Spieler>";
    }
}
