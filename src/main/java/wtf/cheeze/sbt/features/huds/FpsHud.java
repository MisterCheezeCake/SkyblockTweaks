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
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.hud.bases.TextHud;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.hud.utils.HudName;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;

import java.awt.Color;

public class FpsHud extends TextHud {
    public static final FpsHud INSTANCE = new FpsHud();

    private FpsHud() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().fps.x,
                () -> SBTConfig.huds().fps.y,
                () -> SBTConfig.huds().fps.scale,
                () -> SBTConfig.huds().fps.anchor,
                x -> SBTConfig.huds().fps.x = x,
                y -> SBTConfig.huds().fps.y = y,
                scale -> SBTConfig.huds().fps.scale = scale,
                anchor -> SBTConfig.huds().fps.anchor = anchor
        );
        line = new SingleHudLine(
                () -> SBTConfig.huds().fps.color,
                () -> SBTConfig.huds().fps.outlineColor,
                () -> SBTConfig.huds().fps.mode,
                () -> Component.literal(SBTConfig.huds().fps.reverse ?  "FPS: " + mc.getFps() : mc.getFps() + " FPS")

        );
    }

    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        return ((SkyblockData.inSB || SBTConfig.huds().fps.showOutside) && SBTConfig.huds().fps.enabled) || fromHudScreen;
    }

    @Override
    public @NotNull HudName getName() {
        return new HudName("FPS HUD", Colors.LIGHT_BLUE);
    }


    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public boolean showOutside = false;

        @SerialEntry
        public boolean reverse = false;

        @SerialEntry
        public DrawMode mode = DrawMode.SHADOW;

        @SerialEntry
        public int color = Colors.WHITE;

        @SerialEntry
        public int outlineColor = Colors.BLACK;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.55f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(key("fps.enabled"))
                    .description(keyD("fps.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.fps.enabled,
                            () -> config.huds.fps.enabled,
                            value -> config.huds.fps.enabled = value
                    )
                    .build();

            var outside = Option.<Boolean>createBuilder()
                    .name(key("fps.showOutside"))
                    .description(keyD("fps.showOutside"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.fps.showOutside,
                            () -> config.huds.fps.showOutside,
                            value -> config.huds.fps.showOutside = value
                    )
                    .build();

            var reverse = Option.<Boolean>createBuilder()
                    .name(key("fps.reverse"))
                    .description(keyD("fps.reverse"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.fps.reverse,
                            () -> config.huds.fps.reverse,
                            value -> config.huds.fps.reverse = value
                    )
                    .build();
            var color = Option.<Color>createBuilder()
                    .name(key("fps.color"))
                    .description(keyD("fps.color"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.fps.color),
                            () ->  new Color(config.huds.fps.color),
                            value -> config.huds.fps.color = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(key("fps.outlineColor"))
                    .description(keyD("fps.outlineColor"))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.fps.mode == DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.fps.outlineColor),
                            () ->  new Color(config.huds.fps.outlineColor),
                            value -> config.huds.fps.outlineColor = value.getRGB()

                    )
                    .build();
            var mode = Option.<DrawMode>createBuilder()
                    .name(key("fps.mode"))
                    .description(keyD("fps.mode"))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.fps.mode,
                            () -> config.huds.fps.mode,
                            value -> {
                                config.huds.fps.mode = value;
                                outline.setAvailable(value == DrawMode.OUTLINE);
                            }
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(key("fps.scale"))
                    .description(keyD("fps.scale"))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.huds.fps.scale,
                            () -> config.huds.fps.scale,
                            value -> config.huds.fps.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(key("fps"))
                    .description(keyD("fps"))
                    .option(enabled)
                    .option(outside)
                    .option(reverse)
                    .option(mode)
                    .option(color)
                    .option(outline)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }
}