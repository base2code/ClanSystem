package de.base2code.clansystem.manager.objects;

import de.base2code.clansystem.ClanSystem;
import de.base2code.clansystem.database.tables.UsersTable;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public enum ClanPermissions {
    KICK, INVITE;

    public CompletableFuture<Boolean> hasPermission(Clan clan, UUID uuid) {
       return CompletableFuture.supplyAsync(() -> {
               ClanPermissionRole role = new UsersTable(ClanSystem.getInstance().getDataSource()).getPermissionRoleInClan(clan.getId(), uuid).join();
               ClanPermissionRole requiredRole = getPermissionRole(this);
               return role.getLevel() >= requiredRole.getLevel();
         }, ClanSystem.getExecutorService());
    }

    private static HashMap<ClanPermissions, ClanPermissionRole> permissionRoleHashMap = new HashMap<>();

    public static void setPermissionRole(ClanPermissions clanPermissions, ClanPermissionRole clanPermissionRole) {
        permissionRoleHashMap.put(clanPermissions, clanPermissionRole);
    }

    public static ClanPermissionRole getPermissionRole(ClanPermissions clanPermissions) {
        return permissionRoleHashMap.getOrDefault(clanPermissions, ClanPermissionRole.OWNER);
    }

}
