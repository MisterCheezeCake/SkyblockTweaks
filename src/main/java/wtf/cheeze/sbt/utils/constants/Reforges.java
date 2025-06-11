package wtf.cheeze.sbt.utils.constants;

import java.util.Map;

public record Reforges(Map<String, String> specialModifiers) {
    public static Reforges empty() {
        return new Reforges(Map.of());
    }
}
