package de.base2code.clansystem.commands.clan.sub;

import de.base2code.clansystem.ClanSystem;
import de.base2code.clansystem.commands.SubCommand;
import de.base2code.clansystem.commands.clan.ClanCommand;
import de.base2code.clansystem.exceptions.clan.ClanSetRoleException;
import de.base2code.clansystem.manager.confirmations.Confirmation;
import de.base2code.clansystem.manager.objects.Clan;
import de.base2code.clansystem.manager.objects.ClanPermissionRole;
import de.base2code.clansystem.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClanCreateCommand implements SubCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, String[] args, boolean isPlayer) {
        if (!(isPlayer)) {
            commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.no-console"));
            return false;
        }
        Player player = (Player) commandSender;

        if (args.length != 2) {
            ClanCommand.sendHelp(commandSender, this);
            return false;
        }

        ClanSystem.getInstance().getClanManager().getClanOfUser(player.getUniqueId()).whenComplete((currClan, throwable) -> {
            if (currClan != null) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.already-in-clan"));
                return;
            }

            String clanName = args[0];
            String clanTag = args[1];

            int clanPrice = ClanSystem.getInstance().getConfig().getInt("clan.price");

            if (clanPrice < 0) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.unknown"));
                return;
            } else if (clanPrice > 0 && ClanSystem.getInstance().getEconomy() == null) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.no-vault"));
                return;
            }

            // Just check balance
            if (clanPrice >= 1) {
                double balance = ClanSystem.getInstance().getEconomy().getBalance((Player) commandSender);
                if (balance < clanPrice) {
                    commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.not-enough-money"));
                    return;
                }
            }

            // TODO: Make configurable
            if (clanName.length() > 20) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.clan-name-is-too-long"));
                return;
            }
            if (clanName.length() < 5) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.clan-name-is-too-short"));
                return;
            }
            if (!Utils.isAlphaNumeric(clanName)) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.clan-name-is-invalid"));
                return;
            }

            // TODO: Make configurable
            if (clanTag.length() >= 5) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.clan-tag-is-too-long"));
                return;
            }
            if (clanTag.length() < 2) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.clan-tag-is-too-short"));
                return;
            }
            if (!Utils.isAlphaNumeric(clanTag)) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.clan-tag-is-invalid"));
                return;
            }

            boolean clanNameIsTaken = ClanSystem.getInstance().getClanManager().isClanNameTaken(clanName).join();
            boolean clanTagIsTaken = ClanSystem.getInstance().getClanManager().isClanTagTaken(clanTag).join();

            if (clanTagIsTaken) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.clan-tag-is-taken"));
                return;
            } else if (clanNameIsTaken) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.clan-name-is-taken"));
                return;
            }

            Confirmation confirmation = new Confirmation(player) {
                @Override
                public void run() {
                    ClanSystem.getInstance().getConfirmationManager().removeConfirmation((Player) commandSender);

                    ClanSystem.getInstance().getClanManager().createClan(clanName, clanTag, ((Player) commandSender).getUniqueId()).whenComplete((result, throwable1) -> {
                        if (throwable1 != null) {
                            throwable1.printStackTrace();
                            commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.unknown"));
                            return;
                        }

                        Clan clan = ClanSystem.getInstance().getClanManager().getClan(clanName, clanTag).join();

                        boolean roleSuccess = false;
                        try {
                            roleSuccess = ClanSystem.getInstance().getClanManager().setPermissionRoleInClan(clan, ((Player) commandSender).getUniqueId(), ClanPermissionRole.OWNER).get();
                            if (!roleSuccess) {
                                throw new ClanSetRoleException("Could not set role for player " + ((Player) commandSender).getUniqueId() + " in clan " + clan.getName() + " (" + clan.getTag() + ")");
                            }
                        } catch (Exception e) {
                            ClanSystem.getInstance().getLogger().severe("Could not set role of player " + commandSender.getName() + " to owner in clan " + clan.getName() + "!");
                            commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.unknown"));
                            throw new RuntimeException(e);
                        }

                        // Withdraw money
                        if (ClanSystem.getInstance().getEconomy() != null && clanPrice > 0) {
                            ClanSystem.getInstance().getEconomy().withdrawPlayer((Player) commandSender, clanPrice);
                        }

                        commandSender.sendMessage(ClanSystem.getInstance().getMessage("commands.clan.create.success")
                                .replace("%clan_name%", clanName)
                                .replace("%clan_tag%", clanTag));
                    });
                }
            };
            ClanSystem.getInstance().getConfirmationManager().addConfirmation(confirmation);
        });
        return true;
    }

    @Override
    public String getSubCommand() {
        return "create";
    }

    @Override
    public String syntax() {
        return "<name> <tag>";
    }
}
