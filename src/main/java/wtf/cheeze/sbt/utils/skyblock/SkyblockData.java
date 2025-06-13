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

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.hypixel.data.region.Environment;
import net.hypixel.modapi.error.ErrorReason;
import net.hypixel.modapi.packet.ClientboundHypixelPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundHelloPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.events.ChatEvents;
import wtf.cheeze.sbt.utils.errors.ErrorHandler;
import wtf.cheeze.sbt.utils.errors.ErrorLevel;
import wtf.cheeze.sbt.utils.text.MessageManager;
import wtf.cheeze.sbt.utils.timing.TimeUtils;
import wtf.cheeze.sbt.utils.actionbar.ActionBarData;
import wtf.cheeze.sbt.utils.enums.Location;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.tablist.TabListData;

import java.util.Objects;
import java.util.regex.Pattern;


public class SkyblockData {

    private static final MinecraftClient client = MinecraftClient.getInstance();

    private static final Pattern COOLDOWN_PATTERN = Pattern.compile("Cooldown: (\\d+)s");
    private static final Pattern PICK_USED_PATTERN = Pattern.compile("You used your .* Pickaxe Ability!");


    public static void registerEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!inSB) return;
            if (client.player == null) return;
            for (int i = 0; i < 36; i++) {
                var stack = client.player.getInventory().getStack(i);
                if (stack.isEmpty()) continue;
                if (ItemStackUtils.isPickaxe(stack.getItem()) || stack.getName().getString().contains("Drill")) {
                    var lines = stack.getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).lines();
                    if (lines.isEmpty()) continue;
                    boolean shouldBreak = false;
                    for (Text line: lines) {
                        var matcher = COOLDOWN_PATTERN.matcher(line.getString());
                        if (!matcher.matches()) continue;
                        try {
                            int cooldown = Integer.parseInt(matcher.group(1));
                            pickAbilityCooldown = cooldown * 1000L; // Convert to milliseconds
                            shouldBreak = true;
                            break;
                        } catch (NumberFormatException e) {
                            ErrorHandler.handleError(e, "Error Parsing Pickaxe Cooldown", ErrorLevel.SILENT);
                        }

                    }
                    if (shouldBreak) break;
                }
            }
        });
        ChatEvents.ON_GAME.register(message -> {
            if (PICK_USED_PATTERN.matcher(message.getString()).matches()) {
                lastUsedPickAbility = System.currentTimeMillis();
            }
        });
    }


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

    public static long lastUsedPickAbility = 0;
    public static long pickAbilityCooldown = 0;

    public static int calculateCurrentPickAbilityCooldown() {
        if (lastUsedPickAbility == 0) return 0;
        long timeSinceLastUse = System.currentTimeMillis() - lastUsedPickAbility;
        if (timeSinceLastUse < pickAbilityCooldown) {
            return (int) ((pickAbilityCooldown - timeSinceLastUse) / 1000) + 1; // I don't know why the +1 works, but it does.
        }
        return 0; // No cooldown
    }


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

    public static void handlePacket(ClientboundHypixelPacket packet) {
        switch (packet) {
            case ClientboundHelloPacket hello -> {
                SkyblockTweaks.LOGGER.info("Connected to Hypixel Mod API. Environment: {}", hello.getEnvironment());
                // Beta is alpha
                alphaNetwork = hello.getEnvironment() == Environment.BETA;
            }
            case ClientboundPartyInfoPacket partyPacket -> {
                Party.inParty = partyPacket.isInParty();
                var myUUID = client.player.getUuid();
                if (myUUID == null || partyPacket.getMemberMap() == null || partyPacket.getMemberMap().get(myUUID) == null) {
                    Party.leader = false;
                    return;
                }
                Party.leader = partyPacket.getMemberMap().get(myUUID).getRole() == ClientboundPartyInfoPacket.PartyRole.LEADER;
            }
            case ClientboundLocationPacket locationPacket -> {
                currentServer = locationPacket.getServerName();
                inSB = locationPacket.getServerType().isPresent() && locationPacket.getServerType().get().getName().equals("SkyBlock");
                if (inSB) {
                    location = Location.fromMode(locationPacket.getMode().orElse("unknown"));
                } else {
                    location = Location.UNKNOWN;
                }
            }
            default -> {}
        }
    }

    public static void handleModApiError(String id, ErrorReason reason) {
        if (SBTConfig.get().chatModApiErrors) MessageManager.send("The Hypixel Mod API experienced an error. ID: " + id + " Reason: " + reason, Colors.RED);
        SkyblockTweaks.LOGGER.error("The Hypixel Mod API experienced an error. ID: {} Reason: {}", id, reason);
    }
}
