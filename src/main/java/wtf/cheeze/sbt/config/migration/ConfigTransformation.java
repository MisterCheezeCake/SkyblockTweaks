package wtf.cheeze.sbt.config.migration;

/**
 * A ConfigTransformation transforms a single version of a config, rather than migrating from one version to another.
 */
public interface ConfigTransformation<T>{

    T tranform(T config);

    boolean isApplicable(T config);

    boolean isApplicable(int configVersion);
}
