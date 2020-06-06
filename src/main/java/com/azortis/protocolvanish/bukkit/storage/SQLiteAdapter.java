/*
 * Hides you completely from players on your servers by using packets!
 *     Copyright (C) 2019  Azortis
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.azortis.protocolvanish.bukkit.storage;

import com.azortis.protocolvanish.bukkit.Metrics;
import com.azortis.protocolvanish.bukkit.ProtocolVanish;
import com.azortis.protocolvanish.bukkit.VanishPlayer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.*;

@SuppressWarnings("all")
public class SQLiteAdapter implements IDatabase {

    private final ProtocolVanish plugin;
    private String jdbcurl;
    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    SQLiteAdapter(ProtocolVanish plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("Loading database file...");
        File dbFile = new File(plugin.getDataFolder(), "storage.db");
        try {
            if (!dbFile.exists()) {
                plugin.getLogger().info("Database file doesn't exist, creating one...");
                dbFile.createNewFile();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            plugin.getLogger().severe("Cannot create database file, shutting down!");
            plugin.getPluginLoader().disablePlugin(plugin);
        }
        this.jdbcurl = "jdbc:sqlite:" + dbFile.getPath();
        createTables();
        createServerInfoEntry();
        plugin.getMetrics().addCustomChart(new Metrics.SingleLineChart("players_in_vanish", () -> {
            try (Connection connection = createConnection()) {
                PreparedStatement statement = connection.prepareStatement("SELECT playersInVanish FROM serverInfo WHERE serverId=?");
                statement.setInt(1, 1);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    int playersInVanish = resultSet.getInt("playersInVanish");
                    resultSet.close();
                    statement.close();
                    connection.close();
                    return playersInVanish;
                }
                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return 0;
        }));
    }

    @Override
    public VanishPlayer getVanishPlayer(Player player) {
        try (Connection connection = createConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT vanished FROM vanishPlayers WHERE uuid=?");
            statement.setString(1, player.getUniqueId().toString());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                VanishPlayer vanishPlayer = new VanishPlayer(player, resultSet.getBoolean(1));
                resultSet.close();
                statement.close();
                connection.close();
                return vanishPlayer;
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public VanishPlayer.PlayerSettings getPlayerSettings(Player player) {
        try (Connection connection = createConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT playerSettings FROM vanishPlayers WHERE uuid=?");
            statement.setString(1, player.getUniqueId().toString());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                VanishPlayer.PlayerSettings playerSettings = gson.fromJson(resultSet.getString(1), VanishPlayer.PlayerSettings.class);
                playerSettings.setParent(plugin.getVanishPlayer(player.getUniqueId()));
                resultSet.close();
                statement.close();
                connection.close();
                return playerSettings;
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveVanishPlayer(VanishPlayer vanishPlayer) {
        try (Connection connection = createConnection()) {
            PreparedStatement statement = connection.prepareStatement("UPDATE vanishPlayers SET vanished=? WHERE uuid=?");
            statement.setBoolean(1, vanishPlayer.isVanished());
            statement.setString(2, vanishPlayer.getPlayer().getUniqueId().toString());
            statement.execute();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void savePlayerSettings(VanishPlayer.PlayerSettings playerSettings) {
        try (Connection connection = createConnection()) {
            PreparedStatement statement = connection.prepareStatement("UPDATE vanishPlayers SET playerSettings=? WHERE uuid=?");
            statement.setString(1, gson.toJson(playerSettings));
            statement.setString(2, playerSettings.getParent().getPlayer().getUniqueId().toString());
            statement.execute();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void createVanishPlayer(VanishPlayer vanishPlayer) {
        try (Connection connection = createConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO vanishPlayers VALUES (?,?,?)");
            statement.setString(1, vanishPlayer.getPlayer().getUniqueId().toString());
            statement.setBoolean(2, vanishPlayer.isVanished());
            statement.setString(3, gson.toJson(vanishPlayer.getPlayerSettings()));
            statement.execute();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void deleteVanishPlayer(VanishPlayer vanishPlayer) {
        try (Connection connection = createConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM vanishPlayers WHERE uuid=?");
            statement.setString(1, vanishPlayer.getPlayer().getUniqueId().toString());
            statement.execute();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void updateServerInfo() {
        try (Connection connection = createConnection()) {
            PreparedStatement statement = connection.prepareStatement("UPDATE serverInfo SET playersInVanish=? WHERE serverId=?");
            statement.setInt(1, plugin.getVisibilityManager().getVanishedPlayers().size());
            statement.setInt(2, 1);
            statement.execute();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void createTables() {
        try (Connection connection = createConnection()) {
            Statement vanishPlayerStatement = connection.createStatement();
            vanishPlayerStatement.executeUpdate("CREATE TABLE IF NOT EXISTS vanishPlayers (uuid varchar(36), vanished boolean, playerSettings varchar)");

            Statement serverInfoStatement = connection.createStatement();
            serverInfoStatement.execute("CREATE TABLE IF NOT EXISTS serverInfo (serverId SMALLINT, playersInVanish SMALLINT)");
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void createServerInfoEntry() {
        try (Connection connection = createConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT playersInVanish FROM serverInfo WHERE serverId=?");
            statement.setInt(1, 1);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                resultSet.close();
                statement.close();
                connection.close();

                Connection createConnection = createConnection();
                PreparedStatement createStatement = createConnection.prepareStatement("INSERT INTO serverInfo VALUES (?,?)");
                createStatement.setInt(1, 1);
                createStatement.setInt(2, 0);
                createStatement.execute();
                createStatement.close();
                createConnection.close();
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private Connection createConnection() {
        try {
            return DriverManager.getConnection(jdbcurl);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
