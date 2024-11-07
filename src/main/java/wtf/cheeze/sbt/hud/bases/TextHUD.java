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
package wtf.cheeze.sbt.hud.bases;

import net.minecraft.client.gui.DrawContext;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.bounds.Bounds;
import wtf.cheeze.sbt.hud.bounds.BoundsRelative;
import wtf.cheeze.sbt.hud.HUD;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.utils.render.RenderUtils;

public abstract class TextHUD extends HUD {
    //public abstract String getText();

    public SingleHudLine line;

    @Override
    public void render(DrawContext context, boolean fromHudScreen, boolean hovered) {
        if (!shouldRender(fromHudScreen)) return;
        var bounds = getCurrentBounds();
        if (fromHudScreen) {
            drawBackground(context, hovered ? BACKGROUND_HOVERED : BACKGROUND_NOT_HOVERED, line.mode.get() == DrawMode.OUTLINE);
        }
        RenderUtils.beginScale(context, bounds.scale);
        line.render(context, bounds.x, bounds.y, bounds.scale);
        RenderUtils.endScale(context);
    }

    public Bounds getCurrentBounds() {
        var scale = (float) INFO.getScale.get();
        switch (INFO.getAnchorPoint.get()) {
            case LEFT -> {
                return new Bounds(getActualX((float) INFO.getX.get()), getActualY((float) INFO.getY.get()), RenderUtils.getStringWidth(line.text.get()) * scale, SkyblockTweaks.mc.textRenderer.fontHeight * scale, scale);
            }
            case RIGHT -> {
                return new Bounds((int) (getActualX((float) INFO.getX.get()) - RenderUtils.getStringWidth(line.text.get()) * scale), getActualY((float) INFO.getY.get()), RenderUtils.getStringWidth(line.text.get()) * scale, SkyblockTweaks.mc.textRenderer.fontHeight * scale, scale);
            }
            case CENTER -> {
                return new Bounds((int) (getActualX((float) INFO.getX.get()) - RenderUtils.getStringWidth(line.text.get()) * scale / 2), getActualY((float) INFO.getY.get()), RenderUtils.getStringWidth(line.text.get()) * scale, SkyblockTweaks.mc.textRenderer.fontHeight * scale, scale);
            }
            default -> throw new IllegalStateException("Unexpected value: " + INFO.getAnchorPoint.get());
        }
    }

    public BoundsRelative getCurrentBoundsRelative() {
        var scale = (float) INFO.getScale.get();
        switch (INFO.getAnchorPoint.get()) {
            case LEFT -> {
                return new BoundsRelative((float) INFO.getX.get(), (float) INFO.getY.get(), RenderUtils.getStringWidth(line.text.get()) * scale, SkyblockTweaks.mc.textRenderer.fontHeight * scale, scale);
            }
            case RIGHT -> {
                return new BoundsRelative((float) INFO.getX.get() - RenderUtils.getRelativeStringWidth(line.text.get()) * scale, (float) INFO.getY.get(), RenderUtils.getStringWidth(line.text.get()) * scale, SkyblockTweaks.mc.textRenderer.fontHeight * scale, scale);
            }
            case CENTER -> {
                return new BoundsRelative((float) INFO.getX.get() - RenderUtils.getRelativeStringWidth(line.text.get()) * scale / 2, (float) INFO.getY.get(), RenderUtils.getStringWidth(line.text.get()) * scale, SkyblockTweaks.mc.textRenderer.fontHeight * scale, scale);
            }
            default -> throw new IllegalStateException("Unexpected value: " + INFO.getAnchorPoint.get());
        }
    }
}