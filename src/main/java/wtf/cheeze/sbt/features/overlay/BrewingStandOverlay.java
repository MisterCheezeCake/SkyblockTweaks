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
package wtf.cheeze.sbt.features.overlay;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.core.NonNullList;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.config.categories.General;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.render.RenderUtils;

public class BrewingStandOverlay {

    private static final int DRAW_OFFSET_X = 20;
    private static final int DRAW_OFFSET_Y = 4;
    public static final float Z_OFFSET = 251;

    private static final int INPUT_SLOT = 13;
    private static final int TIMER_SLOT = 24;
    private static final int RIGHT_OUTPUT_SLOT = 42;


    public static void render(NonNullList<Slot> slots, GuiGraphics context) {
        if (!SBTConfig.get().brewingStandOverlay.enabled) return;
        var input = slots.get(INPUT_SLOT);
        var timer = slots.get(TIMER_SLOT);
        var output = slots.get(RIGHT_OUTPUT_SLOT);
        RenderUtils.drawWithZ(context, Z_OFFSET, () -> {
            if (input.hasItem()) {
                drawName(input, context);
            }
            if (!timer.getItem().getHoverName().getString().startsWith("Place Water Bottles")) {
                drawName(timer, context);
            }
            if (output.hasItem()) {
                drawName(output, context);
            }
        });

    }


    private static void drawName(Slot slot, GuiGraphics context) {
        var name = slot.getItem().getHoverName();
        var color =  name.getStyle().getColor();
        int rcolor;
        if (color == null) {
            rcolor = Colors.WHITE;
        } else {
            rcolor = color.getValue();
        }
        RenderUtils.drawText(context, name, slot.x + DRAW_OFFSET_X, slot.y + DRAW_OFFSET_Y, rcolor, false);
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = true;



        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(General.key("brewingStandOverlay.enabled"))
                    .description(General.keyD("brewingStandOverlay.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.brewingStandOverlay.enabled,
                            () -> config.brewingStandOverlay.enabled,
                            value -> config.brewingStandOverlay.enabled = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(General.key("brewingStandOverlay"))
                    .description(General.keyD("brewingStandOverlay"))
                    .option(enabled)
                    .build();


        }

    }

}
