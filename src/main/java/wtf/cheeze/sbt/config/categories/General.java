package wtf.cheeze.sbt.config.categories;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.features.BrewingStandOverlay;
import wtf.cheeze.sbt.features.MenuHighlights;
import wtf.cheeze.sbt.utils.Version;
import wtf.cheeze.sbt.utils.actionbar.ActionBarTransformer;

public class General {
    public static ConfigCategory getCategory(ConfigImpl defaults, ConfigImpl config) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("sbt.config.general"))
                .tooltip(Text.translatable("sbt.config.general.desc"))
                .option(GlobalSearchCategory.getOpenGlobalSearchButton(defaults, config))
                .option(Version.getStreamOption(defaults, config))
                .group(InventoryTweaks.getGroup(defaults, config))
                .group(MenuHighlights.Config.getGroup(defaults, config))
                .group(BrewingStandOverlay.Config.getGroup(defaults, config))
                .group(HudTweaks.getGroup(defaults, config))
                .group(ActionBarTransformer.Config.getGroup(defaults, config))
                .build();
    }

    public static final String BASE_KEY = "sbt.config.general.";

    public static Text key(String key) {
        return Text.translatable(BASE_KEY + key);
    }
    public static OptionDescription keyD(String key) {
        return OptionDescription.of(Text.translatable(BASE_KEY + key + ".desc"));
    }

    // These are subclassed because their features live in mixins and so can't control their own config
    public static class InventoryTweaks {
        @SerialEntry
        public boolean redirectRecipeBook = true;

        @SerialEntry
        public boolean noRenderPotionHud = true;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var noRenderPotionHud = Option.<Boolean>createBuilder()
                    .name(key("inventory.noRenderPotionHud"))
                    .description(keyD("inventory.noRenderPotionHud"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.inventory.noRenderPotionHud,
                            () -> config.inventory.noRenderPotionHud,
                            value -> config.inventory.noRenderPotionHud = (Boolean) value
                    )
                    .build();
            var redirectRecipeBook = Option.<Boolean>createBuilder()
                    .name(key("inventory.redirectRecipeBook"))
                    .description(keyD("inventory.redirectRecipeBook"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.inventory.redirectRecipeBook,
                            () -> config.inventory.redirectRecipeBook,
                            value -> config.inventory.redirectRecipeBook = (Boolean) value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(key("inventory"))
                    .description(keyD("inventory"))
                    .option(noRenderPotionHud)
                    .option(redirectRecipeBook)
                    .build();
        }
    }

    public static class HudTweaks {
        @SerialEntry
        public boolean noShadowActionBar = true;

        @SerialEntry
        public boolean noRenderBossBar = false;

        @SerialEntry
        public boolean noRenderHearts = true;

        @SerialEntry
        public boolean noRenderArmor = true;

        @SerialEntry
        public boolean noRenderHunger = true;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var noShadowActionBar = Option.<Boolean>createBuilder()
                    .name(key("hudTweaks.noShadowActionBar"))
                    .description(keyD("hudTweaks.noShadowActionBar"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.hudTweaks.noShadowActionBar,
                            () -> config.hudTweaks.noShadowActionBar,
                            value -> config.hudTweaks.noShadowActionBar = (Boolean) value
                    )
                    .build();

            var noRenderBossBar = Option.<Boolean>createBuilder()
                    .name(key("hudTweaks.noRenderBossBar"))
                    .description(keyD("hudTweaks.noRenderBossBar"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.hudTweaks.noRenderBossBar,
                            () -> config.hudTweaks.noRenderBossBar,
                            value -> config.hudTweaks.noRenderBossBar = (Boolean) value
                    )
                    .build();



            var noRenderHearts = Option.<Boolean>createBuilder()
                    .name(key("hudTweaks.noRenderHearts"))
                    .description(keyD("hudTweaks.noRenderHearts"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.hudTweaks.noRenderHearts,
                            () -> config.hudTweaks.noRenderHearts,
                            value -> config.hudTweaks.noRenderHearts = (Boolean) value
                    )
                    .build();
            var noRenderArmor = Option.<Boolean>createBuilder()
                    .name(key("hudTweaks.noRenderArmor"))
                    .description(keyD("hudTweaks.noRenderArmor"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.hudTweaks.noRenderArmor,
                            () -> config.hudTweaks.noRenderArmor,
                            value -> config.hudTweaks.noRenderArmor = (Boolean) value
                    )
                    .build();

            var noRenderHunger = Option.<Boolean>createBuilder()
                    .name(key("hudTweaks.noRenderHunger"))
                    .description(keyD("hudTweaks.noRenderHunger"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.hudTweaks.noRenderHunger,
                            () -> config.hudTweaks.noRenderHunger,
                            value -> config.hudTweaks.noRenderHunger = (Boolean) value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(key("hudTweaks"))
                    .description(keyD("hudTweaks"))
                    .option(noShadowActionBar)
                    .option(noRenderBossBar)
                    .option(noRenderHearts)
                    .option(noRenderArmor)
                    .option(noRenderHunger)
                    .build();
        }
    }
}
