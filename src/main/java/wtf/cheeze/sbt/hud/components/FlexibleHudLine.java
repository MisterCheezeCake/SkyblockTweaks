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
package wtf.cheeze.sbt.hud.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import wtf.cheeze.sbt.hud.icon.HudIcon;
import wtf.cheeze.sbt.hud.cache.Cache;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.cache.UpdateTiming;
import wtf.cheeze.sbt.utils.DataUtils;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.render.RenderUtils;

import java.util.function.Supplier;

public class FlexibleHudLine implements HudComponent {
    public Supplier<Part[]> parts;

    private final UpdateTiming timing;

    private final Cache<Part[]> partCache;


    private int width = 1;
    private int lines = 1;

    private static final int LINE_HEIGHT = 9;

    public FlexibleHudLine(Supplier<Part[]> parts, UpdateTiming timing) {
        this.parts = parts;
        Part[] ERROR_PARTS = {new Part(() -> ERROR, () -> DrawMode.PURE, DataUtils.ALWAYS_WHITE, () -> Colors.BLACK, new Cache<>(UpdateTiming.MEMOIZED, () -> ERROR, ERROR))};
        this.partCache = new Cache<>(timing, parts, ERROR_PARTS);
        this.timing = timing;
    }

    public FlexibleHudLine(Supplier<Part[]> parts) {
        this(parts, UpdateTiming.SECOND);
    }

    @Override
    public int render(GuiGraphics guiGraphics, int x, int y, float scale) {
        if (timing == UpdateTiming.FRAME || partCache.isDueForUpdate()) {
            partCache.update();
        }
        var pts = parts.get();
        var longest = 0;
        var lineHeight = (int) (9 * scale);
        for (Part part : pts) {
            if (part.cache.isDueForUpdate() || part.cache.timing == UpdateTiming.FRAME) {
                part.cache.update();
            }
            var text = part.cache.get();
            var w = RenderUtils.getStringWidth(text);
            if (part.useIcon.get()) {
                w += 10;
                renderLine(guiGraphics, text, part.icon.get(), x, y, scale, part.mode.get(), part.color.get(), part.outlineColor.get());
            } else {
                renderLine(guiGraphics, text, x, y, scale, part.mode.get(), part.color.get(), part.outlineColor.get());
            }
            if (w > longest) {
                longest = w;
            }

            y += lineHeight;
        }
        this.width = longest;
        this.lines = pts.length;
        return lines;

    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getlines() {
        return lines;
    }


//    private static void renderLine(DrawContext context, Text text, HudIcon icon,  int x, int y, float scale, DrawMode mode, int color) {
//        renderLine(context, text, icon, x, y, scale, mode, color, -1);
//    }
//    private static  void renderLine(DrawContext context, Text text, int x, int y, float scale, DrawMode mode, int color) {
//        renderLine(context, text, x, y, scale, mode, color, -1);
//    }

    private static void renderLine(GuiGraphics guiGraphics, Component text, int x, int y, float scale, DrawMode mode, int color, int outlineColor) {
        switch (mode) {
            case PURE -> RenderUtils.drawText(guiGraphics, text, x, y, color, false, scale, true);
            case SHADOW -> RenderUtils.drawText(guiGraphics, text, x, y, color, true, scale, true);
            case OUTLINE -> RenderUtils.drawTextWithOutline(guiGraphics, text, x, y, color, outlineColor, scale, true);
        }
    }
    private static void renderLine(GuiGraphics guiGraphics, Component text, HudIcon icon, int x, int y, float scale, DrawMode mode, int color, int outlineColor) {
        icon.render(guiGraphics, x, y, scale);
        switch (mode) {
            case PURE -> RenderUtils.drawText(guiGraphics, text, x + (int) (10 * scale), y, color, false, scale, true);
            case SHADOW -> RenderUtils.drawText(guiGraphics, text, x + (int) (10 * scale), y, color, true, scale, true);
            case OUTLINE -> RenderUtils.drawTextWithOutline(guiGraphics, text, x + (int) (10 * scale), y, color, outlineColor, scale, true);
        }
    }

    public static class Part {
        public final Supplier<Component> text;
        public final Supplier<Boolean> useIcon;
        @Nullable
        public Supplier<HudIcon> icon;
        public final Supplier<DrawMode> mode;
        public final Supplier<Integer> color;
        public final Supplier<Integer> outlineColor;

        private final Cache<Component> cache;


        public Part (Supplier<Component> text, Supplier<DrawMode> mode, Supplier<Integer> color, Supplier<Integer> outlineColor) {
            this(text, mode, color, outlineColor, null, DataUtils.ALWAYS_FALSE, new Cache<>(UpdateTiming.FRAME, text, ERROR));
        }

        public Part(Supplier<Component> text, Supplier<DrawMode> mode, Supplier<Integer> color, Supplier<Integer> outlineColor, Supplier<HudIcon> icon, Supplier<Boolean> useIcon) {
            this(text, mode, color, outlineColor, icon, useIcon, new Cache<>(UpdateTiming.FRAME, text, ERROR));
        }

        public Part(Supplier<Component> text, Supplier<DrawMode> mode, Supplier<Integer> color, Supplier<Integer> outlineColor, Cache<Component> cache) {
           this(text, mode, color, outlineColor, null, DataUtils.ALWAYS_FALSE, cache);

        }

        public Part(Supplier<Component> text, Supplier<DrawMode> mode, Supplier<Integer> color, Supplier<Integer> outlineColor, Supplier<HudIcon> icon, Supplier<Boolean> useIcon, Cache<Component> cache) {
            this.text = text;
            this.mode = mode;
            this.color = color;
            this.outlineColor = outlineColor;
            this.icon = icon;
            this.useIcon = useIcon;
            this.cache = cache;
        }

    }

    @Override
    public int getHeight() {
        return lines * LINE_HEIGHT;
    }
}
