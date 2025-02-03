package wtf.cheeze.sbt.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.cheeze.sbt.utils.render.RenderUtils;

public class HudIcon {
    @Nullable private Identifier iconTexture;
    @Nullable private ItemStack iconStack;
    private final Mode mode;

    public HudIcon(@NotNull Identifier iconTexture) {
        this.iconTexture = iconTexture;
        this.mode = Mode.TEXTURE;
    }

    public HudIcon(@NotNull ItemStack iconStack) {
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
               // context.getMatrices().translate(-8, -8, 0);
                context.getMatrices().scale(0.5f, 0.5f, 1.0f);
                context.drawItem(iconStack, (int) (x / (0.5f *scale)) , (int) (y / (0.5f *scale)));
                context.getMatrices().pop();
            }
        }
    }
    private enum Mode {
        TEXTURE,
        ITEM
    }
}
