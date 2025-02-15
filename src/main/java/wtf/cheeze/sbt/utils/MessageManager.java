package wtf.cheeze.sbt.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.utils.render.Colors;

public class MessageManager {

    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static final Text PREFIX = TextUtils.join(
            TextUtils.withColor("[", Colors.GRAY),
            TextUtils.withColor("SkyblockTweaks", Colors.SBT_GREEN),
            TextUtils.withColor("] ", Colors.GRAY)
    );


    public static void send(Text message) {
        if (!checkPlayer()) {
            SkyblockTweaks.LOGGER.info("Message Manager tried to send a message but the player was null");
            return;
        }
        client.player.sendMessage(TextUtils.join(PREFIX, message), false);
    }

    public static void send(String message) {
        send(Text.literal(message));
    }
    public static void send(String message, int color) {
        send(Text.literal(message).withColor(color));
    }

    public static boolean checkPlayer() {
        return client.player != null;
    }
}
