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
package wtf.cheeze.sbt.config;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import wtf.cheeze.sbt.config.categories.General;
import wtf.cheeze.sbt.config.categories.Huds;
import wtf.cheeze.sbt.features.MenuHighlights;
import wtf.cheeze.sbt.features.PartyCommands;
import wtf.cheeze.sbt.utils.Version;
import wtf.cheeze.sbt.utils.actionbar.ActionBarTransformer;

public class ConfigImpl {
    @SerialEntry // This defines the spec version of the config, so that in the future, migration code can be written if necessary
    public int configVersion = 1;

    @SerialEntry
    public Version.NotificationStream notificationStream = Version.NotificationStream.ALPHA;

    @SerialEntry
    public General.HudTweaks hudTweaks = new General.HudTweaks();

    @SerialEntry
    public General.InventoryTweaks inventory = new General.InventoryTweaks();

    @SerialEntry
    public ActionBarTransformer.Config actionBarFilters = new ActionBarTransformer.Config();

    // This is called hubSelectorHighlight for legacy reasons, mainly that I really don't want to write config migration right now
    @SerialEntry
    public MenuHighlights.Config hubSelectorHighlight = new MenuHighlights.Config();

    @SerialEntry
    public PartyCommands.Config partyCommands = new PartyCommands.Config();

    @SerialEntry
    public Huds huds = new Huds();
}
