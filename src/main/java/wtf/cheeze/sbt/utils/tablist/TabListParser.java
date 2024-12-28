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
package wtf.cheeze.sbt.utils.tablist;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.mixin.PlayerListHudAccessor;

import java.util.ArrayList;
import java.util.Comparator;



public class TabListParser {

    private static final Comparator<PlayerListEntry> ORDER = PlayerListHudAccessor.getEntryOrdering();


    /**
     * Serves as the Stage 1 parser for the TabList, gets the data into lines sorted by widget, which can then be used by Stage 2 processors if needed for a feature
     */
    public static TabListData parseTabList() {

        try {
            var data = new TabListData();

            boolean inInfoColumn = false;
            WidgetType currentWidget = null;
            var network = getNetworkHandler();
            if (network == null) return TabListData.EMPTY;
            for (PlayerListEntry entry : network.getPlayerList().stream().sorted(ORDER).toList()) {
                var displayName = entry.getDisplayName();
                if (displayName == null) continue;
                var content = displayName.getString();
                var profileName = entry.getProfile().getName();
                if (profileName.endsWith("a")) {
                    if (content.trim().equals("Info")) {
                        inInfoColumn = true;
                        continue;
                    }
                }
                if (!inInfoColumn) continue;
                if (content.isBlank()) continue;
                if (content.startsWith(" ")) {
                    if (currentWidget != null) {
                        data.widgetLines.get(currentWidget).add(content);
                    }

                } else {
                    var widget = WidgetType.byPrefix(content.split(":")[0]);
                    if (widget != null) {
                        currentWidget = widget;
                        data.widgetLines.putIfAbsent(currentWidget, new ArrayList<>());
                        data.widgetLines.get(currentWidget).add(content);
                        continue;
                    } else {
                        currentWidget = null;
                    }

                }
            }
            data.activeWidgets = data.widgetLines.keySet();
            return data;
        } catch (Exception e) {
            SkyblockTweaks.LOGGER.error("Failed to parse tablist", e);
            return TabListData.EMPTY;
        }
    }

    private static ClientPlayNetworkHandler getNetworkHandler() {
        return SkyblockTweaks.mc.getNetworkHandler();
    }

    public static void registerEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!SkyblockTweaks.DATA.inSB) return;
            SkyblockTweaks.DATA.update(parseTabList());
        });
    }


}
