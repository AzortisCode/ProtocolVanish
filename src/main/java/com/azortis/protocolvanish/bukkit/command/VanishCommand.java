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
import com.azortis.azortislib.command.builders.SubCommandBuilder;
import com.azortis.azortislib.command.executors.ICommandExecutor;
import com.azortis.azortislib.command.executors.ITabCompleter;
import com.azortis.protocolvanish.bukkit.PermissionManager;
import com.azortis.protocolvanish.bukkit.ProtocolVanish;
import com.azortis.protocolvanish.bukkit.command.subcommands.*;
import com.azortis.protocolvanish.bukkit.settings.old.wrappers.CommandSettingsWrapper;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VanishCommand implements ICommandExecutor, ITabCompleter {

    private final ProtocolVanish plugin;
    private Collection<String> enabledSubCommands = new ArrayList<>();

    public VanishCommand(ProtocolVanish plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("Loading command...");
        CommandSettingsWrapper commandSettings = plugin.getSettingsManager().getCommandSettings();
        Command command = new CommandBuilder()
                .setName(commandSettings.getName())
                .setDescription(commandSettings.getDescription())
                .setUsage(commandSettings.getUsage())
                .addAliases(commandSettings.getAliases())
                .setPlugin(plugin)
                .setExecutor(this)
                .setTabCompleter(this)
                .addSubCommands(
                        new SubCommandBuilder()
                                .setName(commandSettings.getSubCommandName("toggleNightVision"))
                                .setExecutor(new ToggleNightVisionSub(plugin))
                                .addAliases(commandSettings.getSubCommandAliases("toggleNightVision")),
                        new SubCommandBuilder()
                                .setName(commandSettings.getSubCommandName("toggleDamage"))
                                .setExecutor(new ToggleDamageSub(plugin))
                                .addAliases(commandSettings.getSubCommandAliases("toggleDamage")),
                        new SubCommandBuilder()
                                .setName(commandSettings.getSubCommandName("toggleHunger"))
                                .setExecutor(new ToggleHungerSub(plugin))
                                .addAliases(commandSettings.getSubCommandAliases("toggleHunger")),
                        new SubCommandBuilder()
                                .setName(commandSettings.getSubCommandName("toggleCreatureTarget"))
                                .setExecutor(new ToggleCreatureTargetSub(plugin))
                                .addAliases(commandSettings.getSubCommandAliases("toggleCreatureTarget")),
                        new SubCommandBuilder()
                                .setName(commandSettings.getSubCommandName("toggleItemPickup"))
                                .setExecutor(new ToggleItemPickupSub(plugin))
                                .addAliases(commandSettings.getSubCommandAliases("toggleItemPickup"))
                ).build();
        CommandInjector.injectCommand("protocolvanish", command, false);
        String[] subCommands = new String[]{"toggleNightVision", "toggleDamage", "toggleHunger", "toggleCreatureTarget", "toggleItemPickup"};
        for (String subCommand : subCommands) {
            if (commandSettings.isSubCommandEnabled(subCommand)) this.enabledSubCommands.add(subCommand);
        }
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
                if (vanishPlayer == null) vanishPlayer = plugin.createVanishPlayer(player);
                if(args.length > 0){
                    plugin.sendPlayerMessage(player, player,"invalidUsage");
                    return false;
                }
                if (vanishPlayer.isVanished()) {
                    plugin.getVisibilityManager().setVanished(player.getUniqueId(), false);
                    plugin.sendPlayerMessage(player, player,"onReappear");
                } else {
                    plugin.getVisibilityManager().setVanished(player.getUniqueId(), true);
                    plugin.sendPlayerMessage(player, player,"onVanish");
                }
                return true;
            } else {
                plugin.sendPlayerMessage(player, player, "noPermission");
                return false;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args, Location location) {
        if(!(commandSender instanceof Player))return null;
        Player player = (Player)commandSender;
        if(args.length < 2 && plugin.getPermissionManager().hasPermissionToVanish(player)){
            List<String> suggestions = new ArrayList<>();
            for (String subCommand : this.enabledSubCommands){
                switch (subCommand){
                    case "toggleNightVision":
                        addSuggestions(subCommand, PermissionManager.Permission.CHANGE_NIGHT_VISION, player, suggestions);
                        break;
                    case "toggleDamage":
                        addSuggestions(subCommand, PermissionManager.Permission.CHANGE_DAMAGE, player, suggestions);
                        break;
                    case "toggleHunger":
                        addSuggestions(subCommand, PermissionManager.Permission.CHANGE_HUNGER, player, suggestions);
                        break;
                    case "toggleCreatureTarget":
                        addSuggestions(subCommand, PermissionManager.Permission.CHANGE_CREATURE_TARGET, player, suggestions);
                        break;
                    case "toggleItemPickup":
                        addSuggestions(subCommand, PermissionManager.Permission.CHANGE_ITEM_PICKUP, player, suggestions);
                        break;
                }
            }
            return StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<>());
        }
        return null;
    }

    private void addSuggestions(String subCommand, PermissionManager.Permission permission, Player player,  List<String> suggestionList){
        if(plugin.getPermissionManager().hasPermission(player, permission)){
            suggestionList.add(plugin.getSettingsManager().getCommandSettings().getSubCommandName(subCommand));
        }
    }

    public void setEnabled(String subCommand, boolean enabled) {
        if (enabled && !enabledSubCommands.contains(subCommand)) enabledSubCommands.add(subCommand);
        else if (!enabled) enabledSubCommands.remove(subCommand);
    }

}
