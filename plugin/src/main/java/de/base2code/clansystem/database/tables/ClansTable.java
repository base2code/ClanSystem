package de.base2code.clansystem.database.tables;

import de.base2code.clansystem.ClanSystem;
import de.base2code.clansystem.manager.objects.Clan;
import de.base2code.clansystem.utils.Debug;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class ClansTable {
    private final DataSource dataSource;

    public ClansTable(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public CompletableFuture<Integer> insertClan(String name, String tag, int maxMembers, int balance, UUID owner) {
        Debug.debug(getClass(), "Inserting clan " + name + " with tag " + tag + " and owner " + owner);
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement("INSERT INTO clans (name, tag, max_members, balance, owner) VALUES (?, ?, ?, ?, ?)")) {
                ps.setString(1, name);
                ps.setString(2, tag);
                ps.setInt(3, maxMembers);
                ps.setInt(4, balance);
                ps.setString(5, owner.toString());
                return ps.executeUpdate();
            } catch (Exception e) {
                ClanSystem.getInstance().getLogger().severe("Error while inserting clan: " + e.getMessage());
                e.printStackTrace();
                throw new CompletionException(e);
            }
        }, ClanSystem.getExecutorService());
    }

    public CompletableFuture<Boolean> clanNameIsTaken(String name) {
        Debug.debug(getClass(), "Checking if clan name " + name + " is taken");
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT * FROM clans WHERE name = ?")) {
                ps.setString(1, name);
                boolean taken = ps.executeQuery().next();
                Debug.debug(getClass(), "Clan name " + name + " is " + (taken ? "" : "not ") + "taken");
                return taken;
            } catch (Exception e) {
                ClanSystem.getInstance().getLogger().severe("Error while checking if clan name is taken: " + e.getMessage());
                e.printStackTrace();
                throw new CompletionException(e);
            }
        }, ClanSystem.getExecutorService());
    }

    public CompletableFuture<Boolean> clanTagIsTaken(String tag) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT * FROM clans WHERE tag = ?")) {
                ps.setString(1, tag);
                boolean taken = ps.executeQuery().next();
                Debug.debug(getClass(), "Clan tag " + tag + " is " + (taken ? "" : "not ") + "taken");
                return taken;
            } catch (Exception e) {
                ClanSystem.getInstance().getLogger().severe("Error while checking if clan tag is taken: " + e.getMessage());
                e.printStackTrace();
                throw new CompletionException(e);
            }
        }, ClanSystem.getExecutorService());
    }

    public CompletableFuture<Clan> getClan(String name, String tag) {
        Debug.debug(getClass(), "Getting clan " + name + " with tag " + tag);
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT * FROM clans WHERE name = ? AND tag = ?")) {
                ps.setString(1, name);
                ps.setString(2, tag);
                ResultSet resultSet = ps.executeQuery();
                if (resultSet.next()) {
                    return
                            new Clan(
                                    resultSet.getInt("clan_id"),
                                    resultSet.getString("name"),
                                    resultSet.getString("tag"),
                                    resultSet.getInt("max_members"),
                                    resultSet.getInt("balance"),
                                    UUID.fromString(resultSet.getString("owner"))
                            );
                } else {
                    return null;
                }
            } catch (Exception e) {
                ClanSystem.getInstance().getLogger().severe("Error while getting clan: " + e.getMessage());
                e.printStackTrace();
                throw new CompletionException(e);
            }
        }, ClanSystem.getExecutorService());
    }

    public CompletableFuture<Clan> getClanOfUser(UUID uuid) {
        Debug.debug(getClass(), "Getting clan of user " + uuid);
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT * FROM clans INNER JOIN users ON clans.clan_id = users.clan_id WHERE users.uuid = ?")) {
                ps.setString(1, uuid.toString());
                ResultSet resultSet = ps.executeQuery();
                if (resultSet.next()) {
                    return
                            new Clan(
                                    resultSet.getInt("clan_id"),
                                    resultSet.getString("name"),
                                    resultSet.getString("tag"),
                                    resultSet.getInt("max_members"),
                                    resultSet.getInt("balance"),
                                    UUID.fromString(resultSet.getString("owner"))
                            );
                } else {
                    return null;
                }
            } catch (Exception e) {
                ClanSystem.getInstance().getLogger().severe("Error while getting clan of user: " + e.getMessage());
                e.printStackTrace();
                throw new CompletionException(e);
            }
        }, ClanSystem.getExecutorService());
    }

    public CompletableFuture<Clan> getClanByName(String name) {
        Debug.debug(getClass(), "Getting clan by name " + name);
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT * FROM clans WHERE name = ?")) {
                ps.setString(1, name);
                ResultSet resultSet = ps.executeQuery();
                if (resultSet.next()) {
                    return
                            new Clan(
                                    resultSet.getInt("clan_id"),
                                    resultSet.getString("name"),
                                    resultSet.getString("tag"),
                                    resultSet.getInt("max_members"),
                                    resultSet.getInt("balance"),
                                    UUID.fromString(resultSet.getString("owner"))
                            );
                } else {
                    return null;
                }
            } catch (Exception e) {
                ClanSystem.getInstance().getLogger().severe("Error while getting clan by name: " + e.getMessage());
                e.printStackTrace();
                throw new CompletionException(e);
            }
        }, ClanSystem.getExecutorService());
    }

    public CompletableFuture<Clan> getClanByTag(String tag) {
        Debug.debug(getClass(), "Getting clan by tag " + tag);
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT * FROM clans WHERE tag = ?")) {
                ps.setString(1, tag);
                ResultSet resultSet = ps.executeQuery();
                if (resultSet.next()) {
                    return
                            new Clan(
                                    resultSet.getInt("clan_id"),
                                    resultSet.getString("name"),
                                    resultSet.getString("tag"),
                                    resultSet.getInt("max_members"),
                                    resultSet.getInt("balance"),
                                    UUID.fromString(resultSet.getString("owner"))
                            );
                } else {
                    return null;
                }
            } catch (Exception e) {
                ClanSystem.getInstance().getLogger().severe("Error while getting clan by tag: " + e.getMessage());
                e.printStackTrace();
                throw new CompletionException(e);
            }
        }, ClanSystem.getExecutorService());
    }

    public void deleteClan(Clan clan) {
        // TODO: Test if mysql statement can be executed on foreign key
        Debug.debug(getClass(), "Deleting clan " + clan.getName());
        ClanSystem.getExecutorService().execute(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement("DELETE FROM clans WHERE clan_id = ?")) {
                ps.setInt(1, clan.getId());
                ps.executeUpdate();
            } catch (Exception e) {
                ClanSystem.getInstance().getLogger().severe("Error while deleting clan: " + e.getMessage());
                e.printStackTrace();
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Clan> getClanById(int id) {
        Debug.debug(getClass(), "Getting clan by id " + id);
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT * FROM clans WHERE clan_id = ?")) {
                ps.setInt(1, id);
                ResultSet resultSet = ps.executeQuery();
                if (resultSet.next()) {
                    return
                            new Clan(
                                    resultSet.getInt("clan_id"),
                                    resultSet.getString("name"),
                                    resultSet.getString("tag"),
                                    resultSet.getInt("max_members"),
                                    resultSet.getInt("balance"),
                                    UUID.fromString(resultSet.getString("owner"))
                            );
                } else {
                    return null;
                }
            } catch (Exception e) {
                ClanSystem.getInstance().getLogger().severe("Error while getting clan by tag: " + e.getMessage());
                e.printStackTrace();
                throw new CompletionException(e);
            }
        }, ClanSystem.getExecutorService());
    }
}
