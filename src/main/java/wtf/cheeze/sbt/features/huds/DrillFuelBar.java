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
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.hud.bases.BarHUD;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.SkyblockUtils;

import java.awt.Color;

public class DrillFuelBar extends BarHUD {

    public DrillFuelBar() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().drillFuelBar.x,
                () -> SBTConfig.huds().drillFuelBar.y,
                () -> SBTConfig.huds().drillFuelBar.scale,
                () -> SBTConfig.huds().drillFuelBar.anchor,
                () -> SBTConfig.huds().drillFuelBar.color,
                () -> SkyblockTweaks.DATA.drillFuel / SkyblockTweaks.DATA.maxDrillFuel,
                x -> SBTConfig.huds().drillFuelBar.x = x,
                y -> SBTConfig.huds().drillFuelBar.y = y,
                scale -> SBTConfig.huds().drillFuelBar.scale = scale,
                anchor -> SBTConfig.huds().drillFuelBar.anchor = anchor

        );
    }

    @Override
    public Text getName() {
        return TextUtils.withColor("Drill Fuel Bar", Colors.DARK_GREEN);
    }

    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        return (SkyblockTweaks.DATA.inSB && SBTConfig.huds().drillFuelBar.enabled) && SkyblockUtils.isThePlayerHoldingADrill() || fromHudScreen;
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.50f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        @SerialEntry
        public int color = Colors.GREEN;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(key("drillFuelBar.enabled"))
                    .description(keyD("drillFuelBar.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.drillFuelBar.enabled,
                            () -> config.huds.drillFuelBar.enabled,
                            value -> config.huds.drillFuelBar.enabled = (boolean) value
                    )
                    .build();

            var color = Option.<Color>createBuilder()
                    .name(key("drillFuelBar.color"))
                    .description(keyD("drillFuelBar.color"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.drillFuelBar.color),
                            () ->  new Color(config.huds.drillFuelBar.color),
                            value -> config.huds.drillFuelBar.color = value.getRGB()

                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(key("drillFuelBar.scale"))
                    .description(keyD("drillFuelBar.scale"))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.huds.drillFuelBar.scale,
                            () -> config.huds.drillFuelBar.scale,
                            value -> config.huds.drillFuelBar.scale = value
                    )
                    .build();
            return OptionGroup.createBuilder()
                    .name(key("drillFuelBar"))
                    .description(keyD("drillFuelBar"))
                    .option(enabled)
                    .option(color)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }
}
