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
package wtf.cheeze.sbt.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;

public class RenderUtils {

    public static void beginScale(DrawContext context, float scale) {
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);
    }
    public static void endScale(DrawContext context) {
        context.getMatrices().pop();
    }
    public static void drawString(DrawContext context, Text text, int x, int y, int color, boolean shadow, float scale) {
        beginScale(context, scale);
        drawString(context, text, (int) (x/scale), (int) (y/scale), color, shadow);
        endScale(context);
    }
    public static void drawString(DrawContext context, Text text, int x, int y, int color, boolean shadow) {
        context.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, color, shadow);
    }

    public static void drawCenteredString(DrawContext context, Text text, int x, int y, int color, boolean shadow, float scale) {
        int width = (int) (getStringWidth(text) * scale);
        drawString(context, text, x - width / 2, y, color, shadow, scale);
    }
    public static void drawCenteredString(DrawContext context, Text text, int x, int y, int color, boolean shadow) {
        int width = getStringWidth(text);
        drawString(context, text, x - width / 2, y, color, shadow);
    }

    public static class ScreenBounds {
        public int width;
        public int height;
        private ScreenBounds() {
            Window window = MinecraftClient.getInstance().getWindow();
            this.width = window.getScaledWidth();
            this.height = window.getScaledHeight();
        }
    }

    public static ScreenBounds getScreenBounds() {
        return new ScreenBounds();
    }

    public static int getStringWidth(Text text) {
        return MinecraftClient.getInstance().textRenderer.getWidth(text);
    }

    public static Color3f getColor3f(int color) {
        return new Color3f(color);
    }
    public static class Color3f {

        public float red;
        public float green;
        public float blue;

        private Color3f(int color) {
            this.red = (float) (color >> 16 & 255) / 255.0F;
            this.green = (float) (color >> 8 & 255) / 255.0F;
            this.blue = (float) (color & 255) / 255.0F;
        }
    }


}
