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
package wtf.cheeze.sbt.utils.skyblock;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.persistent.PersistentData;
import wtf.cheeze.sbt.config.persistent.ProfileData;
import wtf.cheeze.sbt.events.DrawSlotEvents;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.enums.Skill;
import wtf.cheeze.sbt.utils.errors.ErrorHandler;
import wtf.cheeze.sbt.utils.errors.ErrorLevel;

import java.util.regex.Pattern;

public class ProfileManager {
    private static final Pattern ID_MESSAGE_PATTERN = Pattern.compile("Profile ID: (.{36})");
    private static final Pattern SKILL_PATTERN = Pattern.compile("(.*) (\\d\\d?)");
    private static final Pattern SKILL_LEVEL_UP_PATTERN = Pattern.compile("SKILL LEVEL UP (.*) \\d\\d?➜(\\d\\d?)");
    private static final Pattern WISDOM_PATTERN = Pattern.compile("☯ (.+) Wisdom (\\d+)");
    //SKILL LEVEL UP Farming 8➜9

    public static void registerEvents() {
        // TODO: If this is ever added to the Mod API, switch to that method
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (overlay) return;
            var messageString = message.getString();
            var matcher = ID_MESSAGE_PATTERN.matcher(messageString);
            if (matcher.find()) {
                SkyblockData.currentProfile = matcher.group(1);
                PersistentData.get().profiles.putIfAbsent(SkyblockData.getCurrentProfileUnique(), new ProfileData());
                PersistentData.get().requestSave();
                return;

            }
            var text = TextUtils.removeFormatting(messageString).trim();
            var levelUpMatcher = SKILL_LEVEL_UP_PATTERN.matcher(text);
            if (levelUpMatcher.find()) {
                var skill = SkyblockUtils.strictCastStringToSkill(levelUpMatcher.group(1));
                if (skill == null) return;
                var profile = PersistentData.get().currentProfile();
                if (profile == null) return;
                var level = Integer.parseInt(levelUpMatcher.group(2));
                profile.skillLevels.put(skill, level);
                PersistentData.get().requestSave();
            }
        });

        //TODO: Switch to public API
        DrawSlotEvents.BEFORE_ITEM.register(((screenTitle, context, slot) -> {
            if (screenTitle.getString().equals("Your Skills")) {
                var name = slot.getStack().getName().getString();
                var matcher = SKILL_PATTERN.matcher(name);
                if (matcher.matches()) {
                    var skill = SkyblockUtils.strictCastStringToSkill(matcher.group(1));
                    if (skill == null) return;
                    var profile = PersistentData.get().currentProfile();
                    if (profile == null) return;
                    var level = Integer.parseInt(matcher.group(2));
                    profile.skillLevels.put(skill, level);
                }
                PersistentData.get().requestSave();
            } else if (screenTitle.getString().equals("Your Equipment and Stats") && slot.getStack() != null && slot.getStack().getName().getString().equals("Wisdom Stats")) {
                var lines = slot.getStack().getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).lines();
                for (var line: lines) {
                    var matcher = WISDOM_PATTERN.matcher(line.getString());
                    if (matcher.find()) {
                        var profile = PersistentData.get().currentProfile();
                        if (profile == null) return;
                        var wisdom = Integer.parseInt(matcher.group(2));
                        var skill = SkyblockUtils.strictCastStringToSkill(matcher.group(1));
                        if (skill == Skill.UNKNOWN) return;
                        profile.skillWisdom.put(SkyblockUtils.strictCastStringToSkill(matcher.group(1)), wisdom);
                        PersistentData.get().requestSave();
                    }
                }
            }
        }));
    }

}

