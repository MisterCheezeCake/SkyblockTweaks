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

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/**
 * Represents any sort of HUD that can be drawn to the screen, and handles logic that is that same if the HUD is a text or bar based hud
 */
public abstract class HUD  {

    public HudInformation INFO;

    public abstract String getName();

    public abstract void render(DrawContext context, boolean fromHudScreen, boolean hovered);

    public abstract Bounds getCurrentBounds();

    public abstract BoundsRelative getCurrentBoundsRelative();

    public boolean shouldRender(boolean fromHudScreen) {
        // We let the HUD screen handle rendering when it is open
        if (!fromHudScreen && MinecraftClient.getInstance().currentScreen instanceof HudScreen) return false;
        if (!fromHudScreen && MinecraftClient.getInstance().options.hudHidden) return false;
        else return true;
    };

    public void render(DrawContext context, boolean fromHudScreen) {
        render(context, fromHudScreen, false);
    }

    public void updatePosition(float x, float y) {
        INFO.setX.accept(x);
        INFO.setY.accept(y);
    }

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

    public enum AnchorPoint implements NameableEnum {
        LEFT, CENTER, RIGHT;
        @Override
        public Text getDisplayName() {
            return Text.literal(name());
        }
    }
}
