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
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyBlockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SkyBlockTweaksConfig;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.hud.BarHUD;
import wtf.cheeze.sbt.utils.hud.HudInformation;

import java.awt.Color;

public class HealthBar extends BarHUD {

    public HealthBar() {
        INFO = new HudInformation(
                () -> SkyBlockTweaks.CONFIG.config.huds.healthBar.x,
                () -> SkyBlockTweaks.CONFIG.config.huds.healthBar.y,
                () -> SkyBlockTweaks.CONFIG.config.huds.healthBar.scale,
                () -> SkyBlockTweaks.DATA.health > SkyBlockTweaks.DATA.maxHealth ? SkyBlockTweaks.CONFIG.config.huds.healthBar.colorAbsorption : SkyBlockTweaks.CONFIG.config.huds.healthBar.color,
                () -> SkyBlockTweaks.DATA.maxHealth,
                () -> SkyBlockTweaks.DATA.health,
                x -> SkyBlockTweaks.CONFIG.config.huds.healthBar.x = (float) x,
                y -> SkyBlockTweaks.CONFIG.config.huds.healthBar.y = (float) y,
                scale -> SkyBlockTweaks.CONFIG.config.huds.healthBar.scale = (float) scale

        );
    }

    @Override
    public String getName() {
        return TextUtils.SECTION +  "cHealth Bar";
    }

    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyBlockTweaks.DATA.inSB && SkyBlockTweaks.CONFIG.config.huds.healthBar.enabled) || fromHudScreen) return true;
        return false;
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.15f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public int color = 16733525;

        @SerialEntry
        public int colorAbsorption = 16755200;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Text.literal("Enable Health Bar"))
                    .description(OptionDescription.of(Text.literal("Enables the Health Bar")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.healthBar.enabled,
                            () -> config.huds.healthBar.enabled,
                            value -> config.huds.healthBar.enabled = (Boolean) value
                    )
                    .build();
            var color = Option.<Color>createBuilder()
                    .name(Text.literal("Health Bar Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Health Bar")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.healthBar.color),
                            () ->  new Color(config.huds.healthBar.color),
                            value -> config.huds.healthBar.color = value.getRGB()

                    )
                    .build();
            var absorbColor = Option.<Color>createBuilder()
                    .name(Text.literal("Health Bar Absorption Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Health Bar when you have absorption")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.healthBar.colorAbsorption),
                            () ->  new Color(config.huds.healthBar.colorAbsorption),
                            value -> config.huds.healthBar.colorAbsorption = value.getRGB()

                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Text.literal("Health Bar Scale"))
                    .description(OptionDescription.of(Text.literal("The scale of the Health Bar")))
                    .controller(SkyBlockTweaksConfig::generateScaleController)
                    .binding(
                            defaults.huds.healthBar.scale,
                            () -> config.huds.healthBar.scale,
                            value -> config.huds.healthBar.scale = value
                    )
                    .build();
            return OptionGroup.createBuilder()
                    .name(Text.literal("Health Bar"))
                    .description(OptionDescription.of(Text.literal("Settings for the Health Bar")))
                    .option(enabled)
                    .option(color)
                    .option(absorbColor)
                    .option(scale)
                    .collapsed(true)
                    .build();


        }
    }
}
