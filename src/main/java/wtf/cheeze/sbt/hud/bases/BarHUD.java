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

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.hud.bounds.Bounds;
import wtf.cheeze.sbt.hud.bounds.BoundsRelative;
import wtf.cheeze.sbt.hud.HUD;
import wtf.cheeze.sbt.utils.render.RenderUtils;

/**
 * A HUD that displays a bar, code liberally inspired by SBA, but way simpler thanks to modern mc, bar textures taken directly from SBA
 */
public abstract class BarHUD extends HUD {
    public static final Identifier UNFILLED = Identifier.of("skyblocktweaks", "bars/unfilled.png");
    public static final Identifier FILLED = Identifier.of("skyblocktweaks", "bars/filled.png");
    public static final int BAR_WIDTH = 71;
    public static final int BAR_HEIGHT = 5;

    @Override
    public void render(DrawContext context, boolean fromHudScreen, boolean hovered) {
        if (!shouldRender(fromHudScreen)) return;
        var bounds = getCurrentBounds();
        if (fromHudScreen) {
            drawBackground(context, hovered ? BACKGROUND_HOVERED : BACKGROUND_NOT_HOVERED);
        }

            //? if =1.21.1 {
        /*var colors = RenderUtils.getColor3f((int) INFO.getColor.get());
             if (bounds.scale == 1.0f) {
            context.setShaderColor(colors.red, colors.green, colors.blue, 1.0f);
            context.drawTexture(UNFILLED, bounds.x, bounds.y, 0, 0, BAR_WIDTH, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);
            context.drawTexture(FILLED, bounds.x, bounds.y, 0, 0, calculateFill((float) INFO.getFill.get()), BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);
            context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            RenderUtils.beginScale(context, bounds.scale);
            context.setShaderColor(colors.red, colors.green, colors.blue, 1.0f);
            context.drawTexture(UNFILLED, (int)(bounds.x/bounds.scale), (int)(bounds.y/bounds.scale), 0, 0, BAR_WIDTH, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);
            context.drawTexture(FILLED, (int)(bounds.x/bounds.scale), (int)(bounds.y/bounds.scale), 0, 0, calculateFill((float) INFO.getFill.get()), BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);
            context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderUtils.endScale(context);
        }
         *///?} else {

        if (bounds.scale == 1.0f) {
            var color = INFO.getColor.get();
            context.drawTexture(RenderLayer::getGuiTextured, UNFILLED, bounds.x, bounds.y, 0, 0, BAR_WIDTH, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT, color);
            context.drawTexture(RenderLayer::getGuiTextured, FILLED, bounds.x, bounds.y, 0, 0, calculateFill(INFO.getFill.get()), BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT, color);
        } else {
            var color = INFO.getColor.get();
            RenderUtils.beginScale(context, bounds.scale);
            context.drawTexture(RenderLayer::getGuiTextured, UNFILLED, (int)(bounds.x/bounds.scale), (int)(bounds.y/bounds.scale), 0, 0, BAR_WIDTH, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT, color);
            context.drawTexture(RenderLayer::getGuiTextured, FILLED, (int)(bounds.x/bounds.scale), (int)(bounds.y/bounds.scale), 0, 0, calculateFill(INFO.getFill.get()), BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT, color);
            RenderUtils.endScale(context);
        }
            //?}



    }
    @Override
    public Bounds getCurrentBounds() {
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
    public BoundsRelative getCurrentBoundsRelative() {
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