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
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.utils.text.MessageManager;
import wtf.cheeze.sbt.utils.timing.TimeUtils;
import wtf.cheeze.sbt.utils.actionbar.ActionBarData;
import wtf.cheeze.sbt.utils.enums.Location;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.tablist.TabListData;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class SkyblockData {

    private static final MinecraftClient client = MinecraftClient.getInstance();


    public static boolean inSB = false;
    public static boolean alphaNetwork = false;

    public static String currentProfile = "unknown_profile";
    public static String mode = null;
    public static Location location = Location.UNKNOWN;
    public static String currentServer = "Unknown Server";
    public static class Party {
        public static boolean inParty = false;
        public static boolean leader = false;
    }

    public static class Stats {

        public static int defense = 0;
        public static float maxHealth = 0;
        public static float health = 0;
        public static float maxMana = 0;
        public static float mana = 0;
        public static float overflowMana = 0;

        public static float drillFuel = 0;
        public static float maxDrillFuel = 0;

        public static int riftSeconds = 0;
        public static boolean riftTicking = false;

        public static int maxTickers = 0;
        public static int tickers = 0;
        public static boolean tickerActive = false;

        public static int armorStack = 0;
        public static String stackString = null;

        public static int secretsTotal = 0;
        public static int secretsFound = 0;

        public static float effectiveHealth() {
            return Math.round( Stats.health * (1 + (Stats.defense / 100f)));
        }
        public static float damageReduction() {
            return (Stats.defense / (Stats.defense + 100f)) * 100;
        }


    }

    public static TabListData tabData = TabListData.EMPTY;

    public static MiningData miningData = MiningData.EMPTY;


    /**
     * Provides the profile ID with an appended "_ALPHA" if the player is in the alpha network
     * We do this so that what players do on the Alpha network does not affect persistent data for their main profile
     */
    public static String getCurrentProfileUnique() {
        return alphaNetwork ?  currentProfile + "_ALPHA" : currentProfile;
    }

    public static void update(ActionBarData data) {
        if (data == null) return;
        if (data.defense != null) Stats.defense = data.defense;
        if (data.maxHealth != null) Stats.maxHealth = data.maxHealth;
        if (data.currentHealth != null) Stats.health = data.currentHealth;
        if (data.maxMana != null) Stats.maxMana = data.maxMana;
        if (data.currentMana != null) Stats.mana = data.currentMana;
        if (data.overflowMana != null) Stats.overflowMana = data.overflowMana;
        if (data.drillFuel != null) Stats.drillFuel = data.drillFuel;
        if (data.maxDrillFuel != null) Stats.maxDrillFuel = data.maxDrillFuel;
        if (data.maxTickers != null && data.currentTickers != null) {
            Stats.maxTickers = data.maxTickers;
            Stats.tickers = data.currentTickers;
            Stats.tickerActive = true;
        } else {
            Stats.tickerActive = false;
        }
        if (data.stackSymbol != null && data.stackAmount !=null) {
            Stats.stackString = data.stackSymbol;
            Stats.armorStack = data.stackAmount;
        } else {
            Stats.stackString = null;
            Stats.armorStack = 0;
        }

        if (data.riftTime != null) {
            Stats.riftSeconds = TimeUtils.parseDuration(data.riftTime);
            Stats.riftTicking = Objects.requireNonNullElse(data.riftTicking, false);
        } else {
            Stats.riftSeconds = 0;
            Stats.riftTicking = false;
        }

        if (data.secretsFound != null && data.secretsTotal != null) {
            Stats.secretsFound = data.secretsFound;
            Stats.secretsTotal = data.secretsTotal;
        } else {
            Stats.secretsFound = 0;
            Stats.secretsTotal = 0;
        }
    }

    public static void update(TabListData data) {
        tabData = data;
        miningData = SkyblockUtils.inMiningIsland() ? MiningData.of(tabData) : MiningData.EMPTY;

    }

    public static void handlePacket(HypixelS2CPacket packet) {
        //SkyBlockTweaks.LOGGER.info("Handling packet");
        switch (packet) {
            case PartyInfoS2CPacket(boolean parInParty, Map<UUID, PartyRole> members) -> {
                Party.inParty = parInParty;
                var myUUID = client.player.getUuid();
                if (myUUID == null || members == null)  {
                    Party.leader = false;
                    return;
                }
                Party.leader = members.get(myUUID) == PartyRole.LEADER;
            }
            case HelloS2CPacket(Environment environment) -> {
                // Beta is alpha
                SkyblockTweaks.LOGGER.info("Connected to Hypixel Mod API. Environment: {}", environment);
                alphaNetwork = environment == Environment.BETA;
            }
            case LocationUpdateS2CPacket(String serverName, Optional<String> serverType, Optional<String> lobbyName, Optional<String> mode, Optional<String> map) -> {
                currentServer = serverName;
                SkyblockTweaks.LOGGER.info("Location update packet received. Server: {}, Type: {}, Mode: {}", serverName, serverType.orElse("unknown"), mode.orElse("unknown"));
                inSB = serverType.orElse("").equals("SKYBLOCK");
                if (inSB) {
                    location = SkyblockUtils.getLocationFromMode(mode.orElse("unknown"));
                } else {
                    location = Location.UNKNOWN;
                }
            }
            case ErrorS2CPacket(var id, var errorReason) -> {
                if (SBTConfig.get().chatModApiErrors) MessageManager.send("The Hypixel Mod API experienced an error. ID: " + id + " Reason: " + errorReason, Colors.RED);
                SkyblockTweaks.LOGGER.error("The Hypixel Mod API experienced an error. ID: {} Reason: {}", id, errorReason);
            }
            default -> {}
        }

    }

}
