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

@SuppressWarnings("unused")
public class VanishPlayer {

    private Player player;
    private boolean vanished;
    private PlayerSettings playerSettings;

    public VanishPlayer(Player player, boolean vanished) {
        this.player = player;
        this.vanished = vanished;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isVanished() {
        return vanished;
    }

    public void setVanish(boolean vanished) {
        this.vanished = vanished;
    }

    public PlayerSettings getPlayerSettings() {
        return this.playerSettings;
    }

    public void setPlayerSettings(PlayerSettings playerSettings) {
        this.playerSettings = playerSettings;
    }

    public static class PlayerSettings {

        private transient VanishPlayer parent;
        private boolean nightVision;
        private boolean disableDamage;
        private boolean disableHunger;
        private boolean disableCreatureTarget;
        private boolean disableItemPickUp;

        public PlayerSettings(boolean nightVision, boolean disableDamage, boolean disableHunger, boolean disableCreatureTarget, boolean disableItemPickUp) {
            this.nightVision = nightVision;
            this.disableDamage = disableDamage;
            this.disableHunger = disableHunger;
            this.disableCreatureTarget = disableCreatureTarget;
            this.disableItemPickUp = disableItemPickUp;
        }

        public void setParent(VanishPlayer parent) {
            this.parent = parent;
        }

        public VanishPlayer getParent() {
            return parent;
        }

        public boolean doNightVision() {
            return nightVision;
        }

        public void setNightVision(boolean nightVision) {
            this.nightVision = nightVision;
        }

        public boolean getDisableDamage() {
            return disableDamage;
        }

        public void setDisableDamage(boolean disableDamage) {
            this.disableDamage = disableDamage;
        }

        public boolean getDisableHunger() {
            return disableHunger;
        }

        public void setDisableHunger(boolean disableHunger) {
            this.disableHunger = disableHunger;
        }

        public boolean getDisableCreatureTarget() {
            return disableCreatureTarget;
        }

        public void setDisableCreatureTarget(boolean disableCreatureTarget) {
            this.disableCreatureTarget = disableCreatureTarget;
        }

        public boolean getDisableItemPickUp() {
            return disableItemPickUp;
        }

        public void setDisableItemPickUp(boolean disableItemPickUp) {
            this.disableItemPickUp = disableItemPickUp;
        }

    }

}
