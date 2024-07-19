package wtf.cheeze.sbt.utils;

import wtf.cheeze.sbt.SkyBlockTweaks;

public class UpdateChecker {
    public static final String REMOTE_VERSION_FILE = "https://raw.githubusercontent.com/MisterCheezeCake/RemoteData/main/SBT/updateInfo.json";

    public static void checkForUpdates() {
        if (SkyBlockTweaks.CONFIG.config.notificationStream != Version.NotificationStream.NONE && SkyBlockTweaks.VERSION.STREAM != Version.VersionType.UNSTABLE) {
            try {
                SkyBlockTweaks.LOGGER.info("Checking for updates");
                var result = HTTPUtil.get(REMOTE_VERSION_FILE);
                //LOGGER.info(result);
                Version.RemoteVersionFile remote = SkyBlockTweaks.GSON.fromJson(result, Version.RemoteVersionFile.class);
                if (remote == null) {
                    SkyBlockTweaks.LOGGER.error("Failed to parse remote version file");
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
                                    NotificationHandler.NOTIFICATION_QUEUE.add(message);
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
                                    NotificationHandler.NOTIFICATION_QUEUE.add(message);
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
