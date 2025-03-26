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
package wtf.cheeze.sbt.hud.icon;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import wtf.cheeze.sbt.utils.render.RenderUtils;

public class TextureIcon implements HudIcon {

    private final Identifier iconTexture;

    public TextureIcon(@NotNull Identifier texture) {
        this.iconTexture = texture;
    }

    @Override
    public void render(DrawContext context, int x, int y, float scale) {
        RenderUtils.drawTexture(context, iconTexture, (int) (x /scale) , (int) (y / scale),  8, 8, 8, 8);
    }
}
