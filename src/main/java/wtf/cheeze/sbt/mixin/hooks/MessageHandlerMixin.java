package wtf.cheeze.sbt.mixin.hooks;


import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.cheeze.sbt.events.ChatEvents;

@Mixin(MessageHandler.class)
public abstract class MessageHandlerMixin {
    /**
     * Powers {@link ChatEvents}
     */
    @Inject(order = 500, method = "onGameMessage", at = @At("HEAD"))
    private void sbt$onGameMessage(Text message, boolean overlay, CallbackInfo ci) {
        if (overlay) {
            ChatEvents.ON_ACTION_BAR.invoker().onMessage(message);
        } else {
            ChatEvents.ON_GAME.invoker().onMessage(message);
        }
    }
}
