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

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.cheeze.sbt.SkyBlockTweaks;
import wtf.cheeze.sbt.utils.ModifiedDrawContext;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    @Nullable
    private Text overlayMessage;

    @Redirect(method ="renderOverlayMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithBackground(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIII)I"))
    private int sbt$drawTextWithBackgroundNoShadowRedirect(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, int width, int color) {
        if (SkyBlockTweaks.CONFIG.config.hudTweaks.noShadowActionBar) {
            return ((ModifiedDrawContext) instance).sbt$drawTextWithBackgroundNoShadow(textRenderer, text, x, y, width, color);
        } else {
            return instance.drawTextWithBackground(textRenderer, text, x, y, width, color);
        }
    }



    @Inject(method = "renderArmor" , at = @At("HEAD"), cancellable = true)
    private static void sbt$onRenderArmor(CallbackInfo ci) {
        if (SkyBlockTweaks.CONFIG.config.hudTweaks.noRenderArmor && SkyBlockTweaks.DATA.inSB) {
            ci.cancel();
        }
    }

    @Inject(method = "renderHealthBar" , at = @At("HEAD"), cancellable = true)
    private void sbt$onRenderHealth(CallbackInfo ci) {
        if (SkyBlockTweaks.CONFIG.config.hudTweaks.noRenderHearts && SkyBlockTweaks.DATA.inSB) {
            ci.cancel();
        }
    }

    @Inject(method = "renderFood" , at = @At("HEAD"), cancellable = true)
    private void sbt$onRenderFood(CallbackInfo ci) {
        if (SkyBlockTweaks.CONFIG.config.hudTweaks.noRenderHunger && SkyBlockTweaks.DATA.inSB) {
            ci.cancel();
        }
    }

}