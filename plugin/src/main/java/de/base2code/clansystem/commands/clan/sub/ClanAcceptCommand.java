package de.base2code.clansystem.commands.clan.sub;

import de.base2code.clansystem.ClanSystem;
import de.base2code.clansystem.commands.SubCommand;
import de.base2code.clansystem.commands.clan.ClanCommand;
import de.base2code.clansystem.manager.objects.ClanPermissionRole;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClanAcceptCommand implements SubCommand {
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

        String targetTag = strings[0].replace("#", "");

        ClanSystem.getInstance().getClanManager().getClanByTag(targetTag).whenComplete((clan, throwable) -> {
            if (throwable != null || clan == null) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.clan-not-found"));
                if (throwable != null) {
                    throwable.printStackTrace();
                }
                return;
            }

            if (!ClanSystem.getInstance().getClanInviteManager().getInvites(player.getUniqueId()).contains(clan.getId())) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("error.no-invite"));
                return;
            }

            ClanSystem.getInstance().getClanInviteManager().removeInvite(clan.getId(), player.getUniqueId());
            if (ClanSystem.getInstance().getClanManager().getClanMembers(clan) + 1 > clan.getMaxMembers()) {
                commandSender.sendMessage(ClanSystem.getInstance().getMessage("clan.accept.error.max-members"));
                return;
            }
            ClanSystem.getInstance().getClanManager().setPermissionRoleInClan(clan, player.getUniqueId(), ClanPermissionRole.MEMBER);

            commandSender.sendMessage(ClanSystem.getInstance().getMessage("clan.accept.success"));
        });

        return true;
    }

    @Override
    public String getSubCommand() {
        return "accept";
    }

    @Override
    public String syntax() {
        return "<#Tag>";
    }
}
