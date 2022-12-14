package de.base2code.clansystem.manager.confirmations;

import de.base2code.clansystem.ClanSystem;
import de.base2code.clansystem.commands.clan.sub.ClanCreateCommand;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ConfirmationManager {
    private final HashMap<Player, Confirmation> confirmations = new HashMap<>();

    public void addConfirmation(Confirmation confirmation) {
        confirmations.put(confirmation.getPlayer(), confirmation);
        confirmation.getPlayer().sendMessage(ClanSystem.getInstance().getMessage("commands.clan.confirmation.pending"));
    }

    public void removeConfirmation(Player player) {
        confirmations.remove(player);
    }

    public Confirmation getConfirmation(Player player) {
        return confirmations.get(player);
    }
}
