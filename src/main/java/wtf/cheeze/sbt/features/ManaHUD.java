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

import java.awt.*;

public class ManaHUD extends TextHUD {

    public ManaHUD() {
        INFO = new HudInformation(
                () -> SkyBlockTweaks.CONFIG.config.huds.mana.x,
                () -> SkyBlockTweaks.CONFIG.config.huds.mana.y,
                () -> SkyBlockTweaks.CONFIG.config.huds.mana.scale,
                () -> SkyBlockTweaks.CONFIG.config.huds.mana.shadow,
                () -> SkyBlockTweaks.CONFIG.config.huds.mana.color,
                x -> SkyBlockTweaks.CONFIG.config.huds.mana.x = (float) x,
                y -> SkyBlockTweaks.CONFIG.config.huds.mana.y = (float) y,
                scale -> SkyBlockTweaks.CONFIG.config.huds.mana.scale = (float) scale
        );
    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyBlockTweaks.DATA.inSB && SkyBlockTweaks.CONFIG.config.huds.mana.enabled) || fromHudScreen) return true;
        return false;
    }
    @Override
    public String getText() {
        return TextUtils.formatNumber((int) SkyBlockTweaks.DATA.mana, SkyBlockTweaks.CONFIG.config.huds.mana.separator) + "/" + TextUtils.formatNumber((int) SkyBlockTweaks.DATA.maxMana, SkyBlockTweaks.CONFIG.config.huds.mana.separator) + (SkyBlockTweaks.CONFIG.config.huds.mana.icon ? "✎" : "");
    }

    @Override
    public String getName() {
        return TextUtils.SECTION +  "9Mana HUD";
    }

    public static class Config {

        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public boolean shadow = true;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.30f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public int color = 5592575;

        @SerialEntry
        public boolean icon = true;

        @SerialEntry
        public String separator = ",";

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Text.literal("Enable Mana HUD"))
                    .description(OptionDescription.of(Text.literal("Enables the Mana HUD")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.mana.enabled,
                            () -> config.huds.mana.enabled,
                            value -> config.huds.mana.enabled = (Boolean) value
                    )
                    .build();
            var shadow = Option.<Boolean>createBuilder()
                    .name(Text.literal("Mana HUD Shadow"))
                    .description(OptionDescription.of(Text.literal("Enables the shadow for the Mana HUD")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.mana.shadow,
                            () -> config.huds.mana.shadow,
                            value -> config.huds.mana.shadow = (Boolean) value
                    )
                    .build();
            var color = Option.<Color>createBuilder()
                    .name(Text.literal("Mana HUD Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Mana HUD")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.mana.color),
                            () ->  new Color(config.huds.mana.color),
                            value -> config.huds.mana.color = value.getRGB()

                    )
                    .build();
            var icon = Option.<Boolean>createBuilder()
                    .name(Text.literal("Mana HUD Icon"))
                    .description(OptionDescription.of(Text.literal("Enables the icon (✎) in the Mana HUD")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.mana.icon,
                            () -> config.huds.mana.icon,
                            value -> config.huds.mana.icon = (Boolean) value
                    )
                    .build();
            var separator = Option.<String>createBuilder()
                    .name(Text.literal("Mana HUD Separator"))
                    .description(OptionDescription.of(Text.literal("The separator for the Mana HUD")))
                    .controller(StringControllerBuilder::create)
                    .binding(
                            defaults.huds.mana.separator,
                            () -> config.huds.mana.separator,
                            value -> config.huds.mana.separator = value
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Text.literal("Mana HUD Scale"))
                    .description(OptionDescription.of(Text.literal("The scale of the Mana HUD")))
                    .controller(SkyBlockTweaksConfig::generateScaleController)
                    .binding(
                            defaults.huds.mana.scale,
                            () -> config.huds.mana.scale,
                            value -> config.huds.mana.scale = value
                    )
                    .build();
            return OptionGroup.createBuilder()
                    .name(Text.literal("Mana HUD"))
                    .description(OptionDescription.of(Text.literal("Settings for the Mana HUD")))
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
