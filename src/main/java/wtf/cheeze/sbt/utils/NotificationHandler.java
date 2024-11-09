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
package wtf.cheeze.sbt.utils;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class NotificationHandler {

    public static final ArrayList<Text> NOTIFICATION_QUEUE = new ArrayList<Text>();

    public static void registerEvents() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (NOTIFICATION_QUEUE.isEmpty()) return;
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
