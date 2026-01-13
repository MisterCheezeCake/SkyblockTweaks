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
package wtf.cheeze.sbt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wtf.cheeze.sbt.command.SBTCommand;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.config.persistent.PersistentData;
import wtf.cheeze.sbt.events.HudRenderEvents;
import wtf.cheeze.sbt.features.mining.FetchurFeatures;
import wtf.cheeze.sbt.features.mining.MiningTitles;
import wtf.cheeze.sbt.features.misc.HudElementHider;
import wtf.cheeze.sbt.features.overlay.MenuHighlights;
import wtf.cheeze.sbt.features.overlay.MinionExp;
import wtf.cheeze.sbt.features.misc.MouseLock;
import wtf.cheeze.sbt.features.overlay.ReforgeOverlay;
import wtf.cheeze.sbt.hud.HudManager;
import wtf.cheeze.sbt.utils.KillSwitch;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;
import wtf.cheeze.sbt.utils.text.NotificationHandler;
import wtf.cheeze.sbt.utils.version.UpdateChecker;
import wtf.cheeze.sbt.utils.version.Version;
import wtf.cheeze.sbt.utils.actionbar.ActionBarTransformer;
import wtf.cheeze.sbt.utils.constants.loader.ConstantLoader;
import wtf.cheeze.sbt.utils.skyblock.ModAPI;
import wtf.cheeze.sbt.utils.skyblock.ProfileManager;
import wtf.cheeze.sbt.utils.tablist.TabListParser;
import wtf.cheeze.sbt.features.chat.*;
import wtf.cheeze.sbt.utils.version.VersionType;

public class SkyblockTweaks implements ModInitializer {
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Logger LOGGER = LoggerFactory.getLogger("SkyblockTweaks");

	public static final Version VERSION = new Version(VersionType.ALPHA, 0, 1, 0, 18);
	//public static final Version VERSION = new Version(VersionType.UNSTABLE);


	@Override
	public void onInitialize() {
		SBTConfig.load();
		PersistentData.registerEvents();

		KillSwitch.registerEvents();
		ConstantLoader.registerEvents();
		HudManager.registerEvents();
		SBTCommand.registerEvents();
		ActionBarTransformer.registerEvents();
		SkyblockData.registerEvents();
		NotificationHandler.registerEvents();
		ModAPI.registerEvents();
		PartyFeatures.registerEvents();
		ChatProtections.registerEvents();
		ProfileManager.registerEvents();
		TabListParser.registerEvents();
		MouseLock.registerEvents();
		MenuHighlights.registerEvents();
		MinionExp.registerEvents();
		MiningTitles.registerEvents();
		FetchurFeatures.registerEvents();
		ReforgeOverlay.registerEvents();
		HudRenderEvents.registerEvents();
		UpdateChecker.checkForUpdates();

        HudElementHider.hideElements();
	}
}