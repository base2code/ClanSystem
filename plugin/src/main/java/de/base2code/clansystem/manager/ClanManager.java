package de.base2code.clansystem.manager;

import de.base2code.clansystem.ClanSystem;
import de.base2code.clansystem.database.tables.ClansTable;
import de.base2code.clansystem.database.tables.UsersTable;
import de.base2code.clansystem.manager.objects.Clan;
import de.base2code.clansystem.manager.objects.ClanPermissionRole;
import de.base2code.clansystem.utils.Debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ClanManager {
    public CompletableFuture<Boolean> isClanNameTaken(String name) {
        return new ClansTable(ClanSystem.getInstance().getDataSource()).clanNameIsTaken(name);
    }

    public CompletableFuture<Boolean> isClanTagTaken(String tag) {
        return new ClansTable(ClanSystem.getInstance().getDataSource()).clanTagIsTaken(tag);
    }

    public CompletableFuture<Boolean> createClan(String name, String tag, UUID owner) {
        return CompletableFuture.supplyAsync(() -> {
            Debug.debug(getClass(), "Requesting clan creation for " + name + " with tag " + tag + " and owner " + owner);
            return new ClansTable(ClanSystem.getInstance().getDataSource()).insertClan(name, tag, 10, 0, owner).join() == 1;
        }, ClanSystem.getExecutorService());
    }

    public CompletableFuture<Clan> getClanOfUser(UUID uuid) {
        return new ClansTable(ClanSystem.getInstance().getDataSource()).getClanOfUser(uuid);
    }

    public CompletableFuture<Clan> getClan(String name, String tag) {
        return new ClansTable(ClanSystem.getInstance().getDataSource()).getClan(name, tag);
    }

    public CompletableFuture<Boolean> setPermissionRoleInClan(Clan clan, UUID uuid, ClanPermissionRole role) {
        return new UsersTable(ClanSystem.getInstance().getDataSource()).setPermissionRoleInClan(clan.getId(), uuid, role);
    }

    public CompletableFuture<ClanPermissionRole> getPermissionRoleInClan(Clan clan, UUID uuid) {
        return new UsersTable(ClanSystem.getInstance().getDataSource()).getPermissionRoleInClan(clan.getId(), uuid);
    }

    public CompletableFuture<Clan> getClanByName(String name) {
        return new ClansTable(ClanSystem.getInstance().getDataSource()).getClanByName(name);
    }

    public CompletableFuture<Clan> getClanByTag(String tag) {
        return new ClansTable(ClanSystem.getInstance().getDataSource()).getClanByTag(tag);
    }

    public CompletableFuture<HashMap<UUID, ClanPermissionRole>> getUsersOfClan(Clan clan) {
        return new UsersTable(ClanSystem.getInstance().getDataSource()).getUsersOfClan(clan.getId());
    }

    public void leaveClan(UUID uuid) {
        new UsersTable(ClanSystem.getInstance().getDataSource()).leaveClan(uuid);
    }

    public CompletableFuture<Clan> getClanById(int id) {
        return new ClansTable(ClanSystem.getInstance().getDataSource()).getClanById(id);
    }

    public void deleteClan(Clan clan) {
        CompletableFuture.runAsync(() -> {
            Set<UUID> members = new UsersTable(ClanSystem.getInstance().getDataSource()).getUsersOfClan(clan.getId()).join().keySet();
            for (UUID member : members) {
                new UsersTable(ClanSystem.getInstance().getDataSource()).leaveClan(member);
            }
            new ClansTable(ClanSystem.getInstance().getDataSource()).deleteClan(clan);
        });
    }

    public int getClanMembers(Clan clan) {
        return new UsersTable(ClanSystem.getInstance().getDataSource()).getUsersOfClan(clan.getId()).join().size();
    }
}
