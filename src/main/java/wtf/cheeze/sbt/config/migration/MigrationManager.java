package wtf.cheeze.sbt.config.migration;

import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.config.VersionedObject;
import wtf.cheeze.sbt.utils.errors.ErrorHandler;
import wtf.cheeze.sbt.utils.errors.ErrorLevel;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;


/**
 * Manages config migrations and transformations
 */
public class MigrationManager {
    private static final ArrayList<ConfigMigration> migrations = new ArrayList<>();

    public static void registerMigration(ConfigMigration migration) {
        migrations.add(migration);
    }

    /**
     * Checks all registered migrations and runs any needed ones.
     * This MUST be called BEFORE config is initialized
     */
   public static void handleMigrations() {
       var file = new File(String.valueOf(SBTConfig.PATH));
       try {
           if (!file.exists()) return;

           //SkyblockTweaks.LOGGER.info("Checking for migrations");
           var content = Files.readString(SBTConfig.PATH);
           var currentVersion = SkyblockTweaks.GSON.fromJson(content, VersionedObject.class).configVersion;
           for (var migration: migrations) {
               if (migration.isApplicable(currentVersion)) {
                   SkyblockTweaks.LOGGER.info("Running config migration: " + migration.getClass().getSimpleName() + " from version " + currentVersion + " to version " + migration.getTargetVersion());
                   var source = SkyblockTweaks.GSON.fromJson(content, migration.getSourceClass());
                   var target = migration.migrate(source);
                   var writer = new FileWriter(file);
                   writer.write(SkyblockTweaks.GSON.toJson(target));
                   writer.close();
               }
           }

       } catch (Exception e) {
           ErrorHandler.handle(e, "Config Migration Error", ErrorLevel.CRITICAL);
       }

   }

    /**
     * Checks if a given transformation needs to be run, then runs it if applicable
     * This MUST be called AFTER the config is loaded
     */
    @SuppressWarnings("unchecked")
    public static <T extends ConfigImpl> boolean runTransformation(ConfigTransformation<T> transformation) {
        //SkyblockTweaks.LOGGER.info("Checking config transformation: " + transformation.getClass().getSimpleName());
        T currentConfig = (T) SBTConfig.get();
        //SkyblockTweaks.LOGGER.info("Current config version: " + currentConfig.configVersion);
        if (!transformation.isApplicable(currentConfig.configVersion))  {
            //SkyblockTweaks.LOGGER.info("Transformation not applicable for version");
            return false;
        }
        if (!transformation.isApplicable(currentConfig)) {
            //SkyblockTweaks.LOGGER.info("Transformation not applicable for config");
            return false;
        }
        SkyblockTweaks.LOGGER.info("Running config transformation: " + transformation.getClass().getSimpleName());
        transformation.tranform(currentConfig);
        return true;
    }
}
