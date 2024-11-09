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
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.hud.bases.TextHUD;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.SkyblockUtils;

import java.awt.Color;

public class DrillFuelHUD extends TextHUD {

    public DrillFuelHUD() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().drillFuel.x,
                () -> SBTConfig.huds().drillFuel.y,
                () -> SBTConfig.huds().drillFuel.scale,
                () -> SBTConfig.huds().drillFuel.anchor,
                x -> SBTConfig.huds().drillFuel.x = (float) x,
                y -> SBTConfig.huds().drillFuel.y = (float) y,
                scale -> SBTConfig.huds().drillFuel.scale = (float) scale,
                anchor -> SBTConfig.huds().drillFuel.anchor = anchor
        );
        line = new SingleHudLine(
                () -> SBTConfig.huds().drillFuel.color,
                () -> SBTConfig.huds().drillFuel.outlineColor,
                () -> SBTConfig.huds().drillFuel.mode,
                () ->
                        Text.literal((NumberUtils.formatNumber((int) SkyblockTweaks.DATA.drillFuel, SBTConfig.huds().drillFuel.separator))
                        + "/"
                        + (SBTConfig.huds().drillFuel.abridgeSecondNumber ? NumberUtils.addKOrM((int) SkyblockTweaks.DATA.maxDrillFuel, SBTConfig.huds().drillFuel.separator) : NumberUtils.formatNumber((int) SkyblockTweaks.DATA.maxDrillFuel, SBTConfig.huds().drillFuel.separator)))
        );
    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyblockTweaks.DATA.inSB && SBTConfig.huds().drillFuel.enabled) && SkyblockUtils.isThePlayerHoldingADrill() || fromHudScreen) return true;
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
        public DrawMode mode = DrawMode.SHADOW;

        @SerialEntry
        public int color = Colors.GREEN;

        @SerialEntry
        public int outlineColor = Colors.BLACK;

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
                    .name(key("drillFuel.enabled"))
                    .description(keyD("drillFuel.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.drillFuel.enabled,
                            () -> config.huds.drillFuel.enabled,
                            value -> config.huds.drillFuel.enabled = (Boolean) value
                    )
                    .build();
            var secondNo = Option.<Boolean>createBuilder()
                    .name(key("drillFuel.abridgeSecondNumber"))
                    .description(keyD("drillFuel.abridgeSecondNumber"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.drillFuel.abridgeSecondNumber,
                            () -> config.huds.drillFuel.abridgeSecondNumber,
                            value -> config.huds.drillFuel.abridgeSecondNumber = (Boolean) value
                    )
                    .build();
            var color = Option.<Color>createBuilder()
                    .name(key("drillFuel.color"))
                    .description(keyD("drillFuel.color"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.drillFuel.color),
                            () ->  new Color(config.huds.drillFuel.color),
                            value -> config.huds.drillFuel.color = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(key("drillFuel.outlineColor"))
                    .description(keyD("drillFuel.outlineColor"))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.drillFuel.mode == DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.drillFuel.outlineColor),
                            () ->  new Color(config.huds.drillFuel.outlineColor),
                            value -> config.huds.drillFuel.outlineColor = value.getRGB()
                    )
                    .build();
            var mode = Option.<DrawMode>createBuilder()
                    .name(key("drillFuel.mode"))
                    .description(keyD("drillFuel.mode"))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.drillFuel.mode,
                            () -> config.huds.drillFuel.mode,
                            value -> {
                                config.huds.drillFuel.mode = value;
                                if (value == DrawMode.OUTLINE) outline.setAvailable(true);
                                else outline.setAvailable(false);
                            }
                    )
                    .build();
            var separator = Option.<String>createBuilder()
                    .name(key("drillFuel.separator"))
                    .description(keyD("drillFuel.separator"))
                    .controller(StringControllerBuilder::create)
                    .binding(
                            defaults.huds.drillFuel.separator,
                            () -> config.huds.drillFuel.separator,
                            value -> config.huds.drillFuel.separator = value
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(key("drillFuel.scale"))
                    .description(keyD("drillFuel.scale"))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.huds.drillFuel.scale,
                            () -> config.huds.drillFuel.scale,
                            value -> config.huds.drillFuel.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(key("drillFuel"))
                    .description(keyD("drillFuel"))
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


