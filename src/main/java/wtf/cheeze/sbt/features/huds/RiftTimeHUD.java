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
import wtf.cheeze.sbt.hud.bases.TextHUD;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.enums.Location;
import wtf.cheeze.sbt.utils.render.Colors;

import java.awt.*;

public class RiftTimeHUD extends TextHUD {

    public RiftTimeHUD() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().riftTime.x,
                () -> SBTConfig.huds().riftTime.y,
                () -> SBTConfig.huds().riftTime.scale,
                () -> SBTConfig.huds().riftTime.anchor,
                x -> SBTConfig.huds().riftTime.x = (float) x,
                y -> SBTConfig.huds().riftTime.y = (float) y,
                scale -> SBTConfig.huds().riftTime.scale = (float) scale,
                anchor -> SBTConfig.huds().riftTime.anchor = anchor
        );
        line = new SingleHudLine(
                () -> SkyblockTweaks.DATA.riftTicking ? SBTConfig.huds().riftTime.color : SBTConfig.huds().riftTime.inactiveColor,
                () -> SBTConfig.huds().riftTime.outlineColor,
                () -> SBTConfig.huds().riftTime.mode,
                () -> Text.literal(NumberUtils.toDuration(SkyblockTweaks.DATA.riftSeconds) + (SBTConfig.huds().riftTime.icon ? "ф" : "") + (SBTConfig.huds().riftTime.showLeftText ? " Left" : ""))
        );

    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyblockTweaks.DATA.location == Location.RIFT && SBTConfig.huds().riftTime.enabled || fromHudScreen)) return true;
        return false;
    }

    @Override
    public String getName() {
        return TextUtils.SECTION +  "3Rift Time HUD";
    }


    public static class Config {

        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public DrawMode mode = DrawMode.SHADOW;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0.1f;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public int color = Colors.LIME;

        @SerialEntry
        public int inactiveColor = Colors.GRAY;

        @SerialEntry
        public int outlineColor = Colors.BLACK;

        @SerialEntry
        public boolean icon = true;

        @SerialEntry
        public boolean showLeftText = true;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(key("riftTime.enabled"))
                    .description(keyD("riftTime.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.riftTime.enabled,
                            () -> config.huds.riftTime.enabled,
                            value -> config.huds.riftTime.enabled = (Boolean) value
                    )
                    .build();

            var color = Option.<Color>createBuilder()
                    .name(key("riftTime.color"))
                    .description(keyD("riftTime.color"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.riftTime.color),
                            () ->  new Color(config.huds.riftTime.color),
                            value -> config.huds.riftTime.color = value.getRGB()

                    )
                    .build();
            var inactiveColor = Option.<Color>createBuilder()
                    .name(key("riftTime.inactiveColor"))
                    .description(keyD("riftTime.inactiveColor"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.riftTime.inactiveColor),
                            () ->  new Color(config.huds.riftTime.inactiveColor),
                            value -> config.huds.riftTime.inactiveColor = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(key("riftTime.outlineColor"))
                    .description(keyD("riftTime.outlineColor"))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.riftTime.mode == DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.riftTime.outlineColor),
                            () ->  new Color(config.huds.riftTime.outlineColor),
                            value -> config.huds.riftTime.outlineColor = value.getRGB()

                    )
                    .build();
            var mode = Option.<DrawMode>createBuilder()
                    .name(key("riftTime.mode"))
                    .description(keyD("riftTime.mode"))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.riftTime.mode,
                            () -> config.huds.riftTime.mode,
                            value -> {
                                config.huds.riftTime.mode = value;
                                if (value == DrawMode.OUTLINE) outline.setAvailable(true);
                                else outline.setAvailable(false);
                            }
                    )
                    .build();
            var icon = Option.<Boolean>createBuilder()
                    .name(key("riftTime.icon"))
                    .description(keyD("riftTime.icon"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.riftTime.icon,
                            () -> config.huds.riftTime.icon,
                            value -> config.huds.riftTime.icon = (Boolean) value
                    )
                    .build();

            var showLeftText = Option.<Boolean>createBuilder()
                    .name(key("riftTime.showLeftText"))
                    .description(keyD("riftTime.showLeftText"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.riftTime.showLeftText,
                            () -> config.huds.riftTime.showLeftText,
                            value -> config.huds.riftTime.showLeftText = (Boolean) value
                    )
                    .build();

            var scale = Option.<Float>createBuilder()
                    .name(key("riftTime.scale"))
                    .description(keyD("riftTime.scale"))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.huds.riftTime.scale,
                            () -> config.huds.riftTime.scale,
                            value -> config.huds.riftTime.scale = value
                    )
                    .build();
            return OptionGroup.createBuilder()
                    .name(key("riftTime"))
                    .description(keyD("riftTime"))
                    .option(enabled)
                    .option(showLeftText)
                    .option(mode)
                    .option(color)
                    .option(outline)
                    .option(icon)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }

    }
}
