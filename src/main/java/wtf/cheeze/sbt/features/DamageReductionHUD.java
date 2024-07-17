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
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyBlockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SkyBlockTweaksConfig;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.hud.HUD;
import wtf.cheeze.sbt.utils.hud.HudInformation;
import wtf.cheeze.sbt.utils.hud.TextHUD;

import java.awt.*;

public class DamageReductionHUD extends TextHUD {

    public DamageReductionHUD() {
        INFO = new HudInformation(
                () -> SkyBlockTweaks.CONFIG.config.huds.dr.x,
                () -> SkyBlockTweaks.CONFIG.config.huds.dr.y,
                () -> SkyBlockTweaks.CONFIG.config.huds.dr.scale,
                () -> SkyBlockTweaks.CONFIG.config.huds.dr.shadow,
                () -> SkyBlockTweaks.CONFIG.config.huds.dr.color,
                x -> SkyBlockTweaks.CONFIG.config.huds.dr.x = (float) x,
                y -> SkyBlockTweaks.CONFIG.config.huds.dr.y = (float) y,
                scale -> SkyBlockTweaks.CONFIG.config.huds.dr.scale = (float) scale
        );
    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyBlockTweaks.DATA.inSB && SkyBlockTweaks.CONFIG.config.huds.dr.enabled) || fromHudScreen) return true;
        return false;
    }
    @Override
    public String getText() {
        return NumberUtils.round(SkyBlockTweaks.DATA.damageReduction(), 1) + "%";
    }

    @Override
    public String getName() {
        return TextUtils.SECTION + "aDamage Reduction Percentage HUD";
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
        public float y = 0;

        @SerialEntry
        public float scale = 1.0f;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Text.literal("Enable Damage Reduction HUD"))
                    .description(OptionDescription.of(Text.literal("Enables the Damage Reduction HUD")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.dr.enabled,
                            () -> config.huds.dr.enabled,
                            value -> config.huds.dr.enabled = (Boolean) value
                    )
                    .build();
            var shadow = Option.<Boolean>createBuilder()
                    .name(Text.literal("Damage Reduction HUD Shadow"))
                    .description(OptionDescription.of(Text.literal("Enables the shadow for the Damage Reduction HUD")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.dr.shadow,
                            () -> config.huds.dr.shadow,
                            value -> config.huds.dr.shadow = (Boolean) value
                    )
                    .build();
            var color = Option.<Color>createBuilder()
                    .name(Text.literal("Damage Reduction HUD Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Damage Reduction HUD")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.dr.color),
                            () ->  new Color(config.huds.dr.color),
                            value -> config.huds.dr.color = value.getRGB()

                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Text.literal("Damage Reduction HUD Scale"))
                    .description(OptionDescription.of(Text.literal("The scale of the Damage Reduction HUD")))
                    .controller(SkyBlockTweaksConfig::generateScaleController)
                    .binding(
                            defaults.huds.dr.scale,
                            () -> config.huds.dr.scale,
                            value -> config.huds.dr.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(Text.literal("Damage Reduction HUD"))
                    .description(OptionDescription.of(Text.literal("Settings for the Damage Reduction HUD")))
                    .option(enabled)
                    .option(shadow)
                    .option(color)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }

    }
}
