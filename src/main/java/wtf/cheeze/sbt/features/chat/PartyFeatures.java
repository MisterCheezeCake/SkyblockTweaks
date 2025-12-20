// TODO(Ravel): Failed to fully resolve file: null
// TODO(Ravel): Failed to fully resolve file: null
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
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.events.ChatEvents;
import wtf.cheeze.sbt.utils.KillSwitch;
import wtf.cheeze.sbt.utils.text.MessageManager;
import wtf.cheeze.sbt.utils.text.TextUtils;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.ModAPI;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import static wtf.cheeze.sbt.config.categories.Chat.key;
import static wtf.cheeze.sbt.config.categories.Chat.keyD;

public class PartyFeatures {
    private static final String FEATURE_ID = "party_commands";

    public static final Pattern PARTY_PATTERN = Pattern.compile("Party > ([^:]+): !(.+)");
    public static final Pattern BACKUP_UPDATE_PATTERN = Pattern.compile("The party was transferred to (.+) by .+");
    public static final Pattern BOOP_PATTERN = Pattern.compile("From (.*): Boop!");

    public static boolean verboseDebug = false;
    public static long lastPartyCommand = 0;

    private static final Minecraft mc = Minecraft.getInstance();

    public static void registerEvents() {
        ChatEvents.ON_GAME.register(message -> {
            if (!SBTConfig.get().partyCommands.enabled) return;
            var s = TextUtils.removeFormatting(message.getString());
            if (s.startsWith("Party >")) {
                var matcher = PARTY_PATTERN.matcher(s);
                if (!matcher.matches()) {
                    if (verboseDebug) sendDebugMessage("Party message did not match pattern");
                    return;
                }
                if (KillSwitch.shouldKill(FEATURE_ID)) {
                    MessageManager.send("Party commands have been disabled remotely", Colors.RED);
                    return;
                }
                if (System.currentTimeMillis() - lastPartyCommand < SBTConfig.get().partyCommands.cooldown) {
                    if (verboseDebug) sendDebugMessage("Party command on cooldown");
                    return;
                }
                ModAPI.requestPartyInfo();
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
                        if (!SkyblockData.Party.leader) {
                            if (verboseDebug) sendDebugMessage("Party Transfer Requested, but I am not the leader.");
                            return;
                        }
                        if (verboseDebug) sendDebugMessage("Sending '/p transfer " + name + "'");
                        mc.getConnection().sendCommand("p transfer " + name);
                    }
                    case "allinv", "allinvite" -> {
                        if (!SkyblockData.Party.leader) {
                            if (verboseDebug) sendDebugMessage("All Invite Requested, but I am not the leader.");
                            return;
                        }
                        if (verboseDebug) sendDebugMessage("Sending '/p settings allinvite'");
                        mc.getConnection().sendCommand("p settings allinvite");
                    }
                    case "warp" -> {
                        if (!SkyblockData.Party.leader) {
                            if (verboseDebug) sendDebugMessage("Warp Requested, but I am not the leader.");
                            return;
                        }
                        if (verboseDebug) sendDebugMessage("Sending '/p warp'");
                        mc.getConnection().sendCommand("p warp");
                    }
                    case "help" -> {
                        if ((name.equals(mc.player.getName().getString()))) {
                            MessageManager.send("Available party commands: !ptme, !allinvite, !warp, !help");
                            return;
                        }
                        if (verboseDebug) sendDebugMessage("Sending help message");
                        mc.getConnection().sendCommand("pc [SkyblockTweaks] Available party commands: !ptme, !allinvite, !warp, !help");
                    }
                }
            } else if (s.startsWith("The party was transferred to")) {
                // This is here in case the packet api fails for some reason
                if (verboseDebug) sendDebugMessage("Backup Update Message Received");
                var matcher = BACKUP_UPDATE_PATTERN.matcher(s);
                if (!matcher.matches()) return;
                var name = matcher.group(1);
                if (name.contains(" ")) name = name.split(" ")[1];
                var me = mc.player.getName().getString();
                SkyblockData.Party.leader = name.equals(me);
            } else if (s.matches("From .*: Boop!")) {
                if (!SBTConfig.get().partyCommands.boopInvites) return;
                var matcher = BOOP_PATTERN.matcher(s);
                if (!matcher.matches()) return;
                var n = matcher.group(1);
                var name = n.contains(" ") ? n.split(" ")[1] : n;
                mc.execute(() -> MessageManager.send(getInviteMessage(name)));
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
                            value -> config.partyCommands.enabled = value
                    )
                    .build();
            var cooldown = Option.<Integer>createBuilder()
                    .name(key("partyFeatures.cooldown"))
                    .description(keyD("partyFeatures.cooldown"))
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(0).max(10000))
                    .binding(
                            defaults.partyCommands.cooldown,
                            () -> config.partyCommands.cooldown,
                            value -> config.partyCommands.cooldown = value
                    )
                    .build();

            var inviteBoop = Option.<Boolean>createBuilder()
                    .name(key("partyFeatures.boopInvites"))
                    .description(keyD("partyFeatures.boopInvites"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.partyCommands.boopInvites,
                            () -> config.partyCommands.boopInvites,
                            value -> config.partyCommands.boopInvites = value
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

    private static Component getInviteMessage(String name) {
        //TODO: Switch this from legacy formatting
        return TextUtils.getTextThatRunsCommand(
                TextUtils.join(
                        TextUtils.withColor("Cick here to invite ", Colors.LIGHT_BLUE),
                        TextUtils.withColor(name, Colors.YELLOW),
                        TextUtils.withColor(" to your party!", Colors.LIGHT_BLUE)
                ),
                TextUtils.join(
                        TextUtils.withColor("Click here to run ", Colors.CYAN),
                        TextUtils.withColor("/p " + name, Colors.YELLOW)
                ),
                "/p invite " + name
        );
    }


    private static final Component DEBUG_PREFIX = TextUtils.join(
            TextUtils.withColor("[", Colors.DARK_GRAY),
            TextUtils.withColor("SBT Party Debugger", Colors.GREEN),
            TextUtils.withColor("]", Colors.DARK_GRAY)
    );
    private static void sendDebugMessage(String message) {
        mc.player.displayClientMessage(TextUtils.join(DEBUG_PREFIX, TextUtils.SPACE, TextUtils.withColor(message, Colors.CYAN)), false);
    }


}
