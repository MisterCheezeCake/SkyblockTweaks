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
package wtf.cheeze.sbt.mixin.hooks;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.cheeze.sbt.events.DrawSlotEvents;
import wtf.cheeze.sbt.features.overlay.BrewingStandOverlay;
import wtf.cheeze.sbt.utils.KillSwitch;
import wtf.cheeze.sbt.utils.injected.SBTAbstractContainerScreen;
import wtf.cheeze.sbt.utils.render.Popup;

import java.awt.event.MouseEvent;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements SBTAbstractContainerScreen {
    @Unique
    private static final String SBT$FEATURE_ID_POPUP = "handled_screen_popups";

    @Shadow
    @Final
    protected T menu;

    @Unique @Nullable
    private Popup sbt$popup;

    @Unique @Override
    public @Nullable Popup sbt$getPopup() {
        return sbt$popup;
    }

    @Unique @Override
    public void sbt$setPopup(@Nullable Popup popup) {
        if (KillSwitch.shouldKill(SBT$FEATURE_ID_POPUP)) {
            this.sbt$popup = null;
            return;
        }
        if (this.sbt$popup != null) {
            this.sbt$popup.remove();
        }
        this.sbt$popup = popup;
    }

    @Inject(method = "renderSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderItem(Lnet/minecraft/world/item/ItemStack;III)V"))
    protected void sbt$beforeDrawItem(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        DrawSlotEvents.BEFORE_ITEM.invoker().onDrawSlot(getTitle(), guiGraphics, slot);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void sbt$onMouseClicked(MouseButtonEvent event, boolean isDoubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (this.sbt$popup != null) {
            if (this.sbt$popup.mouseClicked(event.x(), event.y(), event.button())) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void sbt$onKeyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (this.sbt$popup != null) {
            if (this.sbt$popup.keyPressed(event.key(), event.scancode(), event.modifiers())) {
                cir.setReturnValue(true);
            }
        }
    }

//    @Inject(
//            method = "renderMain",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlots(Lnet/minecraft/client/gui/DrawContext;)V")
//    )
//    protected void sbtBeforeDrawSlots(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
//        if (getTitle().getString().equals("Brewing Stand")) {
//            if (this.handler instanceof BrewingStandScreenHandler) return;
//            BrewingStandOverlay.render(handler.slots, context);
//        }
//    }

    @Inject(method = "renderContents", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3x2fStack;popMatrix()Lorg/joml/Matrix3x2fStack;"))
    protected void sbt$endRenderMain(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (getTitle().getString().equals("Brewing Stand")) {
            if (this.menu instanceof BrewingStandMenu) return;
            BrewingStandOverlay.render(menu.slots, guiGraphics);
        }
    }

    private AbstractContainerScreenMixin(Component t) {
        super(t);
    }
}
