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
package wtf.cheeze.sbt.features.chat;

import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.azureaaron.hmapi.network.HypixelNetworking;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import static wtf.cheeze.sbt.config.categories.Chat.key;
import static wtf.cheeze.sbt.config.categories.Chat.keyD;

public class PartyFeatures {

    public static Pattern PARTY_PATTERN = Pattern.compile("Party > ([^:]+): !(.+)");
    public static Pattern BACKUP_UPDATE_PATTERN = Pattern.compile("The party was transferred to (.+) by .+");
    public static Pattern BOOP_PATTERN = Pattern.compile("From (.*): Boop!");

    public static boolean verboseDebug = false;
    public static long lastPartyCommand = 0;

    public static void registerEvents() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {

            if (overlay) return;
            if (!SBTConfig.get().partyCommands.enabled) return;
            var s = TextUtils.removeColorCodes(message.getString());
            if (s.startsWith("Party >")) {
                var matcher = PARTY_PATTERN.matcher(s);
                if (!matcher.matches()) {
                    if (verboseDebug) sendDebugMessage("Party message did not match pattern");
                    return;
                }
                if (System.currentTimeMillis() - lastPartyCommand < SBTConfig.get().partyCommands.cooldown) {
                    if (verboseDebug) sendDebugMessage("Party command on cooldown");
                    return;
                }
                HypixelNetworking.sendPartyInfoC2SPacket(2);
                lastPartyCommand = System.currentTimeMillis();
                var name = matcher.group(1);
                if (verboseDebug) sendDebugMessage("Initial Name Field Is '" + name + "'");
                if (name.contains(" ")) {
                    name = name.split(" ")[1];
                    if (verboseDebug) sendDebugMessage("Name Changed To '" + name + "'");
                }
                if (SBTConfig.get().partyCommands.blockedUsers.contains(name)) {
                    SkyblockTweaks.LOGGER.info("Blocked user tried to use party commands: " + name);
                    if (verboseDebug) sendDebugMessage("Blocked user tried to use party commands: " + name);
                    return;
                }
                var msg = matcher.group(2);
                if (verboseDebug) sendDebugMessage("Message Field Is '" + msg + "'");
                var args = msg.split(" ");
                if (verboseDebug) sendDebugMessage("Arg0 is '" + args[0]+"'");
                switch (args[0]) {
                    case "pt", "ptme" -> {
                        if (!SkyblockTweaks.DATA.amITheLeader) {
                            if (verboseDebug) sendDebugMessage("Party Transfer Requested, but I am not the leader.");
                            return;
                        }
                        if (verboseDebug) sendDebugMessage("Sending '/p transfer " + name + "'");
                        SkyblockTweaks.mc.getNetworkHandler().sendChatCommand("p transfer " + name);
                    }
                    case "allinv", "allinvite" -> {
                        if (!SkyblockTweaks.DATA.amITheLeader) {
                            if (verboseDebug) sendDebugMessage("All Invite Requested, but I am not the leader.");
                            return;
                        }
                        if (verboseDebug) sendDebugMessage("Sending '/p settings allinvite'");
                        SkyblockTweaks.mc.getNetworkHandler().sendChatCommand("p settings allinvite");
                    }
                    case "warp" -> {
                        if (!SkyblockTweaks.DATA.amITheLeader) {
                            if (verboseDebug) sendDebugMessage("Warp Requested, but I am not the leader.");
                            return;
                        }
                        if (verboseDebug) sendDebugMessage("Sending '/p warp'");
                        SkyblockTweaks.mc.getNetworkHandler().sendChatCommand("p warp");
                    }
                    case "help" -> {
                        if ((name.equals(SkyblockTweaks.mc.player.getName().getString()))) {
                            SkyblockTweaks.mc.player.sendMessage(Text.of("§7[§aSkyblockTweaks§f§7] §fAvailable party commands: !ptme, !allinvite, !warp, !help"), false);
                            return;
                        }
                        if (verboseDebug) sendDebugMessage("Sending help message");
                        SkyblockTweaks.mc.getNetworkHandler().sendChatCommand("pc [SkyblockTweaks] Available party commands: !ptme, !allinvite, !warp, !help");
                    }
                }
            } else if (s.startsWith("The party was transferred to")) {
                // This is here in case the packet api fails for some reason
                if (verboseDebug) sendDebugMessage("Backup Update Message Received");
                var matcher = BACKUP_UPDATE_PATTERN.matcher(s);
                if (!matcher.matches()) return;
                var name = matcher.group(1);
                if (name.contains(" ")) name = name.split(" ")[1];
                var me = SkyblockTweaks.mc.player.getName().getString();
                if (name.equals(me)) {
                    SkyblockTweaks.DATA.amITheLeader = true;
                } else {
                    SkyblockTweaks.DATA.amITheLeader = false;
                }
            } else if (s.matches("From .*: Boop!")) {
                if (!SBTConfig.get().partyCommands.boopInvites) return;
                var matcher = BOOP_PATTERN.matcher(s);
                if (!matcher.matches()) return;
                var n = matcher.group(1);
                var name = n.contains(" ") ? n.split(" ")[1] : n;
                SkyblockTweaks.mc.send(() -> SkyblockTweaks.mc.player.sendMessage(getInviteMessage(name), false));
            }
        });
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = true;
        @SerialEntry
        public List<String> blockedUsers = new ArrayList<>();
        @SerialEntry
        public int cooldown = 750;

        @SerialEntry
        public boolean boopInvites  = true;




        public static ListOption<String> getBlackList(ConfigImpl defaults, ConfigImpl config) {
            return ListOption.<String>createBuilder()
                    .name(key("partyFeatures.blockedUsers"))
                    .description(keyD("partyFeatures.blockedUsers"))
                    .binding(
                            defaults.partyCommands.blockedUsers,
                            () -> config.partyCommands.blockedUsers,
                            (v) -> config.partyCommands.blockedUsers = v
                    )
                    .controller(StringControllerBuilder::create)
                    .initial("")
                    .collapsed(true)
                    .build();
        }
        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {

            var enabled = Option.<Boolean>createBuilder()
                    .name(key("partyFeatures.enabled"))
                    .description(keyD("partyFeatures.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.partyCommands.enabled,
                            () -> config.partyCommands.enabled,
                            value -> config.partyCommands.enabled = (Boolean) value
                    )
                    .build();
            var cooldown = Option.<Integer>createBuilder()
                    .name(key("partyFeatures.cooldown"))
                    .description(keyD("partyFeatures.cooldown"))
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(0).max(10000))
                    .binding(
                            defaults.partyCommands.cooldown,
                            () -> config.partyCommands.cooldown,
                            value -> config.partyCommands.cooldown = (Integer) value
                    )
                    .build();

            var inviteBoop = Option.<Boolean>createBuilder()
                    .name(key("partyFeatures.boopInvites"))
                    .description(keyD("partyFeatures.boopInvites"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.partyCommands.boopInvites,
                            () -> config.partyCommands.boopInvites,
                            value -> config.partyCommands.boopInvites = (Boolean) value
                    )
                    .build();


            return  OptionGroup.createBuilder()
                    .name(key("partyFeatures"))
                    .description(keyD("partyFeatures"))
                    .option(enabled)
                    .option(cooldown)
                    .option(inviteBoop)
                    .build();

        }

    }

    private static Text getInviteMessage(String name) {
        return TextUtils.getTextThatRunsCommand("§7[§aSkyblockTweaks§f§7] §bClick here to invite §e" + name + "§b to your party!","§3Click here to run §e/p " + name , "/p invite " + name);
    }
    private static void sendDebugMessage(String message) {
        SkyblockTweaks.mc.player.sendMessage(Text.of("§7[§2SBT Party Debugger§f§7]§3 " + message), false);
    }


}
