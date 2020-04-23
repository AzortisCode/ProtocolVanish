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

import com.azortis.protocolvanish.common.PlayerSettings;
import com.azortis.protocolvanish.common.VanishPlayer;
import com.azortis.protocolvanish.common.storage.Driver;
import com.azortis.protocolvanish.common.storage.StorageSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaxxer.hikari.HikariDataSource;

import java.io.StringReader;
import java.sql.*;
import java.util.UUID;

public class MariaDBDriver implements Driver{

    private final HikariDataSource hikari;
    private final String tablePrefix;
    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public MariaDBDriver(StorageSettings storageSettings){
        hikari = new HikariDataSource();
        hikari.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
        hikari.addDataSourceProperty("serverName", storageSettings.getIp());
        hikari.addDataSourceProperty("port", storageSettings.getPort());
        hikari.addDataSourceProperty("databaseName", storageSettings.getDatabase());
        hikari.addDataSourceProperty("username", storageSettings.getUsername());
        hikari.addDataSourceProperty("password", storageSettings.getPassword());
        this.tablePrefix = storageSettings.getTablePrefix();
        createTables();
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

    }

    @Override
    public void deleteVanishPlayer(VanishPlayer vanishPlayer) {

    }

    @Override
    public void createVanishPlayer(VanishPlayer vanishPlayer) {

    }

    private void createTables(){
        try(Connection connection = hikari.getConnection()){
            Statement vanishPlayerStatement = connection.createStatement();
            vanishPlayerStatement.executeUpdate("CREATE TABLE WHEN NOT EXISTS " + tablePrefix + "vanishPlayers(uuid varchar(36), vanished BOOL, playerSettings varchar)");
            vanishPlayerStatement.close();
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

}
