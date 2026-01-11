package wtf.cheeze.sbt.utils.text;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.utils.render.Colors;

public class MessageManager {

    private static final Minecraft client = Minecraft.getInstance();

    public static final Component PREFIX = TextUtils.join(
            TextUtils.withColor("[", Colors.GRAY),
            TextUtils.withColor("SkyblockTweaks", Colors.SBT_GREEN),
            TextUtils.withColor("] ", Colors.GRAY)
    );

    public static void send(Component message) {
        if (!checkPlayer()) {
            SkyblockTweaks.LOGGER.info("Message Manager tried to send a message but the player was null");
            return;
        }
        client.player.displayClientMessage(TextUtils.join(PREFIX, message), false);
    }

    public static void send(String message) {
        send(Component.literal(message));
    }
    public static void send(String message, int color) {
        send(Component.literal(message).withColor(color));
    }

    public static boolean checkPlayer() {
        return client.player != null;
    }
}
