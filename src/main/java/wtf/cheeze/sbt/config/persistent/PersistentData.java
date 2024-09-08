package wtf.cheeze.sbt.config.persistent;

import net.fabricmc.loader.api.FabricLoader;
import wtf.cheeze.sbt.SkyblockTweaks;

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
    private static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("skyblocktweaks-persistent.json");

    public void save() {
        var configFile = new File(String.valueOf(configPath));
        try {
            configFile.createNewFile();
            var toWrite = SkyblockTweaks.GSON.toJson(this);
            var writer = new FileWriter(configFile);
            writer.write(toWrite);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            e.printStackTrace();
            return new PersistentData();
        }
    }
}
