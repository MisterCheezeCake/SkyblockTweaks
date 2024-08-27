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

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import wtf.cheeze.sbt.SkyblockTweaks;

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
    public static void drawString(DrawContext context, Text text, int x, int y, int color, boolean shadow, float scale, boolean imHandlingTheScaleMyself) {
        drawString(context, text, (int) (x/scale), (int) (y/scale), color, shadow);
    }
    public static void drawString(DrawContext context, Text text, int x, int y, int color, boolean shadow) {
        context.drawText(SkyblockTweaks.mc.textRenderer, text, x, y, color, shadow);
    }
    public static void drawStringWithOutline (DrawContext context, Text text, int x, int y, int color, int outlineColor) {
        // TODO: This currently renders weirdly, fix it
        SkyblockTweaks.mc.textRenderer.drawWithOutline(text.asOrderedText(), x, y, color, outlineColor, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(), 15728880);
    }
    public static void drawStringWithOutline (DrawContext context, Text text, int x, int y, int color, int outlineColor, float scale) {
        beginScale(context, scale);
        drawStringWithOutline(context, text, (int) (x/scale), (int) (y/scale), color, outlineColor);
        endScale(context);
    }
    public static void drawStringWithOutline (DrawContext context, Text text, int x, int y, int color, int outlineColor, float scale, boolean imHandlingTheScaleMyself) {
        drawStringWithOutline(context, text, (int) (x/scale), (int) (y/scale), color, outlineColor);
    }

    public static void drawCenteredString(DrawContext context, Text text, int x, int y, int color, boolean shadow, float scale) {
        int width = (int) (getStringWidth(text) * scale);
        drawString(context, text, x - width / 2, y, color, shadow, scale);
    }
    public static void drawCenteredString(DrawContext context, Text text, int x, int y, int color, boolean shadow) {
        int width = getStringWidth(text);
        drawString(context, text, x - width / 2, y, color, shadow);
    }

    public static int getStringWidth(Text text) {
        return SkyblockTweaks.mc.textRenderer.getWidth(text);
    }
    public static int getStringWidth(String text) {
        return SkyblockTweaks.mc.textRenderer.getWidth(text);
    }
    public static int getRelativeStringWidth(String text) {
        return (int) (SkyblockTweaks.mc.textRenderer.getWidth(text) / MinecraftClient.getInstance().getWindow().getScaledWidth());
    }

    public static Color3f getColor3f(int color) {
        return new Color3f(color);
    }
    public static ScreenBounds getScreenBounds() {
        return new ScreenBounds();
    }

    //    public static class Color4f extends Color3f {
//        public float alpha;
//
//        private Color4f(int color) {
//            super(color);
//            this.alpha = (float) (color >> 24 & 255) / 255.0F;
//        }
//    }
//    public static Color4f getColor4f(int color) {
//        return new Color4f(color);
//    }
    // Adapted from Skyblocker
//    private static void renderFilledInternal(WorldRenderContext context, Vec3d pos, Vec3d dimensions, Color3f color, float alpha, boolean throughWalls) {
//        var matrices = context.matrixStack();
//        var camera = context.camera().getPos();
//        matrices.push();
//        matrices.translate(-camera.x, -camera.y, -camera.z);
//        var consumers = context.consumers();
//        var buffer = consumers.getBuffer(throughWalls ? SkyblockerRenderLayers.FILLED_THROUGH_WALLS : SkyblockerRenderLayers.FILLED);
//        WorldRenderer.renderFilledBox(matrices, buffer, pos.x, pos.y, pos.z, pos.x + dimensions.x, pos.y + dimensions.y, pos.z + dimensions.z, color.red, color.green, color.blue, alpha);
//        matrices.pop();
//    }
//
//
//    public static final RenderLayer.MultiPhase FILLED = RenderLayer.of("filled", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP, RenderLayer.CUTOUT_BUFFER_SIZE, false, true, RenderLayer.MultiPhaseParameters.builder()
//            .program(RenderPhase.COLOR_PROGRAM)
//            .cull(RenderPhase.Cull.DISABLE_CULLING)
//            .layering(RenderPhase.POLYGON_OFFSET_LAYERING)
//            .transparency(DEFAULT_TRANSPARENCY)
//            .depthTest(RenderPhase.DepthTest.LEQUAL_DEPTH_TEST)
//            .build(false));

}
