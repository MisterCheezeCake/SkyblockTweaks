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
import net.minecraft.client.gui.screen.DownloadingTerrainScreen.WorldEntryReason;
import net.minecraft.client.world.ClientWorld;

public class WorldLoadEvents {

    public static Event<OnWorldLoad> WORLD_LOAD = EventFactory.createArrayBacked(OnWorldLoad.class, listeners -> (world, worldEntryReason) -> {
        for (OnWorldLoad listener : listeners) {
            listener.onWorldLoad(world, worldEntryReason);
        }
    });

    @FunctionalInterface
    public interface OnWorldLoad {
        void onWorldLoad(ClientWorld world, WorldEntryReason worldEntryReason);
    }
}
