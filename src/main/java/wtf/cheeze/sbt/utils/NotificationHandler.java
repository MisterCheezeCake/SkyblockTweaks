package wtf.cheeze.sbt.utils;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class NotificationHandler {

    public static final ArrayList<Text> NOTIFICATION_QUEUE = new ArrayList<Text>();

    public static void registerEvents() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (NOTIFICATION_QUEUE.size() == 0) return;
            new Thread(() -> {
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                client.execute(() -> {
                    for (Text message : NOTIFICATION_QUEUE) {
                        client.player.sendMessage(message, false);
                    }
                    NOTIFICATION_QUEUE.clear();
                });
            }).start();
        });
    }
}
