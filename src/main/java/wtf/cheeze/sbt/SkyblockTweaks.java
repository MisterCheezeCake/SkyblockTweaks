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

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wtf.cheeze.sbt.command.SBTCommand;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.config.migration.BarColorTransformation;
import wtf.cheeze.sbt.config.migration.MigrationManager;
import wtf.cheeze.sbt.config.persistent.PersistentData;
import wtf.cheeze.sbt.features.chat.ChatProtections;
import wtf.cheeze.sbt.features.chat.PartyFeatures;
import wtf.cheeze.sbt.features.huds.*;
import wtf.cheeze.sbt.utils.*;
import wtf.cheeze.sbt.utils.actionbar.ActionBarTransformer;
import wtf.cheeze.sbt.hud.HUD;
import wtf.cheeze.sbt.utils.skyblock.ModAPIUtils;
import wtf.cheeze.sbt.utils.skyblock.ProfileManager;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;

import java.util.ArrayList;

public class SkyblockTweaks implements ModInitializer {
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Logger LOGGER = LoggerFactory.getLogger("SkyblockTweaks");
	public static final SkyblockData DATA = new SkyblockData();
	public static final PersistentData PD = PersistentData.load();
	public static final ArrayList<HUD> HUDS = new ArrayList<HUD>();
	public static final Version VERSION = new Version(Version.VersionType.ALPHA, 0, 1, 0, 8);
	public static final MinecraftClient mc = MinecraftClient.getInstance();


	@Override
	public void onInitialize() {

		//MigrationManager.handleMigrations();


		SBTConfig.HANDLER.load();

		MigrationManager.runTransformation(BarColorTransformation.INSTANCE);


		HUDS.add(SkillHUDManager.INSTANCE.SKILL_HUD);
		HUDS.add(SkillHUDManager.INSTANCE.SKILL_BAR);

		HUDS.add(new SpeedHUD());
		HUDS.add(new DefenseHUD());
		HUDS.add(new EhpHUD());
		HUDS.add(new DamageReductionHUD());
		HUDS.add(new HealthHUD());
		HUDS.add(new ManaHUD());
		HUDS.add(new OverflowManaHUD());
		HUDS.add(new DrillFuelHUD());

		HUDS.add(new DrillFuelBar());
		HUDS.add(new HealthBar());
		HUDS.add(new ManaBar());

		HUDS.add(new CoordinatesHUD());
		HUDS.add(new RealTimeHUD());
		HUDS.add(new FpsHUD());

		HUDS.add(new TickerHUD());
		HUDS.add(new QuiverHUD());
		HUDS.add(new ArmorStackHUD());

		HudRenderCallback.EVENT.register((context, tickCounter) -> {
			HUDS.forEach(hud -> hud.render(context, false));
		});

		SBTCommand.registerEvents();
		ActionBarTransformer.registerEvents();
		NotificationHandler.registerEvents();
		ModAPIUtils.registerEvents();
		PartyFeatures.registerEvents();
		ChatProtections.registerEvents();
		ProfileManager.registerEvents();

		UpdateChecker.checkForUpdates();

		// TODO: Checking this every tick may be overkill, change this later
		// TODO: Use the mod api for this
		ClientTickEvents.END_CLIENT_TICK.register(client -> {

			if (mc != null && mc.world != null) {
				Scoreboard scoreboard = mc.world.getScoreboard();
				if (scoreboard != null) {
					ScoreboardObjective objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
					if (objective != null) {
						var name = objective.getDisplayName().getString();
						if (name.contains("SKYBLOCK") || name.contains("SKIBLOCK")) {
							DATA.inSB = true;
						} else {
							DATA.inSB = false;
						}
					} else {
						DATA.inSB = false;
					}
				} else {
					DATA.inSB = false;
				}
			} else {
				DATA.inSB = false;
			}

		});



	}
}