package de.base2code.clansystem.manager.objects;

import de.base2code.clansystem.ClanSystem;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

public class Clan {
    private final int id;
    private final String name;
    private final String tag;

    private int maxMembers;
    private int balance;
    private UUID owner;

    public Clan(int id, String name, String tag, int maxMembers, int balance, UUID owner) {
        this.id = id;
        this.name = name;
        this.tag = tag;
        this.maxMembers = maxMembers;
        this.balance = balance;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public Team getTeam(Scoreboard scoreboard) {
        Team team = null;
        if (scoreboard.getTeam(tag) == null) {
            team = scoreboard.registerNewTeam(tag);
        } else {
            team = scoreboard.getTeam(tag);
        }
        if (tag.equalsIgnoreCase("team")) {
            team.setPrefix(ClanSystem.getInstance().getConfigManager().getConfiguration().getString("clan.team.suffix"));
        } else {
            team.setPrefix(ClanSystem.getInstance().getConfigManager().getConfiguration().getString("clan.suffix").replace("%tag%", tag));
        }
        return team;
    }
}
