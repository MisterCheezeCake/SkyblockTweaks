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

public class EhpHUD extends TextHUD {

    public EhpHUD() {
        INFO = new HudInformation(
                () -> SkyBlockTweaks.CONFIG.config.huds.ehp.x,
                () -> SkyBlockTweaks.CONFIG.config.huds.ehp.y,
                () -> SkyBlockTweaks.CONFIG.config.huds.ehp.scale,
                () -> SkyBlockTweaks.CONFIG.config.huds.ehp.shadow,
                () -> SkyBlockTweaks.CONFIG.config.huds.ehp.color,
                x -> SkyBlockTweaks.CONFIG.config.huds.ehp.x = (float) x,
                y -> SkyBlockTweaks.CONFIG.config.huds.ehp.y = (float) y,
                scale -> SkyBlockTweaks.CONFIG.config.huds.ehp.scale = (float) scale
        );
    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyBlockTweaks.DATA.inSB && SkyBlockTweaks.CONFIG.config.huds.ehp.enabled) || fromHudScreen) return true;
        return false;
    }
    @Override
    public String getText() {
        return TextUtils.formatNumber((int) SkyBlockTweaks.DATA.effectiveHealth(), SkyBlockTweaks.CONFIG.config.huds.ehp.separator) + (SkyBlockTweaks.CONFIG.config.huds.ehp.icon ? "❤" : "");
    }

    @Override
    public String getName() {
        return TextUtils.SECTION + "2Effective Health HUD";
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public boolean shadow = true;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.10f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public int color = 43520;

        @SerialEntry
        public boolean icon = true;

        @SerialEntry
        public String separator = ",";

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Text.literal("Enable Effective Health HUD"))
                    .description(OptionDescription.of(Text.literal("Enables the Effective Health HUD")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.ehp.enabled,
                            () -> config.huds.ehp.enabled,
                            value -> config.huds.ehp.enabled = (Boolean) value
                    )
                    .build();
            var shadow = Option.<Boolean>createBuilder()
                    .name(Text.literal("Effective Health HUD Shadow"))
                    .description(OptionDescription.of(Text.literal("Enables the shadow for the Effective Health HUD")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.ehp.shadow,
                            () -> config.huds.ehp.shadow,
                            value -> config.huds.ehp.shadow = (Boolean) value
                    )
                    .build();

            var color = Option.<Color>createBuilder()
                    .name(Text.literal("Effective Health HUD Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Effective Health HUD")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.ehp.color),
                            () ->  new Color(config.huds.ehp.color),
                            value -> config.huds.ehp.color = value.getRGB()

                    )
                    .build();
            var icon = Option.<Boolean>createBuilder()
                    .name(Text.literal("Effective Health HUD Icon"))
                    .description(OptionDescription.of(Text.literal("Enables the icon (❤) in the Effective Health HUD")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.ehp.icon,
                            () -> config.huds.ehp.icon,
                            value -> config.huds.ehp.icon = (Boolean) value
                    )
                    .build();
            var separator = Option.<String>createBuilder()
                    .name(Text.literal("Effective Health HUD Separator"))
                    .description(OptionDescription.of(Text.literal("The separator for the Effective Health HUD")))
                    .controller(StringControllerBuilder::create)
                    .binding(
                            defaults.huds.ehp.separator,
                            () -> config.huds.ehp.separator,
                            value -> config.huds.ehp.separator = value
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Text.literal("Effective Health HUD Scale"))
                    .description(OptionDescription.of(Text.literal("The scale of the Effective Health HUD")))
                    .controller(SkyBlockTweaksConfig::generateScaleController)
                    .binding(
                            defaults.huds.ehp.scale,
                            () -> config.huds.ehp.scale,
                            value -> config.huds.ehp.scale = value
                    )
                    .build();


            return OptionGroup.createBuilder()
                    .name(Text.literal("Effective Health HUD"))
                    .description(OptionDescription.of(Text.literal("Settings for the Effective Health HUD")))
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



