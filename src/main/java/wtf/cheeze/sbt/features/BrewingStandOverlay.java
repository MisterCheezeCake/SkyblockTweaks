package wtf.cheeze.sbt.features;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.utils.render.RenderUtils;

public class BrewingStandOverlay {

    private static final int DRAW_OFFSET_X = 20;
    private static final int DRAW_OFFSET_Y = 4;
    // One more than the z offset that items are rendered at
    private static final float Z_OFFSET = 250.0f;

    public static void render(DefaultedList<Slot> slots, DrawContext context) {
        if (!SBTConfig.get().brewingStandOverlay.enabled) return;

        var slot13 = slots.get(13);
        var slot24 = slots.get(24);
        var slot42 = slots.get(42);

        context.getMatrices().push();
        context.getMatrices().translate(0.0f, 0.0f, Z_OFFSET);

        if (slot13.hasStack()) {
            drawName(slot13, context);
        }
        if (!slot24.getStack().getName().getString().startsWith("Place Water Bottles")) {
            drawName(slot24, context);
        }
        if (slot42.hasStack()) {
            drawName(slot42, context);
        }
        context.getMatrices().pop();
    }

    private static void drawName(Slot slot, DrawContext context) {
        var name = slot.getStack().getName();
        var color =  name.getStyle().getColor();
        int rcolor;
        if (color == null) {
            rcolor = 0xFFFFFF;
        } else {
            rcolor = color.getRgb();
        }
        RenderUtils.drawString(context, name, slot.x + DRAW_OFFSET_X, slot.y + DRAW_OFFSET_Y, rcolor, false);
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = true;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Text.literal("Brewing Stand Overlay"))
                    .description(OptionDescription.of(Text.literal("Enables the overlay for the brewing stand")))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.brewingStandOverlay.enabled,
                            () -> config.brewingStandOverlay.enabled,
                            value -> config.brewingStandOverlay.enabled = (Boolean) value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(Text.literal("Brewing Stand Overlay"))
                    .description(OptionDescription.of(Text.literal("Options for the brewing stand overlay")))
                    .option(enabled)
                    .build();


        }

    }

}
