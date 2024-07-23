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
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyBlockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SkyBlockTweaksConfig;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.hud.BarHUD;
import wtf.cheeze.sbt.utils.hud.HudInformation;

import java.awt.Color;

public class DrillFuelBar extends BarHUD {

    public DrillFuelBar() {
        INFO = new HudInformation(
                () -> SkyBlockTweaks.CONFIG.config.huds.drillFuelBar.x,
                () -> SkyBlockTweaks.CONFIG.config.huds.drillFuelBar.y,
                () -> SkyBlockTweaks.CONFIG.config.huds.drillFuelBar.scale,
                () -> SkyBlockTweaks.CONFIG.config.huds.drillFuelBar.color,
                () -> SkyBlockTweaks.DATA.maxDrillFuel,
                () -> SkyBlockTweaks.DATA.drillFuel,
                x -> SkyBlockTweaks.CONFIG.config.huds.drillFuelBar.x = (float) x,
                y -> SkyBlockTweaks.CONFIG.config.huds.drillFuelBar.y = (float) y,
                scale -> SkyBlockTweaks.CONFIG.config.huds.drillFuelBar.scale = (float) scale

        );
    }

    @Override
    public String getName() {
        return TextUtils.SECTION +  "2Drill Fuel Bar Bar";
    }

    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyBlockTweaks.DATA.inSB && SkyBlockTweaks.CONFIG.config.huds.drillFuelBar.enabled) && SkyBlockTweaks.DATA.isThePlayerHoldingADrill() || fromHudScreen) return true;
        return false;
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
        public int color = 43520;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Text.literal("Enable Drill Fuel Bar"))
                    .description(OptionDescription.of(Text.literal("Enables the Drill Fuel Bar")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.drillFuelBar.enabled,
                            () -> config.huds.drillFuelBar.enabled,
                            value -> config.huds.drillFuelBar.enabled = (Boolean) value
                    )
                    .build();

            var color = Option.<Color>createBuilder()
                    .name(Text.literal("Drill Fuel Bar Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Drill Fuel Bar")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.drillFuelBar.color),
                            () ->  new Color(config.huds.drillFuelBar.color),
                            value -> config.huds.drillFuelBar.color = value.getRGB()

                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Text.literal("Drill Fuel Bar Scale"))
                    .description(OptionDescription.of(Text.literal("The scale of the Drill Fuel Bar")))
                    .controller(SkyBlockTweaksConfig::generateScaleController)
                    .binding(
                            defaults.huds.drillFuelBar.scale,
                            () -> config.huds.drillFuelBar.scale,
                            value -> config.huds.drillFuelBar.scale = value
                    )
                    .build();
            return OptionGroup.createBuilder()
                    .name(Text.literal("Drill Fuel Bar"))
                    .description(OptionDescription.of(Text.literal("Settings for the Drill Fuel Bar")))
                    .option(enabled)
                    .option(color)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }
}
