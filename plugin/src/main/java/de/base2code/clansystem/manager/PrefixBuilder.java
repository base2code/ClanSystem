package de.base2code.clansystem.manager;

import de.base2code.clansystem.manager.objects.Clan;
import org.bukkit.entity.Player;

public class PrefixBuilder {
    public static String chatPrefix(Clan clan) {
        return "§7[§6" + clan.getTag() + "§7] §r";
    }
}
