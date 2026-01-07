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
package wtf.cheeze.sbt.hud.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import wtf.cheeze.sbt.utils.text.TextUtils;
import wtf.cheeze.sbt.utils.render.Colors;

public interface HudComponent {
    Component ERROR = TextUtils.withColor("ERROR", Colors.RED);

    int render(GuiGraphics guiGraphics, int x, int y, float scale);
    int getWidth();
    int getHeight();
    int getlines();
}
