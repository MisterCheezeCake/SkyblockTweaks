package wtf.cheeze.sbt.features.huds;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SkyblockTweaksConfig;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.hud.AbstractTickerHUD;
import wtf.cheeze.sbt.utils.hud.HudInformation;

public class TickerHUD extends AbstractTickerHUD {

    public TickerHUD() {
        INFO = new HudInformation(
                () -> SkyblockTweaks.CONFIG.config.huds.ticker.x,
                () -> SkyblockTweaks.CONFIG.config.huds.ticker.y,
                () -> SkyblockTweaks.CONFIG.config.huds.ticker.scale,
                () -> SkyblockTweaks.CONFIG.config.huds.ticker.anchor,
                x -> SkyblockTweaks.CONFIG.config.huds.ticker.x = x,
                y -> SkyblockTweaks.CONFIG.config.huds.ticker.y = y,
                scale -> SkyblockTweaks.CONFIG.config.huds.ticker.scale = scale,
                anchor -> SkyblockTweaks.CONFIG.config.huds.ticker.anchor = anchor
        );
    }


    @Override
    public String getName() {
        return TextUtils.SECTION + "eTicker/Charges Hud";
    }

    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyblockTweaks.DATA.inSB && SkyblockTweaks.CONFIG.config.huds.ticker.enabled && SkyblockTweaks.DATA.tickerActive) || fromHudScreen) return true;
        return false;
    }


    @Override
    public int getMax(boolean fromHudScreen) {
        if (!fromHudScreen) return SkyblockTweaks.DATA.maxTickers;
        if (SkyblockTweaks.DATA.tickerActive) return SkyblockTweaks.DATA.maxTickers;
        return 5;
    }

    @Override
    public int getUsable(boolean fromHudScreen) {
        if (!fromHudScreen) return SkyblockTweaks.DATA.tickers;
        if (SkyblockTweaks.DATA.tickerActive) return SkyblockTweaks.DATA.tickers;
        return 5;

    }

    @Override
    public int getMax() {
        return getMax(false);
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.9f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Text.literal("Enable Ticker HUD"))
                    .description(OptionDescription.of(Text.literal("Enables the Ticker/Charges HUD")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.ticker.enabled,
                            () -> config.huds.ticker.enabled,
                            value -> config.huds.ticker.enabled = (Boolean) value
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Text.literal("Ticker HUD Scale"))
                    .description(OptionDescription.of(Text.literal("The scale of the Mana Bar")))
                    .controller(SkyblockTweaksConfig::generateScaleController)
                    .binding(
                            defaults.huds.ticker.scale,
                            () -> config.huds.ticker.scale,
                            value -> config.huds.ticker.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(Text.literal("Ticker/Charges HUD"))
                    .description(OptionDescription.of(Text.literal("Settings for the Ticker/Charges HUD")))
                    .option(enabled)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }

}
