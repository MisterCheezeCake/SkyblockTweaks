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
package wtf.cheeze.sbt.utils.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.utils.RenderUtils;

public abstract class TextHUD extends HUD {
    public abstract String getText();

    public void render(DrawContext context, boolean fromHudScreen, boolean hovered) {
        if (!shouldRender(fromHudScreen)) return;
        var bounds = getCurrentBounds();
        if (fromHudScreen) {
            drawBackground(context, hovered ? BACKGROUND_HOVERED : BACKGROUND_NOT_HOVERED);
        }
        if (bounds.scale == 1.0f) {
            RenderUtils.drawString(context, Text.literal(getText()), bounds.x, bounds.y, (int) INFO.getColor.get(), (boolean) INFO.getShadow.get());
        } else {
            RenderUtils.drawString(context, Text.literal(getText()), bounds.x, bounds.y, (int) INFO.getColor.get(), (boolean) INFO.getShadow.get(), bounds.scale);
        }
    }
    public Bounds getCurrentBounds() {
        var textRenderer = MinecraftClient.getInstance().textRenderer;
        var scale = (float) INFO.getScale.get();
        return new Bounds(getActualX((float) INFO.getX.get()), getActualY((float) INFO.getY.get()), textRenderer.getWidth(getText()) * scale, textRenderer.fontHeight * scale, scale);
    }

    public BoundsRelative getCurrentBoundsRelative() {
        var textRenderer = MinecraftClient.getInstance().textRenderer;
        var scale = (float) INFO.getScale.get();
        return new BoundsRelative((float) INFO.getX.get(), (float) INFO.getY.get(), textRenderer.getWidth(getText()) * scale, textRenderer.fontHeight * scale, scale);
    }
}
