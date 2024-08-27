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
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.persistent.ProfileData;
import wtf.cheeze.sbt.utils.TextUtils;

import java.util.regex.Pattern;

public class ProfileManager {
    private static final Pattern ID_MESSAGE_PATTERN = Pattern.compile("Profile ID: (.{36})");
    private static final Pattern SKILL_PATTERN = Pattern.compile("(.*) (\\d\\d?)");
    private static final Pattern SKILL_LEVEL_UP_PATTERN = Pattern.compile("SKILL LEVEL UP (.*) \\d\\d?➜(\\d\\d?)");
    //SKILL LEVEL UP Farming 8➜9

    public static void registerEvents() {
        // TODO: If this is ever added to the Mod API, switch to that method
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (overlay) return;
            var messageString = message.getString();
            var matcher = ID_MESSAGE_PATTERN.matcher(messageString);
            if (matcher.find()) {
                var id = matcher.group(1);
                SkyblockTweaks.DATA.currentProfile = id;
                SkyblockTweaks.PD.profiles.putIfAbsent(SkyblockTweaks.DATA.getCurrentProfileUnique(), new ProfileData());
                SkyblockTweaks.PD.save();
                return;

            }
            var ss = TextUtils.removeColorCodes(messageString).trim();
            var mm = SKILL_LEVEL_UP_PATTERN.matcher(ss);
            if (mm.find()) {
                var skill = SkyblockUtils.strictCastStringToSkill(mm.group(1));
                if (skill == null) return;
                var profile = SkyblockTweaks.PD.profiles.get(SkyblockTweaks.DATA.getCurrentProfileUnique());
                if (profile == null) return;
                var level = Integer.parseInt(mm.group(2));
                profile.skillLevels.put(skill, level);
                SkyblockTweaks.PD.save();
            }
        });

        ScreenEvents.AFTER_INIT.register(((client, screen, scaledWidth, scaledHeight) -> {
            if (screen.getTitle().getString(
            ).equals("Your Skills")) {
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (client.currentScreen != screen) return;
                    var containerScreen = (GenericContainerScreen) screen;
                    var handler = containerScreen.getScreenHandler();
                    for (int i = 0; i < 45; i++) {
                        var stack = handler.getSlot(i).getStack();
                        var name = stack.getName().getString();
                        var matcher = SKILL_PATTERN.matcher(name);
                        if (matcher.matches()) {
                            var skill = SkyblockUtils.strictCastStringToSkill(matcher.group(1));
                            if (skill == null) return;
                            var profile = SkyblockTweaks.PD.profiles.get(SkyblockTweaks.DATA.getCurrentProfileUnique());
                            if (profile == null) return;
                            var level = Integer.parseInt(matcher.group(2));
                            profile.skillLevels.put(skill, level);
                        }
                    }
                    SkyblockTweaks.PD.save();
                }).start();

            }
        }));
    }

}
