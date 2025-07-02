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
package wtf.cheeze.sbt.hud.cache;

import wtf.cheeze.sbt.utils.errors.ErrorHandler;
import wtf.cheeze.sbt.utils.errors.ErrorLevel;
import wtf.cheeze.sbt.events.CacheUpdateEvents;

import java.util.function.Supplier;

/**
 * A cache contains a value which is managed externally by a rendering function, and it tells the rendering function when it needs to update the value.
 */
public class Cache<T>{
    private T value;
    private final T errorValue;

    private boolean dueForUpdate = true;
    private final Supplier<T> supplier;

    public final UpdateTiming timing;

    public T get() {
        return value;
    }

    public boolean isDueForUpdate() {
        return dueForUpdate;
    }

    public void update() {
        try {
            value = supplier.get();
            dueForUpdate = false;
        } catch (Exception e) {
            ErrorHandler.handle(e, "Error while updating cached HUD value", ErrorLevel.WARNING);
            value = errorValue;
        }
    }


    public Cache(UpdateTiming timing, Supplier<T> supplier, T errorValue) {
        this.timing = timing;
        this.errorValue = errorValue;
        this.value = errorValue;
        this.supplier = supplier;
        switch (timing) {
            case FRAME -> {
                // The hud will never check the cache's state in frame mode, so we don't need to do anything here
            }
            case TICK -> CacheUpdateEvents.TICK.register(client -> dueForUpdate = true);
            case QUARTER_SECOND -> CacheUpdateEvents.QUARTER_SECOND.register(() -> dueForUpdate = true);
            case HALF_SECOND -> CacheUpdateEvents.HALF_SECOND.register(() -> dueForUpdate = true);
            case SECOND -> CacheUpdateEvents.SECOND.register(() -> dueForUpdate = true);
            case MEMOIZED -> {
                // The value will never change in memoized mode, so we don't need to do anything here
            }
        }

    }


}
