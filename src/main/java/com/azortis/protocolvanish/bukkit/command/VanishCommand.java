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

package com.azortis.protocolvanish.bukkit.command;

import com.azortis.azortislib.command.Command;
import com.azortis.azortislib.command.CommandInjector;
import com.azortis.azortislib.command.builders.CommandBuilder;
import com.azortis.azortislib.command.executors.ICommandExecutor;
import com.azortis.protocolvanish.bukkit.ProtocolVanish;
import com.azortis.protocolvanish.common.player.VanishPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements ICommandExecutor {

    private final ProtocolVanish plugin;

    public VanishCommand(ProtocolVanish plugin){
        this.plugin = plugin;
        CommandSettings commandSettings = plugin.getSettingsManager().getSettings().getCommandSettings();
        Command command = new CommandBuilder()
                .setName(commandSettings.getName())
                .setDescription(commandSettings.getDescription())
                .setUsage(commandSettings.getUsage())
                .addAliases(commandSettings.getAliases())
                .setPermission("protocolvanish.use")
                .setPlugin(plugin)
                .setExecutor(this).build();
        CommandInjector.injectCommand("protocolvanish", command, true);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage("This command cannot be run from console!");
            return false;
        } else if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (plugin.getPermissionManager().hasPermissionToVanish(player)) {
                VanishPlayer vanishPlayer = plugin.getVanishPlayer(player.getUniqueId());
                if (vanishPlayer == null) return false;
                if (args.length > 0) {
                    plugin.sendPlayerMessage(player, player, "invalidUsage", null);
                    return false;
                }
                if (vanishPlayer.isVanished()) {
                    plugin.getVisibilityManager().setVanished(player.getUniqueId(), false);
                    plugin.sendPlayerMessage(player, player, "onReappear", null);
                } else {
                    plugin.getVisibilityManager().setVanished(player.getUniqueId(), true);
                    plugin.sendPlayerMessage(player, player, "onVanish", null);
                }
                return true;
            } else {
                plugin.sendPlayerMessage(player, player, "noPermission", null);
                return false;
            }
        }
        return false;
    }
}
