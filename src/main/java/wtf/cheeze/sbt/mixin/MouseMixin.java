package wtf.cheeze.sbt.mixin;


import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import wtf.cheeze.sbt.features.misc.MouseLock;

@Mixin(Mouse.class)
public abstract class MouseMixin {
    @WrapWithCondition(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    private boolean sbt$allowMouseMove(ClientPlayerEntity instance, double v, double v2) {
        return !MouseLock.locked;
    }
}
