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
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.azureaaron.hmapi.network.HypixelNetworking;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SkyblockTweaksConfig;
import wtf.cheeze.sbt.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PartyFeatures {

    public static Pattern PARTY_PATTERN = Pattern.compile("Party > ([^:]+): !(.+)");
    public static Pattern BACKUP_UPDATE_PATTERN = Pattern.compile("The party was transferred to (.+) by .+");
    public static Pattern BOOP_PATTERN = Pattern.compile("From (.*): Boop!");

    public static long lastPartyCommand = 0;

    public static void registerEvents() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {

            if (overlay) return;
            if (!SkyblockTweaks.CONFIG.config.partyCommands.enabled) return;
            var s = TextUtils.removeColorCodes(message.getString());
            if (s.startsWith("Party >")) {
                var matcher = PARTY_PATTERN.matcher(s);
                if (!matcher.matches()) return;
                if (System.currentTimeMillis() - lastPartyCommand < SkyblockTweaks.CONFIG.config.partyCommands.cooldown) return;
                HypixelNetworking.sendPartyInfoC2SPacket(2);
                lastPartyCommand = System.currentTimeMillis();
                var name = matcher.group(1);
                if (name.contains(" ")) name = name.split(" ")[1];
                if (SkyblockTweaks.CONFIG.config.partyCommands.blockedUsers.contains(name)) {
                    SkyblockTweaks.LOGGER.info("Blocked user tried to use party commands: " + name);
                    return;
                }
                var msg = matcher.group(2);
                var args = msg.split(" ");
                switch (args[0]) {
                    case "pt", "ptme" -> {
                        if (!SkyblockTweaks.DATA.amITheLeader) {
                            return;
                        }
                        SkyblockTweaks.mc.getNetworkHandler().sendChatCommand("p transfer " + name);
                    }
                    case "allinv", "allinvite" -> {
                        if (!SkyblockTweaks.DATA.amITheLeader) {
                            return;
                        }
                        SkyblockTweaks.mc.getNetworkHandler().sendChatCommand("p settings allinvite");
                    }
                    case "help" -> {
                        SkyblockTweaks.LOGGER.info(name);
                        if ((name.equals(SkyblockTweaks.mc.player.getName().getString()))) {
                            SkyblockTweaks.mc.player.sendMessage(Text.of("§7[§aSkyblockTweaks§f§7] §fAvailable party commands: !ptme, !allinvite, !help"), false);
                            return;
                        }
                        SkyblockTweaks.mc.getNetworkHandler().sendChatCommand("pc [SkyblockTweaks] Available party commands: !ptme, !allinvite, !help");
                    }
                }
            } else if (s.startsWith("The party was transferred to")) {
                // This is here in case the packet api fails for some reason
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
                if (!SkyblockTweaks.CONFIG.config.partyCommands.boopInvites) return;
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
                    .name(Text.literal("Party Commands Blocked Users"))
                    .description(OptionDescription.of(Text.literal("Users that are blocked from using party commands")))
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
                    .name(Text.literal("Enable Party Commands"))
                    .description(OptionDescription.of(Text.literal("Whether or not to enable party commands")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.partyCommands.enabled,
                            () -> config.partyCommands.enabled,
                            value -> config.partyCommands.enabled = (Boolean) value
                    )
                    .build();
            var cooldown = Option.<Integer>createBuilder()
                    .name(Text.literal("Command Cooldown"))
                    .description(OptionDescription.of(Text.literal("The cooldown in milliseconds between party commands")))
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(0).max(10000))
                    .binding(
                            defaults.partyCommands.cooldown,
                            () -> config.partyCommands.cooldown,
                            value -> config.partyCommands.cooldown = (Integer) value
                    )
                    .build();

            var inviteBoop = Option.<Boolean>createBuilder()
                    .name(Text.literal("Boop Invites"))
                    .description(OptionDescription.of(Text.literal("Whether or not to prompt a party invite when booped by a player")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.partyCommands.boopInvites,
                            () -> config.partyCommands.boopInvites,
                            value -> config.partyCommands.boopInvites = (Boolean) value
                    )
                    .build();


            return OptionGroup.createBuilder()
                    .name(Text.literal("Party Features"))
                    .description(OptionDescription.of(Text.literal("Settings for Party Features")))
                    .option(enabled)
                    .option(cooldown)
                    .option(inviteBoop)
                    .build();
        }

    }

    private static Text getInviteMessage(String name) {
        return TextUtils.getTextThatRunsCommand("§7[§aSkyblockTweaks§f§7] §bClick here to invite §e" + name + "§b to your party!","§3Click here to run §e/p " + name , "/p invite " + name);
    }


}
