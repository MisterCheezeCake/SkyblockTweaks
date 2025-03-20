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

import wtf.cheeze.sbt.utils.enums.Crop;

import java.util.Map;

public record Garden(
        int[] gardenLevels,
        Map<String, int[]> cropTables,
        Map<Crop, String> cropEntries
) {
    public int[] getCropTable(Crop crop) {
        return cropTables.get(cropEntries.get(crop));
    }
    public static Garden empty() {
        return new Garden(new int[0], Map.of(), Map.of());
    }

}
