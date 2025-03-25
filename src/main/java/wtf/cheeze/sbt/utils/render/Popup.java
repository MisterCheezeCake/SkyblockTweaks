package wtf.cheeze.sbt.utils.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.util.Identifier;
import wtf.cheeze.sbt.hud.bounds.Bounds;

import java.util.List;

public interface Popup extends Drawable {
    Identifier BACKGROUND = Identifier.of("skyblocktweaks", "gui/panel.png");
    int WIDTH = 80;
    int HEIGHT = 130;

    int x();
    int y();

    List<? extends ClickableWidget> childrenList();
    Screen screen();

    default void renderBackground(DrawContext context) {
        RenderUtils.drawTexture(context, BACKGROUND, x(), y(), WIDTH, HEIGHT, WIDTH, HEIGHT);
    }

    default void remove() {
        screen().drawables.remove(this);
        for (var child : childrenList()) {
            screen().remove(child);
        }
    }
    default int centerX() {
        return x() + (WIDTH / 2);
    }
    default Bounds getBounds() {
         return new Bounds(x(), y(), WIDTH, HEIGHT, 1.0f);
    }


}

