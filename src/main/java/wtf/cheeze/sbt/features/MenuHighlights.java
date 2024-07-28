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
import wtf.cheeze.sbt.SkyBlockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SkyBlockTweaksConfig;

import java.util.regex.Pattern;

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
        if (!SkyBlockTweaks.CONFIG.config.hubSelectorHighlight.enabledRegular) return;
        if (!slot.getStack().getName().getString().contains("SkyBlock Hub #")) return;
        var lines = slot.getStack().getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).lines();
        var matcher = PLAYER_COUNT_PATTERN.matcher(lines.getFirst().getString());
        if (!matcher.matches()) return;
        var playerCount = Integer.parseInt(matcher.group(1));
        if (playerCount >= 60) context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, HIGHLIGHT_RED);
        else if (playerCount >= 40) context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, HIGHLIGHT_ORANGE);
        else if (playerCount >= 20) context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, HIGHLIGHT_YELLOW);
        else context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, HIGHLIGHT_GREEN);

    }
    public static void tryDrawHighlightDH(DrawContext context, Slot slot) {
        if (!SkyBlockTweaks.CONFIG.config.hubSelectorHighlight.enabledDungeon) return;
        if (!slot.getStack().getName().getString().contains("Dungeon Hub #")) return;
        var lines = slot.getStack().getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).lines();
        var matcher = PLAYER_COUNT_PATTERN_DH.matcher(lines.getFirst().getString());
        if (!matcher.matches()) return;
        var playerCount = Integer.parseInt(matcher.group(1));
        if (playerCount >= 18) context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, HIGHLIGHT_RED);
        else if (playerCount >= 12) context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, HIGHLIGHT_ORANGE);
        else if (playerCount >= 6) context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, HIGHLIGHT_YELLOW);
        else context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, HIGHLIGHT_GREEN);
    }
    public static void tryDrawHighlightHOTM(DrawContext context, Slot slot) {
        if (!SkyBlockTweaks.CONFIG.config.hubSelectorHighlight.hotmHighlight) return;
        var lines = slot.getStack().getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).lines();
        for (var line: lines) {
            switch (line.getString()) {
                case "ENABLED", "SELECTED" -> {
                    context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, HIGHLIGHT_GREEN2);
                    return;
                }
                case "DISABLED", "Click to select!" ->   {
                    context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, HIGHLIGHT_RED2);
                    return;
                }
                case "1 Token of the Mountain" -> {
                    context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, HIGHLIGHT_GREY);
                    return;
                }
            }
        }
    }

    public static class Config {
        @SerialEntry
        public boolean enabledRegular = true;

        @SerialEntry
        public boolean enabledDungeon = true;

        @SerialEntry
        public boolean hotmHighlight = true;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Text.literal("Hub Selector Highlights"))
                    .description(OptionDescription.of(Text.literal("Whether or not to highlight hubs in the hub selector based on their capacity")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                                    defaults.hubSelectorHighlight.enabledRegular,
                                    () -> config.hubSelectorHighlight.enabledRegular,
                                    value -> config.hubSelectorHighlight.enabledRegular = (Boolean) value
                    )
                    .build();
            var enabledDungeon = Option.<Boolean>createBuilder()
                    .name(Text.literal("Dungeon Hub Selector Highlights"))
                    .description(OptionDescription.of(Text.literal("Whether or not to highlight hubs in the dungeon hub selector based on their capacity")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                                    defaults.hubSelectorHighlight.enabledDungeon,
                                    () -> config.hubSelectorHighlight.enabledDungeon,
                                    value -> config.hubSelectorHighlight.enabledDungeon = (Boolean) value
                    )
                    .build();
            var hotmHighlight = Option.<Boolean>createBuilder()
                    .name(Text.literal("HOTM Highlights"))
                    .description(OptionDescription.of(Text.literal("Whether or not to highlight HOTM perks based on their status (enabled/disabled)")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                                    defaults.hubSelectorHighlight.hotmHighlight,
                                    () -> config.hubSelectorHighlight.hotmHighlight,
                                    value -> config.hubSelectorHighlight.hotmHighlight = (Boolean) value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(Text.literal("Menu Highlights"))
                    .description(OptionDescription.of(Text.literal("Settings for Menu Highlights and coloring")))
                    .option(enabled)
                    .option(enabledDungeon)
                    .option(hotmHighlight)
                    .build();
        }
    }
}
