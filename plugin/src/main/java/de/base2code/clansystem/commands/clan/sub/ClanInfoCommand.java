package de.base2code.clansystem.commands.clan.sub;

import de.base2code.clansystem.ClanSystem;
import de.base2code.clansystem.commands.SubCommand;
import de.base2code.clansystem.commands.clan.ClanCommand;
import de.base2code.clansystem.manager.ClanManager;
import de.base2code.clansystem.manager.objects.Clan;
import de.base2code.clansystem.manager.objects.ClanPermissionRole;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ClanInfoCommand implements SubCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, String[] args, boolean isPlayer) {
        if (!(isPlayer)) {
            commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.no-console"));
            return false;
        }

        final String[][] finalArgs = {args.clone()};
        CompletableFuture.runAsync(() -> {
            if (finalArgs[0].length != 1) {
                Clan clan = ClanSystem.getInstance().getClanManager().getClanOfUser(((Player) commandSender).getUniqueId()).join();
                if (clan != null) {
                    finalArgs[0] = new String[]{clan.getName()};
                } else {
                    ClanCommand.sendHelp(commandSender, this);
                }
            }

            CompletableFuture.runAsync(() -> {
                String arg = finalArgs[0][0];
                Clan clan;

                // TODO: make configurable
                if (arg.startsWith("#")) {
                    // Clan Tag
                    arg = arg.replace("#", "");
                    clan = ClanSystem.getInstance().getClanManager().getClanByTag(arg).join();
                } else {
                    // Clan Name
                    clan = ClanSystem.getInstance().getClanManager().getClanByName(arg).join();
                }

                if (clan == null) {
                    commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.clan-not-found"));
                    return;
                }

                HashMap<UUID, ClanPermissionRole> members = ClanSystem.getInstance().getClanManager().getUsersOfClan(clan).join();

                ArrayList<UUID> owners = new ArrayList<>();
                ArrayList<UUID> mods = new ArrayList<>();
                ArrayList<UUID> clanMembers = new ArrayList<>();

                for (UUID uuid : members.keySet()) {
                    ClanPermissionRole role = members.get(uuid);
                    if (role == ClanPermissionRole.OWNER) {
                        owners.add(uuid);
                    } else if (role == ClanPermissionRole.MODERATOR) {
                        mods.add(uuid);
                    } else {
                        clanMembers.add(uuid);
                    }
                }

                String message = ClanSystem.getInstance().getMessage("commands.clan.info.info");
                message = message.replace("%clan_name%", clan.getName());
                message = message.replace("%clan_tag%", clan.getTag());

                StringBuilder membersString = new StringBuilder("\n");
                membersString.append(ClanSystem.getInstance().getMessage("commands.clan.info.owners")).append("\n");
                for (UUID member : owners) {
                    membersString.append(ClanSystem.getInstance().getServer().getOfflinePlayer(member).getName()).append("\n");
                }
                membersString.append(ClanSystem.getInstance().getMessage("commands.clan.info.mods")).append("\n");
                for (UUID member : mods) {
                    membersString.append(ClanSystem.getInstance().getServer().getOfflinePlayer(member).getName()).append("\n");
                }
                membersString.append(ClanSystem.getInstance().getMessage("commands.clan.info.members")).append("\n");
                for (UUID member : clanMembers) {
                    membersString.append(ClanSystem.getInstance().getServer().getOfflinePlayer(member).getName()).append("\n");
                }
                message = message.replace("%clan_members%", membersString.toString());

                commandSender.sendMessage(message);

            });
        });

        return false;
    }

    @Override
    public String getSubCommand() {
        return "info";
    }

    @Override
    public String syntax() {
        return "[Name oder #Tag]";
    }
}
