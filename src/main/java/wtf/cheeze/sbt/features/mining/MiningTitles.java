// TODO(Ravel): Failed to fully resolve file: null
// TODO(Ravel): Failed to fully resolve file: null
package wtf.cheeze.sbt.features.mining;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.Minecraft;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.events.ChatEvents;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.text.TextUtils;

import java.awt.Color;

import static wtf.cheeze.sbt.config.categories.Mining.key;
import static wtf.cheeze.sbt.config.categories.Mining.keyD;

public class MiningTitles {
    public static void registerEvents() {
        ChatEvents.ON_GAME.register(message -> {
            if (!SBTConfig.mining().titles.goblinTitles) return;
            var content = TextUtils.removeFormatting(message.getString()).trim();
            if (content.equals("A Golden Goblin has spawned!")) {
                Minecraft.getInstance().gui.setTitle(TextUtils.withColor(
                        SBTConfig.mining().titles.goldenText,
                        SBTConfig.mining().titles.goldenColor
                ));
            } else if (content.equals("A Diamond Goblin has spawned!")) {
                Minecraft.getInstance().gui.setTitle(TextUtils.withColor(
                        SBTConfig.mining().titles.diamondText,
                        SBTConfig.mining().titles.diamondColor
                ));
            }
        });
    }

    public static class Config {
        @SerialEntry
        public boolean goblinTitles = true;

        @SerialEntry
        public String goldenText = "Golden Goblin!";

        @SerialEntry
        public int goldenColor = Colors.ORANGE;

        @SerialEntry
        public String diamondText = "Diamond Goblin!";

        @SerialEntry
        public int diamondColor = Colors.LIGHT_BLUE;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var goblinTitles = Option.<Boolean>createBuilder()
                    .name(key("titles.goblinTitles"))
                    .description(keyD("titles.goblinTitles"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.mining.titles.goblinTitles,
                            () -> config.mining.titles.goblinTitles,
                            value -> config.mining.titles.goblinTitles = value
                    )
                    .build();

            var goldenText = Option.<String>createBuilder()
                    .name(key("titles.goldenText"))
                    .description(keyD("titles.goldenText"))
                    .controller(StringControllerBuilder::create)
                    .binding(
                            defaults.mining.titles.goldenText,
                            () -> config.mining.titles.goldenText,
                            value -> config.mining.titles.goldenText = value
                    )
                    .build();

            var goldenColor = Option.<Color>createBuilder()
                    .name(key("titles.goldenColor"))
                    .description(keyD("titles.goldenColor"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.mining.titles.goldenColor),
                            () -> new Color(config.mining.titles.goldenColor),
                            value -> config.mining.titles.goldenColor = value.getRGB()
                    )
                    .build();

            var diamondText = Option.<String>createBuilder()
                    .name(key("titles.diamondText"))
                    .description(keyD("titles.diamondText"))
                    .controller(StringControllerBuilder::create)
                    .binding(
                            defaults.mining.titles.diamondText,
                            () -> config.mining.titles.diamondText,
                            value -> config.mining.titles.diamondText = value
                    )
                    .build();

            var diamondColor = Option.<Color>createBuilder()
                    .name(key("titles.diamondColor"))
                    .description(keyD("titles.diamondColor"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.mining.titles.diamondColor),
                            () -> new Color(config.mining.titles.diamondColor),
                            value -> config.mining.titles.diamondColor = value.getRGB()
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(key("titles"))
                    .description(keyD("titles.desc"))
                    .option(goblinTitles)
                    .option(goldenText)
                    .option(goldenColor)
                    .option(diamondText)
                    .option(diamondColor)
                    .collapsed(true)
                    .build();
        }
    }
}
