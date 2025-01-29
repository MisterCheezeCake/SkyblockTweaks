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
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.hud.bases.TextHUD;
import wtf.cheeze.sbt.utils.render.Colors;

import java.awt.Color;

public class SpeedHUD extends TextHUD {

    public SpeedHUD() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().speed.x,
                () -> SBTConfig.huds().speed.y,
                () -> SBTConfig.huds().speed.scale,
                () -> SBTConfig.huds().speed.anchor,
                x -> SBTConfig.huds().speed.x = x,
                y -> SBTConfig.huds().speed.y = y,
                scale -> SBTConfig.huds().speed.scale = scale,
                anchor -> SBTConfig.huds().speed.anchor = anchor
        );
        line = new SingleHudLine(
                () -> SBTConfig.huds().speed.color,
                () -> SBTConfig.huds().speed.outlineColor,
                () -> SBTConfig.huds().speed.mode,
                () -> Text.literal((SkyblockTweaks.DATA.getSpeed()+"").split("\\.")[0] + "%")
        );
    }

    @Override
    public Text getName() {
        return Text.literal("Speed HUD");
    }

    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        return (SkyblockTweaks.DATA.inSB && SBTConfig.huds().speed.enabled) || fromHudScreen;
    }



    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public DrawMode mode = DrawMode.SHADOW;

        @SerialEntry
        public int color = Colors.WHITE;

        @SerialEntry
        public int outlineColor = Colors.BLACK;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.35f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(key("speed.enabled"))
                    .description(keyD("speed.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.speed.enabled,
                            () -> config.huds.speed.enabled,
                            value -> config.huds.speed.enabled = (boolean) value
                    )
                    .build();

            var color = Option.<Color>createBuilder()
                    .name(key("speed.color"))
                    .description(keyD("speed.color"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.speed.color),
                            () ->  new Color(config.huds.speed.color),
                            value -> config.huds.speed.color = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(key("speed.outlineColor"))
                    .description(keyD("speed.outlineColor"))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.speed.mode == DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.speed.outlineColor),
                            () ->  new Color(config.huds.speed.outlineColor),
                            value -> config.huds.speed.outlineColor = value.getRGB()

                    )
                    .build();
            var mode = Option.<DrawMode>createBuilder()
                    .name(key("speed.mode"))
                    .description(keyD("speed.mode"))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.speed.mode,
                            () -> config.huds.speed.mode,
                            value -> {
                                config.huds.speed.mode = value;
                                outline.setAvailable(value == DrawMode.OUTLINE);
                            }
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(key("speed.scale"))
                    .description(keyD("speed.scale"))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.huds.speed.scale,
                            () -> config.huds.speed.scale,
                            value -> config.huds.speed.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(key("speed"))
                    .description(keyD("speed"))
                    .option(enabled)
                    .option(mode)
                    .option(color)
                    .option(outline)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }
}

