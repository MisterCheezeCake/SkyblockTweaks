package wtf.cheeze.sbt.utils.errors;


public enum ErrorLevel {
    /**
     * Critical errors will always be displayed to the user in chat and logged
     */
    CRITICAL,
    /**
     * Warnings will only be displayed to the user in chat if the config is set to do so
     */
    WARNING,

    /**
     * Silent errors will not be displayed to the user in chat, but will still be logged
     */
    SILENT

}
