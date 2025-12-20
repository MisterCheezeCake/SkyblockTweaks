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

import wtf.cheeze.sbt.SkyblockTweaks;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TabListData {
    public Set<WidgetType> activeWidgets;
    public Map<WidgetType, List<String>> widgetLines = new EnumMap<>(WidgetType.class);

    public String serialize() {
       return SkyblockTweaks.GSON.toJson(this);
    }

    public static final TabListData EMPTY = new TabListData();
}
