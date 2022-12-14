package de.base2code.clansystem.listener;

import de.base2code.clansystem.ClanSystem;
import de.base2code.clansystem.manager.PrefixBuilder;
import de.base2code.clansystem.manager.objects.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Clan clan = ClanSystem.getInstance().getClanManager().getClanOfUser(player.getUniqueId()).join();
        if (clan == null) return;
        event.setFormat(PrefixBuilder.chatPrefix(clan) + " " + event.getFormat());
    }
}
