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
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wtf.cheeze.sbt.config.SBTCommand;
import wtf.cheeze.sbt.config.SkyBlockTweaksConfig;
import wtf.cheeze.sbt.utils.HTTPUtil;
import wtf.cheeze.sbt.utils.SkyBlockData;
import wtf.cheeze.sbt.features.*;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.Version;
import wtf.cheeze.sbt.utils.actionbar.ActionBarTransformer;
import wtf.cheeze.sbt.utils.hud.HUD;

import java.util.ArrayList;

public class SkyBlockTweaks implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("SkyBlockTweaks");
	public static final SkyBlockData DATA = new SkyBlockData();
	public static final SkyBlockTweaksConfig CONFIG = new SkyBlockTweaksConfig();
	public static final ArrayList<HUD> HUDS = new ArrayList<HUD>();
	public static final Version VERSION = new Version(Version.VersionType.ALPHA, 0, 1, 0, 2);
	public static final Gson GSON = new Gson();
	public static final ArrayList<Text> NOTIFICATION_QUEUE = new ArrayList<Text>();

	public static final String REMOTE_VERSION_FILE = "https://raw.githubusercontent.com/MisterCheezeCake/RemoteData/main/SBT/updateInfo.json";

	@Override
	public void onInitialize() {
		boolean loaded = CONFIG.HANDLER.load();

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

		if (!loaded) {
			LOGGER.error("Failed to load config!");
		}


		SBTCommand.register();



		ClientReceiveMessageEvents.MODIFY_GAME.register((message, overlay) -> {

			if (!overlay) return message;
			//SkyBlockTweaks.LOGGER.info("Old: " + message.getString());
			var data = ActionBarTransformer.extractDataAndRunTransformation(message.getString());
			//SkyBlockTweaks.LOGGER.info("New: " + data.transformedText);
			SkyBlockTweaks.DATA.update(data);
			SkyBlockTweaks.DATA.isThePlayerHoldingADrill();
			return Text.of(data.transformedText);

		});
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

		HudRenderCallback.EVENT.register((context, tickCounter) -> {
			HUDS.forEach(hud -> hud.render(context, false));
		});

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			if (NOTIFICATION_QUEUE.size() == 0) return;
			new Thread(() -> {
				try {
					Thread.sleep(2500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				client.execute(() -> {
					for (Text message : NOTIFICATION_QUEUE) {
						client.player.sendMessage(message, false);
					}
					NOTIFICATION_QUEUE.clear();
				});
			}).start();


		});

		if (SkyBlockTweaks.CONFIG.config.notificationStream != Version.NotificationStream.NONE && VERSION.STREAM != Version.VersionType.UNSTABLE) {
			try {
				LOGGER.info("Checking for updates");
				var result = HTTPUtil.get(REMOTE_VERSION_FILE);
				Version.RemoteVersionFile remote = GSON.fromJson(result, Version.RemoteVersionFile.class);
				if (remote == null) {
					LOGGER.error("Failed to parse remote version file");
				} else {
					if (remote.enabled != false) {
						switch (SkyBlockTweaks.CONFIG.config.notificationStream) {
							case Version.NotificationStream.ALPHA -> {
								if (remote.latestAlpha == null) {
									break;
								}
								var version = new Version(remote.latestAlpha.versionString);
								var comparison = Version.compareVersions(version, SkyBlockTweaks.VERSION);
								if (comparison == Version.VersionComparison.GREATER) {
									var link = Version.getModrinthLink(remote.latestAlpha.modrinthName);
									var message = TextUtils.getTextThatLinksToURL("§7[§aSkyblockTweaks§f§7] §3Update §e" + remote.latestAlpha.modrinthName + " §3is available! §2[Download]", "§3Click to open Modrinth in your browser", link);
									NOTIFICATION_QUEUE.add(message);
								}
							}
							case Version.NotificationStream.BETA -> {
								if (remote.latestBeta == null) {
									break;
								}
								var version = new Version(remote.latestBeta.versionString);
								var comparison = Version.compareVersions(version, SkyBlockTweaks.VERSION);
								if (comparison == Version.VersionComparison.GREATER) {
									var link = Version.getModrinthLink(remote.latestBeta.modrinthName);
									var message = TextUtils.getTextThatLinksToURL("§7[§aSkyblockTweaks§f§7] §3Update §e" + remote.latestBeta.modrinthName + " §3is available! §2[Download]", "§3Click to open Modrinth in your browser", link);
									NOTIFICATION_QUEUE.add(message);
								}

							}
							case Version.NotificationStream.RELEASE -> {
								if (remote.latestRelease == null) {
									break;
								}
								var version = new Version(remote.latestRelease.versionString);
								var comparison = Version.compareVersions(version, SkyBlockTweaks.VERSION);
								if (comparison == Version.VersionComparison.GREATER) {
									var link = Version.getModrinthLink(remote.latestRelease.modrinthName);
									var message = TextUtils.getTextThatLinksToURL("§7[§aSkyblockTweaks§f§7] §3Update §e" + remote.latestRelease.modrinthName + " §3is available! §2[Download]", "§3Click to open Modrinth in your browser", link);
									NOTIFICATION_QUEUE.add(message);
								}

							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}