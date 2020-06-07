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

package com.azortis.protocolvanish.bukkit;

import com.azortis.protocolvanish.bukkit.api.VanishAPI;
import com.azortis.protocolvanish.bukkit.listeners.*;
import com.azortis.protocolvanish.bukkit.settings.old.wrappers.SettingsManager;
import com.azortis.protocolvanish.bukkit.command.VanishCommand;
import com.azortis.protocolvanish.bukkit.hooks.HookManager;
import com.azortis.protocolvanish.bukkit.storage.StorageManager;
import com.azortis.protocolvanish.bukkit.visibility.VisibilityManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;

public final class ProtocolVanish extends JavaPlugin {

    private Metrics metrics;
    private SettingsManager settingsManager;
    private PermissionManager permissionManager;
    private VisibilityManager visibilityManager;
    private StorageManager storageManager;
    private HookManager hookManager;
    private UpdateChecker updateChecker;

    private VanishCommand vanishCommand;

    private final HashMap<UUID, VanishPlayer> vanishPlayerMap = new HashMap<>();

    @Override
    public void onEnable() {
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            this.getLogger().severe("ProtocolLib isn't present, please install ProtocolLib! Shutting down...");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.getLogger().severe("PlaceholderAPI isn't present, please install PlaceholderAPI! Shutting down...");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.updateChecker = new UpdateChecker(this);
        this.settingsManager = new SettingsManager(this);
        if(!settingsManager.areFilesUpToDate())return;

        this.metrics = new Metrics(this);
        this.storageManager = new StorageManager(this);
        this.permissionManager = new PermissionManager(this);
        this.visibilityManager = new VisibilityManager(this);
        this.hookManager = new HookManager(this);
        this.vanishCommand = new VanishCommand(this);

        this.getLogger().info("Registering events...");
        new PlayerLoginListener(this);
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
        new EntityDamageListener(this);
        new FoodLevelChangeListener(this);
        new EntityTargetLivingEntityListener(this);
        new EntityPickupItemListener(this);
        new PlayerToggleSneakListener(this);

        VanishAPI.setPlugin(this);
    }

    public UpdateChecker getUpdateChecker(){
        return updateChecker;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public VisibilityManager getVisibilityManager() {
        return visibilityManager;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public HookManager getHookManager() {
        return hookManager;
    }

    public VanishCommand getVanishCommand() {
        return vanishCommand;
    }

    /**
     * Gets the vanishPlayer of a specific player.
     *
     * @param uuid The {@link UUID} of the player.
     * @return The {@link VanishPlayer} of the player, null if have no permission.
     */
    public VanishPlayer getVanishPlayer(UUID uuid) {
        if (!vanishPlayerMap.containsKey(uuid)) return null;
        return vanishPlayerMap.get(uuid);
    }

    /**
     * Called to load the {@link VanishPlayer} from storage.
     * Should only be called on join.
     *
     * @param player The player of which instance has to be loaded.
     * @return The {@link VanishPlayer} that has been loaded.
     */
    public VanishPlayer loadVanishPlayer(Player player) {
        VanishPlayer vanishPlayer = storageManager.getVanishPlayer(player);
        if (vanishPlayer == null) return null;
        vanishPlayerMap.put(player.getUniqueId(), vanishPlayer);
        return vanishPlayer;
    }

    /**
     * Called to unload the {@link VanishPlayer} instance.
     * Should only be called on leave.
     *
     * @param player The player of which instance has to be unloaded.
     */
    public void unloadVanishPlayer(Player player) {
        vanishPlayerMap.remove(player.getUniqueId());
    }

    /**
     * Called to create an new instance of {@link VanishPlayer}
     * should only be called if there doesn't exist an entry,
     * and if the player has the permission in order to keep the database clean.
     *
     * @param player The {@link Player} of which to create a vanishPlayer instance.
     * @return The created vanishPlayer instance.
     */
    public VanishPlayer createVanishPlayer(Player player) {
        VanishPlayer vanishPlayer = storageManager.createVanishPlayer(player);
        vanishPlayerMap.put(player.getUniqueId(), vanishPlayer);
        return vanishPlayer;
    }

    /**
     * Sends message to specified {@link Player}.
     * Message will be processed, to replace all placeholders.
     *
     * @param receiver The player that should receive the message.
     * @param messagePath The path of the message in the MessageSettings.
     */
    public void sendPlayerMessage(Player receiver, Player placeholderPlayer,  String messagePath){
        String rawMessage = settingsManager.getMessageSettings().getMessage(messagePath);
        String message = PlaceholderAPI.setPlaceholders(placeholderPlayer, rawMessage);
        if(message.startsWith("[JSON]")){
            try {
                String jsonString = message.replace("[JSON]", "").trim();
                WrappedChatComponent chatComponent = WrappedChatComponent.fromJson(jsonString);
                PacketContainer packet = new PacketContainer(PacketType.Play.Server.CHAT);
                packet.getChatTypes().write(0, EnumWrappers.ChatType.CHAT);
                packet.getChatComponents().write(0, chatComponent);
                ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, packet);
            }catch (InvocationTargetException ex){
                ex.printStackTrace();
            }
        }
        receiver.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

}
