package wtf.cheeze.sbt.utils;

/**
 * A simple pair implemented using a record. Called CheezePair to disambiguate from the 85 other Pair classes.
 */
public record CheezePair<K, V>(K key, V val) {
    public static <K, V> CheezePair<K, V> of(K key, V val) {
        return new CheezePair<>(key, val);
    }
}
