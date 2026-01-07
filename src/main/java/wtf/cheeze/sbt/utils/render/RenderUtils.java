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
package wtf.cheeze.sbt.utils.render;

import net.minecraft.client.Minecraft;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import wtf.cheeze.sbt.hud.bases.BarHud;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import java.util.stream.Collectors;
import java.util.List;

public class RenderUtils {
    private static final Minecraft client = Minecraft.getInstance();
    private static final int OUTLINE_LIGHT = 15728880;

    public static void pushMatrix(GuiGraphics context) {
        context.pose().pushMatrix();
    }

    public static void beginScale(GuiGraphics context, float scale) {
        pushMatrix(context);
        context.pose().scale(scale, scale);
    }
    public static void popMatrix(GuiGraphics context) {
        context.pose().popMatrix();
    }

    public static void drawTranslated(GuiGraphics context, float z, int layers, Runnable runnable) {
        pushMatrix(context);
        runnable.run();
        popMatrix(context);
    }
    
    public static void drawWithZ(GuiGraphics context, float z, Runnable runnable) {
        runnable.run();
    }

    public static void drawNonBlockingTooltip(GuiGraphics context, Component text, int x, int y) {
        drawNonBlockingTooltip(context, List.of(text), x, y);
    }

    /**
     * Minecraft handles directly rendering tooltips differently pre and post 1.21.6. Post 1.21.6,
     * a tooltip drawer, of which a context can have only one, is used. This method should be used
     * where a tooltip needs to be rendered without interfering with other tooltips.
     */
    public static void drawNonBlockingTooltip(GuiGraphics context, List<Component> text, int x, int y) {
        context.renderTooltip(client.font, text.stream().map(it -> new ClientTextTooltip(it.getVisualOrderText()) ).collect(Collectors.toList()), x, y, DefaultTooltipPositioner.INSTANCE, null);
    }

    public static void drawText(GuiGraphics context, Component text, int x, int y, int color, boolean shadow, float scale) {
        beginScale(context, scale);
        drawText(context, text, (int) (x/scale), (int) (y/scale), color, shadow);
        popMatrix(context);
    }

    public static void drawText(GuiGraphics context, Component text, int x, int y, int color, boolean shadow, float scale, boolean imHandlingTheScaleMyself) {
        drawText(context, text, (int) (x/scale), (int) (y/scale), color, shadow);
    }

    public static void drawText(GuiGraphics context, Component text, int x, int y, int color, boolean shadow) {
        context.drawString(client.font, text, x, y, color, shadow);
    }

    public static void drawTextWithOutline(GuiGraphics context, Component text, int x, int y, int color, int outlineColor) {
        // We reimplement it ourselves post 1.21.6 because it's more annoying to make the vanilla method work with rendering changes
        //TODO: Check if this works well and explore replacing it if possible
        context.drawString(client.font, text, x -1, y -1, outlineColor, false);
        context.drawString(client.font, text, x, y -1, outlineColor, false);
        context.drawString(client.font, text, x + 1, y -1, outlineColor, false);
        context.drawString(client.font, text, x + 1, y, outlineColor, false);
        context.drawString(client.font, text, x + 1, y + 1, outlineColor, false);
        context.drawString(client.font, text, x, y + 1, outlineColor, false);
        context.drawString(client.font, text, x - 1, y + 1, outlineColor, false);
        context.drawString(client.font, text, x - 1, y, outlineColor, false);
        context.drawString(client.font, text, x, y, color, false);
    }

    public static void drawTextWithOutline(GuiGraphics context, Component text, int x, int y, int color, int outlineColor, float scale) {
        beginScale(context, scale);
        drawTextWithOutline(context, text, (int) (x/scale), (int) (y/scale), color, outlineColor);
        popMatrix(context);
    }

    public static void drawTextWithOutline(GuiGraphics context, Component text, int x, int y, int color, int outlineColor, float scale, boolean imHandlingTheScaleMyself) {
        drawTextWithOutline(context, text, (int) (x/scale), (int) (y/scale), color, outlineColor);
    }

    public static void drawCenteredText(GuiGraphics context, Component text, int x, int y, int color, boolean shadow, float scale) {
        int width = (int) (getStringWidth(text) * scale);
        drawText(context, text, x - width / 2, y, color, shadow, scale);
    }

    public static void drawCenteredText(GuiGraphics context, Component text, int x, int y, int color, boolean shadow) {
        int width = getStringWidth(text);
        drawText(context, text, x - width / 2, y, color, shadow);
    }

    public static int getStringWidth(Component text) {
        return client.font.width(text);
    }

    public static int getStringWidth(String text) {
        return client.font.width(text);
    }

    public static int getRelativeStringWidth(String text) {
        return client.font.width(text) / Minecraft.getInstance().getWindow().getGuiScaledWidth();
    }

    //TODO: Is this correct?
    public static int getRelativeStringWidth(Component text) {
        return client.font.width(text) / Minecraft.getInstance().getWindow().getGuiScaledWidth();
    }

    public static void drawBorder(GuiGraphics context, int borderWidth, int color, int x, int y, int rectWidth, int rectHeight) {
        // Top border
        context.fill(x - borderWidth, y - borderWidth, x + rectWidth + borderWidth, y, color);

        // Bottom border
        context.fill(x - borderWidth, y + rectHeight, x + rectWidth + borderWidth, y + rectHeight + borderWidth, color);

        // Left border
        context.fill(x - borderWidth, y, x, y + rectHeight, color);

        // Right border
        context.fill(x + rectWidth, y, x + rectWidth + borderWidth, y + rectHeight, color);
    }

    public static BreachResult isOffscreen(ScreenRectangle rect) {
        var screenBounds = getScreenBounds();
        return new BreachResult(
                rect.position().x() < 0,
                rect.position().y() < 0,
                rect.position().x() + rect.width() > screenBounds.width,
                rect.position().y() + rect.height() > screenBounds.height);
    }

    public static float getRelativeWidth(int width) {
        return width / Minecraft.getInstance().getWindow().getGuiScaledWidth();
    }

    public record BreachResult(boolean left, boolean top, boolean right, boolean bottom) {

        public boolean breachesAll() {
            return !left && !top && !right && !bottom;
        }

    }

    public static void drawTexture(GuiGraphics context, Identifier texture, int x, int y, int width, int height, int textureWidth, int textureHeight) {
        context.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, width, height, textureWidth, textureHeight);
    }

    public static void drawBar(GuiGraphics context, Identifier texture, int x, int y, int width, int color) {
        context.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, width, BarHud.BAR_HEIGHT, BarHud.BAR_WIDTH, BarHud.BAR_HEIGHT, color);
    }

    public static ScreenBounds getScreenBounds() {
        return new ScreenBounds();
    }
}
