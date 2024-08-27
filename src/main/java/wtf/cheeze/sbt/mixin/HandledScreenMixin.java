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

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BrewingStandScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.cheeze.sbt.features.BrewingStandOverlay;
import wtf.cheeze.sbt.features.MenuHighlights;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen {


    @Shadow @Final protected T handler;

    @Shadow protected int x;

    @Shadow protected int y;

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/item/ItemStack;III)V"))
    protected void sbt$beforeDrawItem(DrawContext context, Slot slot, CallbackInfo ci) {
        var title = this.getTitle().getString();
        switch (title) {
            case "SkyBlock Hub Selector" -> MenuHighlights.tryDrawHighlight(context, slot);
            case "Dungeon Hub Selector" -> MenuHighlights.tryDrawHighlightDH(context, slot);
            case "Heart of the Mountain" -> MenuHighlights.tryDrawHighlightHOTM(context, slot);
            case "Brewing Stand" -> {
                // We do this so it only attempts to handle Skyblock brewing stands, not vanilla ones
                if (this.handler instanceof BrewingStandScreenHandler) return;
                if (slot.id == 13) BrewingStandOverlay.render(handler.slots, context);

            }
        }
        if (title.contains("Widget") || title.contains("Setting")) {
            MenuHighlights.tryDrawHighlightWidget(context, slot);
        }
    }

    private HandledScreenMixin(Text t) {super(t);}
}
