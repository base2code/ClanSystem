package de.base2code.clansystem.database.tables;

import de.base2code.clansystem.ClanSystem;
import de.base2code.clansystem.manager.objects.ClanPermissionRole;
import de.base2code.clansystem.manager.objects.ClanPermissions;
import de.base2code.clansystem.utils.Debug;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class UsersTable {
    private DataSource dataSource;

    public UsersTable(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public CompletableFuture<Boolean> setPermissionRoleInClan(int clanId, UUID uuid, ClanPermissionRole role) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO users (clan_id, uuid, role) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE role = ?, clan_id = ?");
                statement.setString(1, String.valueOf(clanId));
                statement.setString(2, uuid.toString());
                statement.setString(3, role.name());
                statement.setString(4, role.name());
                statement.setString(5, String.valueOf(clanId));
                return statement.executeUpdate() == 1;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }, ClanSystem.getExecutorService());
    }

    public CompletableFuture<HashMap<UUID, ClanPermissionRole>> getUsersOfClan(int clanId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE clan_id = ?")) {
                ps.setInt(1, clanId);
                ResultSet resultSet = ps.executeQuery();
                HashMap<UUID, ClanPermissionRole> users = new HashMap<>();
                while (resultSet.next()) {
                    users.put(UUID.fromString(resultSet.getString("uuid")), ClanPermissionRole.valueOf(resultSet.getString("role")));
                }
                return users;
            } catch (Exception e) {
                ClanSystem.getInstance().getLogger().severe("Error while getting users of clan: " + e.getMessage());
                e.printStackTrace();
                throw new CompletionException(e);
            }
        }, ClanSystem.getExecutorService());
    }

    public void leaveClan(UUID uuid) {
        Debug.debug(getClass(), "Leaving clan of user " + uuid);
        ClanSystem.getExecutorService().execute(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement("DELETE FROM users WHERE uuid = ?")) {
                ps.setString(1, uuid.toString());
                ps.executeUpdate();
            } catch (Exception e) {
                ClanSystem.getInstance().getLogger().severe("Error while leaving clan: " + e.getMessage());
                e.printStackTrace();
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<ClanPermissionRole> getPermissionRoleInClan(int id, UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE clan_id = ? AND uuid = ?")) {
                ps.setInt(1, id);
                ps.setString(2, uuid.toString());
                ResultSet resultSet = ps.executeQuery();
                if (resultSet.next()) {
                    return ClanPermissionRole.valueOf(resultSet.getString("role"));
                }
                return null;
            } catch (Exception e) {
                ClanSystem.getInstance().getLogger().severe("Error while getting permission role in clan: " + e.getMessage());
                e.printStackTrace();
                throw new CompletionException(e);
            }
        }, ClanSystem.getExecutorService());
    }
}
