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
import wtf.cheeze.sbt.utils.render.RenderUtils;

import java.util.function.Supplier;

public class SingleHudLine implements HudComponent {
    public Supplier<Integer> color;
    public Supplier<Integer> outlineColor;
    public Supplier<DrawMode> mode;
    public Supplier<Component> text;
    public Supplier<Boolean> useIcon;

    private final UpdateTiming timing;

    private final Cache<Component> cache;


    private static final int LINE_COUNT = 1;
    private static final int LINE_HEIGHT = 9;

    @Nullable
    // Make sure not to create a new instance of HudIcon every time you call this
    public Supplier<HudIcon> icon;

    public SingleHudLine(Supplier<Integer> getColor, Supplier<Integer> getOutlineColor, Supplier<DrawMode> getMode, Supplier<Component> getText) {
        this(UpdateTiming.FRAME, getColor, getOutlineColor, getMode, getText, null, DataUtils.ALWAYS_FALSE);
    }
    public SingleHudLine(UpdateTiming timing, Supplier<Integer> getColor, Supplier<Integer> getOutlineColor, Supplier<DrawMode> getMode, Supplier<Component> getText) {
        this(timing, getColor, getOutlineColor, getMode, getText, null, DataUtils.ALWAYS_FALSE);
    }

    public SingleHudLine(Supplier<Integer> getColor, Supplier<Integer> getOutlineColor, Supplier<DrawMode> getMode, Supplier<Component> getText, Supplier<HudIcon> icon, Supplier<Boolean> useIcon) {
        this(UpdateTiming.FRAME, getColor, getOutlineColor, getMode, getText, icon, useIcon);
    }

    public SingleHudLine(UpdateTiming timing, Supplier<Integer> getColor, Supplier<Integer> getOutlineColor, Supplier<DrawMode> getMode, Supplier<Component> getText, Supplier<HudIcon> icon, Supplier<Boolean> useIcon) {
        this.timing = timing;
        this.color = getColor;
        this.outlineColor = getOutlineColor;
        this.text = getText;
        this.mode = getMode;
        this.icon = icon;
        this.useIcon = useIcon;
        this.cache = new Cache<>(timing, text, ERROR);
    }




    @Override
    public int render(GuiGraphics context, int x, int y, float scale) {
        if (timing == UpdateTiming.FRAME || cache.isDueForUpdate()) {
            cache.update();
        }

        switch (mode.get()) {
            case PURE -> render(context, x, y, scale, false);
            case SHADOW ->  render(context, x, y, scale, true);
            case OUTLINE -> {
                if (useIcon.get()) {
                    icon.get().render(context, x, y, scale);

                    RenderUtils.drawTextWithOutline(context, cache.get(), x + (int) (10 * scale), y, color.get(), outlineColor.get(), scale, true);
                } else {
                    RenderUtils.drawTextWithOutline(context, cache.get(), x, y, color.get(), outlineColor.get(), scale, true);
                }
            }
        }
        return 1;
    }



    private void render(GuiGraphics context, int x, int y, float scale, boolean shadow) {

        if (useIcon.get()) {
            icon.get().render(context, x, y, scale);
            RenderUtils.drawText(context, cache.get(), x + (int) (10 * scale), y, color.get(), shadow, scale, true);
        } else {
            RenderUtils.drawText(context, cache.get(), x, y, color.get(), shadow, scale, true);
        }
    }

    @Override
    public int getWidth() {
        return RenderUtils.getStringWidth(cache.get()) + (useIcon.get() ? 10 : 0);
    }

    @Override
    public int getlines() { return
            LINE_COUNT;
    }

    @Override
    public int getHeight() {
        return LINE_HEIGHT;
    }
}