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
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyBlockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SkyBlockTweaksConfig;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.hud.HudInformation;
import wtf.cheeze.sbt.utils.hud.TextHUD;

import java.awt.Color;

public class DrillFuelHUD extends TextHUD {

    public DrillFuelHUD() {
        INFO = new HudInformation(
                () -> SkyBlockTweaks.CONFIG.config.huds.drillFuel.x,
                () -> SkyBlockTweaks.CONFIG.config.huds.drillFuel.y,
                () -> SkyBlockTweaks.CONFIG.config.huds.drillFuel.scale,
                () -> SkyBlockTweaks.CONFIG.config.huds.drillFuel.shadow,
                () -> SkyBlockTweaks.CONFIG.config.huds.drillFuel.color,
                x -> SkyBlockTweaks.CONFIG.config.huds.drillFuel.x = (float) x,
                y -> SkyBlockTweaks.CONFIG.config.huds.drillFuel.y = (float) y,
                scale -> SkyBlockTweaks.CONFIG.config.huds.drillFuel.scale = (float) scale
        );
    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyBlockTweaks.DATA.inSB && SkyBlockTweaks.CONFIG.config.huds.drillFuel.enabled) && SkyBlockTweaks.DATA.isThePlayerHoldingADrill() || fromHudScreen) return true;
        return false;
    }
    @Override
    public String getText() {
       var first = TextUtils.formatNumber((int) SkyBlockTweaks.DATA.drillFuel, SkyBlockTweaks.CONFIG.config.huds.drillFuel.separator);
         var second = SkyBlockTweaks.CONFIG.config.huds.drillFuel.abridgeSecondNumber ? TextUtils.addKOrM((int) SkyBlockTweaks.DATA.maxDrillFuel, SkyBlockTweaks.CONFIG.config.huds.drillFuel.separator) : TextUtils.formatNumber((int) SkyBlockTweaks.DATA.maxDrillFuel, SkyBlockTweaks.CONFIG.config.huds.drillFuel.separator);
        return first + "/" + second;
    }

    @Override
    public String getName() {
        return "2Drill Fuel HUD";
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public boolean abridgeSecondNumber = false;

        @SerialEntry
        public boolean shadow = true;

        @SerialEntry
        public int color = 43520;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.45f;

        @SerialEntry
        public String separator = ",";

        @SerialEntry
        public float scale = 1.0f;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Text.literal("Enable Drill Fuel HUD"))
                    .description(OptionDescription.of(Text.literal("Enables the Drill Fuel HUD")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.drillFuel.enabled,
                            () -> config.huds.drillFuel.enabled,
                            value -> config.huds.drillFuel.enabled = (Boolean) value
                    )
                    .build();
            var secondNo = Option.<Boolean>createBuilder()
                    .name(Text.literal("Abridge Max Fuel"))
                    .description(OptionDescription.of(Text.literal("Replaces thousands with k in the max fuel in the Drill Fuel HUD")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.drillFuel.abridgeSecondNumber,
                            () -> config.huds.drillFuel.abridgeSecondNumber,
                            value -> config.huds.drillFuel.abridgeSecondNumber = (Boolean) value
                    )
                    .build();
            var shadow = Option.<Boolean>createBuilder()
                    .name(Text.literal("Drill Fuel HUD Shadow"))
                    .description(OptionDescription.of(Text.literal("Enables the shadow for the Drill Fuel HUD")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.drillFuel.shadow,
                            () -> config.huds.drillFuel.shadow,
                            value -> config.huds.drillFuel.shadow = (Boolean) value
                    )
                    .build();

            var color = Option.<Color>createBuilder()
                    .name(Text.literal("Drill Fuel HUD Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Drill Fuel HUD")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.drillFuel.color),
                            () ->  new Color(config.huds.drillFuel.color),
                            value -> config.huds.drillFuel.color = value.getRGB()

                    )
                    .build();
            var separator = Option.<String>createBuilder()
                    .name(Text.literal("Drill Fuel HUD Separator"))
                    .description(OptionDescription.of(Text.literal("The separator for the Drill Fuel HUD")))
                    .controller(StringControllerBuilder::create)
                    .binding(
                            defaults.huds.drillFuel.separator,
                            () -> config.huds.drillFuel.separator,
                            value -> config.huds.drillFuel.separator = value
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Text.literal("Drill Fuel HUD Scale"))
                    .description(OptionDescription.of(Text.literal("The scale of the Drill Fuel HUD")))
                    .controller(SkyBlockTweaksConfig::generateScaleController)
                    .binding(
                            defaults.huds.drillFuel.scale,
                            () -> config.huds.drillFuel.scale,
                            value -> config.huds.drillFuel.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(Text.literal("Drill Fuel HUD"))
                    .description(OptionDescription.of(Text.literal("Settings for the Drill Fuel HUD")))
                    .option(enabled)
                    .option(secondNo)
                    .option(shadow)
                    .option(color)
                    .option(separator)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }


}


