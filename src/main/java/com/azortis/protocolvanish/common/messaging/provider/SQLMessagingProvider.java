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

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class SQLMessagingProvider implements MessagingProvider{

    private final Driver driver;
    private final Runnable runnable;

    private final Collection<UUID> sendMessages = new ArrayList<>();
    private final Collection<UUID> processedMessages = new ArrayList<>();

    public SQLMessagingProvider(MessagingService service, Driver driver){
        this.driver = driver;
        this.runnable = new SQLRunnable(this, service, driver);
        createTable();
    }

    @Override
    public void postMessage(Message message) {
        try(Connection connection = driver.getConnection()){
            PreparedStatement messageStatement = connection.prepareStatement("INSERT INTO " + driver.getTablePrefix() + "messages VALUES (?,?,?)");
            messageStatement.setString(1, message.getId().toString());
            messageStatement.setString(2, message.getMessage());
            messageStatement.setLong(3, System.currentTimeMillis());
            messageStatement.executeUpdate();
            messageStatement.close();
            sendMessages.add(message.getId());
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public Runnable getRunnable() {
        return runnable;
    }

    private class SQLRunnable implements Runnable{

        private final SQLMessagingProvider parent;
        private final MessagingService service;
        private final Driver driver;

        public SQLRunnable(SQLMessagingProvider parent, MessagingService service, Driver driver) {
            this.parent = parent;
            this.service = service;
            this.driver = driver;
        }

        @Override
        public void run() {
            try(Connection connection = driver.getConnection()){
                Statement fetchStatement = connection.createStatement();
                ResultSet resultSet = fetchStatement.executeQuery("SELECT * FROM " + driver.getTablePrefix() + "messages");
                Collection<UUID> fetchedMessageIds = new ArrayList<>();
                while(resultSet.next()){
                    UUID messageId = UUID.fromString(resultSet.getString("id"));
                    String message = resultSet.getString("message");
                    long timeStamp = resultSet.getLong("timeStamp");
                    if(!parent.sendMessages.contains(messageId)){
                        if(!parent.processedMessages.contains(messageId)) {
                            processedMessages.add(messageId);
                            service.consumeMessage(message);
                        }
                        fetchedMessageIds.add(messageId);
                    }else if((System.currentTimeMillis() - timeStamp) > 59000){
                        PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM " + driver.getTablePrefix() + "messages WHERE id=?");
                        deleteStatement.setString(1, messageId.toString());
                        deleteStatement.executeUpdate();
                        deleteStatement.close();
                    }
                }
                processedMessages.removeIf(uuid -> !fetchedMessageIds.contains(uuid));
            }catch (SQLException ex){
                ex.printStackTrace();
            }
        }
    }

    private void createTable(){
        try(Connection connection = driver.getConnection()){
            Statement messagingStatement = connection.createStatement();
            messagingStatement.executeUpdate("CREATE TABLE IF NOT EXISTS " + driver.getTablePrefix() + "messages (id varchar(36), message tinytext, timeStamp bigint)");
            messagingStatement.close();
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

}
