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
package wtf.cheeze.sbt.utils.enums;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum Rarity {
    COMMON(16777215, "common", true),
    UNCOMMON(5635925, "uncommon", true),
    RARE(5592575, "rare", true),
    EPIC(11141290, "epic", true),
    LEGENDARY(16755200, "legendary", true),
    MYTHIC(16733695, "mythic", true),
    DIVINE(5636095, "divine", false),
    SPECIAL(16733525, "special", false),
    // This is called VERY rather than VERY_SPECIAL because of how rarity on items is parsed
    VERY(16733525, "special", false),
    ULTIMATE(16733525, "ultimate", false),
    ADMIN(16733525, "ultimate", false);

    public final String tooltipId;
    public final int color;
    public final boolean hasPets;

    Rarity(int color, String toolTipId, boolean hasPets) {
        this.color = color;
        this.tooltipId = toolTipId;
        this.hasPets = hasPets;
    }


    private static final Map<String, Rarity> STRING_RARITY_MAP;

    static {
        STRING_RARITY_MAP = new HashMap<>();
        for (Rarity rarity : values()) {
            STRING_RARITY_MAP.put(rarity.name(), rarity);
        }
    }

    /**
     * Parses Rarity from string
     * @param str must be uppercase and align with name in enum
     */
    public static @Nullable Rarity parse(String str) {
        return STRING_RARITY_MAP.get(str);
    }


}
