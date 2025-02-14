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
package wtf.cheeze.sbt.features;

import wtf.cheeze.sbt.utils.MessageManager;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.events.WorldLoadEvents;
import wtf.cheeze.sbt.utils.render.Colors;

public class MouseLock {
    public static boolean locked = false;

    public static void registerEvents() {
        WorldLoadEvents.WORLD_LOAD.register((world, wer) -> {
            if (locked) {
                locked = false;
                MessageManager.send("Disabled Mouse Lock due to world change", Colors.RED);
            }

        });
    }

    public static void toggle() {
        locked = !locked;
        MessageManager.send(
                MouseLock.locked ?
                        TextUtils.withColor("Enabled Mouse Lock", Colors.GREEN) :
                        TextUtils.withColor("Disabled Mouse Lock", Colors.RED)
        );
    }
}
