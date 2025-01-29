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

public class TimedValue<T> {
    private final T value;
    private final long time;

    private int expireTime = 1000;

    private TimedValue(T value) {
        this.value = value;
        this.time = System.currentTimeMillis();
    }
    private TimedValue(T value, int expireTime) {
        this.value = value;
        this.time = System.currentTimeMillis();
        this.expireTime = expireTime;
    }
    public T getValue() {
        if (isExpired()) {
            return null;
        }
        return value;
    }
    private boolean isExpired() {
        return System.currentTimeMillis() - time > expireTime;
    }
    public static <T> TimedValue<T> of(T value) {
        return new TimedValue<>(value);
    }
    public static <T> TimedValue<T> of(T value, int expireTime) {
        return new TimedValue<>(value, expireTime);
    }
}
