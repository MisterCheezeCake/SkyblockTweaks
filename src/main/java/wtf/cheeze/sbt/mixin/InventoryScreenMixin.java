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

import net.minecraft.client.gui.screen.ingame.*;

import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.SBTConfig;




//? if =1.21.1 {
/*@Mixin(InventoryScreen.class)
 *///?} else {
@Mixin(RecipeBookScreen.class)
//?}

public abstract class InventoryScreenMixin {
    // This injects into the head of the synthetic method that is triggered when you click the recipe book button
    //? if =1.21.1 {
    /*@Inject(method = "method_19891", at = @At("HEAD"), cancellable = true)
     *///?} else {
    @Inject(method = "method_64513", at = @At("HEAD"), cancellable = true)
    //?}
    private void sbt$onPressRBook(ButtonWidget button, CallbackInfo ci) {
        if (SBTConfig.get().inventory.redirectRecipeBook && SkyblockTweaks.DATA.inSB) {
            ci.cancel();
            MinecraftClient.getInstance().getNetworkHandler().sendChatCommand("recipebook");
        }
    }
}

