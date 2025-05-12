/*
 * Copyright (C) 2024 MisterCheezeCake
 *
 * This file is part of SkyblockTweaks.
 *
 * SkyblockTweaks is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * SkyblockTweaks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SkyblockTweaks. If not, see <https://www.gnu.org/licenses/>.
 */
package wtf.cheeze.sbt.features.huds;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import org.jetbrains.annotations.NotNull;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.hud.utils.HudName;
import wtf.cheeze.sbt.hud.bases.AbstractTickerHud;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;

public class TickerHud extends AbstractTickerHud {

    public static final TickerHud INSTANCE = new TickerHud();

    private TickerHud() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().ticker.x,
                () -> SBTConfig.huds().ticker.y,
                () -> SBTConfig.huds().ticker.scale,
                () -> SBTConfig.huds().ticker.anchor,
                x -> SBTConfig.huds().ticker.x = x,
                y -> SBTConfig.huds().ticker.y = y,
                scale -> SBTConfig.huds().ticker.scale = scale,
                anchor -> SBTConfig.huds().ticker.anchor = anchor
        );
    }


    @Override
    public @NotNull HudName getName() {
        return new HudName("Ticker/Charges HUD", "Ticker HUD", Colors.YELLOW);
    }

    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        return (SkyblockData.inSB && SBTConfig.huds().ticker.enabled && SkyblockData.Stats.tickerActive) || fromHudScreen;
    }


    @Override
    public int getMax(boolean fromHudScreen) {
        if (!fromHudScreen) return SkyblockData.Stats.maxTickers;
        if (SkyblockData.Stats.tickerActive) return SkyblockData.Stats.maxTickers;
        return 5;
    }

    @Override
    public int getUsable(boolean fromHudScreen) {
        if (!fromHudScreen) return SkyblockData.Stats.tickers;
        if (SkyblockData.Stats.tickerActive) return SkyblockData.Stats.tickers;
        return 5;

    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0.1f;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.1f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(key("ticker.enabled"))
                    .description(keyD("ticker.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.ticker.enabled,
                            () -> config.huds.ticker.enabled,
                            value -> config.huds.ticker.enabled = value
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(key("ticker.scale"))
                    .description(keyD("ticker.scale"))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.huds.ticker.scale,
                            () -> config.huds.ticker.scale,
                            value -> config.huds.ticker.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(key("ticker"))
                    .description(keyD("ticker"))
                    .option(enabled)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }

}
