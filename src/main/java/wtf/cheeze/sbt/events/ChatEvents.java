package wtf.cheeze.sbt.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.text.Text;

/**
 * Custom chat events which get us the message before Fabric API has
 * the chance to change their content or cancel them. These should always
 * be used over {@link net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents}
 * when information needs to be extracted.
 */
public class ChatEvents {

    public static final Event<OnMessage> ON_GAME = EventFactory.createArrayBacked(OnMessage.class, listeners -> (message) -> {
        for (OnMessage listener : listeners) {
            listener.onMessage(message);
        }
    });

    public static final Event<OnMessage> ON_ACTION_BAR = EventFactory.createArrayBacked(OnMessage.class, listeners -> (message) -> {
        for (OnMessage listener : listeners) {
            listener.onMessage(message);
        }
    });


    @FunctionalInterface
    public interface OnMessage {
        void onMessage(Text message);
    }
}
