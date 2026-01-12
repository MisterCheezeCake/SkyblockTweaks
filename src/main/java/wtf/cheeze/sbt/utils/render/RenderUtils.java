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
import net.minecraft.resources.ResourceLocation;
import wtf.cheeze.sbt.hud.bases.BarHud;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import java.util.stream.Collectors;
import java.util.List;

public class RenderUtils {
    private static final Minecraft client = Minecraft.getInstance();
    private static final int OUTLINE_LIGHT = 15728880;

    public static void pushMatrix(GuiGraphics guiGraphics) {
        guiGraphics.pose().pushMatrix();
    }

    public static void beginScale(GuiGraphics guiGraphics, float scale) {
        pushMatrix(guiGraphics);
        guiGraphics.pose().scale(scale, scale);
    }
    public static void popMatrix(GuiGraphics guiGraphics) {
        guiGraphics.pose().popMatrix();
    }

    public static void drawNonBlockingTooltip(GuiGraphics guiGraphics, Component text, int x, int y) {
        drawNonBlockingTooltip(guiGraphics, List.of(text), x, y);
    }

    /**
     * Minecraft handles directly rendering tooltips differently pre and post 1.21.6. Post 1.21.6,
     * a tooltip drawer, of which a context can have only one, is used. This method should be used
     * where a tooltip needs to be rendered without interfering with other tooltips.
     */
    public static void drawNonBlockingTooltip(GuiGraphics guiGraphics, List<Component> text, int x, int y) {
        guiGraphics.renderTooltip(client.font, text.stream().map(it -> new ClientTextTooltip(it.getVisualOrderText()) ).collect(Collectors.toList()), x, y, DefaultTooltipPositioner.INSTANCE, null);
    }

    public static void drawScaledText(GuiGraphics guiGraphics, Component text, int x, int y, int color, boolean shadow, float scale) {
        beginScale(guiGraphics, scale);
        drawText(guiGraphics, text, (int) (x/scale), (int) (y/scale), color, shadow);
        popMatrix(guiGraphics);
    }

    public static void drawText(GuiGraphics guiGraphics, Component text, int x, int y, int color, boolean shadow, float scale) {
        drawText(guiGraphics, text, (int) (x/scale), (int) (y/scale), color, shadow);
    }

    public static void drawText(GuiGraphics guiGraphics, Component text, int x, int y, int color, boolean shadow) {
        guiGraphics.drawString(client.font, text, x, y, color, shadow);
    }

    public static void drawTextWithOutline(GuiGraphics guiGraphics, Component text, int x, int y, int color, int outlineColor) {
        // We reimplement it ourselves post 1.21.6 because it's more annoying to make the vanilla method work with rendering changes
        //TODO: Check if this works well and explore replacing it if possible
        guiGraphics.drawString(client.font, text, x -1, y -1, outlineColor, false);
        guiGraphics.drawString(client.font, text, x, y -1, outlineColor, false);
        guiGraphics.drawString(client.font, text, x + 1, y -1, outlineColor, false);
        guiGraphics.drawString(client.font, text, x + 1, y, outlineColor, false);
        guiGraphics.drawString(client.font, text, x + 1, y + 1, outlineColor, false);
        guiGraphics.drawString(client.font, text, x, y + 1, outlineColor, false);
        guiGraphics.drawString(client.font, text, x - 1, y + 1, outlineColor, false);
        guiGraphics.drawString(client.font, text, x - 1, y, outlineColor, false);
        guiGraphics.drawString(client.font, text, x, y, color, false);
    }

    public static void drawScaledTextWithOutline(GuiGraphics guiGraphics, Component text, int x, int y, int color, int outlineColor, float scale) {
        beginScale(guiGraphics, scale);
        drawTextWithOutline(guiGraphics, text, (int) (x/scale), (int) (y/scale), color, outlineColor);
        popMatrix(guiGraphics);
    }

    public static void drawTextWithOutline(GuiGraphics guiGraphics, Component text, int x, int y, int color, int outlineColor, float scale) {
        drawTextWithOutline(guiGraphics, text, (int) (x/scale), (int) (y/scale), color, outlineColor);
    }

    public static void drawCenteredText(GuiGraphics guiGraphics, Component text, int x, int y, int color, boolean shadow, float scale) {
        int width = (int) (getStringWidth(text) * scale);
        drawScaledText(guiGraphics, text, x - width / 2, y, color, shadow, scale);
    }

    public static void drawCenteredText(GuiGraphics guiGraphics, Component text, int x, int y, int color, boolean shadow) {
        int width = getStringWidth(text);
        drawText(guiGraphics, text, x - width / 2, y, color, shadow);
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

    public static void drawBorder(GuiGraphics guiGraphics, int borderWidth, int color, int x, int y, int rectWidth, int rectHeight) {
        // Top border
        guiGraphics.fill(x - borderWidth, y - borderWidth, x + rectWidth + borderWidth, y, color);

        // Bottom border
        guiGraphics.fill(x - borderWidth, y + rectHeight, x + rectWidth + borderWidth, y + rectHeight + borderWidth, color);

        // Left border
        guiGraphics.fill(x - borderWidth, y, x, y + rectHeight, color);

        // Right border
        guiGraphics.fill(x + rectWidth, y, x + rectWidth + borderWidth, y + rectHeight, color);
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

    public static void drawTexture(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int width, int height, int textureWidth, int textureHeight) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, width, height, textureWidth, textureHeight);
    }

    public static void drawBar(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int width, int color) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, width, BarHud.BAR_HEIGHT, BarHud.BAR_WIDTH, BarHud.BAR_HEIGHT, color);
    }

    public static ScreenBounds getScreenBounds() {
        return new ScreenBounds();
    }
}
