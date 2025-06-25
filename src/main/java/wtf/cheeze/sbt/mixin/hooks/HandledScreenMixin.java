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

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.events.DrawSlotEvents;
import wtf.cheeze.sbt.features.overlay.BrewingStandOverlay;
import wtf.cheeze.sbt.utils.KillSwitch;
import wtf.cheeze.sbt.utils.injected.SBTHandledScreen;
import wtf.cheeze.sbt.utils.render.Popup;

import java.util.Optional;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements SBTHandledScreen {

    @Unique
    private static final String SBT$FEATURE_ID_POPUP = "handled_screen_popups";

    @Shadow
    @Final
    protected T handler;

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

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/item/ItemStack;III)V"))
    protected void sbt$beforeDrawItem(DrawContext context, Slot slot, CallbackInfo ci) {
        DrawSlotEvents.BEFORE_ITEM.invoker().onDrawSlot(getTitle(), context, slot);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void sbt$onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.sbt$popup != null) {
            if (this.sbt$popup.mouseClicked(mouseX, mouseY, button)) {
                cir.setReturnValue(true);
            }
        }
    }


    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void sbt$onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (this.sbt$popup != null) {
            if (this.sbt$popup.keyPressed(keyCode, scanCode, modifiers)) {
                cir.setReturnValue(true);
            }
        }
    }

//    @Inject(
//            method = /*? if <=1.21.5 {*//*"render"*//*?} else {*/ "renderMain"/*?}*/,
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlots(Lnet/minecraft/client/gui/DrawContext;)V")
//    )
//    protected void sbtBeforeDrawSlots(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
//        if (getTitle().getString().equals("Brewing Stand")) {
//            if (this.handler instanceof BrewingStandScreenHandler) return;
//            BrewingStandOverlay.render(handler.slots, context);
//        }
//    }

    //? if <=1.21.5 {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlots(Lnet/minecraft/client/gui/DrawContext;)V"))
    protected void sbtBeforeDrawSlots(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (getTitle().getString().equals("Brewing Stand")) {
            if (this.handler instanceof BrewingStandScreenHandler) return;
            BrewingStandOverlay.render(handler.slots, context);
        }
    }
    //?} else {
    /*@Inject(method = "renderMain", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3x2fStack;popMatrix()Lorg/joml/Matrix3x2fStack;"))
    protected void sbt$endRenderMain(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (getTitle().getString().equals("Brewing Stand")) {
            if (this.handler instanceof BrewingStandScreenHandler) return;
            BrewingStandOverlay.render(handler.slots, context);
        }
    }
    *///?}


    private HandledScreenMixin(Text t) {
        super(t);
    }
}
