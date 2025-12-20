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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;

public class DrawSlotEvents {

    public static Event<OnDrawSlot> BEFORE_ITEM = EventFactory.createArrayBacked(OnDrawSlot.class, listeners -> (screenTitle, context, slot) -> {
        for (OnDrawSlot listener : listeners) {
            listener.onDrawSlot(screenTitle, context, slot);
        }
    });

    @FunctionalInterface
    public interface OnDrawSlot {
        void onDrawSlot(Component screenTitle, GuiGraphics guiGraphics, Slot slot);
    }
}
