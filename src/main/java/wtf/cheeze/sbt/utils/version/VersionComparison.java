package wtf.cheeze.sbt.utils.version;

public enum VersionComparison {
    EQUAL,
    GREATER,
    LESS,
    FAILURE;

    public static VersionComparison compare(Version a, Version b) {
        try {
            if (a.STREAM == VersionType.UNSTABLE || b.STREAM == VersionType.UNSTABLE) {
                return FAILURE;
            }

            if (a.MAJOR > b.MAJOR) {
                return GREATER;
            } else if (a.MAJOR < b.MAJOR) {
                return LESS;
            }
            if (a.MINOR > b.MINOR) {
                return GREATER;
            } else if (a.MINOR < b.MINOR) {
                return LESS;
            }
            if (a.PATCH > b.PATCH) {
                return GREATER;
            } else if (a.PATCH < b.PATCH) {
                return LESS;
            }
            // If we get here, the semvers are equal
            if (a.STREAM == VersionType.ALPHA && b.STREAM == VersionType.BETA) {
                return LESS;
            } else if (a.STREAM == VersionType.BETA && b.STREAM == VersionType.ALPHA) {
                return GREATER;
            }
            if (a.BUILD > b.BUILD) {
                return GREATER;
            } else if (a.BUILD < b.BUILD) {
                return LESS;
            }
            return EQUAL;
        } catch (Exception e) {
            return FAILURE;
        }
    }
}
