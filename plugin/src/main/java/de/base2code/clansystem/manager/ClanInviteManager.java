package de.base2code.clansystem.manager;

import de.base2code.clansystem.ClanSystem;
import org.bukkit.entity.Player;

import java.util.*;

public class ClanInviteManager {
    private final Map<Integer, ArrayList<UUID>> invites = new HashMap<>();

    public void addInvite(int clanId, UUID uuid) {
        ArrayList<UUID> uuids;
        if (invites.containsKey(clanId)) {
            uuids = invites.get(clanId);
        } else {
            uuids = new ArrayList<>();
        }
        uuids.add(uuid);
        invites.put(clanId, uuids);
    }

    public void removeInvite(int clanId, UUID uuid) {
        if (invites.containsKey(clanId)) {
            ArrayList<UUID> uuids = invites.get(clanId);
            uuids.remove(uuid);
            invites.put(clanId, uuids);
        }
    }

    public boolean hasInvite(int clanId, UUID uuid) {
        if (invites.containsKey(clanId)) {
            ArrayList<UUID> uuids = invites.get(clanId);
            return uuids.contains(uuid);
        }
        return false;
    }

    public List<UUID> getInvites(int clanId) {
        if (invites.containsKey(clanId)) {
            return invites.get(clanId);
        }
        return new ArrayList<>();
    }

    public List<Integer> getInvites(UUID uuid) {
        ArrayList<Integer> clanIds = new ArrayList<>();
        for (int clanId : invites.keySet()) {
            ArrayList<UUID> uuids = invites.get(clanId);
            if (uuids.contains(uuid)) {
                clanIds.add(clanId);
            }
        }
        return clanIds;
    }

    public void sendMessage(int clanId, Player player) {
        ClanSystem.getInstance().getClanManager().getClanById(clanId).whenComplete((clan, throwable) -> {
            if (clan != null) {
                player.sendMessage(ClanSystem.getInstance().getMessage("clan.invite.message")
                        .replace("%name%", clan.getName())
                        .replace("%tag%", clan.getTag()));
            }
        });
    }
}
