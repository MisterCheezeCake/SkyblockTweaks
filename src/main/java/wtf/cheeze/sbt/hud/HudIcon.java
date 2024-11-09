package wtf.cheeze.sbt.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import wtf.cheeze.sbt.utils.render.RenderUtils;

public class HudIcon {
    @Nullable private Identifier iconTexture;
    @Nullable private ItemStack iconStack;
    private final Mode mode;

    public HudIcon(Identifier iconTexture) {
        this.iconTexture = iconTexture;
        this.mode = Mode.TEXTURE;
    }

    public HudIcon(ItemStack iconStack) {
        this.iconStack = iconStack;
        this.mode = Mode.ITEM;
    }

    public void render(DrawContext context, int x, int y, float scale) {
        switch (mode) {
            case TEXTURE ->  {
                RenderUtils.drawTexture(context, iconTexture, (int) (x /scale) , (int) (y / scale),  8, 8, 8, 8);
            }
            case ITEM ->  {
                context.getMatrices().push();
                context.getMatrices().scale(0.5f, 0.5f, 0.5f);
                context.drawItem(iconStack, x, y);
                context.getMatrices().pop();
            }
        }
    }
    private static enum Mode {
        TEXTURE,
        ITEM
    }
}
