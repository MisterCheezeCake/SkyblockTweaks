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
import wtf.cheeze.sbt.utils.hud.HudInformation;
import wtf.cheeze.sbt.utils.hud.TextHUD;

import java.awt.Color;

public class SpeedHUD extends TextHUD {

    public SpeedHUD() {
        INFO = new HudInformation(
                () -> SkyBlockTweaks.CONFIG.config.huds.speed.x,
                () -> SkyBlockTweaks.CONFIG.config.huds.speed.y,
                () -> SkyBlockTweaks.CONFIG.config.huds.speed.scale,
                () -> SkyBlockTweaks.CONFIG.config.huds.speed.shadow,
                () -> SkyBlockTweaks.CONFIG.config.huds.speed.color,
                x -> SkyBlockTweaks.CONFIG.config.huds.speed.x = (float) x,
                y -> SkyBlockTweaks.CONFIG.config.huds.speed.y = (float) y,
                scale -> SkyBlockTweaks.CONFIG.config.huds.speed.scale = (float) scale
        );
    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyBlockTweaks.DATA.inSB && SkyBlockTweaks.CONFIG.config.huds.speed.enabled) || fromHudScreen) return true;
        return false;
    }
    @Override
    public String getText() {
        float _speed = SkyBlockTweaks.DATA.getSpeed();
        String speed = ""+_speed;
        speed = speed.split("\\.")[0];
        speed = speed + "%";
        return speed;
    }

    @Override
    public String getName() {
        return "Speed Percentage HUD";
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public boolean shadow = true;

        @SerialEntry
        public int color = 0xFFFFFF;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.35f;

        @SerialEntry
        public float scale = 1.0f;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Text.literal("Enable Speed HUD"))
                    .description(OptionDescription.of(Text.literal("Enables the Speed HUD")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.speed.enabled,
                            () -> config.huds.speed.enabled,
                            value -> config.huds.speed.enabled = (Boolean) value
                    )
                    .build();
            var shadow = Option.<Boolean>createBuilder()
                    .name(Text.literal("Speed HUD Shadow"))
                    .description(OptionDescription.of(Text.literal("Enables the shadow for the Speed HUD")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.speed.shadow,
                            () -> config.huds.speed.shadow,
                            value -> config.huds.speed.shadow = (Boolean) value
                    )
                    .build();

            var color = Option.<Color>createBuilder()
                    .name(Text.literal("Speed HUD Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Speed HUD")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.speed.color),
                            () ->  new Color(config.huds.speed.color),
                            value -> config.huds.speed.color = value.getRGB()

                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Text.literal("Speed HUD Scale"))
                    .description(OptionDescription.of(Text.literal("The scale of the Speed HUD")))
                    .controller(SkyBlockTweaksConfig::generateScaleController)
                    .binding(
                            defaults.huds.speed.scale,
                            () -> config.huds.speed.scale,
                            value -> config.huds.speed.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(Text.literal("Speed HUD"))
                    .description(OptionDescription.of(Text.literal("Settings for the Speed HUD")))
                    .option(enabled)
                    .option(shadow)
                    .option(color)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }


}

