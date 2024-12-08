package wtf.cheeze.sbt.config.migration;


/**
 * A ConfigMigration migrates a config object from one version to another.
 * These should be used instead of Transformations when the actual structure
 * of the config changes, as opposed to simply needing to modify certain values.
 * A ConfigMigration will always be accompanied by an increase in the config version
 */
public interface ConfigMigration<Source, Target> {
    Source migrate(Target config);

    boolean isApplicable(int aVersion);

    int getTargetVersion();
    int getSourceVersion();

    Class<Source> getSourceClass();
    Class<Target> getTargetClass();
}
