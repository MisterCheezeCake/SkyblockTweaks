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
package wtf.cheeze.sbt.utils.text;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.utils.errors.ErrorHandler;
import wtf.cheeze.sbt.utils.errors.ErrorLevel;

import java.util.ArrayList;
public class NotificationHandler {

    private static final ArrayList<Text> NOTIFICATION_QUEUE = new ArrayList<Text>();

    public static void registerEvents() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (NOTIFICATION_QUEUE.isEmpty()) return;
            new Thread(() -> {
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    //SkyblockTweaks.LOGGER.error("Notification manager failed to sleep thread", e);
                    ErrorHandler.handleError(e, "Notification manager failed to sleep thread", ErrorLevel.WARNING);
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
    public static void pushChat(Text message) {
        if (MinecraftClient.getInstance().player == null)  NOTIFICATION_QUEUE.add(message);
        else {
            MinecraftClient.getInstance().player.sendMessage(message, false);
        }
    }
}
