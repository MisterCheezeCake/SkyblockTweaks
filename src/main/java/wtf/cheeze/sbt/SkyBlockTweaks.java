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
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wtf.cheeze.sbt.config.SBTCommand;
import wtf.cheeze.sbt.config.SkyBlockTweaksConfig;
import wtf.cheeze.sbt.features.huds.*;
import wtf.cheeze.sbt.utils.*;
import wtf.cheeze.sbt.utils.actionbar.ActionBarTransformer;
import wtf.cheeze.sbt.utils.hud.HUD;

import java.util.ArrayList;

public class SkyBlockTweaks implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("SkyBlockTweaks");
	public static final SkyBlockData DATA = new SkyBlockData();
	public static final SkyBlockTweaksConfig CONFIG = new SkyBlockTweaksConfig();
	public static final ArrayList<HUD> HUDS = new ArrayList<HUD>();
	public static final Version VERSION = new Version(Version.VersionType.UNSTABLE);
	public static final Gson GSON = new Gson();


	@Override
	public void onInitialize() {
		boolean loaded = CONFIG.HANDLER.load();

		if (!loaded) {
			LOGGER.error("Failed to load config!");
		}

		// This fixes config not actually loading on initial startup... for some reason
		CONFIG.getScreen(null);

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

		HudRenderCallback.EVENT.register((context, tickCounter) -> {
			HUDS.forEach(hud -> hud.render(context, false));
		});

		SBTCommand.registerEvents();
		ActionBarTransformer.registerEvents();
		NotificationHandler.registerEvents();

		UpdateChecker.checkForUpdates();


		// TODO: Checking this every tick may be overkill, change this later
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			MinecraftClient mc = MinecraftClient.getInstance();

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