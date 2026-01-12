package wtf.cheeze.sbt.features.huds;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.hud.bases.TextHud;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.hud.icon.Icons;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.hud.utils.HudName;
import wtf.cheeze.sbt.utils.DataUtils;
import wtf.cheeze.sbt.utils.enums.Location;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;
import wtf.cheeze.sbt.utils.text.TextUtils;

import java.awt.*;

public class SecretsHud extends TextHud {

    public static final SecretsHud INSTANCE = new SecretsHud();

    private SecretsHud() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().secrets.x,
                () -> SBTConfig.huds().secrets.y,
                () -> SBTConfig.huds().secrets.scale,
                () -> SBTConfig.huds().secrets.anchor,
                x -> SBTConfig.huds().secrets.x = x,
                y -> SBTConfig.huds().secrets.y = y,
                scale -> SBTConfig.huds().secrets.scale = scale,
                anchor -> SBTConfig.huds().secrets.anchor = anchor
        );
        line = new SingleHudLine(
                DataUtils.ALWAYS_WHITE,
                () -> SBTConfig.huds().time.outlineColor,
                () -> SBTConfig.huds().time.mode,
                () -> SBTConfig.huds().secrets.label ?
                        TextUtils.join(
                                TextUtils.withColor("Secrets: ", SBTConfig.huds().secrets.labelColor),
                                getNumText()
                        ) : getNumText(),
                () -> Icons.CHEST,
                () -> SBTConfig.huds().secrets.icon
        );
    }

    private Component getNumText() {
        return TextUtils.withColor(SkyblockData.Stats.secretsFound + "/" + SkyblockData.Stats.secretsTotal, SBTConfig.huds().secrets.numberColor);
    }


    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        return SkyblockData.location == Location.DUNGEON && (SBTConfig.huds().secrets.enabled|| fromHudScreen);
    }


    @Override
    public @NotNull HudName getName() {
        return new HudName("Secret HUD", "Secrets", Colors.LIGHT_BLUE);
    }

    public static class Config {

        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public DrawMode mode = DrawMode.SHADOW;
        
        @SerialEntry
        public boolean label = true;

        @SerialEntry
        
        public boolean icon = true;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0.15f;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.35f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public int labelColor = Colors.GRAY;

        @SerialEntry
        public int numberColor = Colors.YELLOW;

        
        @SerialEntry
        public int outlineColor = Colors.BLACK;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(key("secrets.enabled"))
                    .description(keyD("secrets.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.secrets.enabled,
                            () -> config.huds.secrets.enabled,
                            value -> config.huds.secrets.enabled = value
                    )
                    .build();

            var labelColor = Option.<Color>createBuilder()
                    .name(key("secrets.labelColor"))
                    .description(keyD("secrets.labelColor"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.secrets.labelColor),
                            () ->  new Color(config.huds.secrets.labelColor),
                            value -> config.huds.secrets.labelColor = value.getRGB()

                    )
                    .build();
            var numberColor = Option.<Color>createBuilder()
                    .name(key("secrets.numberColor"))
                    .description(keyD("secrets.numberColor"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.secrets.numberColor),
                            () ->  new Color(config.huds.secrets.numberColor),
                            value -> config.huds.secrets.numberColor = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(key("secrets.outlineColor"))
                    .description(keyD("secrets.outlineColor"))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.secrets.mode == DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.secrets.outlineColor),
                            () ->  new Color(config.huds.secrets.outlineColor),
                            value -> config.huds.secrets.outlineColor = value.getRGB()

                    )
                    .build();
            var mode = Option.<DrawMode>createBuilder()
                    .name(key("secrets.mode"))
                    .description(keyD("secrets.mode"))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.secrets.mode,
                            () -> config.huds.secrets.mode,
                            value -> {
                                config.huds.secrets.mode = value;
                                outline.setAvailable(value == DrawMode.OUTLINE);
                            }
                    )
                    .build();
            var icon = Option.<Boolean>createBuilder()
                    .name(key("secrets.icon"))
                    .description(keyD("secrets.icon"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.secrets.icon,
                            () -> config.huds.secrets.icon,
                            value -> config.huds.secrets.icon = value
                    )
                    .build();

            var label = Option.<Boolean>createBuilder()
                    .name(key("secrets.label"))
                    .description(keyD("secrets.label"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.secrets.label,
                            () -> config.huds.secrets.label,
                            value -> config.huds.secrets.label = value
                    )
                    .build();

            var scale = Option.<Float>createBuilder()
                    .name(key("secrets.scale"))
                    .description(keyD("secrets.scale"))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.huds.secrets.scale,
                            () -> config.huds.secrets.scale,
                            value -> config.huds.secrets.scale = value
                    )
                    .build();
            return OptionGroup.createBuilder()
                    .name(key("secrets"))
                    .description(keyD("secrets"))
                    .option(enabled)
                    .option(label)
                    .option(icon)
                    .option(numberColor)
                    .option(labelColor)
                    .option(mode)
                    .option(outline)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }

    }
}
