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

import com.mojang.brigadier.Message;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.hud.HudIcon;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.render.RenderUtils;

import java.util.function.Supplier;

public class SingleHudLine implements HudComponent {

    public Supplier<Integer> color;
    public Supplier<Integer> outlineColor;
    public Supplier<DrawMode> mode;
    public Supplier<Text> text;
    public Supplier<Boolean> useIcon;

    private static final Text ERROR = TextUtils.withColor("ERROR", Colors.RED);

    public int lineCount = 1;

    @Nullable
    // Make sure not to create a new instance of HudIcon every time you call this
    public Supplier<HudIcon> icon;

    public SingleHudLine(Supplier<Integer> getColor, Supplier<Integer> getOutlineColor, Supplier<DrawMode> getMode, Supplier<Text> getText) {
        this.color = getColor;
        this.outlineColor = getOutlineColor;
        this.text = getText;
        this.mode = getMode;
        this.useIcon = () -> false;

    }

    public SingleHudLine(Supplier<Integer> getColor, Supplier<Integer> getOutlineColor, Supplier<DrawMode> getMode, Supplier<Text> getText, Supplier<HudIcon> icon, Supplier<Boolean> useIcon) {
        this.color = getColor;
        this.outlineColor = getOutlineColor;
        this.text = getText;
        this.mode = getMode;
        this.icon = icon;
        this.useIcon = useIcon;
    }


    @Override
    public int render(DrawContext context, int x, int y, float scale) {

        switch (mode.get()) {
            case PURE:
                render(context, x, y, scale, false);
                break;
            case SHADOW:
                render(context, x, y, scale, true);
                break;
            case OUTLINE:
                if (useIcon.get()) {
                    icon.get().render(context, x, y, scale);

                    RenderUtils.drawTextWithOutline(context, getText() , x + (int) (10 * scale), y, color.get(), outlineColor.get(), scale, true);
                } else {
                    RenderUtils.drawTextWithOutline(context, getText(), x, y, color.get(), outlineColor.get(), scale, true);
                }
        }
        return 1;
    }

    private Text getText() {
        try {
            return text.get();
        } catch (Exception e) {
            SkyblockTweaks.LOGGER.error("Error while getting text for HUD line", e);
            return ERROR;
        }
    }

    private void render(DrawContext context, int x, int y, float scale, boolean shadow) {

        if (useIcon.get()) {

//            context.fill(x - i, y - i, x + RenderUtils.getStringWidth(text.get()) + 10, y + 9, 1694433280);
            icon.get().render(context, x, y, scale);
            RenderUtils.drawText(context, text.get(), x + (int) (10 * scale), y, color.get(), shadow, scale, true);
        } else {
            RenderUtils.drawText(context, text.get(), x, y, color.get(), shadow, scale, true);
        }
    }

    @Override
    public int getWidth() {
        return RenderUtils.getStringWidth(text.get()) + (useIcon.get() ? 10 : 0);
    }

    @Override
    public int getlines() { return 1;}
}