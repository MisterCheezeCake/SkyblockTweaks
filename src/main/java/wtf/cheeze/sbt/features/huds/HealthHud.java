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
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.hud.utils.HudName;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.hud.bases.TextHud;
import wtf.cheeze.sbt.utils.enums.Location;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;

import java.awt.Color;

public class HealthHud extends TextHud {
    public static final HealthHud INSTANCE = new HealthHud();

    private HealthHud() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().health.x,
                () -> SBTConfig.huds().health.y,
                () -> SBTConfig.huds().health.scale,
                () -> SBTConfig.huds().health.anchor,
                x -> SBTConfig.huds().health.x = x,
                y -> SBTConfig.huds().health.y = y,
                scale -> SBTConfig.huds().health.scale = scale,
                anchor -> SBTConfig.huds().health.anchor = anchor
        );
        line = new SingleHudLine(
                () -> SkyblockData.Stats.health > SkyblockData.Stats.maxHealth ? SBTConfig.huds().health.colorAbsorption : SBTConfig.huds().health.color,
                () -> SBTConfig.huds().health.outlineColor,
                () -> SBTConfig.huds().health.mode,
                () -> Component.literal(NumberUtils.formatNumber((int)SkyblockData.Stats.health, SBTConfig.huds().health.separator) + "/" + NumberUtils.formatNumber((int) SkyblockData.Stats.maxHealth, SBTConfig.huds().health.separator) + (SBTConfig.huds().health.icon ? "❤" : ""))
        );
    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        return (SkyblockData.inSB && SBTConfig.huds().health.enabled && (SkyblockData.location != Location.RIFT || !SBTConfig.huds().health.hideInRift)) || fromHudScreen;
    }

    @Override
    public @NotNull HudName getName() {
        return new HudName("Health HUD", "HP HUD", Colors.RED);
    }
    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public DrawMode mode = DrawMode.SHADOW;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.20f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public int color = Colors.RED;

        @SerialEntry
        public int colorAbsorption = Colors.ORANGE;

        @SerialEntry
        public int outlineColor = Colors.BLACK;

        @SerialEntry
        public boolean icon = true;

        @SerialEntry
        public String separator = ",";

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        @SerialEntry
        public boolean hideInRift = true;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(key("health.enabled"))
                    .description(keyD("health.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.health.enabled,
                            () -> config.huds.health.enabled,
                            value -> config.huds.health.enabled = value
                    )
                    .build();
            var color = Option.<Color>createBuilder()
                    .name(key("health.color"))
                    .description(keyD("health.color"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.health.color),
                            () ->  new Color(config.huds.health.color),
                            value -> config.huds.health.color = value.getRGB()

                    )
                    .build();
            var colorAbsorption = Option.<Color>createBuilder()
                    .name(key("health.colorAbsorption"))
                    .description(keyD("health.colorAbsorption"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.health.colorAbsorption),
                            () ->  new Color(config.huds.health.colorAbsorption),
                            value -> config.huds.health.colorAbsorption = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(key("health.outlineColor"))
                    .description(keyD("health.outlineColor"))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.health.mode == DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.health.outlineColor),
                            () ->  new Color(config.huds.health.outlineColor),
                            value -> config.huds.health.outlineColor = value.getRGB()

                    )
                    .build();
            var mode = Option.<DrawMode>createBuilder()
                    .name(key("health.mode"))
                    .description(keyD("health.mode"))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.health.mode,
                            () -> config.huds.health.mode,
                            value -> {
                                config.huds.health.mode = value;
                                outline.setAvailable(value == DrawMode.OUTLINE);
                            }
                    )
                    .build();
            var icon = Option.<Boolean>createBuilder()
                    .name(key("health.icon"))
                    .description(keyD("health.icon"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.health.icon,
                            () -> config.huds.health.icon,
                            value -> config.huds.health.icon = value
                    )
                    .build();
            var separator = Option.<String>createBuilder()
                    .name(key("health.separator"))
                    .description(keyD("health.separator"))
                    .controller(StringControllerBuilder::create)
                    .binding(
                            defaults.huds.health.separator,
                            () -> config.huds.health.separator,
                            value -> config.huds.health.separator = value
                    )
                    .build();
            var rift = Option.<Boolean>createBuilder()
                    .name(key("health.hideInRift"))
                    .description(keyD("health.hideInRift"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.healthBar.hideInRift,
                            () -> config.huds.healthBar.hideInRift,
                            value -> config.huds.healthBar.hideInRift = value
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(key("health.scale"))
                    .description(keyD("health.scale"))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.huds.health.scale,
                            () -> config.huds.health.scale,
                            value -> config.huds.health.scale = value
                    )
                    .build();
            return OptionGroup.createBuilder()
                    .name(key("health"))
                    .description(keyD("health"))
                    .option(enabled)
                    .option(mode)
                    .option(color)
                    .option(colorAbsorption)
                    .option(outline)
                    .option(rift)
                    .option(icon)
                    .option(separator)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }
}
