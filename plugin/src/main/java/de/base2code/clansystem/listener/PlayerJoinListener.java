package de.base2code.clansystem.listener;

import de.base2code.clansystem.ClanSystem;
import de.base2code.clansystem.manager.ClanManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        List<Integer> clanInvites = ClanSystem.getInstance().getClanInviteManager().getInvites(event.getPlayer().getUniqueId());
        if (clanInvites != null && !clanInvites.isEmpty()) {
            clanInvites.forEach(clanId -> {
                ClanSystem.getInstance().getClanInviteManager().sendMessage(clanId, event.getPlayer());
            });
        }

        Player player = event.getPlayer();
        Scoreboard scoreboard;
        if(player.getScoreboard() == null) scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        else scoreboard = player.getScoreboard();
        player.setScoreboard(scoreboard);

        ClanSystem.getInstance().getClanManager().getClanOfUser(player.getUniqueId()).whenComplete(((clan, throwable) -> {
            Team team = clan.getTeam(scoreboard);
            team.addPlayer(player);
        }));
    }
}
