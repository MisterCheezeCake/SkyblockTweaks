package wtf.cheeze.sbt.utils.constants;

import wtf.cheeze.sbt.utils.enums.Skill;

import java.util.Map;

public record Minions(
        Map<String, ExpEntry> minionExp
) {

    public record ExpEntry(Skill skill, float exp) {}

    public static Minions empty() {
        return new Minions(Map.of());
    }
}
