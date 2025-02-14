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
package wtf.cheeze.sbt.config.persistent;

import net.fabricmc.loader.api.FabricLoader;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.features.MouseLock;
import wtf.cheeze.sbt.utils.errors.ErrorHandler;
import wtf.cheeze.sbt.utils.errors.ErrorLevel;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;


/**
 * This class stores non user-configurable data that needs to be saved between sessions. Almost everything is stored by profile id, which are v4 UUIDs.
 * We use these instead of the cute names so that profiles, even across accounts, will never inherit the persistent data of another
 */
public class PersistentData {

    public HashMap<String, ProfileData> profiles = new HashMap<String, ProfileData>();

    public void save() {
        var configFile = new File(String.valueOf(configPath));
        try {
            configFile.createNewFile();
            var toWrite = SkyblockTweaks.GSON.toJson(this);
            var writer = new FileWriter(configFile);
            writer.write(toWrite);
            writer.close();
        } catch (Exception e) {
            ErrorHandler.handleError(e, "Failed to save persistent data", ErrorLevel.CRITICAL);
        }
    }

    private static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("skyblocktweaks-persistent.json");

    public static PersistentData load() {
        var configFile = new File(String.valueOf(configPath));
        try {
            if (!configFile.exists()) {
                var p = new PersistentData();
                p.save();
                return p;
            }
            var content = Files.readString(configPath);
            return SkyblockTweaks.GSON.fromJson(content, PersistentData.class);

        } catch (Exception e) {
            ErrorHandler.handleError(e, "Failed to load persistent data", ErrorLevel.CRITICAL);
            return new PersistentData();
        }
    }



}
