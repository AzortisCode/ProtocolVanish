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

package com.azortis.protocolvanish.common.messaging.provider;

import com.azortis.protocolvanish.common.messaging.MessagingService;
import com.azortis.protocolvanish.common.messaging.message.Message;
import com.azortis.protocolvanish.common.storage.Driver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLMessagingProvider implements MessagingProvider{

    private final MessagingService service;
    private final Driver driver;
    private final Runnable runnable;

    public SQLMessagingProvider(MessagingService service, Driver driver){
        this.service = service;
        this.driver = driver;
        this.runnable = new SQLRunnable(service, driver);
        createTable();
    }

    @Override
    public void postMessage(Message message) {
        try(Connection connection = driver.getConnection()){
            PreparedStatement messageStatement = connection.prepareStatement("INSERT INTO " + driver.getTablePrefix() + "messages VALUES (?,?)");
            messageStatement.setString(1, message.getId().toString());
            messageStatement.setString(1, message.getMessage());
            messageStatement.executeUpdate();
            messageStatement.close();
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public Runnable getRunnable() {
        return runnable;
    }

    private class SQLRunnable implements Runnable{

        private MessagingService service;
        private Driver driver;

        public SQLRunnable(MessagingService service, Driver driver) {
            this.service = service;
            this.driver = driver;
        }

        @Override
        public void run() {

        }
    }

    private void createTable(){
        try(Connection connection = driver.getConnection()){
            Statement messagingStatement = connection.createStatement();
            messagingStatement.executeUpdate("CREATE TABLE IF NOT EXISTS " + driver.getTablePrefix() + "messages(id varchar(36), message tinytext)");
            messagingStatement.close();
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

}
