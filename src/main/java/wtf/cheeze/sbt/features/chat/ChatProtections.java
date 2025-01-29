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

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.utils.MessageManager;
import wtf.cheeze.sbt.utils.TimedValue;
import wtf.cheeze.sbt.utils.render.Colors;

import static wtf.cheeze.sbt.config.categories.Chat.key;
import static wtf.cheeze.sbt.config.categories.Chat.keyD;

import java.util.regex.Pattern;

public class ChatProtections {

    private static TimedValue<String> lastMessageCoop = TimedValue.of(null);
    private static TimedValue<String> lastMessageIp = TimedValue.of(null);
    //TODO: Switch this away from legacy formatting
    private static final String BASE_COOP_MESSAGE = "§cAre you sure you want to invite §e%s §cto your island? They will have complete access and you may not be able to remove them! Run the command again to add them.";
    private static final Pattern IP_PATTERN = Pattern.compile("(?:[0-9]{1,3}\\.){3}[0-9]{1,3}");


    public static void registerEvents() {
        ClientSendMessageEvents.ALLOW_COMMAND.register((message) -> {
            if (message.startsWith("coopadd") && !message.trim().equals("coopadd") && SBTConfig.get().chatProtections.coop) {

                if (lastMessageCoop.getValue() != null && lastMessageCoop.getValue().equals(message)) {
                    return true;
                }
                lastMessageCoop = TimedValue.of(message, 5000);
                MessageManager.send(String.format(BASE_COOP_MESSAGE, message.split(" ")[1]));
                return false;
            } else if (SBTConfig.get().chatProtections.ip) {
                var i = checkIp(message);
                   if (i != 0) {
                       return handleIpMessage(message, i == 2);
                   }
            }
            return true;
            });
        ClientSendMessageEvents.ALLOW_CHAT.register((message) -> {
            if (SBTConfig.get().chatProtections.ip) {
                var i = checkIp(message);
                if (i != 0) {
                    return handleIpMessage(message, i == 2);
                }
            }
            return true;
        });


    }

    /**
     * @param address Whether it's "ip" or an ip adress
     */
    private static boolean handleIpMessage(String message, boolean address) {
            if (lastMessageIp.getValue() != null && lastMessageIp.getValue().equals(message)) {
                return true;
            }
            lastMessageIp = TimedValue.of(message, 5000);
            if (address) {
               MessageManager.send("Are you sure you want to send a message with an ip address? Hypixel may ban you for this! Send the message again to confirm.", Colors.LIGHT_RED);
            } else {
               MessageManager.send("Are you sure you want to send a message with the word \"ip\" in it? Hypixel has been known to auto mute/ban messages containing \"ip\"! Send the message again to confirm.", Colors.LIGHT_RED);
            }

            return false;
    }

    private static int checkIp(String message) {
        if (IP_PATTERN.matcher(message).find()) return 2;
        if (message.contains(" ip ") || message.startsWith("ip ") || message.endsWith(" ip") || message.contains(" ip.") || message.contains(" ip?")) return 1;
        return 0;
    }

    public static class Config {
        @SerialEntry
        public boolean coop = true;

        @SerialEntry
        public boolean ip = true;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {

            var coop = Option.<Boolean>createBuilder()
                    .name(key("chatProtections.coop"))
                    .description(keyD("chatProtections.coop"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.chatProtections.coop,
                            () -> config.chatProtections.coop,
                            value -> config.chatProtections.coop = (Boolean) value
                    )
                    .build();
            var ip = Option.<Boolean>createBuilder()
                    .name(key("chatProtections.ip"))
                    .description(keyD("chatProtections.ip"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.chatProtections.ip,
                            () -> config.chatProtections.ip,
                            value -> config.chatProtections.ip = (Boolean) value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(key("chatProtections"))
                    .description(keyD("chatProtections"))
                    .option(coop)
                    .option(ip)
                    .build();
        }
    }
}
