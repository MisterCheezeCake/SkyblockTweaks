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

//? if > 1.21.5
/*import net.minecraft.client.gl.RenderPipelines;*/
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import wtf.cheeze.sbt.hud.bounds.Bounds;
import wtf.cheeze.sbt.hud.bounds.BoundsRelative;
import wtf.cheeze.sbt.hud.HUD;
import wtf.cheeze.sbt.utils.render.RenderUtils;

/**
 * A HUD that displays a bar, code liberally inspired by SBA, but way simpler thanks to modern mc, bar textures taken directly from SBA
 */
public abstract class BarHud extends HUD {
    public static final Identifier UNFILLED = Identifier.of("skyblocktweaks", "bars/unfilled.png");
    public static final Identifier FILLED = Identifier.of("skyblocktweaks", "bars/filled.png");

    public abstract int getColor();
    public abstract float getFill();

    public static final int BAR_WIDTH = 71;
    public static final int BAR_HEIGHT = 5;

    @Override
    public void render(DrawContext context, boolean fromHudScreen, boolean hovered) {
        if (!shouldRender(fromHudScreen)) return;
        var bounds = getCurrentBounds();
        if (fromHudScreen) {
            drawBackground(context, hovered ? BACKGROUND_HOVERED : BACKGROUND_NOT_HOVERED);
        }

        var color = getColor();
        if (bounds.scale == 1.0f) {
            RenderUtils.drawBar(context, UNFILLED, bounds.x, bounds.y, BAR_WIDTH, color);
            RenderUtils.drawBar(context, FILLED, bounds.x, bounds.y, calculateFill(getFill()), color);
        } else {
            RenderUtils.beginScale(context, bounds.scale);
            RenderUtils.drawBar(context, UNFILLED, (int) (bounds.x / bounds.scale), (int) (bounds.y / bounds.scale), BAR_WIDTH, color);
            RenderUtils.drawBar(context, FILLED, (int) (bounds.x / bounds.scale), (int) (bounds.y / bounds.scale), calculateFill(getFill()), color);
            RenderUtils.popMatrix(context);
        }
    }

    @Override
    public @NotNull Bounds getCurrentBounds() {
        var scale = (float) INFO.getScale.get();
        switch (INFO.getAnchorPoint.get()) {
            case LEFT -> {
                return new Bounds(getActualX(INFO.getX.get()), getActualY(INFO.getY.get()), BAR_WIDTH * scale, BAR_HEIGHT * scale, scale);
            }
            case RIGHT -> {
                return new Bounds((int) (getActualX(INFO.getX.get()) - BAR_WIDTH * scale), getActualY(INFO.getY.get()), BAR_WIDTH * scale, BAR_HEIGHT * scale, scale);
            }
            case CENTER -> {
                return new Bounds((int) (getActualX(INFO.getX.get()) - BAR_WIDTH * scale / 2), getActualY(INFO.getY.get()), BAR_WIDTH * scale, BAR_HEIGHT * scale, scale);
            }
            default -> throw new IllegalStateException("Unexpected value: " + INFO.getAnchorPoint.get());
        }
    }
    @Override
    public @NotNull BoundsRelative getCurrentBoundsRelative() {
        var scale = (float) INFO.getScale.get();
        switch (INFO.getAnchorPoint.get()) {
            case LEFT -> {
                return new BoundsRelative(INFO.getX.get(), INFO.getY.get(), BAR_WIDTH * scale, BAR_HEIGHT * scale, scale);
            }
            case RIGHT -> {
                return new BoundsRelative(INFO.getX.get() - getRelativeBarWidth() * scale, INFO.getY.get(), BAR_WIDTH * scale, BAR_HEIGHT * scale, scale);
            }
            case CENTER -> {
                return new BoundsRelative(INFO.getX.get() - getRelativeBarWidth() * scale / 2, INFO.getY.get(), BAR_WIDTH * scale, BAR_HEIGHT * scale, scale);
            }
            default -> throw new IllegalStateException("Unexpected value: " + INFO.getAnchorPoint.get());

        }
    }
    private static int calculateFill(float percent) {
        if (percent >= 1.0f) return BAR_WIDTH;
        var i = (int) (percent * BAR_WIDTH);
        return i;
    }

    private static float getRelativeBarWidth(){
        return BAR_WIDTH / client.getWindow().getWidth();
    }

}