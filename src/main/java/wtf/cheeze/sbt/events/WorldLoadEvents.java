package wtf.cheeze.sbt.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen.WorldEntryReason;
import net.minecraft.client.world.ClientWorld;

public class WorldLoadEvents {

    public static Event<OnWorldLoad> WORLD_LOAD = EventFactory.createArrayBacked(OnWorldLoad.class, listeners -> (world, worldEntryReason) -> {
        for (OnWorldLoad listener : listeners) {
            listener.onWorldLoad(world, worldEntryReason);
        }
    });

    @FunctionalInterface
    public interface OnWorldLoad {
        void onWorldLoad(ClientWorld world, WorldEntryReason worldEntryReason);
    }
}
