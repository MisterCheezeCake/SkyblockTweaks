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
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SkyblockTweaksConfig;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.hud.HudInformation;
import wtf.cheeze.sbt.utils.hud.TextHUD;
import wtf.cheeze.sbt.utils.hud.HudLine;

import java.awt.Color;

public class DrillFuelHUD extends TextHUD {

    public DrillFuelHUD() {
        INFO = new HudInformation(
                () -> SkyblockTweaks.CONFIG.config.huds.drillFuel.x,
                () -> SkyblockTweaks.CONFIG.config.huds.drillFuel.y,
                () -> SkyblockTweaks.CONFIG.config.huds.drillFuel.scale,
                () -> SkyblockTweaks.CONFIG.config.huds.drillFuel.anchor,
                x -> SkyblockTweaks.CONFIG.config.huds.drillFuel.x = (float) x,
                y -> SkyblockTweaks.CONFIG.config.huds.drillFuel.y = (float) y,
                scale -> SkyblockTweaks.CONFIG.config.huds.drillFuel.scale = (float) scale,
                anchor -> SkyblockTweaks.CONFIG.config.huds.drillFuel.anchor = anchor
        );
        line = new HudLine(
                () -> SkyblockTweaks.CONFIG.config.huds.drillFuel.color,
                () -> SkyblockTweaks.CONFIG.config.huds.drillFuel.outlineColor,
                () -> SkyblockTweaks.CONFIG.config.huds.drillFuel.mode,
                () ->
                        Text.literal((NumberUtils.formatNumber((int) SkyblockTweaks.DATA.drillFuel, SkyblockTweaks.CONFIG.config.huds.drillFuel.separator))
                        + "/"
                        + (SkyblockTweaks.CONFIG.config.huds.drillFuel.abridgeSecondNumber ? NumberUtils.addKOrM((int) SkyblockTweaks.DATA.maxDrillFuel, SkyblockTweaks.CONFIG.config.huds.drillFuel.separator) : NumberUtils.formatNumber((int) SkyblockTweaks.DATA.maxDrillFuel, SkyblockTweaks.CONFIG.config.huds.drillFuel.separator)))
        );
    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyblockTweaks.DATA.inSB && SkyblockTweaks.CONFIG.config.huds.drillFuel.enabled) && SkyblockTweaks.DATA.isThePlayerHoldingADrill() || fromHudScreen) return true;
        return false;
    }


    @Override
    public String getName() {
        return TextUtils.SECTION + "2Drill Fuel HUD";
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public boolean abridgeSecondNumber = false;

        @SerialEntry
        public HudLine.DrawMode mode = HudLine.DrawMode.SHADOW;

        @SerialEntry
        public int color = 43520;

        @SerialEntry
        public int outlineColor = 0x000000;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.45f;

        @SerialEntry
        public String separator = ",";

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Text.literal("Enable Drill Fuel HUD"))
                    .description(OptionDescription.of(Text.literal("Enables the Drill Fuel HUD")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.drillFuel.enabled,
                            () -> config.huds.drillFuel.enabled,
                            value -> config.huds.drillFuel.enabled = (Boolean) value
                    )
                    .build();
            var secondNo = Option.<Boolean>createBuilder()
                    .name(Text.literal("Abridge Max Fuel"))
                    .description(OptionDescription.of(Text.literal("Replaces thousands with k in the max fuel in the Drill Fuel HUD")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.drillFuel.abridgeSecondNumber,
                            () -> config.huds.drillFuel.abridgeSecondNumber,
                            value -> config.huds.drillFuel.abridgeSecondNumber = (Boolean) value
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
            var outline = Option.<Color>createBuilder()
                    .name(Text.literal("Drill Fuel HUD Outline Color"))
                    .description(OptionDescription.of(Text.literal("The outline color of the Drill Fuel HUD")))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.drillFuel.mode == HudLine.DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.drillFuel.outlineColor),
                            () ->  new Color(config.huds.drillFuel.outlineColor),
                            value -> config.huds.drillFuel.outlineColor = value.getRGB()
                    )
                    .build();
            var mode = Option.<HudLine.DrawMode>createBuilder()
                    .name(Text.literal("Drill Fuel HUD Mode"))
                    .description(OptionDescription.of(Text.literal("The draw mode of the Drill Fuel HUD. Pure will render without shadow, Shadow will render with a shadow, and Outline will render with an outline\n§4Warning: §cOutline mode is still a work in progress and can cause annoying visual bugs in menus.")))
                    .controller(SkyblockTweaksConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.drillFuel.mode,
                            () -> config.huds.drillFuel.mode,
                            value -> {
                                config.huds.drillFuel.mode = value;
                                if (value == HudLine.DrawMode.OUTLINE) outline.setAvailable(true);
                                else outline.setAvailable(false);
                            }
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
                    .controller(SkyblockTweaksConfig::generateScaleController)
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
                    .option(mode)
                    .option(color)
                    .option(outline)
                    .option(separator)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }


}


