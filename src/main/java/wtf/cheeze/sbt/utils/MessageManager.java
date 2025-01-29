package wtf.cheeze.sbt.utils;

import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.utils.render.Colors;

public class MessageManager {

    public static final Text PREFIX = TextUtils.join(
            TextUtils.withColor("[", Colors.GRAY),
            TextUtils.withColor("SkyblockTweaks", Colors.SBT_GREEN),
            TextUtils.withColor("] ", Colors.GRAY)
    );


    public static void send(Text message) {
        SkyblockTweaks.mc.player.sendMessage(TextUtils.join(PREFIX, TextUtils.SPACE, message), false);
    }

    public static void send(String message) {
        send(Text.literal(message));
    }
    public static void send(String message, int color) {
        send(Text.literal(message).withColor(color));
    }
}
