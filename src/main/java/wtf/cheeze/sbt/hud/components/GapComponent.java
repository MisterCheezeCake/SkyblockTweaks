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
package wtf.cheeze.sbt.hud.components;

import net.minecraft.client.gui.DrawContext;

public class GapComponent implements HudComponent{


    public static final GapComponent TWO = new GapComponent(2);

    private final int width;

    public GapComponent(int width) {
        this.width = width;
    }


    @Override
    public int render(DrawContext context, int x, int y, float scale) {
        // This component is used to create a gap in the HUD, so it doesn't need to render anything.
        return 1;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getlines() {
        return 1;
    }
}
