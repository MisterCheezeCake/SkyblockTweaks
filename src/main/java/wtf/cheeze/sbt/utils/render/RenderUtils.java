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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import wtf.cheeze.sbt.SkyblockTweaks;

public class RenderUtils {

    public static void beginScale(DrawContext context, float scale) {
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);
    }
    public static void endScale(DrawContext context) {
        context.getMatrices().pop();
    }

    public static void drawText(DrawContext context, Text text, int x, int y, int color, boolean shadow, float scale) {
        beginScale(context, scale);
        drawText(context, text, (int) (x/scale), (int) (y/scale), color, shadow);
        endScale(context);
    }
    public static void drawText(DrawContext context, Text text, int x, int y, int color, boolean shadow, float scale, boolean imHandlingTheScaleMyself) {
        drawText(context, text, (int) (x/scale), (int) (y/scale), color, shadow);
    }
    public static void drawText(DrawContext context, Text text, int x, int y, int color, boolean shadow) {
        context.drawText(SkyblockTweaks.mc.textRenderer, text, x, y, color, shadow);
    }
    public static void drawTextWithOutline(DrawContext context, Text text, int x, int y, int color, int outlineColor) {
        // TODO: This currently renders weirdly, fix it
        SkyblockTweaks.mc.textRenderer.drawWithOutline(
                text.asOrderedText(),
                x, y, color, outlineColor,
                context.getMatrices().peek().getPositionMatrix(),
                //? if =1.21.1 {
                /*context.getVertexConsumers(),
                 *///?} else {
                getVertexConsumers(context),
                //?}
                15728880);
    }
    public static void drawTextWithOutline(DrawContext context, Text text, int x, int y, int color, int outlineColor, float scale) {
        beginScale(context, scale);
        drawTextWithOutline(context, text, (int) (x/scale), (int) (y/scale), color, outlineColor);
        endScale(context);
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
        return SkyblockTweaks.mc.textRenderer.getWidth(text);
    }
    public static int getStringWidth(String text) {
        return SkyblockTweaks.mc.textRenderer.getWidth(text);
    }

    public static int getRelativeStringWidth(String text) {
        return SkyblockTweaks.mc.textRenderer.getWidth(text) / MinecraftClient.getInstance().getWindow().getScaledWidth();
    }
    public static int getRelativeStringWidth(Text text) {
        return SkyblockTweaks.mc.textRenderer.getWidth(text) / MinecraftClient.getInstance().getWindow().getScaledWidth();
    }

    public static void drawTexture(DrawContext context, Identifier texture, int x, int y, int width, int height, int textureWidth, int textureHeight) {
        //? if =1.21.1 {
        /*context.drawTexture(texture, x, y, 0, 0, width, height, textureWidth, textureHeight);
         *///?} else {
        context.drawTexture(RenderLayer::getGuiTextured, texture, x, y, 0, 0, width, height, textureWidth, textureHeight);
        //?}
    }
//    public static void drawTextureWithColor(DrawContext context, Identifier texture, int x, int y, int width, int height, int textureWidth, int textureHeight, int color) {
//        //? if <1.21.1 {
//        /*context.drawTexture(texture, x, y, 0, 0, width, height, textureWidth, textureHeight, color);
//         *///?} else {
//        context.drawTexture(RenderLayer::getGuiTextured, texture, x, y, 0, 0, width, height, textureWidth, textureHeight, color);
//        //?}
//    }

    //? if >=1.21.3 {
    public static VertexConsumerProvider.Immediate getVertexConsumers(DrawContext context) {
        return ((SBTDrawContext) context).sbt$getVertexConsumers();
    }
    //?}

    public static Color3f getColor3f(int color) {
        return new Color3f(color);
    }
    public static ScreenBounds getScreenBounds() {
        return new ScreenBounds();
    }

}
