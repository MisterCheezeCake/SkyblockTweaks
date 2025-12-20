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
package wtf.cheeze.sbt.utils.constants;

import wtf.cheeze.sbt.SkyblockTweaks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record  DisabledFeatures(
        Map<String, RemoteEntry> features
) {

    public Map<String, Entry> getDisabledFeatures() {
        String currentVersion = SkyblockTweaks.VERSION.getVersionStringWithMc();
        HashMap<String, Entry> map = new HashMap<>();
        for (var entry: features.entrySet()) {
            var value =  entry.getValue();
            for (var version: value.versions()) {
                if (currentVersion.equals(version)) {
                    map.put(entry.getKey(), new Entry(value.message(), value.link()));
                    break;
                }
            }
        }
        return map;
    }

    public static DisabledFeatures empty() {
        return new DisabledFeatures(Map.of());
    }

    public record Entry(
            String message,
            String link
    ) {
        public Entry(String message) {
            this(message, "");
        }
    }
    public record RemoteEntry (
            String message,
            String link,
            List<String> versions
    ) {}
}
