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

import net.minecraft.client.MinecraftClient;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;


import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import wtf.cheeze.sbt.hud.bases.BarHud;

//? if <=1.21.5 {
import net.minecraft.client.render.RenderLayer;
import wtf.cheeze.sbt.utils.injected.SBTDrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
//?}


//? if >1.21.5 {
/*import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import java.util.stream.Collectors;
*///?}

import java.util.List;

public class RenderUtils {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final int OUTLINE_LIGHT = 15728880;

    public static void pushMatrix(DrawContext context) {
        //? if <=1.21.5 {
        context.getMatrices().push();
        //?} else {
        /*context.getMatrices().pushMatrix();
        *///?}
    }

    public static void beginScale(DrawContext context, float scale) {
        pushMatrix(context);
        //? if <=1.21.5 {
        context.getMatrices().scale(scale, scale, 1.0f);
        //?} else {
        /*context.getMatrices().scale(scale, scale);
        *///?}
    }
    public static void popMatrix(DrawContext context) {
        //? if <=1.21.5 {
        context.getMatrices().pop();
        //?} else {
        /*context.getMatrices().popMatrix();
        *///?}
    }

    public static void drawTranslated(DrawContext context, float z, int layers, Runnable runnable) {
        pushMatrix(context);
        //? if <=1.21.5 {
        context.getMatrices().translate(0, 0, z);
        runnable.run();
        //?} else {
/*//        for (int i = 0; i < layers; i++) {
//            context.state.goUpLayer();
//        }
        runnable.run();
//        for (int i = 0; i < layers; i++) {
//            context.state.goDownLayer();
//        }
        *///?}
        popMatrix(context);
    }
    
    public static void drawWithZ(DrawContext context, float z, Runnable runnable) {
        //? if <=1.21.5 {
        
        pushMatrix(context);
        context.getMatrices().translate(0, 0, z);
        runnable.run();
        popMatrix(context);
        //?} else {
        /*runnable.run();
        *///?}
    }

    public static void drawNonBlockingTooltip(DrawContext context, Text text, int x, int y) {
        drawNonBlockingTooltip(context, List.of(text), x, y);
    }

    /**
     * Minecraft handles directly rendering tooltips differently pre and post 1.21.6. Post 1.21.6,
     * a tooltip drawer, of which a context can have only one, is used. This method should be used
     * where a tooltip needs to be rendered without interfering with other tooltips.
     */
    public static void drawNonBlockingTooltip(DrawContext context, List<Text> text, int x, int y) {
        //? if <=1.21.5 {
        context.drawTooltip(client.textRenderer, text, x, y);
        //?} else {
        /*context.drawTooltipImmediately(client.textRenderer, text.stream().map(it -> new OrderedTextTooltipComponent(it.asOrderedText()) ).collect(Collectors.toList()), x, y, HoveredTooltipPositioner.INSTANCE, null);
        *///?}
    }




    public static void drawText(DrawContext context, Text text, int x, int y, int color, boolean shadow, float scale) {
        beginScale(context, scale);
        drawText(context, text, (int) (x/scale), (int) (y/scale), color, shadow);
        popMatrix(context);
    }
    public static void drawText(DrawContext context, Text text, int x, int y, int color, boolean shadow, float scale, boolean imHandlingTheScaleMyself) {
        drawText(context, text, (int) (x/scale), (int) (y/scale), color, shadow);
    }
    public static void drawText(DrawContext context, Text text, int x, int y, int color, boolean shadow) {
        context.drawText(client.textRenderer, text, x, y, color, shadow);
    }
    public static void drawTextWithOutline(DrawContext context, Text text, int x, int y, int color, int outlineColor) {
        //? if <=1.21.5 {
        client.textRenderer.drawWithOutline(
                text.asOrderedText(),
                x, y, color, outlineColor,
                context.getMatrices().peek().getPositionMatrix(),
                getVertexConsumers(context),
                OUTLINE_LIGHT
        );
        //?} else {
        /*// We reimplement it ourselves post 1.21.6 because it's more annoying to make the vanilla method work with rendering changes
        //TODO: Check if this works well and explore replacing it if possible
        context.drawText(client.textRenderer, text, x -1, y -1, outlineColor, false);
        context.drawText(client.textRenderer, text, x, y -1, outlineColor, false);
        context.drawText(client.textRenderer, text, x + 1, y -1, outlineColor, false);
        context.drawText(client.textRenderer, text, x + 1, y, outlineColor, false);
        context.drawText(client.textRenderer, text, x + 1, y + 1, outlineColor, false);
        context.drawText(client.textRenderer, text, x, y + 1, outlineColor, false);
        context.drawText(client.textRenderer, text, x - 1, y + 1, outlineColor, false);
        context.drawText(client.textRenderer, text, x - 1, y, outlineColor, false);

        context.drawText(client.textRenderer, text, x, y, color, false);
        *///?}
    }
    public static void drawTextWithOutline(DrawContext context, Text text, int x, int y, int color, int outlineColor, float scale) {
        beginScale(context, scale);
        drawTextWithOutline(context, text, (int) (x/scale), (int) (y/scale), color, outlineColor);
        popMatrix(context);
    }
    public static void drawTextWithOutline(DrawContext context, Text text, int x, int y, int color, int outlineColor, float scale, boolean imHandlingTheScaleMyself) {
        drawTextWithOutline(context, text, (int) (x/scale), (int) (y/scale), color, outlineColor);
    }

    public static void drawCenteredText(DrawContext context, Text text, int x, int y, int color, boolean shadow, float scale) {
        int width = (int) (getStringWidth(text) * scale);
        drawText(context, text, x - width / 2, y, color, shadow, scale);
    }
    public static void drawCenteredText(DrawContext context, Text text, int x, int y, int color, boolean shadow) {
        int width = getStringWidth(text);
        drawText(context, text, x - width / 2, y, color, shadow);
    }

    public static int getStringWidth(Text text) {
        return client.textRenderer.getWidth(text);
    }
    public static int getStringWidth(String text) {
        return client.textRenderer.getWidth(text);
    }

    public static int getRelativeStringWidth(String text) {
        return client.textRenderer.getWidth(text) / MinecraftClient.getInstance().getWindow().getScaledWidth();
    }
    //TODO: Is this correct?
    public static int getRelativeStringWidth(Text text) {
        return client.textRenderer.getWidth(text) / MinecraftClient.getInstance().getWindow().getScaledWidth();
    }

    public static void drawBorder(DrawContext context, int borderWidth, int color, int x, int y, int rectWidth, int rectHeight) {
        // Top border
        context.fill(x - borderWidth, y - borderWidth, x + rectWidth + borderWidth, y, color);

        // Bottom border
        context.fill(x - borderWidth, y + rectHeight, x + rectWidth + borderWidth, y + rectHeight + borderWidth, color);

        // Left border
        context.fill(x - borderWidth, y, x, y + rectHeight, color);

        // Right border
        context.fill(x + rectWidth, y, x + rectWidth + borderWidth, y + rectHeight, color);
    }




    public static BreachResult isOffscreen(ScreenRect rect) {
        var screenBounds = getScreenBounds();
        return new BreachResult(
                rect.position().x() < 0,
                rect.position().y() < 0,
                rect.position().x() + rect.width() > screenBounds.width,
                rect.position().y() + rect.height() > screenBounds.height);
    }

    public static float getRelativeWidth(int width) {
        return width / MinecraftClient.getInstance().getWindow().getScaledWidth();
    }


    public record BreachResult(boolean left, boolean top, boolean right, boolean bottom) {

        public boolean breachesAll() {
            return !left && !top && !right && !bottom;
        }

    }


    public static void drawTexture(DrawContext context, Identifier texture, int x, int y, int width, int height, int textureWidth, int textureHeight) {
        //? if <=1.21.5 {
        context.drawTexture(RenderLayer::getGuiTextured, texture, x, y, 0, 0, width, height, textureWidth, textureHeight);
        //?} else {
        /*context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, width, height, textureWidth, textureHeight);
        *///?}
    }

    public static void drawBar(DrawContext context, Identifier texture, int x, int y, int width, int color) {
        context.drawTexture(
                //? if <=1.21.5 {
                RenderLayer::getGuiTextured,
                 //?} else {
                /*RenderPipelines.GUI_TEXTURED,
                *///?}
                texture,
                x,
                y,
                0,
                0,
                width,
                BarHud.BAR_HEIGHT,
                BarHud.BAR_WIDTH,
                BarHud.BAR_HEIGHT,
                color
        );
    }

    //? if <=1.21.5 {
    public static VertexConsumerProvider.Immediate getVertexConsumers(DrawContext context) {
        return ((SBTDrawContext) context).sbt$getVertexConsumers();
    }
    //?}


    public static ScreenBounds getScreenBounds() {
        return new ScreenBounds();
    }

}
