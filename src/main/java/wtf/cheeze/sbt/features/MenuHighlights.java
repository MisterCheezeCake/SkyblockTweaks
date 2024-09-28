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
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;

import java.util.regex.Pattern;
import static wtf.cheeze.sbt.config.categories.General.key;
import static wtf.cheeze.sbt.config.categories.General.keyD;

public class MenuHighlights {

    // Players: n/80
    public static final Pattern PLAYER_COUNT_PATTERN = Pattern.compile("Players: (\\d\\d?)/80");
    public static final Pattern PLAYER_COUNT_PATTERN_DH = Pattern.compile("Players: (\\d\\d?)/24");

    public static final int HIGHLIGHT_RED = -16842752;
    public static final int HIGHLIGHT_ORANGE = -16804352;
    public static final int HIGHLIGHT_YELLOW = -16777472;
    public static final int HIGHLIGHT_GREEN = -33489152;

    public static final int HIGHLIGHT_RED2 = -16826038;
    public static final int HIGHLIGHT_GREEN2 = -33423577;
    public static final int HIGHLIGHT_GREY = -27962027;

    public static void tryDrawHighlight(DrawContext context, Slot slot) {
        if (!SBTConfig.get().hubSelectorHighlight.enabledRegular) return;
        if (!slot.getStack().getName().getString().contains("SkyBlock Hub #")) return;
        var lines = slot.getStack().getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).lines();
        var matcher = PLAYER_COUNT_PATTERN.matcher(lines.getFirst().getString());
        if (!matcher.matches()) return;
        var playerCount = Integer.parseInt(matcher.group(1));
        if (playerCount >= 60) highlight(context, slot, HIGHLIGHT_RED);
        else if (playerCount >= 40) highlight(context, slot, HIGHLIGHT_ORANGE);
        else if (playerCount >= 20) highlight(context, slot, HIGHLIGHT_YELLOW);
        else highlight(context, slot, HIGHLIGHT_GREEN);

    }
    public static void tryDrawHighlightDH(DrawContext context, Slot slot) {
        if (!SBTConfig.get().hubSelectorHighlight.enabledDungeon) return;
        if (!slot.getStack().getName().getString().contains("Dungeon Hub #")) return;
        var lines = slot.getStack().getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).lines();
        var matcher = PLAYER_COUNT_PATTERN_DH.matcher(lines.getFirst().getString());
        if (!matcher.matches()) return;
        var playerCount = Integer.parseInt(matcher.group(1));
        if (playerCount >= 18) highlight(context, slot, HIGHLIGHT_RED);
        else if (playerCount >= 12) highlight(context, slot, HIGHLIGHT_ORANGE);
        else if (playerCount >= 6) highlight(context, slot, HIGHLIGHT_YELLOW);
        else highlight(context, slot, HIGHLIGHT_GREEN);
    }
    public static void tryDrawHighlightHOTM(DrawContext context, Slot slot) {
        if (!SBTConfig.get().hubSelectorHighlight.hotmHighlight) return;
        var lines = slot.getStack().getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).lines();
        for (var line: lines) {
            switch (line.getString()) {
                case "ENABLED", "SELECTED" -> {
                    highlight(context, slot, HIGHLIGHT_GREEN2);
                    return;
                }
                case "DISABLED", "Click to select!" ->   {
                    highlight(context, slot, HIGHLIGHT_RED2);
                    return;
                }
                case "1 Token of the Mountain" -> {
                    highlight(context, slot, HIGHLIGHT_GREY);
                    return;
                }
            }
        }
    }

    public static void tryDrawHighlightWidget(DrawContext context, Slot slot) {
        if (!SBTConfig.get().hubSelectorHighlight.widgetHighlight) return;
        var name = slot.getStack().getName().getString();
        if (!name.contains("✔") && !name.contains("✖")) {
            if (name.equals("Third Column")) {
                var fistLine = slot.getStack().getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).lines().getFirst().getString();
                if (fistLine.equals("Currently: ENABLED")) highlight(context, slot, HIGHLIGHT_GREEN2);
                else highlight(context, slot, HIGHLIGHT_RED2);
            }
            return;
        }
        if (name.contains("✔")) highlight(context, slot, HIGHLIGHT_GREEN2);
        else highlight(context, slot, HIGHLIGHT_RED2);
    }

    public static void tryDrawHighlightTasks(DrawContext context, Slot slot) {
        if (!SBTConfig.get().hubSelectorHighlight.sblevelHighlight) return;
        var lines = slot.getStack().getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).lines();
        for (var line: lines) {
            var s = line.getString();
            if (s.equals("Total Progress: 100%")) highlight(context, slot, HIGHLIGHT_GREEN2);
            else if (s.contains("Total Progress: ")) highlight(context, slot, HIGHLIGHT_RED2);
        }
    }

    private static void highlight(DrawContext context, Slot slot, int color) {
        context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, color);
    }

    public static class Config {
        @SerialEntry
        public boolean enabledRegular = true;

        @SerialEntry
        public boolean enabledDungeon = true;

        @SerialEntry
        public boolean hotmHighlight = true;

        @SerialEntry
        public boolean widgetHighlight = true;

        @SerialEntry
        public boolean sblevelHighlight = true;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(key("menuHighlights.enabledRegular"))
                    .description(keyD("menuHighlights.enabledRegular"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                                    defaults.hubSelectorHighlight.enabledRegular,
                                    () -> config.hubSelectorHighlight.enabledRegular,
                                    value -> config.hubSelectorHighlight.enabledRegular = (Boolean) value
                    )
                    .build();
            var enabledDungeon = Option.<Boolean>createBuilder()
                    .name(key("menuHighlights.enabledDungeon"))
                    .description(keyD("menuHighlights.enabledDungeon"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                                    defaults.hubSelectorHighlight.enabledDungeon,
                                    () -> config.hubSelectorHighlight.enabledDungeon,
                                    value -> config.hubSelectorHighlight.enabledDungeon = (Boolean) value
                    )
                    .build();
            var hotmHighlight = Option.<Boolean>createBuilder()
                    .name(key("menuHighlights.hotmHighlight"))
                    .description(keyD("menuHighlights.hotmHighlight"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                                    defaults.hubSelectorHighlight.hotmHighlight,
                                    () -> config.hubSelectorHighlight.hotmHighlight,
                                    value -> config.hubSelectorHighlight.hotmHighlight = (Boolean) value
                    )
                    .build();

            var widgetHighlight = Option.<Boolean>createBuilder()
                    .name(key("menuHighlights.widgetHighlight"))
                    .description(keyD("menuHighlights.widgetHighlight"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                                    defaults.hubSelectorHighlight.widgetHighlight,
                                    () -> config.hubSelectorHighlight.widgetHighlight,
                                    value -> config.hubSelectorHighlight.widgetHighlight = (Boolean) value
                    )
                    .build();
            var sblevelHighlight = Option.<Boolean>createBuilder()
                    .name(key("menuHighlights.sblevelHighlight"))
                    .description(keyD("menuHighlights.sblevelHighlight"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                                    defaults.hubSelectorHighlight.sblevelHighlight,
                                    () -> config.hubSelectorHighlight.sblevelHighlight,
                                    value -> config.hubSelectorHighlight.sblevelHighlight = (Boolean) value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(key("menuHighlights"))
                    .description(keyD("menuHighlights"))
                    .option(enabled)
                    .option(enabledDungeon)
                    .option(hotmHighlight)
                    .option(widgetHighlight)
                    .option(sblevelHighlight)
                    .build();
        }
    }
}
