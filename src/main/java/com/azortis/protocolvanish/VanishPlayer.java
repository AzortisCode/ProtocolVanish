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

package com.azortis.protocolvanish;

import org.bukkit.entity.Player;

@SuppressWarnings("all")
public class VanishPlayer {

    private Player player;
    private boolean vanished;
    private PlayerSettings playerSettings;
    private ProtocolVanish plugin;

    public VanishPlayer(Player player, boolean vanished, PlayerSettings playerSettings, ProtocolVanish plugin){
        this.player = player;
        this.vanished = vanished;
        this.playerSettings = playerSettings;
        this.plugin = plugin;
    }

    public Player getPlayer(){
        return player;
    }

    public boolean isVanished(){
        return vanished;
    }

    public void setVanish(boolean vanished){
        this.vanished = vanished;
    }

    public PlayerSettings getPlayerSettings(){
        return this.playerSettings;
    }

    public class PlayerSettings {

        private boolean nightVision;
        private boolean damage;
        private boolean hunger;
        private boolean creatureTarget;
        private boolean itemPickUp;

        public PlayerSettings(boolean nightVision, boolean damage, boolean hunger, boolean creatureTarget, boolean itemPickUp){
            this.nightVision = nightVision;
            this.damage = damage;
            this.hunger = hunger;
            this.creatureTarget = creatureTarget;
            this.itemPickUp = itemPickUp;
        }

        public boolean doNightVision() {
            return nightVision;
        }

        public void setNightVision(boolean nightVision) {
            this.nightVision = nightVision;
        }

        public boolean doDamage() {
            return damage;
        }

        public void setDamage(boolean damage) {
            this.damage = damage;
        }

        public boolean doHunger() {
            return hunger;
        }

        public void setHunger(boolean hunger) {
            this.hunger = hunger;
        }

        public boolean doCreatureTarget() {
            return creatureTarget;
        }

        public void setCreatureTarget(boolean creatureTarget) {
            this.creatureTarget = creatureTarget;
        }

        public boolean doItemPickUp() {
            return itemPickUp;
        }

        public void setItemPickUp(boolean itemPickUp) {
            this.itemPickUp = itemPickUp;
        }

    }

}
