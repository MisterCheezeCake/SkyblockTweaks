package wtf.cheeze.sbt.utils.hud;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import wtf.cheeze.sbt.utils.RenderUtils;

import java.util.function.Supplier;

public class HudLine {
    public Supplier<Integer> color;
    public Supplier<Integer> outlineColor;


    public Supplier<DrawMode> mode;
    public Supplier<String> text;


    public Supplier<Boolean> useIcon;

    @Nullable
    // Make sure not to create a new instance of Identifier every time you call this
    public Supplier<Identifier> icon;


    public HudLine(Supplier<Integer> getColor, Supplier<Integer> getOutlineColor,  Supplier<DrawMode> getMode, Supplier<String> getText) {
        this.color = getColor;
        this.outlineColor = getOutlineColor;
        this.text = getText;
        this.mode = getMode;
        this.useIcon = () -> false;

    }

    public HudLine(Supplier<Integer> getColor, Supplier<Integer> getOutlineColor, Supplier<DrawMode> getMode, Supplier<String> getText, Supplier<Identifier> icon, Supplier<Boolean> useIcon) {
        this.color = getColor;
        this.outlineColor = getOutlineColor;
        this.text = getText;
        this.mode = getMode;
        this.icon = icon;
        this.useIcon = useIcon;
    }


    public static enum DrawMode implements NameableEnum {
        PURE,
        SHADOW,
        OUTLINE;

        @Override
        public Text getDisplayName() {
            return Text.literal(name());
        }
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
                        context.drawTexture(icon.get(), x, y, 0, 0, 8, 8, 8, 8);
                        RenderUtils.drawStringWithOutline(context, Text.literal(text.get()), x + 10, y, color.get(), outlineColor.get(), scale, true);
                    } else {
                        RenderUtils.drawStringWithOutline(context, Text.literal(text.get()), x, y, color.get(), outlineColor.get(), scale, true);
                    }


        }

    }

    private void render(DrawContext context, int x, int y, float scale, boolean shadow) {

            if (useIcon.get()) {
                context.drawTexture(icon.get(), x, y, 0, 0, 8, 8, 8, 8);
                RenderUtils.drawString(context, Text.literal(text.get()), x + 10, y, color.get(), shadow, scale, true);
            } else {
                RenderUtils.drawString(context, Text.literal(text.get()), x, y, color.get(), shadow, scale, true);
            }


    }
}
