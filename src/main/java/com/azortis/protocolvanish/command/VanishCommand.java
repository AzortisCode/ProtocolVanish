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

package com.azortis.protocolvanish.command;

import com.azortis.azortislib.command.AlCommand;
import com.azortis.azortislib.command.CommandBuilder;
import com.azortis.azortislib.command.IAlCommandExecutor;
import com.azortis.azortislib.command.IAlTabCompleter;
import com.azortis.protocolvanish.ProtocolVanish;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class VanishCommand implements IAlCommandExecutor, IAlTabCompleter {

    private ProtocolVanish plugin;

    public VanishCommand(ProtocolVanish plugin){
        this.plugin = plugin;
        AlCommand command = new CommandBuilder()
                .setName("vanish").build();
        command.setExecutor(this);
        plugin.getAzortisLib().getCommandManager().register("vanish", command);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, AlCommand alCommand, String s, String[] strings) {
        Player p = (Player)commandSender;
        if(plugin.getVisibilityManager().isVanished(p.getUniqueId())){
            plugin.getVisibilityManager().setVanished(p.getUniqueId(), false);
            commandSender.sendMessage(ChatColor.GREEN + "You're no longer vanished!");
        }else{
            plugin.getVisibilityManager().setVanished(p.getUniqueId(), true);
            commandSender.sendMessage(ChatColor.GREEN + "You're vanished!");
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String s, String[] strings, Location location) {
        return null;
    }
}
