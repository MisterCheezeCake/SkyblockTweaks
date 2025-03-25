package wtf.cheeze.sbt.utils;

import java.util.function.Predicate;

public class Predicates {

    public static final Predicate<String> ZERO_TO_ONE = string -> {
        if (string.isEmpty()) return true; // allow empty strings
        if (string.startsWith(".")) return false; // don't allow just a dot
        if (string.endsWith(".")) string = string + "0"; // allow trailing dots
        try {
            var f = Float.parseFloat(string);
            return !(f < 0) && !(f > 1);
        } catch (NumberFormatException e) {
            return false;
        }
    };


    public static final Predicate<String> INT = string -> {
        if (string.isEmpty()) return true; // allow empty strings
        if (string.equals("____")) return true; // allow this specific placeholder
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    };
}

