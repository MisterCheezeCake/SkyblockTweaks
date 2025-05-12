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

import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.utils.enums.Location;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;

@Mixin(InGameHud.class)
public abstract class HudElementHiderMixin {
    @Inject(method = "renderArmor" , at = @At("HEAD"), cancellable = true)
    private static void sbt$onRenderArmor(CallbackInfo ci) {
        if (SBTConfig.get().hudTweaks.noRenderArmor && SkyblockData.inSB) {
            ci.cancel();
        }
    }

    @Inject(method = "renderHealthBar" , at = @At("HEAD"), cancellable = true)
    private void sbt$onRenderHealth(CallbackInfo ci) {

        if (SBTConfig.get().hudTweaks.noRenderHearts && SkyblockData.inSB && (!SBTConfig.get().hudTweaks.showHearsInRift || SkyblockData.location != Location.RIFT)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderFood" , at = @At("HEAD"), cancellable = true)
    private void sbt$onRenderFood(CallbackInfo ci) {
        if (SBTConfig.get().hudTweaks.noRenderHunger && SkyblockData.inSB) {
            ci.cancel();
        }
    }

    @Inject(method = "renderStatusEffectOverlay" , at = @At("HEAD"), cancellable = true)
    private void sbt$onRenderStatusEffectOverlay(CallbackInfo ci) {
        if (SBTConfig.get().hudTweaks.noRenderPotionOverlay && SkyblockData.inSB) {
            ci.cancel();
        }
    }
}
