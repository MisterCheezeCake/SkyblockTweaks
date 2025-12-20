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
package wtf.cheeze.sbt.mixin.hooks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import wtf.cheeze.sbt.utils.injected.SBTDrawContext;

import java.util.Objects;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin implements SBTDrawContext {
    @Final
    @Shadow
    private Minecraft minecraft;

    @Shadow
    public abstract void fill(int x1, int y1, int x2, int y2, int color);

    @Shadow
    public abstract void drawString(Font font, Component component, int i, int j, int k, boolean bl);

    @Override
    public void sbt$drawTextWithBackgroundNoShadow(Font font, Component text, int x, int y, int width, int color) {
        int i = this.minecraft.options.getBackgroundColor(0.0F);
        if (i != 0) {
            Objects.requireNonNull(font);
            this.fill(x -2, y-2 , x + width + 2, y + 9 + 2, ARGB.multiply(i, color));
        }
        this.drawString(font, text, x, y, color, false);
    }
}
