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
package wtf.cheeze.sbt.utils.render;


public class Colors {

    public static final int WHITE = 0xFFFFFFFF;
    public static final int BLACK = 0xFF000000;
    public static final int GRAY = 0xFFAAAAAA;
    public static final int DARK_GRAY = 0xFF555555;

    public static final int BLUE = 0xFF5555FF;
    public static final int ORANGE = 0xFFFFAA00;
    public static final int CYAN = 0xFF00AAAA;
    public static final int LIME = 0xFF55FF55;
    public static final int RED = 0xFFFF5555;
    public static final int YELLOW = 0xFFFFFF55;
    public static final int GREEN = 0xFF00AA00;
    public static final int PINK = 0xFFFF55FF;
    public static final int LIGHT_BLUE = 0xFF55FFFF;


    public static final int SBT_GREEN = 0xFF37D363;


    private static final float[] THRESHOLDS = {.25f, .5f, .75f};

    public static int fromFloatValue(float f) {
        if (f < THRESHOLDS[0]) return RED;
        if (f < THRESHOLDS[1]) return ORANGE;
        if (f < THRESHOLDS[2]) return YELLOW;
        return LIME;
    }
}
