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
package wtf.cheeze.sbt.hud;

import dev.isxander.yacl3.api.OptionDescription;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.hud.bounds.Bounds;
import wtf.cheeze.sbt.hud.bounds.BoundsRelative;
import wtf.cheeze.sbt.hud.utils.HudInformation;

/**
 * Represents any sort of HUD that can be drawn to the screen, and handles logic that is that same no matter how the HUD renders
 */
public abstract class HUD  {

    public HudInformation INFO;

    /**
     * Whether the HUD supports non-left anchors
     */
    public boolean supportsNonLeftAnchors = true;

    /**
     * @return the name of the HUD that will be shown in the HUD screen
     */
    public abstract Text getName();


    public abstract Bounds getCurrentBounds();

    public abstract BoundsRelative getCurrentBoundsRelative();


    public boolean shouldRender(boolean fromHudScreen) {
        // We let the HUD screen handle rendering when it is open
        if (!fromHudScreen && MinecraftClient.getInstance().currentScreen instanceof HudScreen) return false;
        return fromHudScreen || !MinecraftClient.getInstance().options.hudHidden;
    }

    /**
     * Calls the render method with hovered set to false
     */
    public void render(DrawContext context, boolean fromHudScreen) {
        render(context, fromHudScreen, false);
    }

    /**
     * Draws the HUD to the screen
     * @param context the DrawContext
     * @param fromHudScreen whether the HUD is being drawn in the context of the edit screen
     * @param hovered whether the HUD is hovered while being drawn in the context of the edit screen
     */
    public abstract void render(DrawContext context, boolean fromHudScreen, boolean hovered);

    /**
     * Updates the position of the HUD
     */
    public void updatePosition(float x, float y) {
        INFO.setX.accept(x);
        INFO.setY.accept(y);
    }

    /**
     * Updates the scale of the HUD, clamping it to the min and max scale
     */
    public void updateScale(float scale) {
        if (scale <= MIN_SCALE) {
            INFO.setScale.accept(MIN_SCALE);
            return;
        } else if (scale >= MAX_SCALE) {
            INFO.setScale.accept(MAX_SCALE);
            return;
        }
        INFO.setScale.accept(scale);
    }

    public void drawBackground(DrawContext context, int color, boolean hasOutline) {
        var bounds = getCurrentBounds();
        int i = (int) (1 * bounds.scale);
        context.fill(bounds.x - i, bounds.y - i, (int) (bounds.x + bounds.width + i), (int) (bounds.y + bounds.height + i - 1), color);
    }
    public void drawBackground(DrawContext context, int color) {
        drawBackground(context, color, false);
    }

    public static final float MIN_SCALE = 0.1f;
    public static final float MAX_SCALE = 3.0f;

    public static final int BACKGROUND_NOT_HOVERED = 855638015;
    public static final int BACKGROUND_HOVERED = -1761607681;

    public static int getActualX(float x) {
        return (int) (x * MinecraftClient.getInstance().getWindow().getScaledWidth());
    }
    public static int getActualY(float y) {
        return (int) (y * MinecraftClient.getInstance().getWindow().getScaledHeight());
    }
    public static float getRelativeX(double x) {
        return (float) (x / MinecraftClient.getInstance().getWindow().getScaledWidth());
    }
    public static float getRelativeX(int x) {
        return (float) (x / MinecraftClient.getInstance().getWindow().getScaledWidth());
    }
    public static float getRelativeY(double y) {
        return (float) (y / MinecraftClient.getInstance().getWindow().getScaledHeight());
    }
    public static float getRelativeY(int y) {
        return (float) (y / MinecraftClient.getInstance().getWindow().getScaledHeight());
    }

    private static final String BASE_KEY = "sbt.config.huds.";
    public static Text key(String key) {
        return Text.translatable(BASE_KEY + key);
    }
    public static OptionDescription keyD(String key) {
        return OptionDescription.of(Text.translatable(BASE_KEY + key + ".desc"));
    }

}
