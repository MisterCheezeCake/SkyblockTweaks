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
package wtf.cheeze.sbt.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import wtf.cheeze.sbt.utils.ModifiedDrawContext;

import java.util.Objects;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin implements ModifiedDrawContext {
    @Shadow
    private MinecraftClient client;

    @Shadow
    public abstract void fill(int x1, int y1, int x2, int y2, int color);

    @Shadow
    public abstract int drawText(TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow);

    @Override
    public int sbt$drawTextWithBackgroundNoShadow(TextRenderer textRenderer, Text text, int x, int y, int width, int color) {
        int i = this.client.options.getTextBackgroundColor(0.0F);
        if (i != 0) {
            int var10001 = x - 2;
            int var10002 = y - 2;
            int var10003 = x + width + 2;
            Objects.requireNonNull(textRenderer);
            this.fill(var10001, var10002, var10003, y + 9 + 2, ColorHelper.Argb.mixColor(i, color));
        }

        return this.drawText(textRenderer, text, x, y, color, false);
    }
}
