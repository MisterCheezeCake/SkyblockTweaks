package wtf.cheeze.sbt.config.categories;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.OptionDescription;
import net.minecraft.network.chat.Component;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.features.overlay.*;

public class Overlays {
    public static ConfigCategory getCategory(ConfigImpl defaults, ConfigImpl config) {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("sbt.config.overlays"))
                .tooltip(Component.translatable("sbt.config.overlays.desc"))
                .group(MenuHighlights.Config.getGroup(defaults, config))
                .group(BrewingStandOverlay.Config.getGroup(defaults, config))
                .group(MinionExp.Config.getGroup(defaults, config))
                .group(ReforgeOverlay.Config.getGroup(defaults, config))
                .group(TooltipColors.Config.getGroup(defaults, config))
                .build();

    }

    public static final String BASE_KEY = "sbt.config.overlays.";

    public static Component key(String key) {
        return Component.translatable(BASE_KEY + key);
    }

    public static OptionDescription keyD(String key) {
        return OptionDescription.of(Component.translatable(BASE_KEY + key + ".desc"));
    }
}
