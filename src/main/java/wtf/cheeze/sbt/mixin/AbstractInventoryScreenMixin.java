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

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.gui.DrawContext;
//? if =1.21.1
/*import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;*/
//? if >=1.21.3 {
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//? if =1.21.1 {
/*import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?}
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;

@Mixin(/*? if >=1.21.3 {*/InventoryScreen.class /*?} else {*/ /*AbstractInventoryScreen.class *//*?}*/)
public abstract class AbstractInventoryScreenMixin {
    //? if >=1.21.3 {
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/StatusEffectsDisplay;drawStatusEffects(Lnet/minecraft/client/gui/DrawContext;IIF)V"))
    private boolean sbt$onDrawStatusEffects(StatusEffectsDisplay instance, DrawContext context, int mouseX, int mouseY, float tickDelta){
        return !SBTConfig.get().inventory.noRenderPotionHud || !SkyblockData.inSB;
    }
    //?} else {
    /*@Inject(method = "drawStatusEffects", at = @At("HEAD"), cancellable = true)
    private void sbt$onDrawStatusEffects(DrawContext context, int mouseX, int mouseY, CallbackInfo ci){
        if (SBTConfig.get().inventory.noRenderPotionHud && SkyblockData.inSB) {
            ci.cancel();
        }
    }
    *///?}

}