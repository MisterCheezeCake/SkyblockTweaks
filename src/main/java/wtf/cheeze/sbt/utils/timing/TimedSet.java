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
package wtf.cheeze.sbt.utils.timing;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public class TimedSet<T> {

    private final int expireTime;
    private final Set<Entry<T>> backingSet = new HashSet<>();


    public TimedSet(int expireTime) {
        this.expireTime = expireTime;
    }

    private Set<T> getValues() {
        expiryCheck();
        return backingSet.stream().map(Entry::value).collect(Collectors.toSet());
    }

    public boolean contains(T value) {
        return getValues().contains(value);
    }

    public void add(T value) {
        backingSet.add(new Entry<>(value, System.currentTimeMillis()));
    }

    public void remove(T value) {
        backingSet.removeIf(entry -> entry.value().equals(value));
    }

    private void expiryCheck() {
        long t = System.currentTimeMillis();
        backingSet.removeIf(entry ->  t - entry.time() > expireTime);
    }


    private record Entry<T>(T value, long time) {}


}
