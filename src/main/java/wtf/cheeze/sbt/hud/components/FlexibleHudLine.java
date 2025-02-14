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

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import wtf.cheeze.sbt.hud.icon.HudIcon;
import wtf.cheeze.sbt.hud.cache.Cache;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.cache.UpdateTiming;
import wtf.cheeze.sbt.utils.DataUtils;
import wtf.cheeze.sbt.utils.render.RenderUtils;

import java.util.function.Supplier;

public class FlexibleHudLine implements HudComponent {


    public Supplier<Part[]> parts;

    private final UpdateTiming timing;

    private final Cache<Part[]> partCache;


    private int width = 1;
    private int lines = 1;

    public FlexibleHudLine(Supplier<Part[]> parts, UpdateTiming timing) {
        this.parts = parts;
        Part[] ERROR_PARTS = {new Part(() -> ERROR, () -> DrawMode.PURE, DataUtils.alwaysZero, DataUtils.alwaysZero, new Cache<>(UpdateTiming.MEMOIZED, () -> ERROR, ERROR))};
        this.partCache = new Cache<>(timing, parts, ERROR_PARTS);
        this.timing = timing;
    }

    public FlexibleHudLine(Supplier<Part[]> parts) {
        this(parts, UpdateTiming.SECOND);
    }

    @Override
    public int render(DrawContext context, int x, int y, float scale) {
        if (timing == UpdateTiming.FRAME ||partCache.isDueForUpdate()) {
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
                renderLine(context, text, part.icon.get(), x, y, scale, part.mode.get(), part.color.get(), part.outlineColor.get());
            } else {
                renderLine(context, text, x, y, scale, part.mode.get(), part.color.get(), part.outlineColor.get());
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

    private static void renderLine(DrawContext context, Text text, int x, int y, float scale, DrawMode mode, int color, int outlineColor) {
        switch (mode) {
            case PURE -> RenderUtils.drawText(context, text, x, y, color, false, scale, true);
            case SHADOW -> RenderUtils.drawText(context, text, x, y, color, true, scale, true);
            case OUTLINE -> RenderUtils.drawTextWithOutline(context, text, x, y, color, outlineColor, scale, true);
        }
    }
    private static void renderLine(DrawContext context, Text text, HudIcon icon, int x, int y, float scale, DrawMode mode, int color, int outlineColor) {
        icon.render(context, x, y, scale);
        switch (mode) {
            case PURE -> {
                RenderUtils.drawText(context, text, x + (int) (10 * scale), y, color, false, scale, true);
            }
            case SHADOW -> {
                RenderUtils.drawText(context, text, x + (int) (10 * scale), y, color, true, scale, true);
            }
            case OUTLINE -> {
                RenderUtils.drawTextWithOutline(context, text, x + (int) (10 * scale), y, color, outlineColor, scale, true);
            }
        }
    }

    public static class Part {
        public final Supplier<Text> text;
        public final Supplier<Boolean> useIcon;
        @Nullable
        public Supplier<HudIcon> icon;
        public final Supplier<DrawMode> mode;
        public final Supplier<Integer> color;
        public final Supplier<Integer> outlineColor;

        private final Cache<Text> cache;


        public Part (Supplier<Text> text, Supplier<DrawMode> mode, Supplier<Integer> color, Supplier<Integer> outlineColor) {
            this(text, mode, color, outlineColor, null, DataUtils.alwaysFalse, new Cache<>(UpdateTiming.FRAME, text, ERROR));
        }

        public Part(Supplier<Text> text, Supplier<DrawMode> mode, Supplier<Integer> color, Supplier<Integer> outlineColor, Supplier<HudIcon> icon, Supplier<Boolean> useIcon) {
            this(text, mode, color, outlineColor, icon, useIcon, new Cache<>(UpdateTiming.FRAME, text, ERROR));
        }

        public Part(Supplier<Text> text, Supplier<DrawMode> mode, Supplier<Integer> color, Supplier<Integer> outlineColor, Cache<Text> cache) {
           this(text, mode, color, outlineColor, null, DataUtils.alwaysFalse, cache);

        }

        public Part(Supplier<Text> text, Supplier<DrawMode> mode, Supplier<Integer> color, Supplier<Integer> outlineColor, Supplier<HudIcon> icon, Supplier<Boolean> useIcon, Cache<Text> cache) {
            this.text = text;
            this.mode = mode;
            this.color = color;
            this.outlineColor = outlineColor;
            this.icon = icon;
            this.useIcon = useIcon;
            this.cache = cache;
        }

    }


}
