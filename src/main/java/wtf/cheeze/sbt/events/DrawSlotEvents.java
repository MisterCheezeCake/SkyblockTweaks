package wtf.cheeze.sbt.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

public class DrawSlotEvents {

    public static Event<OnDrawSlot> BEFORE_ITEM = EventFactory.createArrayBacked(OnDrawSlot.class, listeners -> (screenTitle, context, slot) -> {
        for (OnDrawSlot listener : listeners) {
            listener.onDrawSlot(screenTitle, context, slot);
        }
    });





    @FunctionalInterface
    public interface OnDrawSlot {
        void onDrawSlot(Text screenTitle, DrawContext context, Slot slot);
    }
}
