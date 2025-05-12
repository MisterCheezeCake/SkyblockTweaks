/*
 * Copyright (C) 2025 MisterCheezeCake
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
package wtf.cheeze.sbt.mixin.features;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.utils.injected.SBTDrawContext;

@Mixin(InGameHud.class)
public abstract class NoShadowActionBarMixin {
    @WrapOperation(method = "renderOverlayMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithBackground(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIII)I"))
    private int sbt$drawTextWithBackgroundNoShadowWrap(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, int width, int color, Operation<Integer> original) {
        if (SBTConfig.get().hudTweaks.noShadowActionBar) {
            return ((SBTDrawContext) instance).sbt$drawTextWithBackgroundNoShadow(textRenderer, text, x, y, width, color);
        } else {
            return original.call(instance, textRenderer, text, x, y, width, color);
        }
    }
}
