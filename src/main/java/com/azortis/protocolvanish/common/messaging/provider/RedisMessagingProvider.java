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
import com.azortis.protocolvanish.common.messaging.RedisSettings;
import com.azortis.protocolvanish.common.messaging.message.Message;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class RedisMessagingProvider implements MessagingProvider {

    private final Jedis jedis;
    private final Runnable runnable;

    private final transient Collection<UUID> sendMessages = new ArrayList<>();
    private final transient Collection<UUID> processedMessages = new ArrayList<>();

    public RedisMessagingProvider(MessagingService service, RedisSettings redisSettings){
        this.jedis = new Jedis();
        this.runnable = null;
    }

    @Override
    public void postMessage(Message message) {

    }

    @Override
    public void clearMessages() {

    }

    @Override
    public Runnable getRunnable() {
        return null;
    }

    public static class RedisRunnable implements Runnable {

        private final RedisMessagingProvider parent;
        private final MessagingService service;
        private final Jedis jedis;

        public RedisRunnable(RedisMessagingProvider parent, MessagingService service, Jedis jedis){
            this.parent = parent;
            this.service = service;
            this.jedis = jedis;
        }

        @Override
        public void run() {

        }
    }

}
