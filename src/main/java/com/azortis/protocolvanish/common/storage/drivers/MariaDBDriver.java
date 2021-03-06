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

package com.azortis.protocolvanish.common.storage.drivers;

import com.azortis.protocolvanish.common.player.PlayerSettings;
import com.azortis.protocolvanish.common.player.VanishPlayer;
import com.azortis.protocolvanish.common.storage.Driver;
import com.azortis.protocolvanish.common.storage.StorageSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaxxer.hikari.HikariDataSource;

import java.io.StringReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class MariaDBDriver implements Driver{

    private final HikariDataSource hikari;
    private final String tablePrefix;
    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public MariaDBDriver(StorageSettings storageSettings){
        hikari = new HikariDataSource();
        hikari.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
        hikari.addDataSourceProperty("serverName", storageSettings.getAddress().split(":")[0]);
        hikari.addDataSourceProperty("port", storageSettings.getAddress().split(":")[1]);
        hikari.addDataSourceProperty("databaseName", storageSettings.getDatabase());
        hikari.addDataSourceProperty("userName", storageSettings.getUsername());
        hikari.addDataSourceProperty("password", storageSettings.getPassword());
        this.tablePrefix = storageSettings.getTablePrefix();
        createTable();
    }

    @Override
    public VanishPlayer getVanishPlayer(UUID uuid) {
        try(Connection connection = hikari.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + tablePrefix + "vanishPlayers WHERE uuid=?");
            preparedStatement.setString(1, uuid.toString());

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                PlayerSettings playerSettings = gson.fromJson(new StringReader(resultSet.getString("playerSettings")), PlayerSettings.class);
                VanishPlayer vanishPlayer = new VanishPlayer(uuid, resultSet.getBoolean("vanished"), playerSettings);
                resultSet.close();
                preparedStatement.close();
                connection.close();
                return vanishPlayer;
            }
            resultSet.close();
            preparedStatement.close();
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveVanishPlayer(VanishPlayer vanishPlayer) {
        try(Connection connection = hikari.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + tablePrefix + "vanishPlayers SET vanished=?, playerSettings=? WHERE uuid=?");
            preparedStatement.setBoolean(1, vanishPlayer.isVanished());
            preparedStatement.setString(2, vanishPlayer.getPlayerSettings().toString());
            preparedStatement.setString(3, vanishPlayer.getUUID().toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void deleteVanishPlayer(UUID uuid) {
        try(Connection connection = hikari.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM " + tablePrefix + "vanishPlayers WHERE uuid=?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void createVanishPlayer(VanishPlayer vanishPlayer) {
        try(Connection connection = hikari.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + tablePrefix + "vanishPlayers VALUES (?,?,?)");
            preparedStatement.setString(1, vanishPlayer.getUUID().toString());
            preparedStatement.setBoolean(2, vanishPlayer.isVanished());
            preparedStatement.setString(3, vanishPlayer.getPlayerSettings().toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public Collection<UUID> getVanishedUUIDs() {
        try(Connection connection = hikari.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT uuid FROM " + tablePrefix + "vanishPlayers WHERE vanished=true");
            Collection<UUID> UUIDs = new ArrayList<>();
            while(resultSet.next()){
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                UUIDs.add(uuid);
            }
            return UUIDs;
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return null;
    }

    private void createTable(){
        try(Connection connection = hikari.getConnection()){
            Statement vanishPlayerStatement = connection.createStatement();
            Statement playerSettingsStatement = connection.createStatement();
            vanishPlayerStatement.executeUpdate("CREATE TABLE IF NOT EXISTS " + tablePrefix + "vanishPlayers(uuid varchar(36), vanished boolean);");
            playerSettingsStatement.executeUpdate("CREATE TABLE IF NOT EXISTS " + tablePrefix + "playerSettings(uuid varchar(36), serverId varchar(36), isGlobal boolean, settings TEXT)");
            vanishPlayerStatement.close();
            playerSettingsStatement.close();
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public Connection getConnection()throws SQLException {
        return hikari.getConnection();
    }

    @Override
    public String getTablePrefix() {
        return tablePrefix;
    }
}
