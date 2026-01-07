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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.utils.KillSwitch;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;

@Mixin(AbstractRecipeBookScreen.class)
public abstract class RecipeBookRedirectorMixin {
    @Unique private static final String FEATURE_ID = "recipe_book_redirect";

    @Inject(method = "method_64513", at = @At("HEAD"), cancellable = true)
    private void sbt$onPressRBook(Button button, CallbackInfo ci) {
        if (SBTConfig.get().inventory.redirectRecipeBook && SkyblockData.inSB) {
            if (KillSwitch.shouldKill(FEATURE_ID)) {
                return;
            }
            ci.cancel();
            Minecraft.getInstance().getConnection().sendCommand("recipebook");
        }
    }
}
