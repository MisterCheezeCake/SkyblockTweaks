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
package wtf.cheeze.sbt.hud.bases;

import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;
import wtf.cheeze.sbt.hud.HUD;
import wtf.cheeze.sbt.hud.bounds.Bounds;
import wtf.cheeze.sbt.hud.bounds.BoundsRelative;
import wtf.cheeze.sbt.hud.components.HudComponent;
import wtf.cheeze.sbt.utils.CheezePair;
import wtf.cheeze.sbt.utils.render.RenderUtils;

public abstract class SingleLineHybridHud extends HUD {
    private int height = 0;
    private int width = 0;
    protected CheezePair<HudComponent, Integer>[] components;


    //TODO: Scale is not perfect here
    @Override
    public void render(DrawContext context, boolean fromHudScreen, boolean hovered) {
        if (!shouldRender(fromHudScreen)) return;
        var bounds = getCurrentBounds();
        if (fromHudScreen) {
            drawBackground(context, hovered ? BACKGROUND_HOVERED : BACKGROUND_NOT_HOVERED);
        }
        RenderUtils.beginScale(context, bounds.scale);
        int renderX = bounds.x;
        int tallest = 0;
        for (var pair: components) {
            var component = pair.key();
            component.render(context, renderX, bounds.y + (int) (pair.val()*bounds.scale), bounds.scale);
            renderX += (int) (bounds.scale * component.getWidth());
            if (component.getHeight() > tallest) {
                tallest = component.getHeight();
            }
        }
        RenderUtils.popMatrix(context);
        this.height = (int) (tallest * bounds.scale);
        this.width = renderX - bounds.x;
    }

    @Override
    public @NotNull Bounds getCurrentBounds() {
        var scale = (float) INFO.getScale.get();
        switch (INFO.getAnchorPoint.get()) {
            case LEFT -> {
                return new Bounds(getActualX(INFO.getX.get()), getActualY(INFO.getY.get()), width, height , scale);
            }
            case RIGHT -> {
                return new Bounds((int) (getActualX(INFO.getX.get()) - width ), getActualY(INFO.getY.get()), width , height , scale);
            }
            case CENTER -> {
                return new Bounds((int) (getActualX(INFO.getX.get()) - width  / 2), getActualY(INFO.getY.get()), width , height , scale);
            }
            default -> throw new IllegalStateException("Unexpected value: " + INFO.getAnchorPoint.get());
        }
    }
    @Override
    public @NotNull BoundsRelative getCurrentBoundsRelative() {
        var scale = (float) INFO.getScale.get();
        switch (INFO.getAnchorPoint.get()) {
            case LEFT -> {
                return new BoundsRelative(INFO.getX.get(), INFO.getY.get(), width, height , scale);
            }
            case RIGHT -> {
                return new BoundsRelative(INFO.getX.get() - RenderUtils.getRelativeWidth(width), INFO.getY.get(), width, height , scale);
            }
            case CENTER -> {
                return new BoundsRelative(INFO.getX.get() - RenderUtils.getRelativeWidth(width) / 2, INFO.getY.get(), width , height, scale);
            }
            default -> throw new IllegalStateException("Unexpected value: " + INFO.getAnchorPoint.get());

        }
    }

    @Override
    public void drawBackground(DrawContext context, int color) {
        var bounds = getCurrentBounds();
        int i = (int) (1 * bounds.scale);
        context.fill(bounds.x - i, bounds.y - i, (int) (bounds.x + bounds.width + i), (int) (bounds.y + bounds.height + i - 1), color);
    }
}
