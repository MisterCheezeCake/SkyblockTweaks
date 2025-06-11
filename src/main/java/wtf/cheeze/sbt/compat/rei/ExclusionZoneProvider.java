package wtf.cheeze.sbt.compat.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZonesProvider;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import wtf.cheeze.sbt.hud.bounds.Bounds;
import wtf.cheeze.sbt.utils.injected.SBTHandledScreen;
import wtf.cheeze.sbt.utils.render.Popup;

import java.util.Collection;
import java.util.List;

public class ExclusionZoneProvider implements ExclusionZonesProvider<GenericContainerScreen> {
    @Override
    public Collection<Rectangle> provide(GenericContainerScreen screen) {
        Popup popup = ((SBTHandledScreen) screen).sbt$getPopup();
        if (popup != null) {
            Bounds bounds = popup.getBounds();
            return List.of(new Rectangle(bounds.x, bounds.y , bounds.width, bounds.height));
        }
        return List.of();
    }
}
