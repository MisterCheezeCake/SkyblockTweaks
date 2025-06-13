/*
 * Copyright (C) 2025 MisterCheezeCake
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
package wtf.cheeze.sbt.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.text.Text;

/**
 * Custom chat events which get us the message before Fabric API has
 * the chance to change their content or cancel them. These should always
 * be used over {@link net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents}
 * when information needs to be extracted.
 */
public class ChatEvents {

    public static final Event<OnMessage> ON_GAME = EventFactory.createArrayBacked(OnMessage.class, listeners -> (message) -> {
        for (OnMessage listener : listeners) {
            listener.onMessage(message);
        }
    });

    public static final Event<OnMessage> ON_ACTION_BAR = EventFactory.createArrayBacked(OnMessage.class, listeners -> (message) -> {
        for (OnMessage listener : listeners) {
            listener.onMessage(message);
        }
    });


    @FunctionalInterface
    public interface OnMessage {
        void onMessage(Text message);
    }
}
