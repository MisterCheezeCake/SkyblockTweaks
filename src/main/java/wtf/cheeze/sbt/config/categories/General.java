package wtf.cheeze.sbt.config.categories;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SkyblockTweaksConfig;
import wtf.cheeze.sbt.features.BrewingStandOverlay;
import wtf.cheeze.sbt.features.MenuHighlights;
import wtf.cheeze.sbt.features.chat.PartyFeatures;
import wtf.cheeze.sbt.utils.Version;
import wtf.cheeze.sbt.utils.actionbar.ActionBarTransformer;

public class General {
    public static ConfigCategory getCategory(ConfigImpl defaults, ConfigImpl config) {
        return ConfigCategory.createBuilder()
                .name(Text.literal("General"))
                .tooltip(Text.literal("General settings for SkyblockTweaks"))
                .option(Version.getStreamOption(defaults, config))
                .group(InventoryTweaks.getGroup(defaults, config))
                .group(MenuHighlights.Config.getGroup(defaults, config))
                .group(BrewingStandOverlay.Config.getGroup(defaults, config))
                .group(PartyFeatures.Config.getGroup(defaults, config))
                .option(PartyFeatures.Config.getBlackList(defaults, config))
                .group(HudTweaks.getGroup(defaults, config))
                .group(ActionBarTransformer.Config.getGroup(defaults, config))
                .build();
    }

    // These are subclassed because their features live in mixins and so can't control their own config
    public static class InventoryTweaks {
        @SerialEntry
        public boolean redirectRecipeBook = true;

        @SerialEntry
        public boolean noRenderPotionHud = true;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var noRenderPotionHud = Option.<Boolean>createBuilder()
                    .name(Text.literal("Disable Potion HUD"))
                    .description(OptionDescription.of(Text.literal("Disables rendering of the potion HUD in your inventory")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.inventory.noRenderPotionHud,
                            () -> config.inventory.noRenderPotionHud,
                            value -> config.inventory.noRenderPotionHud = (Boolean) value
                    )
                    .build();
            var redirectRecipeBook = Option.<Boolean>createBuilder()
                    .name(Text.literal("Redirect Recipe Book Clicks"))
                    .description(OptionDescription.of(Text.literal("Redirects the recipe book button to the SkyBlock recipe book")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.inventory.redirectRecipeBook,
                            () -> config.inventory.redirectRecipeBook,
                            value -> config.inventory.redirectRecipeBook = (Boolean) value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(Text.literal("Inventory Tweaks"))
                    .description(OptionDescription.of(Text.literal("Various tweaks to the inventory screen")))
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
                    .name(Text.literal("Disable Action Bar Shadow"))
                    .description(OptionDescription.of(Text.literal("Removes the shadow from the action bar")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.hudTweaks.noShadowActionBar,
                            () -> config.hudTweaks.noShadowActionBar,
                            value -> config.hudTweaks.noShadowActionBar = (Boolean) value
                    )
                    .build();

            var noRenderBossBar = Option.<Boolean>createBuilder()
                    .name(Text.literal("Disable Boss Bar"))
                    .description(OptionDescription.of(Text.literal("Disables rendering of boss bars")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.hudTweaks.noRenderBossBar,
                            () -> config.hudTweaks.noRenderBossBar,
                            value -> config.hudTweaks.noRenderBossBar = (Boolean) value
                    )
                    .build();



            var noRenderHearts = Option.<Boolean>createBuilder()
                    .name(Text.literal("Disable Hearts"))
                    .description(OptionDescription.of(Text.literal("Disables rendering of health hearts ")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.hudTweaks.noRenderHearts,
                            () -> config.hudTweaks.noRenderHearts,
                            value -> config.hudTweaks.noRenderHearts = (Boolean) value
                    )
                    .build();
            var noRenderArmor = Option.<Boolean>createBuilder()
                    .name(Text.literal("Disable Armor Bar"))
                    .description(OptionDescription.of(Text.literal("Disables rendering of the armor bar")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.hudTweaks.noRenderArmor,
                            () -> config.hudTweaks.noRenderArmor,
                            value -> config.hudTweaks.noRenderArmor = (Boolean) value
                    )
                    .build();

            var noRenderHunger = Option.<Boolean>createBuilder()
                    .name(Text.literal("Disable Hunger Bar"))
                    .description(OptionDescription.of(Text.literal("Disables rendering of the hunger bar")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.hudTweaks.noRenderHunger,
                            () -> config.hudTweaks.noRenderHunger,
                            value -> config.hudTweaks.noRenderHunger = (Boolean) value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(Text.literal("HUD Tweaks"))
                    .description(OptionDescription.of(Text.literal("Various tweaks to Minecraft's HUD")))
                    .option(noShadowActionBar)
                    .option(noRenderBossBar)
                    .option(noRenderHearts)
                    .option(noRenderArmor)
                    .option(noRenderHunger)
                    .build();
        }
    }
}
