package wtf.cheeze.sbt.config.migration;

import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.config.categories.Huds;
import wtf.cheeze.sbt.utils.render.Colors;


/**
 * Continuing the rich tradition of breaking our rendering through consideration of an alpha channel,
 * Mojang decided to have a laugh and make text rendering consider alpha, much like they did with texture
 * rendering in 1.21.2, which {@link BarColorTransformation} deals with. Now, this shouldn't have been a problem
 * since I changed configs to use the {@link Colors} class, and the colors there had an alpha channel. But, I conveniently
 * forgot to change the Health HUD defaults, meaning another transformation is needed to allow it to render if the default settings
 * were present. This transformation also handles colors for any other HUDs present prior to 0.1.0-Alpha.8, to be safe if someone has an old config.
 */
public class TextColorTransformation implements ConfigTransformation<ConfigImpl> {

    private static final int OLD_RED = 16733525;
    private static final int OLD_ORANGE = 16755200;
    private static final int OLD_BLUE = 5592575;
    private static final int OLD_CYAN = 43690;
    private static final int OLD_GREEN = 43520;
    private static final int OLD_LIME = 5635925;
    private static final int OLD_WHITE = 0xFFFFFF;
    private static final int OLD_BLACK = 0;


    private TextColorTransformation() {}

    public static final TextColorTransformation INSTANCE = new TextColorTransformation();


    @Override
    public ConfigImpl tranform(ConfigImpl config) {
        if (config.huds.coordinates.color == OLD_WHITE) config.huds.coordinates.color = Colors.WHITE;
        if (config.huds.coordinates.outlineColor == OLD_BLACK) config.huds.coordinates.outlineColor = Colors.BLACK;
        if (config.huds.dr.color == OLD_LIME) config.huds.dr.color = Colors.LIME;
        if (config.huds.dr.outlineColor == OLD_BLACK) config.huds.dr.outlineColor = Colors.BLACK;
        if (config.huds.defense.color == OLD_LIME) config.huds.defense.color = Colors.LIME;
        if (config.huds.defense.outlineColor == OLD_BLACK) config.huds.defense.outlineColor = Colors.BLACK;
        if (config.huds.drillFuel.color == OLD_GREEN) config.huds.drillFuel.color = Colors.GREEN;
        if (config.huds.drillFuel.outlineColor == OLD_BLACK) config.huds.drillFuel.outlineColor = Colors.BLACK;
        if (config.huds.ehp.color == OLD_GREEN) config.huds.ehp.color = Colors.GREEN;
        if (config.huds.ehp.outlineColor == OLD_BLACK) config.huds.ehp.outlineColor = Colors.BLACK;
        if (config.huds.fps.color == OLD_WHITE) config.huds.fps.color = Colors.WHITE;
        if (config.huds.fps.outlineColor == OLD_BLACK) config.huds.fps.outlineColor = Colors.BLACK;
        if (config.huds.health.color == OLD_RED) config.huds.health.color = Colors.RED;
        if (config.huds.health.colorAbsorption == OLD_ORANGE) config.huds.health.colorAbsorption = Colors.ORANGE;
        if (config.huds.health.outlineColor == OLD_BLACK) config.huds.health.outlineColor = Colors.BLACK;
        if (config.huds.mana.color == OLD_BLUE) config.huds.mana.color = Colors.BLUE;
        if (config.huds.mana.outlineColor == OLD_BLACK) config.huds.mana.outlineColor = Colors.BLACK;
        if (config.huds.overflowMana.color == OLD_CYAN) config.huds.overflowMana.color = Colors.CYAN;
        if (config.huds.overflowMana.outlineColor == OLD_BLACK) config.huds.overflowMana.outlineColor = Colors.BLACK;
        if (config.huds.time.color == OLD_WHITE) config.huds.time.color = Colors.WHITE;
        if (config.huds.time.outlineColor == OLD_BLACK) config.huds.time.outlineColor = Colors.BLACK;
        if (config.huds.skills.color == OLD_CYAN) config.huds.skills.color = Colors.CYAN;
        if (config.huds.skills.outlineColor == OLD_BLACK) config.huds.skills.outlineColor = Colors.BLACK;
        if (config.huds.speed.color == OLD_WHITE) config.huds.speed.color = Colors.WHITE;
        if (config.huds.speed.outlineColor == OLD_BLACK) config.huds.speed.outlineColor = Colors.BLACK;
        return config;
    }

    @Override
    public boolean isApplicable(ConfigImpl config) {
        Huds huds = SBTConfig.huds();
        return huds.coordinates.color == OLD_WHITE ||
                huds.coordinates.outlineColor == OLD_BLACK ||
                huds.dr.color == OLD_LIME ||
                huds.dr.outlineColor == OLD_BLACK ||
                huds.defense.color == OLD_LIME ||
                huds.defense.outlineColor == OLD_BLACK ||
                huds.drillFuel.color == OLD_GREEN ||
                huds.drillFuel.outlineColor == OLD_BLACK ||
                huds.ehp.color == OLD_GREEN ||
                huds.ehp.outlineColor == OLD_BLACK ||
                huds.fps.color == OLD_WHITE ||
                huds.fps.outlineColor == OLD_BLACK ||
                huds.health.color == OLD_RED ||
                huds.health.colorAbsorption == OLD_ORANGE ||
                huds.health.outlineColor == OLD_BLACK ||
                huds.mana.color == OLD_BLUE ||
                huds.mana.outlineColor == OLD_BLACK ||
                huds.overflowMana.color == OLD_CYAN ||
                huds.overflowMana.outlineColor == OLD_BLACK ||
                huds.time.color == OLD_WHITE ||
                huds.time.outlineColor == OLD_BLACK ||
                huds.skills.color == OLD_CYAN ||
                huds.skills.outlineColor == OLD_BLACK ||
                huds.speed.color == OLD_WHITE ||
                huds.speed.outlineColor == OLD_BLACK;
    }

    @Override
    public boolean isApplicable(int configVersion) {
        return configVersion == 1;
    }
}
