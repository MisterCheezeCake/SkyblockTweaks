/*
 * Copyright (C) 2025 MisterCheezeCake
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
package wtf.cheeze.sbt.utils;

import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.utils.constants.DisabledFeatures;
import wtf.cheeze.sbt.utils.constants.loader.ConstantLoader;
import wtf.cheeze.sbt.utils.constants.loader.Constants;
import wtf.cheeze.sbt.utils.render.Colors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Certain SBT features have kill-switches which can be toggled remotely for specific versions. Features which interact with the server
 * and have the potential to be restricted at some point in the future should have a kill-switch. The following kill switches are available:
 * <ul>
 *     <li>chat_protections_coop</li>
 *     <li>chat_protections_ip</li>
 *     <li>mouse_lock</li>
 *     <li>party_commands</li>
 *     <li>recipe_book_redirect</li>
 *
 * </ul>
 */
public class KillSwitch {
    private static Map<String, DisabledFeatures.Entry> disabledFeatures = Map.of();
    private static final Map<String, Boolean> sentWarnings = new HashMap<>();

    public static void registerEvents() {
        ConstantLoader.RELOAD.register(() -> {
            disabledFeatures = Constants.disabledFeatures().getDisabledFeatures();
            for (var feature: disabledFeatures.keySet()) {
                if (shouldKill(feature)) {
                    sendWarning(feature);
                }
            }
        });
    }



    public static boolean shouldKill(String feature) {
        return disabledFeatures.containsKey(feature);

    }

    private static void sendWarning(String feature) {
        if (sentWarnings.containsKey(feature)) return;
        sentWarnings.put(feature, true);
        var entry = disabledFeatures.get(feature);
        var message = entry.message();
        var link = entry.link();
        var text = TextUtils.join(
                MessageManager.PREFIX,
                TextUtils.SPACE,
                TextUtils.withColor(message, Colors.RED)
        );
        if (!link.isEmpty()) {
            text = TextUtils.getTextThatLinksToURL(TextUtils.join(
                    text,
                    TextUtils.SPACE,
                    TextUtils.withColor("[Click here for more info]", Colors.CYAN)
            ), TextUtils.withColor("Click here for more info", Colors.CYAN), link);
        }
        NotificationHandler.pushChat(text);

    }


}
