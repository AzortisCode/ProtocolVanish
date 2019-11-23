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

import com.azortis.azortislib.command.*;
import com.azortis.protocolvanish.PermissionManager;
import com.azortis.protocolvanish.ProtocolVanish;
import com.azortis.protocolvanish.settings.CommandSettingsWrapper;
import com.azortis.protocolvanish.settings.MessageSettingsWrapper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class VanishCommand implements ICommandExecutor, ITabCompleter {

    private ProtocolVanish plugin;
    private MessageSettingsWrapper messageSettings;

    public VanishCommand(ProtocolVanish plugin){
        this.plugin = plugin;
        CommandSettingsWrapper commandSettings = plugin.getSettingsManager().getCommandSettings();
        Command command = new CommandBuilder()
                .setName(commandSettings.getName())
                .setDescription(commandSettings.getDescription())
                .setUsage(commandSettings.getUsage())
                .addAliases(commandSettings.getAliases())
                .setPlugin(plugin).build();
        command.setExecutor(this);
        command.setTabCompleter(this);
        plugin.getAzortisLib().getCommandManager().register(commandSettings.getName(), command);
        this.messageSettings = plugin.getSettingsManager().getMessageSettings();
    }

    //TODO rework command, with current player settings system
    @Override
    public boolean onCommand(CommandSender sender, Command alCommand, String label, String[] args) {
        Player player = (Player)sender;
        if(plugin.getPermissionManager().hasPermissionToVanish(player)) {
            if(args.length == 0){
                if (plugin.getVisibilityManager().isVanished(player.getUniqueId())) {
                    plugin.getVisibilityManager().setVanished(player.getUniqueId(), false);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageSettings.getMessage("onReappear")));
                } else {
                    plugin.getVisibilityManager().setVanished(player.getUniqueId(), true);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageSettings.getMessage("onVanish")));
                }
                return true;
            } else {
                if (args[0].equalsIgnoreCase("tipu")) {
                    if (plugin.getPermissionManager().hasPermission(player, PermissionManager.Permission.CHANGE_ITEM_PICKUP)) {
                        boolean itemPickup = plugin.getVanishPlayer(player.getUniqueId()).getPlayerSettings().doItemPickUp();
                        if (!itemPickup) {
                            plugin.getVanishPlayer(player.getUniqueId()).getPlayerSettings().setItemPickUp(true);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageSettings.getMessage("enabledItemPickup")));
                            return true;
                        } else {
                            plugin.getVanishPlayer(player.getUniqueId()).getPlayerSettings().setItemPickUp(false);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageSettings.getMessage("disabledItemPickup")));
                            return true;
                        }
                    } else if (!plugin.getPermissionManager().hasPermission(player, PermissionManager.Permission.CHANGE_ITEM_PICKUP)) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageSettings.getMessage("noPermission")));
                        return false;
                    }
                }
            }
        }else{
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageSettings.getMessage("noPermission")));
        }
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String s, String[] strings, Location location) {
        return null;
    }
}
