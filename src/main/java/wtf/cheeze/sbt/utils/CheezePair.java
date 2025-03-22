package wtf.cheeze.sbt.utils;

/**
 * A simple pair implemented using a record. Called CheezePair to disambiguate from the 85 other Pair classes.
 */
public record CheezePair<K, V>(K key, V val) {}
