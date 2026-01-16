/*
 * Copyright (C) 2026 MisterCheezeCake
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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * This mixin allows SBT to only have to provide the custom tooltip frames for rarity colored tooltips and not also a duplicate
 * of the Minecraft background for each, this also ensures that changes to the default texture are reflected
 */
@Mixin(TooltipRenderUtil.class)
public abstract class TooltipRenderUtilMixin {

    @Shadow @Final private static Identifier BACKGROUND_SPRITE;

    @ModifyReturnValue(method = "getBackgroundSprite", at = @At("RETURN"))
    private static Identifier sbt$wrapGetBgSprite(Identifier original){
        return original.getNamespace().equals("skyblocktweaks") ? BACKGROUND_SPRITE : original;
    }
}
