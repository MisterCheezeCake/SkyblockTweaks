package wtf.cheeze.sbt.utils.constants;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.lib.sat4j.specs.IConstr;
import wtf.cheeze.sbt.config.persistent.PersistentData;
import wtf.cheeze.sbt.utils.errors.ErrorHandler;
import wtf.cheeze.sbt.utils.errors.ErrorLevel;

import java.nio.file.Path;

public class ConstantLoader {
    public static final Path CONSTANTS_FOLDER = FabricLoader.getInstance().getConfigDir().resolve("skyblocktweaks/constants");

    public static void registerEvents() {
      if (!CONSTANTS_FOLDER.toFile().exists()) {
          if(!CONSTANTS_FOLDER.toFile().mkdirs()) ErrorHandler.handleError(new Exception("Failed to create constants folder"), "Failed to create constants folder", ErrorLevel.CRITICAL);
      }
    }
}
