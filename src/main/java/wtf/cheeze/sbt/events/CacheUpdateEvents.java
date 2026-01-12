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

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.Event;

import java.util.Timer;

public class CacheUpdateEvents {
    public static final Event<ClientTickEvents.EndTick> TICK = ClientTickEvents.END_CLIENT_TICK;
    public static final Event<Runnable> SECOND = EventUtils.getRunnableBackedEvent();
    public static final Event<Runnable> HALF_SECOND = EventUtils.getRunnableBackedEvent();
    public static final Event<Runnable> QUARTER_SECOND = EventUtils.getRunnableBackedEvent();

    private static final Timer timer = new Timer();

    static {
        timer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                QUARTER_SECOND.invoker().run();
            }
        }, 0, 250);
        timer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                HALF_SECOND.invoker().run();
            }
        }, 0, 500);
        timer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                SECOND.invoker().run();
            }
        }, 0, 1000);
    }
}
