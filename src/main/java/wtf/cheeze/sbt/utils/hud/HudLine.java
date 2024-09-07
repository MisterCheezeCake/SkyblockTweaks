package wtf.cheeze.sbt.utils.hud;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import wtf.cheeze.sbt.utils.render.RenderUtils;

import java.util.function.Supplier;

public class HudLine {

    public Supplier<Integer> color;
    public Supplier<Integer> outlineColor;
    public Supplier<DrawMode> mode;
    public Supplier<Text> text;
    public Supplier<Boolean> useIcon;

    @Nullable
    // Make sure not to create a new instance of HudIcon every time you call this
    public Supplier<HudIcon> icon;

    public HudLine(Supplier<Integer> getColor, Supplier<Integer> getOutlineColor, Supplier<DrawMode> getMode, Supplier<Text> getText) {
        this.color = getColor;
        this.outlineColor = getOutlineColor;
        this.text = getText;
        this.mode = getMode;
        this.useIcon = () -> false;

    }

    public HudLine(Supplier<Integer> getColor, Supplier<Integer> getOutlineColor, Supplier<DrawMode> getMode, Supplier<Text> getText, Supplier<HudIcon> icon, Supplier<Boolean> useIcon) {
        this.color = getColor;
        this.outlineColor = getOutlineColor;
        this.text = getText;
        this.mode = getMode;
        this.icon = icon;
        this.useIcon = useIcon;
    }

    public void render(DrawContext context, int x, int y, float scale) {

        switch (mode.get()) {
            case PURE:
                render(context, x, y, scale, false);
                break;
            case SHADOW:
                render(context, x, y, scale, true);
                break;
            case OUTLINE:
                if (useIcon.get()) {
                    icon.get().render(context, (int) (x /scale), (int) (y /scale));
                    RenderUtils.drawStringWithOutline(context, text.get(), x + (int) (10 * scale), y, color.get(), outlineColor.get(), scale, true);
                } else {
                    RenderUtils.drawStringWithOutline(context, text.get(), x, y, color.get(), outlineColor.get(), scale, true);
                }
        }
    }

    private void render(DrawContext context, int x, int y, float scale, boolean shadow) {

        if (useIcon.get()) {
            int i = (int) (1 * scale);
//            context.fill(x - i, y - i, x + RenderUtils.getStringWidth(text.get()) + 10, y + 9, 1694433280);
            icon.get().render(context, (int) (x /scale), (int) (y /scale));
            RenderUtils.drawString(context, text.get(), x + (int) (10 * scale), y, color.get(), shadow, scale, true);
        } else {
            RenderUtils.drawString(context, text.get(), x, y, color.get(), shadow, scale, true);
        }
    }

    public int getWidth() {
        return RenderUtils.getStringWidth(text.get()) + (useIcon.get() ? 10 : 0);
    }

    public enum DrawMode implements NameableEnum {
        PURE, SHADOW, OUTLINE;
        @Override
        public Text getDisplayName() {
            return Text.literal(name());
        }
    }

}
