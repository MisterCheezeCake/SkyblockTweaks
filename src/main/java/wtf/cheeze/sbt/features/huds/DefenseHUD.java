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

public class DefenseHUD extends TextHUD {

    public DefenseHUD() {
        INFO = new HudInformation(
                () -> SkyBlockTweaks.CONFIG.config.huds.defense.x,
                () -> SkyBlockTweaks.CONFIG.config.huds.defense.y,
                () -> SkyBlockTweaks.CONFIG.config.huds.defense.scale,
                () -> SkyBlockTweaks.CONFIG.config.huds.defense.shadow,
                () -> SkyBlockTweaks.CONFIG.config.huds.defense.color,
                x -> SkyBlockTweaks.CONFIG.config.huds.defense.x = (float) x,
                y -> SkyBlockTweaks.CONFIG.config.huds.defense.y = (float) y,
                scale -> SkyBlockTweaks.CONFIG.config.huds.defense.scale = (float) scale
        );
    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyBlockTweaks.DATA.inSB && SkyBlockTweaks.CONFIG.config.huds.defense.enabled) || fromHudScreen) return true;
        return false;
    }
    @Override
    public String getText() {
        return TextUtils.formatNumber(SkyBlockTweaks.DATA.defense, SkyBlockTweaks.CONFIG.config.huds.defense.separator)+ (SkyBlockTweaks.CONFIG.config.huds.defense.icon ? "❈" : "");
    }

    @Override
    public String getName() {
        return TextUtils.SECTION + "aDefense HUD";
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public boolean shadow = true;

        @SerialEntry
        public int color = 5635925;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.05f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public boolean icon = true;

        @SerialEntry
        public String separator  = ",";

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Text.literal("Enable Defense HUD"))
                    .description(OptionDescription.of(Text.literal("Enables the Defense HUD")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.defense.enabled,
                            () -> config.huds.defense.enabled,
                            value -> config.huds.defense.enabled = (Boolean) value
                    )
                    .build();
            var shadow = Option.<Boolean>createBuilder()
                    .name(Text.literal("Defense HUD Shadow"))
                    .description(OptionDescription.of(Text.literal("Enables the shadow for the Defense HUD")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.defense.shadow,
                            () -> config.huds.defense.shadow,
                            value -> config.huds.defense.shadow = (Boolean) value
                    )
                    .build();

            var color = Option.<Color>createBuilder()
                    .name(Text.literal("Defense HUD Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Defense HUD")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.defense.color),
                            () ->  new Color(config.huds.defense.color),
                            value -> config.huds.defense.color = value.getRGB()

                    )
                    .build();
            var icon = Option.<Boolean>createBuilder()
                    .name(Text.literal("Defense HUD Icon"))
                    .description(OptionDescription.of(Text.literal("Enables the icon (❈) in the Defense HUD")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.defense.icon,
                            () -> config.huds.defense.icon,
                            value -> config.huds.defense.icon = (Boolean) value
                    )
                    .build();
            var separator = Option.<String>createBuilder()
                    .name(Text.literal("Defense HUD Separator"))
                    .description(OptionDescription.of(Text.literal("The separator for the Defense HUD")))
                    .controller(StringControllerBuilder::create)
                    .binding(
                            defaults.huds.defense.separator,
                            () -> config.huds.defense.separator,
                            value -> config.huds.defense.separator = value
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Text.literal("Defense HUD Scale"))
                    .description(OptionDescription.of(Text.literal("The scale of the Defense HUD")))
                    .controller(SkyBlockTweaksConfig::generateScaleController)
                    .binding(
                            defaults.huds.defense.scale,
                            () -> config.huds.defense.scale,
                            value -> config.huds.defense.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(Text.literal("Defense HUD"))
                    .description(OptionDescription.of(Text.literal("Settings for the Defense HUD")))
                    .option(enabled)
                    .option(shadow)
                    .option(color)
                    .option(icon)
                    .option(separator)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }
}


