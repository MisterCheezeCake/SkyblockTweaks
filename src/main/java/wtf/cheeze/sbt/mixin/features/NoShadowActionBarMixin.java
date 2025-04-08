package wtf.cheeze.sbt.mixin.features;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.utils.injected.SBTDrawContext;

@Mixin(InGameHud.class)
public abstract class NoShadowActionBarMixin {
    @WrapOperation(method = "renderOverlayMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithBackground(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIII)I"))
    private int sbt$drawTextWithBackgroundNoShadowWrap(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, int width, int color, Operation<Integer> original) {
        if (SBTConfig.get().hudTweaks.noShadowActionBar) {
            return ((SBTDrawContext) instance).sbt$drawTextWithBackgroundNoShadow(textRenderer, text, x, y, width, color);
        } else {
            return original.call(instance, textRenderer, text, x, y, width, color);
        }
    }
}
