/*
 * Copyright (C) 2024 MisterCheezeCake
 *
 * This file is part of SkyblockTweaks.
 *
 * SkyblockTweaks is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * SkyblockTweaks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SkyblockTweaks. If not, see <https://www.gnu.org/licenses/>.
 */
package wtf.cheeze.sbt.utils.skyblock;

import net.azureaaron.hmapi.data.party.PartyRole;
import net.azureaaron.hmapi.data.server.Environment;
import net.azureaaron.hmapi.network.packet.s2c.ErrorS2CPacket;
import net.azureaaron.hmapi.network.packet.s2c.HelloS2CPacket;
import net.azureaaron.hmapi.network.packet.s2c.HypixelS2CPacket;
import net.azureaaron.hmapi.network.packet.v1.s2c.LocationUpdateS2CPacket;
import net.azureaaron.hmapi.network.packet.v2.s2c.PartyInfoS2CPacket;
import net.minecraft.client.MinecraftClient;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.utils.MessageManager;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.actionbar.ActionBarData;
import wtf.cheeze.sbt.utils.enums.Location;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.tablist.TabListData;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class SkyblockData {


    public boolean inSB = false;
    public boolean alphaNetwork = false;
    public boolean inParty = false;
    public boolean amITheLeader = false;


    public String currentProfile = null;
    public String mode = null;
    public Location location = Location.UNKNOWN;

    public int defense = 0;
    public float maxHealth = 0;
    public float health = 0;
    public float maxMana = 0;
    public float mana = 0;
    public float overflowMana = 0;

    public float drillFuel = 0;
    public float maxDrillFuel = 0;

    public int riftSeconds = 0;
    public boolean riftTicking = false;

    public int maxTickers = 0;
    public int tickers = 0;
    public boolean tickerActive = false;

    public int armorStack = 0;
    public String stackString = null;

    public TabListData tabData = TabListData.EMPTY;

    public MiningData miningData = MiningData.EMPTY;


    public float getSpeed() {
        MinecraftClient mc = MinecraftClient.getInstance();
        // sprint = 1.3 x base speed
        return mc.player.isSprinting() ? (mc.player.getMovementSpeed() / 1.3f) * 1000 : mc.player.getMovementSpeed() * 1000;
    }
    public float effectiveHealth() {
        return Math.round( health * (1 + (defense / 100f)));
    }
    public float damageReduction() {
        return (defense / (defense + 100f)) * 100;
    }

    /**
     * Provides the profile ID with an appended "_ALPHA" if the player is in the alpha network
     * We do this so that what players do on the Alpha network does not affect persistent data for their main profile
     */
    public String getCurrentProfileUnique() {
        return alphaNetwork ?  currentProfile + "_ALPHA" : currentProfile;
    }

    public void update(ActionBarData data) {
        if (data == null) return;
        if (data.defense != null) this.defense = data.defense;
        if (data.maxHealth != null) this.maxHealth = data.maxHealth;
        if (data.currentHealth != null) this.health = data.currentHealth;
        if (data.maxMana != null) this.maxMana = data.maxMana;
        if (data.currentMana != null) this.mana = data.currentMana;
        if (data.overflowMana != null) this.overflowMana = data.overflowMana;
        if (data.drillFuel != null) this.drillFuel = data.drillFuel;
        if (data.maxDrillFuel != null) this.maxDrillFuel = data.maxDrillFuel;
        if (data.maxTickers != null && data.currentTickers != null) {
            this.maxTickers = data.maxTickers;
            this.tickers = data.currentTickers;
            this.tickerActive = true;
        } else {
            this.tickerActive = false;
        }
        if (data.stackSymbol != null && data.stackAmount !=null) {
            this.stackString = data.stackSymbol;
            this.armorStack = data.stackAmount;
        } else {
            this.stackString = null;
            this.armorStack = 0;
        }

        if (data.riftTime != null) {
            this.riftSeconds = NumberUtils.parseDuration(data.riftTime);
            this.riftTicking = Objects.requireNonNullElse(data.riftTicking, false);
        } else {
            this.riftSeconds = 0;
            this.riftTicking = false;

        }

    }

    public void update(TabListData data) {
        this.tabData = data;
        this.miningData = inMiningIsland() ? MiningData.of(this.tabData) : MiningData.EMPTY;
    }

    public void handlePacket(HypixelS2CPacket packet) {
        //SkyBlockTweaks.LOGGER.info("Handling packet");
        switch (packet) {
            case PartyInfoS2CPacket(boolean parInParty, Map<UUID, PartyRole> members) -> {
                //SkyBlockTweaks.LOGGER.info("Handling party info packet");
                inParty = parInParty;
                //SkyBlockTweaks.LOGGER.info("I am in a party: {}", inParty);
                var myUUID = SkyblockTweaks.mc.player.getUuid();
                if (myUUID == null || members == null)  {
                    amITheLeader = false;
                    return;
                }
                amITheLeader = members.get(myUUID) == PartyRole.LEADER;
                //SkyBlockTweaks.LOGGER.info("I am the leader: {}", amITheLeader);
            }
            case HelloS2CPacket(Environment environment) -> {
                // Beta is alpha
                SkyblockTweaks.LOGGER.info("Connected to Hypixel Mod API. Environment: {}", environment);
                alphaNetwork = environment == Environment.BETA;
            }
            case LocationUpdateS2CPacket(String serverName, Optional<String> serverType, Optional<String> lobbyName, Optional<String> mode, Optional<String> map) -> {
//                this.mode = mode.orElse(null);
                SkyblockTweaks.LOGGER.info("Location update packet received. Server: {}, Type: {}, Mode: {}", serverName, serverType.orElse("unknown"), mode.orElse("unknown"));
                this.inSB = serverType.orElse("").equals("SKYBLOCK");
                if (inSB) {
                    this.location = SkyblockUtils.getLocationFromMode(mode.orElse("unknown"));
                } else {
                    this.location = Location.UNKNOWN;
                }
            }
            case ErrorS2CPacket(var id, var errorReason) -> {
                MessageManager.send("The Hypixel Mod API experienced an error. ID: " + id + " Reason: " + errorReason, Colors.RED);
                SkyblockTweaks.LOGGER.error("The Hypixel Mod API experienced an error. ID: {} Reason: {}", id, errorReason);
            }
//            case LocationUpdateS2CPacket(String serverName, Optional<String> serverType, Optional<String> lobbyName, Optional<String> mode, Optional<String> map) -> {
//                SkyBlockTweaks.LOGGER.info(serverName);
//            }
            default -> {
                //Do nothing
            }
        }

    }

    public boolean inMiningIsland() {
        return location == Location.DWARVEN_MINES|| location == Location.CRYSTAL_HOLLOWS|| location == Location.GLACITE_MINESHAFT;
    }
}
