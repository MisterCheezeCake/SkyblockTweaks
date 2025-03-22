package wtf.cheeze.sbt.hud.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import wtf.cheeze.sbt.hud.bounds.Bounds;
import wtf.cheeze.sbt.utils.CheezePair;
import wtf.cheeze.sbt.utils.render.RenderUtils;

import java.util.List;

public record Popup(Screen screen, int x, int y, Text title, List<CheezePair<String, ? extends ClickableWidget>> children) implements Drawable {

    private static final Identifier BACKGROUND = Identifier.of("skyblocktweaks", "gui/panel.png");
    public static final int WIDTH = 80;
    public static final int HEIGHT = 130;

    public static final int POPUP_Z = 300;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, POPUP_Z);
        RenderUtils.drawTexture(context, BACKGROUND, x, y, WIDTH, HEIGHT, WIDTH, HEIGHT);
        RenderUtils.drawCenteredText(context, title, x + 40, y + 5, 0xFFFFFF, false);
        var renderY = y + 15;
        for (var entry : children) {
            RenderUtils.drawCenteredText(context, Text.literal(entry.key()), centerX(), renderY, 0xFFFFFF, false);
            renderY+= 10;
            var widget = entry.val();
            widget.setX(centerX() - 35);
            widget.setY(renderY);
            widget.setWidth(70);
            widget.setHeight(15);
            renderY += 18;
        }
        context.getMatrices().pop();
    }

     public Popup {
        screen.drawables.add(this);
        for (var entry : children) {
            screen.addDrawableChild(entry.val());
        }

    }

    public void remove() {
        screen.drawables.remove(this);
        for (var entry : children) {
            screen.remove(entry.val());
        }
    }



    private int centerX() {
        return x + (WIDTH / 2);
    }

    public Bounds getBounds() {
        return new Bounds(x, y, WIDTH, HEIGHT, 1.0f);
    }

}
