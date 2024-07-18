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
package wtf.cheeze.sbt.features;

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

public class HealthHUD extends TextHUD {

    public HealthHUD() {
        INFO = new HudInformation(
                () -> SkyBlockTweaks.CONFIG.config.huds.health.x,
                () -> SkyBlockTweaks.CONFIG.config.huds.health.y,
                () -> SkyBlockTweaks.CONFIG.config.huds.health.scale,
                () -> SkyBlockTweaks.CONFIG.config.huds.health.shadow,
                () -> SkyBlockTweaks.DATA.health > SkyBlockTweaks.DATA.maxHealth ? SkyBlockTweaks.CONFIG.config.huds.health.colorAbsorption : SkyBlockTweaks.CONFIG.config.huds.health.color,
                x -> SkyBlockTweaks.CONFIG.config.huds.health.x = (float) x,
                y -> SkyBlockTweaks.CONFIG.config.huds.health.y = (float) y,
                scale -> SkyBlockTweaks.CONFIG.config.huds.health.scale = (float) scale
        );
    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyBlockTweaks.DATA.inSB && SkyBlockTweaks.CONFIG.config.huds.health.enabled) || fromHudScreen) return true;
        return false;
    }
    @Override
    public String getText() {
        return TextUtils.formatNumber((int) SkyBlockTweaks.DATA.health, SkyBlockTweaks.CONFIG.config.huds.health.separator) + "/" + TextUtils.formatNumber((int) SkyBlockTweaks.DATA.maxHealth, SkyBlockTweaks.CONFIG.config.huds.health.separator) + (SkyBlockTweaks.CONFIG.config.huds.health.icon ? "❤" : "");
    }

    @Override
    public String getName() {
        return TextUtils.SECTION +  "cHealth HUD";
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public boolean shadow = true;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.20f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public int color = 16733525;

        @SerialEntry
        public int colorAbsorption = 16755200;

        @SerialEntry
        public boolean icon = true;

        @SerialEntry
        public String separator = ",";

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Text.literal("Enable Health HUD"))
                    .description(OptionDescription.of(Text.literal("Enables the Health HUD")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.health.enabled,
                            () -> config.huds.health.enabled,
                            value -> config.huds.health.enabled = (Boolean) value
                    )
                    .build();
            var shadow = Option.<Boolean>createBuilder()
                    .name(Text.literal("Health HUD Shadow"))
                    .description(OptionDescription.of(Text.literal("Enables the shadow for the Health HUD")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.health.shadow,
                            () -> config.huds.health.shadow,
                            value -> config.huds.health.shadow = (Boolean) value
                    )
                    .build();
            var color = Option.<Color>createBuilder()
                    .name(Text.literal("Health HUD Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Health HUD")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.health.color),
                            () ->  new Color(config.huds.health.color),
                            value -> config.huds.health.color = value.getRGB()

                    )
                    .build();
            var colorAbsorption = Option.<Color>createBuilder()
                    .name(Text.literal("Health HUD Absorption Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Health HUD when you have absorption")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.health.colorAbsorption),
                            () ->  new Color(config.huds.health.colorAbsorption),
                            value -> config.huds.health.colorAbsorption = value.getRGB()

                    )
                    .build();
            var icon = Option.<Boolean>createBuilder()
                    .name(Text.literal("Health HUD Icon"))
                    .description(OptionDescription.of(Text.literal("Enables the icon (❤) in the Health HUD")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.health.icon,
                            () -> config.huds.health.icon,
                            value -> config.huds.health.icon = (Boolean) value
                    )
                    .build();
            var separator = Option.<String>createBuilder()
                    .name(Text.literal("Health HUD Separator"))
                    .description(OptionDescription.of(Text.literal("The separator for the Health HUD")))
                    .controller(StringControllerBuilder::create)
                    .binding(
                            defaults.huds.health.separator,
                            () -> config.huds.health.separator,
                            value -> config.huds.health.separator = value
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Text.literal("Health HUD Scale"))
                    .description(OptionDescription.of(Text.literal("The scale of the Health HUD")))
                    .controller(SkyBlockTweaksConfig::generateScaleController)
                    .binding(
                            defaults.huds.health.scale,
                            () -> config.huds.health.scale,
                            value -> config.huds.health.scale = value
                    )
                    .build();
            return OptionGroup.createBuilder()
                    .name(Text.literal("Health HUD"))
                    .description(OptionDescription.of(Text.literal("Settings for the Health HUD")))
                    .option(enabled)
                    .option(shadow)
                    .option(color)
                    .option(colorAbsorption)
                    .option(icon)
                    .option(separator)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }
}
