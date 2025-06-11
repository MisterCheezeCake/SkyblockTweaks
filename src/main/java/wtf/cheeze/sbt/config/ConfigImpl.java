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
import wtf.cheeze.sbt.config.categories.Mining;
import wtf.cheeze.sbt.features.overlay.BrewingStandOverlay;
import wtf.cheeze.sbt.features.overlay.MenuHighlights;
import wtf.cheeze.sbt.features.overlay.MinionExp;
import wtf.cheeze.sbt.features.chat.ChatProtections;
import wtf.cheeze.sbt.features.chat.PartyFeatures;
import wtf.cheeze.sbt.features.overlay.ReforgeOverlay;
import wtf.cheeze.sbt.utils.actionbar.ActionBarTransformer;
import wtf.cheeze.sbt.utils.constants.loader.ConstantLoader;
import wtf.cheeze.sbt.utils.version.UpdateChecker;

public class ConfigImpl extends VersionedObject {
    @SerialEntry // This defines the spec version of the config, so that in the future, migration code can be written if necessary
    public int configVersion = 1;

    @SerialEntry
    public UpdateChecker.NotificationStream notificationStream = UpdateChecker.NotificationStream.ALPHA;

    @SerialEntry
    public boolean chatAllErrors = true;

    @SerialEntry
    public boolean chatModApiErrors = true;

    @SerialEntry
    public ConstantLoader.Config constantLoader = new ConstantLoader.Config();

    @SerialEntry
    public General.HudTweaks hudTweaks = new General.HudTweaks();

    @SerialEntry
    public General.InventoryTweaks inventory = new General.InventoryTweaks();

    @SerialEntry
    public MinionExp.Config minionExp = new MinionExp.Config();

    @SerialEntry
    public ReforgeOverlay.Config reforgeOverlay = new ReforgeOverlay.Config();

    @SerialEntry
    public ActionBarTransformer.Config actionBarFilters = new ActionBarTransformer.Config();

    // This is called hubSelectorHighlight for legacy reasons, mainly that I really don't want to write config migration right now
    @SerialEntry
    public MenuHighlights.Config hubSelectorHighlight = new MenuHighlights.Config();

    @SerialEntry
    public PartyFeatures.Config partyCommands = new PartyFeatures.Config();

    @SerialEntry
    public BrewingStandOverlay.Config brewingStandOverlay = new BrewingStandOverlay.Config();

    @SerialEntry
    public ChatProtections.Config chatProtections = new ChatProtections.Config();

    @SerialEntry
    public Huds huds = new Huds();

    @SerialEntry
    public Mining mining = new Mining();


}
