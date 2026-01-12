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

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import wtf.cheeze.sbt.features.huds.TickerHud;
import wtf.cheeze.sbt.hud.bounds.Bounds;
import wtf.cheeze.sbt.hud.bounds.BoundsRelative;
import wtf.cheeze.sbt.hud.HUD;
import wtf.cheeze.sbt.utils.render.RenderUtils;

/**
 * The abstract representation of the ticker HUD, for the implementation, see {@link TickerHud}
 */
public abstract class AbstractTickerHud extends HUD {
    private static final ResourceLocation FULL = ResourceLocation.fromNamespaceAndPath("skyblocktweaks", "tickers/full.png");
    private static final ResourceLocation BLANK = ResourceLocation.fromNamespaceAndPath("skyblocktweaks", "tickers/blank.png");
    private static final int DIMENSION = 9;

    /**
     * FromHudScreen
     */
    protected boolean fhs = false;

    public abstract int getMax(boolean fromHudScreen);
    public abstract int getUsable(boolean fromHudScreen);

    @Override
    public void render(GuiGraphics guiGraphics, boolean fromHudScreen, boolean hovered) {
        if (!shouldRender(fromHudScreen)) return;
        var bounds = getCurrentBounds();
        if (fromHudScreen) {
            drawBackground(guiGraphics, hovered ? BACKGROUND_HOVERED : BACKGROUND_NOT_HOVERED);
            fhs = true;
        } else {
            fhs = false;
        }
        if (bounds.scale != 1.0f) {
            RenderUtils.beginScale(guiGraphics, bounds.scale);
        }
        var x2 = drawTickers(guiGraphics, getUsable(fromHudScreen), bounds.x, bounds.y, bounds.scale, true);
        drawTickers(guiGraphics, getMax(fromHudScreen) - getUsable(fromHudScreen), x2, bounds.y, bounds.scale, false);
        if (bounds.scale != 1.0f) {
            RenderUtils.popMatrix(guiGraphics);
        }

    }

    private static float drawTickers(GuiGraphics guiGraphics, int number, float x, int y, float scale, boolean filled) {
        float drawX = x;
        for (int i = 0; i < number; i++) {
            RenderUtils.drawTexture(guiGraphics, filled ? FULL : BLANK, (int) (drawX / scale), (int) (y / scale), DIMENSION, DIMENSION, DIMENSION , DIMENSION);
            drawX = (2 + drawX + (DIMENSION * scale));
        }
        return drawX;
    }

    private int getWidth() {
        return DIMENSION * getMax(fhs) + 2 * getMax(fhs);
    }

    private int getRelativeWidth() {
        return getWidth() / client.getWindow().getScreenWidth();
    }

    @Override
    public @NotNull Bounds getCurrentBounds() {
        var scale = (float) INFO.getScale.get();
        switch (INFO.getAnchorPoint.get()) {
            case LEFT -> {
                return new Bounds(getActualX(INFO.getX.get()), getActualY(INFO.getY.get()), getWidth() * scale, DIMENSION * scale, scale);
            }
            case RIGHT -> {
                return new Bounds((int) (getActualX(INFO.getX.get()) - getWidth() * scale), getActualY(INFO.getY.get()), getWidth() * scale, DIMENSION * scale, scale);
            }
            case CENTER -> {
                return new Bounds((int) (getActualX(INFO.getX.get()) - getWidth() * scale / 2), getActualY(INFO.getY.get()), getWidth() * scale, DIMENSION * scale, scale);
            }
            default -> throw new IllegalStateException("Unexpected value: " + INFO.getAnchorPoint.get());
        }
    }
    @Override
    public @NotNull BoundsRelative getCurrentBoundsRelative() {
        var scale = (float) INFO.getScale.get();
        switch (INFO.getAnchorPoint.get()) {
            case LEFT -> {
                return new BoundsRelative(INFO.getX.get(), INFO.getY.get(), getWidth() * scale, DIMENSION * scale, scale);
            }
            case RIGHT -> {
                return new BoundsRelative(INFO.getX.get() - getRelativeWidth() * scale, INFO.getY.get(), getWidth() * scale, DIMENSION * scale, scale);
            }
            case CENTER -> {
                return new BoundsRelative(INFO.getX.get() - getRelativeWidth() * scale / 2, INFO.getY.get(), getWidth() * scale, DIMENSION * scale, scale);
            }
            default -> throw new IllegalStateException("Unexpected value: " + INFO.getAnchorPoint.get());
        }
    }



}
