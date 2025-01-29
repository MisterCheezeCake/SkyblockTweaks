package wtf.cheeze.sbt.config.migration;

import net.minecraft.util.Identifier;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.utils.render.Colors;

import java.util.function.Function;


/**
 * In mod versions prior to 0.1.0-Alpha.8, bar colors (all colors actually), defaulted to RGB ints.
 * This was fine pre 1.21.2 since the shader system was used, which was fine with RGB ints
 * However, in 1.21.2, the shader system was tweaked, and we moved to the color parameter in {@link net.minecraft.client.gui.DrawContext#drawTexture(Function, Identifier, int, int, float, float, int, int, int, int, int)}
 * This method interprets ints as ARGB, not RGB, which resulted in the bars not rendering with default settings due to the alpha value being 0.
 * YACL always treated the color as ARGB, so this issue only affects users who never modified the settings.
 * This transformation checks for the old defaults and updates them to the new defaults.
 */
public class BarColorTransformation implements ConfigTransformation<ConfigImpl> {

    private static final int OLD_HEALTH = 16733525;
    private static final int OLD_HEALTH_ABSORB = 16755200;
    private static final int OLD_MANA = 5592575;
    private static final int OLD_SKILL = 43690;
    private static final int OLD_DRILL = 43520;

    private BarColorTransformation() {}

    public static final BarColorTransformation INSTANCE = new BarColorTransformation();

    @Override
    public ConfigImpl tranform(ConfigImpl config) {
        if (config.huds.healthBar.color == OLD_HEALTH) config.huds.healthBar.color = Colors.RED;
        if (config.huds.healthBar.colorAbsorption == OLD_HEALTH_ABSORB) config.huds.healthBar.colorAbsorption = Colors.ORANGE;
        if (config.huds.manaBar.color == OLD_MANA) config.huds.manaBar.color = Colors.BLUE;
        if (config.huds.skillBar.color == OLD_SKILL) config.huds.skillBar.color = Colors.CYAN;
        if (config.huds.drillFuelBar.color == OLD_DRILL) config.huds.drillFuelBar.color = Colors.GREEN;

        return config;
    }

    @Override
    public boolean isApplicable(ConfigImpl config) {
        return config.huds.healthBar.color == OLD_HEALTH
                || config.huds.healthBar.colorAbsorption == OLD_HEALTH_ABSORB
                || config.huds.manaBar.color == OLD_MANA
                || config.huds.skillBar.color == OLD_SKILL
                || config.huds.drillFuelBar.color == OLD_DRILL;
    }

    @Override
    public boolean isApplicable(int configVersion) {
        return configVersion == 1;
    }


}
