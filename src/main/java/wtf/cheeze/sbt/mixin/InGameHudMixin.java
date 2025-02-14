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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LayeredDrawer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.events.HudRenderEvents;
import wtf.cheeze.sbt.utils.enums.Location;
import wtf.cheeze.sbt.utils.render.SBTDrawContext;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Final @Shadow
    private LayeredDrawer layeredDrawer;
    @WrapOperation(method = "renderOverlayMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithBackground(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIII)I"))
    private int sbt$drawTextWithBackgroundNoShadowWrap(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, int width, int color, Operation<Integer> original) {
        if (SBTConfig.get().hudTweaks.noShadowActionBar) {
            return ((SBTDrawContext) instance).sbt$drawTextWithBackgroundNoShadow(textRenderer, text, x, y, width, color);
        } else {
            return original.call(instance, textRenderer, text, x, y, width, color);
        }
    }

    @Inject(method = "renderArmor" , at = @At("HEAD"), cancellable = true)
    private static void sbt$onRenderArmor(CallbackInfo ci) {
        if (SBTConfig.get().hudTweaks.noRenderArmor && SkyblockTweaks.DATA.inSB) {
            ci.cancel();
        }
    }

    @Inject(method = "renderHealthBar" , at = @At("HEAD"), cancellable = true)
    private void sbt$onRenderHealth(CallbackInfo ci) {
        if (SBTConfig.get().hudTweaks.noRenderHearts && SkyblockTweaks.DATA.inSB && (!SBTConfig.get().hudTweaks.showHearsInRift || SkyblockTweaks.DATA.location != Location.RIFT)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderFood" , at = @At("HEAD"), cancellable = true)
    private void sbt$onRenderFood(CallbackInfo ci) {
        if (SBTConfig.get().hudTweaks.noRenderHunger && SkyblockTweaks.DATA.inSB) {
            ci.cancel();
        }
    }

    // The following injectors power HudRenderEvents and were likewise taken from Skyblocker
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LayeredDrawer;addLayer(Lnet/minecraft/client/gui/LayeredDrawer$Layer;)Lnet/minecraft/client/gui/LayeredDrawer;", ordinal = 2))
    private LayeredDrawer.Layer sbt$afterMainHud(LayeredDrawer.Layer mainHudLayer) {
        return (context, tickCounter) -> {
            mainHudLayer.render(context, tickCounter);
            HudRenderEvents.AFTER_MAIN_HUD.invoker().onRender(context, tickCounter);
        };
    }

    @ModifyArg(method = "<init>", slice = @Slice(from = @At(value = "NEW", target = "Lnet/minecraft/client/gui/LayeredDrawer;", ordinal = 2)), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LayeredDrawer;addLayer(Lnet/minecraft/client/gui/LayeredDrawer$Layer;)Lnet/minecraft/client/gui/LayeredDrawer;", ordinal = 5))
    private LayeredDrawer.Layer sbt$beforeChat(LayeredDrawer.Layer beforeChatLayer) {
        return (context, tickCounter) -> {
            HudRenderEvents.BEFORE_CHAT.invoker().onRender(context, tickCounter);
            beforeChatLayer.render(context, tickCounter);
        };
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void sbt$afterDrawersInitialized(CallbackInfo ci) {
        this.layeredDrawer.addLayer(HudRenderEvents.LAST.invoker()::onRender);
    }

}
