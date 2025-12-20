// TODO(Ravel): Failed to fully resolve file: null
// TODO(Ravel): Failed to fully resolve file: null
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
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.component.ItemLore;
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

    public static void onDrawSlot(Component screenTitle, GuiGraphics guiGraphics, Slot slot) {
        var title = screenTitle.getString();
        switch (title) {
            case "SkyBlock Hub Selector" -> tryDrawHighlight(guiGraphics, slot);
            case "Dungeon Hub Selector" -> tryDrawHighlightDH(guiGraphics, slot);
            case "Heart of the Mountain" -> tryDrawHighlightHOTM(guiGraphics, slot);
            case "Heart of the Forest" -> tryDrawHighlightHOTF(guiGraphics, slot);
            case "Commissions" -> tryDrawHighlightComs(guiGraphics, slot);
            case "Your Contests" -> tryDrawHighlightContests(guiGraphics, slot);
        }
        if (title.contains("Widget") || title.contains("Setting")) {
          tryDrawHighlightWidget(guiGraphics, slot);
        } else if (title.equals("Ways to Level Up") || title.equals("Skill Related Tasks") ||title.contains(" ➜ ")) {
          tryDrawHighlightTasks(guiGraphics, slot);
        } else if (title.startsWith("Toggle Potion Effects")) {
            tryDrawHighlightEffects(guiGraphics, slot);
        }

    }

    private static void tryDrawHighlight(GuiGraphics guiGraphics, Slot slot) {
        if (!SBTConfig.get().hubSelectorHighlight.enabledRegular) return;
        if (!slot.getItem().getHoverName().getString().contains("SkyBlock Hub #")) return;
        sharedHighlight(guiGraphics, slot);

    }
    private static void tryDrawHighlightDH(GuiGraphics guiGraphics, Slot slot) {
        if (!SBTConfig.get().hubSelectorHighlight.enabledDungeon) return;
        if (!slot.getItem().getHoverName().getString().contains("Dungeon Hub #")) return;
        sharedHighlight(guiGraphics, slot);
    }

    private static void sharedHighlight(GuiGraphics guiGraphics, Slot slot) {
        var lines = slot.getItem().getOrDefault(DataComponents.LORE, ItemLore.EMPTY).lines();
        var matcher = PLAYER_COUNT_PATTERN.matcher(lines.getFirst().getString());
        if (!matcher.matches()) return;
        var playerCount = Integer.parseInt(matcher.group(1));
        var max = Integer.parseInt(matcher.group(2));
        var threshold1 = max * 3 / 4;
        var threshold2 = max * 2 / 4;
        var threshold3 = max / 4;

        if (playerCount >= threshold1) highlight(guiGraphics, slot, HIGHLIGHT_RED);
        else if (playerCount >= threshold2) highlight(guiGraphics, slot, HIGHLIGHT_ORANGE);
        else if (playerCount >= threshold3) highlight(guiGraphics, slot, HIGHLIGHT_YELLOW);
        else highlight(guiGraphics, slot, HIGHLIGHT_GREEN);
    }

    private static void tryDrawHighlightHOTM(GuiGraphics guiGraphics, Slot slot) {
        if (!SBTConfig.get().hubSelectorHighlight.hotmHighlight) return;
        var lines = slot.getItem().getOrDefault(DataComponents.LORE, ItemLore.EMPTY).lines();
        for (var line: lines) {
            switch (line.getString()) {
                case "ENABLED", "SELECTED" -> {
                    highlight(guiGraphics, slot, HIGHLIGHT_GREEN2);
                    return;
                }
                case "DISABLED", "Click to select!" ->   {
                    highlight(guiGraphics, slot, HIGHLIGHT_RED2);
                    return;
                }
                case "1 Token of the Mountain" -> {
                    highlight(guiGraphics, slot, HIGHLIGHT_GREY);
                    return;
                }
            }
        }
    }

    private static void tryDrawHighlightHOTF(GuiGraphics guiGraphics, Slot slot) {
        if (!SBTConfig.get().hubSelectorHighlight.hotfHighlight) return;
        var lines = slot.getItem().getOrDefault(DataComponents.LORE, ItemLore.EMPTY).lines();
        for (var line: lines) {
            switch (line.getString()) {
                case "ENABLED", "SELECTED" -> {
                    highlight(guiGraphics, slot, HIGHLIGHT_GREEN2);
                    return;
                }
                case "DISABLED", "Click to select!" ->   {
                    highlight(guiGraphics, slot, HIGHLIGHT_RED2);
                    return;
                }
                case "1 Token of the Forest" -> {
                    highlight(guiGraphics, slot, HIGHLIGHT_GREY);
                    return;
                }
            }
        }
    }

    private static void tryDrawHighlightWidget(GuiGraphics guiGraphics, Slot slot) {
        if (!SBTConfig.get().hubSelectorHighlight.widgetHighlight) return;
        var name = slot.getItem().getHoverName().getString();
        if (!name.contains("✔") && !name.contains("✖")) {
            if (name.equals("Third Column")) {
                var fistLine = slot.getItem().getOrDefault(DataComponents.LORE, ItemLore.EMPTY).lines().getFirst().getString();
                if (fistLine.equals("Currently: ENABLED")) highlight(guiGraphics, slot, HIGHLIGHT_GREEN2);
                else highlight(guiGraphics, slot, HIGHLIGHT_RED2);
            }
            return;
        }
        if (name.contains("✔")) highlight(guiGraphics, slot, HIGHLIGHT_GREEN2);
        else highlight(guiGraphics, slot, HIGHLIGHT_RED2);
    }

    private static void tryDrawHighlightTasks(GuiGraphics context, Slot slot) {
        if (!SBTConfig.get().hubSelectorHighlight.sblevelHighlight) return;
        var lines = slot.getItem().getOrDefault(DataComponents.LORE, ItemLore.EMPTY).lines();
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

    private static void tryDrawHighlightComs(GuiGraphics guiGraphics, Slot slot) {
        if (!SBTConfig.get().hubSelectorHighlight.unclaimedCommissionHighlight) return;
        if (slot.getItem().isEmpty()) return;
        var lines = slot.getItem().getOrDefault(DataComponents.LORE, ItemLore.EMPTY).lines();
        if (lines.isEmpty()) return;
        if (lines.getLast().getString().equals("Click to claim rewards!")) {
            highlight(guiGraphics, slot, HIGHLIGHT_GREEN2);
        }
    }

    private static void tryDrawHighlightContests(GuiGraphics guiGraphics, Slot slot) {
        if (!SBTConfig.get().hubSelectorHighlight.unclaimedContestHighlight) return;
        if (slot.getItem().isEmpty()) return;
        var lines = slot.getItem().getOrDefault(DataComponents.LORE, ItemLore.EMPTY).lines();
        if (lines.isEmpty()) return;
        if (lines.getLast().getString().equals("Click to claim reward!")) {
            highlight(guiGraphics, slot, HIGHLIGHT_GREEN2);
        }
    }

    private static void tryDrawHighlightEffects(GuiGraphics guiGraphics, Slot slot) {
        if (!SBTConfig.get().hubSelectorHighlight.toggleEffectHighlight) return;
        if (slot.getItem().isEmpty()) return;
        var lines = slot.getItem().getOrDefault(DataComponents.LORE, ItemLore.EMPTY).lines();
        if (lines.isEmpty()) return;
        var lastStr = lines.getLast().getString();
        if (lastStr.equals("Click to disable!")) {
            highlight(guiGraphics, slot, HIGHLIGHT_GREEN2);
        } else if (lastStr.equals("Click to enable!")) {
            highlight(guiGraphics, slot, HIGHLIGHT_RED2);
        }
    }

    private static void highlight(GuiGraphics guiGraphics, Slot slot, int color) {
        guiGraphics.fill(slot.x, slot.y, slot.x + SLOT_DIMENSION, slot.y + SLOT_DIMENSION, color);
    }

    public static class Config {
        @SerialEntry
        public boolean enabledRegular = true;

        @SerialEntry
        public boolean enabledDungeon = true;

        @SerialEntry
        public boolean hotmHighlight = true;

        @SerialEntry
        public boolean hotfHighlight = true;

        @SerialEntry
        public boolean widgetHighlight = true;

        @SerialEntry
        public boolean sblevelHighlight = true;

        @SerialEntry
        public boolean unclaimedCommissionHighlight = true;

        @SerialEntry
        public boolean unclaimedContestHighlight = true;

        @SerialEntry
        public boolean toggleEffectHighlight = true;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(key("menuHighlights.enabledRegular"))
                    .description(keyD("menuHighlights.enabledRegular"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                                    defaults.hubSelectorHighlight.enabledRegular,
                                    () -> config.hubSelectorHighlight.enabledRegular,
                                    value -> config.hubSelectorHighlight.enabledRegular = value
                    )
                    .build();
            var enabledDungeon = Option.<Boolean>createBuilder()
                    .name(key("menuHighlights.enabledDungeon"))
                    .description(keyD("menuHighlights.enabledDungeon"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                                    defaults.hubSelectorHighlight.enabledDungeon,
                                    () -> config.hubSelectorHighlight.enabledDungeon,
                                    value -> config.hubSelectorHighlight.enabledDungeon = value
                    )
                    .build();
            var hotmHighlight = Option.<Boolean>createBuilder()
                    .name(key("menuHighlights.hotmHighlight"))
                    .description(keyD("menuHighlights.hotmHighlight"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                                    defaults.hubSelectorHighlight.hotmHighlight,
                                    () -> config.hubSelectorHighlight.hotmHighlight,
                                    value -> config.hubSelectorHighlight.hotmHighlight = value
                    )
                    .build();

            var hotfHighlight = Option.<Boolean>createBuilder()
                    .name(key("menuHighlights.hotfHighlight"))
                    .description(keyD("menuHighlights.hotfHighlight"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                                    defaults.hubSelectorHighlight.hotfHighlight,
                                    () -> config.hubSelectorHighlight.hotfHighlight,
                                    value -> config.hubSelectorHighlight.hotfHighlight = value
                    )
                    .build();

            var widgetHighlight = Option.<Boolean>createBuilder()
                    .name(key("menuHighlights.widgetHighlight"))
                    .description(keyD("menuHighlights.widgetHighlight"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                                    defaults.hubSelectorHighlight.widgetHighlight,
                                    () -> config.hubSelectorHighlight.widgetHighlight,
                                    value -> config.hubSelectorHighlight.widgetHighlight = value
                    )
                    .build();
            var sblevelHighlight = Option.<Boolean>createBuilder()
                    .name(key("menuHighlights.sblevelHighlight"))
                    .description(keyD("menuHighlights.sblevelHighlight"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                                    defaults.hubSelectorHighlight.sblevelHighlight,
                                    () -> config.hubSelectorHighlight.sblevelHighlight,
                                    value -> config.hubSelectorHighlight.sblevelHighlight = value
                    )
                    .build();

            var unclaimedCommissionHighlight = Option.<Boolean>createBuilder()
                    .name(key("menuHighlights.unclaimedCommissionHighlight"))
                    .description(keyD("menuHighlights.unclaimedCommissionHighlight"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                                    defaults.hubSelectorHighlight.unclaimedCommissionHighlight,
                                    () -> config.hubSelectorHighlight.unclaimedCommissionHighlight,
                                    value -> config.hubSelectorHighlight.unclaimedCommissionHighlight = value
                    )
                    .build();
            var unclaimedContestHighlight = Option.<Boolean>createBuilder()
                    .name(key("menuHighlights.unclaimedContestHighlight"))
                    .description(keyD("menuHighlights.unclaimedContestHighlight"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                                    defaults.hubSelectorHighlight.unclaimedContestHighlight,
                                    () -> config.hubSelectorHighlight.unclaimedContestHighlight,
                                    value -> config.hubSelectorHighlight.unclaimedContestHighlight = value
                    )
                    .build();
            var toggleEffectHighlight = Option.<Boolean>createBuilder()
                    .name(key("menuHighlights.toggleEffectHighlight"))
                    .description(keyD("menuHighlights.toggleEffectHighlight"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                                    defaults.hubSelectorHighlight.toggleEffectHighlight,
                                    () -> config.hubSelectorHighlight.toggleEffectHighlight,
                                    value -> config.hubSelectorHighlight.toggleEffectHighlight = value
                    )
                    .build();



            return OptionGroup.createBuilder()
                    .name(key("menuHighlights"))
                    .description(keyD("menuHighlights"))
                    .option(enabled)
                    .option(enabledDungeon)
                    .option(hotmHighlight)
                    .option(hotfHighlight)
                    .option(widgetHighlight)
                    .option(sblevelHighlight)
                    .option(unclaimedCommissionHighlight)
                    .option(unclaimedContestHighlight)
                    .option(toggleEffectHighlight)
                    .build();
        }
    }
}
