package wtf.cheeze.sbt.features.chat;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SkyblockTweaksConfig;
import wtf.cheeze.sbt.utils.TimedValue;

import java.util.regex.Pattern;

public class ChatProtections {

    private static TimedValue<String> lastMessageCoop = TimedValue.of(null);
    private static TimedValue<String> lastMessageIp = TimedValue.of(null);
    private static final String BASE_COOP_MESSAGE = "§7[§aSkyblockTweaks§f§7] §cAre you sure you want to invite §e%s §cto your island? They will have complete access and you may not be able to remove them! Run the command again to add them.";
    private static final Pattern IP_PATTERN = Pattern.compile("(?:[0-9]{1,3}\\.){3}[0-9]{1,3}");


    public static void registerEvents() {
        ClientSendMessageEvents.ALLOW_COMMAND.register((message) -> {
            if (message.startsWith("coopadd") && !message.trim().equals("coopadd") && SkyblockTweaks.CONFIG.config.chatProtections.coop) {

                if (lastMessageCoop.getValue() != null && lastMessageCoop.getValue().equals(message)) {
                    return true;
                }
                lastMessageCoop = TimedValue.of(message, 5000);
                SkyblockTweaks.mc.player.sendMessage(Text.literal(String.format(BASE_COOP_MESSAGE, message.split(" ")[1])));
                return false;
            } else if (SkyblockTweaks.CONFIG.config.chatProtections.ip) {
                var i = checkIp(message);
                   if (i != 0) {
                       return handleIpMessage(message, i == 2);
                   }
            }
            return true;
            });
        ClientSendMessageEvents.ALLOW_CHAT.register((message) -> {
            if (SkyblockTweaks.CONFIG.config.chatProtections.ip) {
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
                SkyblockTweaks.mc.player.sendMessage(Text.literal("§7[§aSkyblockTweaks§f§7] §cAre you sure you want to send a message with an ip address? Hypixel may ban you for this! Send the message again to confirm."));
            } else {
                SkyblockTweaks.mc.player.sendMessage(Text.literal("§7[§aSkyblockTweaks§f§7] §cAre you sure you want to send a message with the word \"ip\" in it? Hypixel has been known to auto mute/ban messages containing \"ip\"! Send the message again to confirm."));
            }

            return false;
    }

    private static int checkIp(String message) {
        if (IP_PATTERN.matcher(message).find()) return 2;
        if (message.contains(" ip ") || message.startsWith("ip ") || message.endsWith(" ip")) return 1;
        return 0;
    }

    public static class Config {
        @SerialEntry
        public boolean coop = true;

        @SerialEntry
        public boolean ip = true;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {

            var coop = Option.<Boolean>createBuilder()
                    .name(Text.literal("Coop Add Confirmation"))
                    .description(OptionDescription.of(Text.literal("Requires confirmation before actually sending a /coopadd request")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.chatProtections.coop,
                            () -> config.chatProtections.coop,
                            value -> config.chatProtections.coop = (Boolean) value
                    )
                    .build();
            var ip = Option.<Boolean>createBuilder()
                    .name(Text.literal("IP Protection"))
                    .description(OptionDescription.of(Text.literal("Warns you before sending a message containing an ip address")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.chatProtections.ip,
                            () -> config.chatProtections.ip,
                            value -> config.chatProtections.ip = (Boolean) value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(Text.literal("Chat Protections"))
                    .description(OptionDescription.of(Text.literal("Settings for chat related features")))
                    .option(coop)
                    .option(ip)
                    .build();
        }
    }
}
