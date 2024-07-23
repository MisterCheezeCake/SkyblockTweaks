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

import net.minecraft.client.gui.DrawContext;
import wtf.cheeze.sbt.SkyBlockTweaks;
import wtf.cheeze.sbt.utils.RenderUtils;
import wtf.cheeze.sbt.utils.HudLine;

public abstract class TextHUD extends HUD {
    //public abstract String getText();

    public HudLine line;

    @Override
    public void render(DrawContext context, boolean fromHudScreen, boolean hovered) {
        if (!shouldRender(fromHudScreen)) return;
        var bounds = getCurrentBounds();
        if (fromHudScreen) {
            drawBackground(context, hovered ? BACKGROUND_HOVERED : BACKGROUND_NOT_HOVERED, line.mode.get() == HudLine.DrawMode.OUTLINE);
        }
        RenderUtils.beginScale(context, bounds.scale);
        line.render(context, bounds.x, bounds.y, bounds.scale);
        RenderUtils.endScale(context);



    }
    public Bounds getCurrentBounds() {
        var scale = (float) INFO.getScale.get();
        return new Bounds(getActualX((float) INFO.getX.get()), getActualY((float) INFO.getY.get()), RenderUtils.getStringWidth(line.text.get()) * scale, SkyBlockTweaks.mc.textRenderer.fontHeight * scale, scale);
    }

    public BoundsRelative getCurrentBoundsRelative() {
        var scale = (float) INFO.getScale.get();
        return new BoundsRelative((float) INFO.getX.get(), (float) INFO.getY.get(), RenderUtils.getStringWidth(line.text.get()) * scale, SkyBlockTweaks.mc.textRenderer.fontHeight * scale, scale);
    }
}
