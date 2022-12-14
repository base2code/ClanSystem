package de.base2code.clansystem.manager.confirmations;

import de.base2code.clansystem.ClanSystem;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class Confirmation {
    private Player player;
    private long time;

    public Confirmation(Player player) {
        this.player = player;
        this.time = System.currentTimeMillis();
    }

    public abstract void run();

    public Player getPlayer() {
        return player;
    }

    public long getTime() {
        return time;
    }

    public void remove() {
        ClanSystem.getInstance().getConfirmationManager().removeConfirmation(player);
    }
}
