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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.events.DrawSlotEvents;

import java.util.regex.Pattern;
import static wtf.cheeze.sbt.config.categories.General.key;
import static wtf.cheeze.sbt.config.categories.General.keyD;

public class MenuHighlights {

    // Players: n/80
    public static final Pattern PLAYER_COUNT_PATTERN = Pattern.compile("Players: (\\d\\d?)/(\\d\\d?)");

    public static final int HIGHLIGHT_RED = -16842752;
    public static final int HIGHLIGHT_ORANGE = -16804352;
    public static final int HIGHLIGHT_YELLOW = -16777472;
    public static final int HIGHLIGHT_GREEN = -33489152;

    public static final int HIGHLIGHT_RED2 = -16826038;
    public static final int HIGHLIGHT_GREEN2 = -33423577;
    public static final int HIGHLIGHT_GREY = -27962027;

    private static final int SLOT_DIMENSION = 16;

    public static void registerEvents() {
        DrawSlotEvents.BEFORE_ITEM.register(MenuHighlights::onDrawSlot);
    }


    public static void onDrawSlot(Text screenTitle, DrawContext context, Slot slot) {
        var title = screenTitle.getString();
        switch (title) {
            case "SkyBlock Hub Selector" -> tryDrawHighlight(context, slot);
            case "Dungeon Hub Selector" -> tryDrawHighlightDH(context, slot);
            case "Heart of the Mountain" -> tryDrawHighlightHOTM(context, slot);
        }
        if (title.contains("Widget") || title.contains("Setting")) {
          tryDrawHighlightWidget(context, slot);
        } else if (title.equals("Ways to Level Up") || title.equals("Skill Related Tasks") ||title.contains(" ➜ ")) {
          tryDrawHighlightTasks(context, slot);
        }

    }

    private static void tryDrawHighlight(DrawContext context, Slot slot) {
        if (!SBTConfig.get().hubSelectorHighlight.enabledRegular) return;
        if (!slot.getStack().getName().getString().contains("SkyBlock Hub #")) return;
        sharedHighlight(context, slot);

    }
    private static void tryDrawHighlightDH(DrawContext context, Slot slot) {
        if (!SBTConfig.get().hubSelectorHighlight.enabledDungeon) return;
        if (!slot.getStack().getName().getString().contains("Dungeon Hub #")) return;
        sharedHighlight(context, slot);
    }

    private static void sharedHighlight(DrawContext context, Slot slot) {
        var lines = slot.getStack().getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).lines();
        var matcher = PLAYER_COUNT_PATTERN.matcher(lines.getFirst().getString());
        if (!matcher.matches()) return;
        var playerCount = Integer.parseInt(matcher.group(1));
        var max = Integer.parseInt(matcher.group(2));
        var threshold1 = max * 3 / 4;
        var threshold2 = max * 2 / 4;
        var threshold3 = max / 4;

        if (playerCount >= threshold1) highlight(context, slot, HIGHLIGHT_RED);
        else if (playerCount >= threshold2) highlight(context, slot, HIGHLIGHT_ORANGE);
        else if (playerCount >= threshold3) highlight(context, slot, HIGHLIGHT_YELLOW);
        else highlight(context, slot, HIGHLIGHT_GREEN);
    }

    private static void tryDrawHighlightHOTM(DrawContext context, Slot slot) {
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

    private static void tryDrawHighlightWidget(DrawContext context, Slot slot) {
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

    private static void tryDrawHighlightTasks(DrawContext context, Slot slot) {
        if (!SBTConfig.get().hubSelectorHighlight.sblevelHighlight) return;
        var lines = slot.getStack().getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).lines();
        for (var line: lines) {
            var s = line.getString();
            if (s.equals("Total Progress: 100%")) highlight(context, slot, HIGHLIGHT_GREEN2);
            else if (s.contains("Total Progress: ")) highlight(context, slot, HIGHLIGHT_RED2);
            else if (s.equals("You have completed this task!")) highlight(context, slot, HIGHLIGHT_GREEN2);
            else if (s.equals("This task can only be completed once!")) highlight(context, slot, HIGHLIGHT_RED2);
            else if (s.equals("Progress to Complete Category: 100%")) highlight(context, slot, HIGHLIGHT_GREEN2);
            else if (s.contains("Progress to Complete Category: ")) highlight(context, slot, HIGHLIGHT_RED2);

        }
    }

    private static void highlight(DrawContext context, Slot slot, int color) {
        context.fill(slot.x, slot.y, slot.x + SLOT_DIMENSION, slot.y + SLOT_DIMENSION, color);
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
                                    value -> config.hubSelectorHighlight.enabledRegular = (boolean) value
                    )
                    .build();
            var enabledDungeon = Option.<Boolean>createBuilder()
                    .name(key("menuHighlights.enabledDungeon"))
                    .description(keyD("menuHighlights.enabledDungeon"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                                    defaults.hubSelectorHighlight.enabledDungeon,
                                    () -> config.hubSelectorHighlight.enabledDungeon,
                                    value -> config.hubSelectorHighlight.enabledDungeon = (boolean) value
                    )
                    .build();
            var hotmHighlight = Option.<Boolean>createBuilder()
                    .name(key("menuHighlights.hotmHighlight"))
                    .description(keyD("menuHighlights.hotmHighlight"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                                    defaults.hubSelectorHighlight.hotmHighlight,
                                    () -> config.hubSelectorHighlight.hotmHighlight,
                                    value -> config.hubSelectorHighlight.hotmHighlight = (boolean) value
                    )
                    .build();

            var widgetHighlight = Option.<Boolean>createBuilder()
                    .name(key("menuHighlights.widgetHighlight"))
                    .description(keyD("menuHighlights.widgetHighlight"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                                    defaults.hubSelectorHighlight.widgetHighlight,
                                    () -> config.hubSelectorHighlight.widgetHighlight,
                                    value -> config.hubSelectorHighlight.widgetHighlight = (boolean) value
                    )
                    .build();
            var sblevelHighlight = Option.<Boolean>createBuilder()
                    .name(key("menuHighlights.sblevelHighlight"))
                    .description(keyD("menuHighlights.sblevelHighlight"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                                    defaults.hubSelectorHighlight.sblevelHighlight,
                                    () -> config.hubSelectorHighlight.sblevelHighlight,
                                    value -> config.hubSelectorHighlight.sblevelHighlight = (boolean) value
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
