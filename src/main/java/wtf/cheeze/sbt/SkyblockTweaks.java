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
package wtf.cheeze.sbt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wtf.cheeze.sbt.command.SBTCommand;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.config.migration.BarColorTransformation;
import wtf.cheeze.sbt.config.migration.MigrationManager;
import wtf.cheeze.sbt.config.persistent.PersistentData;
import wtf.cheeze.sbt.features.MenuHighlights;
import wtf.cheeze.sbt.features.MouseLock;
import wtf.cheeze.sbt.hud.HudManager;
import wtf.cheeze.sbt.utils.NotificationHandler;
import wtf.cheeze.sbt.utils.UpdateChecker;
import wtf.cheeze.sbt.utils.Version;
import wtf.cheeze.sbt.utils.actionbar.ActionBarTransformer;
import wtf.cheeze.sbt.utils.skyblock.ModAPIUtils;
import wtf.cheeze.sbt.utils.skyblock.ProfileManager;
import wtf.cheeze.sbt.utils.tablist.TabListParser;
import wtf.cheeze.sbt.features.chat.*;

public class SkyblockTweaks implements ModInitializer {
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Logger LOGGER = LoggerFactory.getLogger("SkyblockTweaks");


	public static final PersistentData PD = PersistentData.load();
	//public static final Version VERSION = new Version(Version.VersionType.ALPHA, 0, 1, 0, 10);
	public static final Version VERSION = new Version(Version.VersionType.UNSTABLE);



	@Override
	public void onInitialize() {

		//MigrationManager.handleMigrations();

		SBTConfig.HANDLER.load();

		MigrationManager.runTransformation(BarColorTransformation.INSTANCE);


		HudManager.registerEvents();
		SBTCommand.registerEvents();
		ActionBarTransformer.registerEvents();
		NotificationHandler.registerEvents();
		ModAPIUtils.registerEvents();
		PartyFeatures.registerEvents();
		ChatProtections.registerEvents();
		ProfileManager.registerEvents();
		TabListParser.registerEvents();
		MouseLock.registerEvents();
		MenuHighlights.registerEvents();
		UpdateChecker.checkForUpdates();
	}
}