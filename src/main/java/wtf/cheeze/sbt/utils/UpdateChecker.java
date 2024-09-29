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
package wtf.cheeze.sbt.utils;

import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.SBTConfig;

public class UpdateChecker {
    public static final String REMOTE_VERSION_FILE = "https://raw.githubusercontent.com/MisterCheezeCake/RemoteData/main/SBT/updateInfo.json";

    public static void checkForUpdates() {
        if (SBTConfig.get().notificationStream != Version.NotificationStream.NONE && SkyblockTweaks.VERSION.STREAM != Version.VersionType.UNSTABLE) {
            try {
                SkyblockTweaks.LOGGER.info("Checking for updates");
                var result = HTTPUtils.get(REMOTE_VERSION_FILE);
                //LOGGER.info(result);
                Version.RemoteVersionFile remote = SkyblockTweaks.GSON.fromJson(result, Version.RemoteVersionFile.class);
                if (remote == null) {
                    SkyblockTweaks.LOGGER.error("Failed to parse remote version file");
                } else {
                    if (remote.enabled != false) {
                        switch (SBTConfig.get().notificationStream) {
                            case Version.NotificationStream.ALPHA -> {
                                if (remote.latestAlpha == null) {
                                    break;
                                }
                                var version = new Version(remote.latestAlpha.versionString);
                                var comparison = Version.compareVersions(version, SkyblockTweaks.VERSION);
                                if (comparison == Version.VersionComparison.GREATER) {
                                    var link = Version.getModrinthLink(remote.latestAlpha.modrinthName);
                                    var message = TextUtils.getTextThatLinksToURL("§7[§aSkyblockTweaks§f§7] §3Update §e" + remote.latestAlpha.modrinthName + " §3is available! §2[Download]", "§3Click to open Modrinth in your browser", link);
                                    NotificationHandler.NOTIFICATION_QUEUE.add(message);
                                }
                            }
                            case Version.NotificationStream.BETA -> {
                                if (remote.latestBeta == null) {
                                    break;
                                }
                                var version = new Version(remote.latestBeta.versionString);
                                var comparison = Version.compareVersions(version, SkyblockTweaks.VERSION);
                                if (comparison == Version.VersionComparison.GREATER) {
                                    var link = Version.getModrinthLink(remote.latestBeta.modrinthName);
                                    var message = TextUtils.getTextThatLinksToURL("§7[§aSkyblockTweaks§f§7] §3Update §e" + remote.latestBeta.modrinthName + " §3is available! §2[Download]", "§3Click to open Modrinth in your browser", link);
                                    NotificationHandler.NOTIFICATION_QUEUE.add(message);
                                }

                            }
                            case Version.NotificationStream.RELEASE -> {
                                if (remote.latestRelease == null) {
                                    break;
                                }
                                var version = new Version(remote.latestRelease.versionString);
                                var comparison = Version.compareVersions(version, SkyblockTweaks.VERSION);
                                if (comparison == Version.VersionComparison.GREATER) {
                                    var link = Version.getModrinthLink(remote.latestRelease.modrinthName);
                                    var message = TextUtils.getTextThatLinksToURL("§7[§aSkyblockTweaks§f§7] §3Update §e" + remote.latestRelease.modrinthName + " §3is available! §2[Download]", "§3Click to open Modrinth in your browser", link);
                                    NotificationHandler.NOTIFICATION_QUEUE.add(message);
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
