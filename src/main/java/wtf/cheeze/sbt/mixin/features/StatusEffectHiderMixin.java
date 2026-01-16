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
package wtf.cheeze.sbt.mixin.features;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;

@Mixin(InventoryScreen.class)
public abstract class StatusEffectHiderMixin {

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target =
              //? if <1.21.11   {
            /*"Lnet/minecraft/client/gui/screens/inventory/EffectsInInventory;renderEffects(Lnet/minecraft/client/gui/GuiGraphics;II)V" */
            //? } else {
            "Lnet/minecraft/client/gui/screens/inventory/EffectsInInventory;render(Lnet/minecraft/client/gui/GuiGraphics;II)V"
            //?}
    ))
    private boolean sbt$onDrawStatusEffects(EffectsInInventory instance, GuiGraphics guiGraphics, int mouseX, int mouseY) {
        return !SBTConfig.get().inventory.noRenderPotionHud || !SkyblockData.inSB;
    }


}
