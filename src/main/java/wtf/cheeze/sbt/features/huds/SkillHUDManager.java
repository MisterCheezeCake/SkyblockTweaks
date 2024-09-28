package wtf.cheeze.sbt.features.huds;

import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.hud.BarHUD;
import wtf.cheeze.sbt.utils.hud.HudInformation;
import wtf.cheeze.sbt.utils.hud.HudLine;
import wtf.cheeze.sbt.utils.hud.TextHUD;
import wtf.cheeze.sbt.utils.skyblock.IconDict;
import wtf.cheeze.sbt.utils.skyblock.SkyblockConstants;
import wtf.cheeze.sbt.utils.skyblock.SkyblockUtils;

import java.awt.*;

/**
 * Manages the Skill HUD and Skill Bar, storing data and common functions, Singleton
 */
public class SkillHUDManager {

    public static final SkillHUDManager INSTANCE = new SkillHUDManager();

    private SkillHUDManager() {}

    public final SkillHUD SKILL_HUD = new SkillHUD();
    public final SkillBar SKILL_BAR = new SkillBar();

    private static final int PERSIST_TICKS = 60;

    private int timeLeft = 0;
    private SkyblockConstants.Skills currentSkill;
    private float gained = 0;
    private float total = 0;
    private float progress = 0;
    private float percent = 0;

    public void update(String skillP, float gainedP, float percentP) {
        commonUpdate(skillP, gainedP);
        percent = percentP;
        total = 0;
        progress = 0;
    }

    public void update(String skillP, float gainedP, float totalP, float progressP) {
        commonUpdate(skillP, gainedP);
        total = totalP;
        progress = progressP;
        percent = 0;
    }

    private void commonUpdate(String skillP, float gainedP) {
        timeLeft = PERSIST_TICKS;
        currentSkill = SkyblockUtils.strictCastStringToSkill(skillP);
        gained = gainedP;
    }

    public class SkillHUD extends TextHUD {
        public SkillHUD() {
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (timeLeft > 0) {
                    timeLeft--;
                }
            });
            INFO = new HudInformation(
                    () -> SBTConfig.huds().skills.x,
                    () -> SBTConfig.huds().skills.y,
                    () -> SBTConfig.huds().skills.scale,
                    () -> SBTConfig.huds().skills.anchor,
                    x -> SBTConfig.huds().skills.x = x,
                    y -> SBTConfig.huds().skills.y = y,
                    scale -> SBTConfig.huds().skills.scale = scale,
                    anchor -> SBTConfig.huds().skills.anchor = anchor
            );
            line = new HudLine(
                    () -> SBTConfig.huds().skills.color,
                    () -> SBTConfig.huds().skills.outlineColor,
                    () -> SBTConfig.huds().skills.mode,
                    () -> {
                        if (timeLeft <= 0) return Text.literal("Skill HUD Placeholder Text");
                        if (percent == 0) {
                            if (total == 0) {
                                return Text.literal("+" + gained + " (" + NumberUtils.formatNumber((int) progress, ",") + ")");
                            } else {
                                if (SBTConfig.huds().skills.skillMode == Mode.PERCENT) {
                                    var base = "+" + gained + " (" + NumberUtils.formatPercent(progress, total) + ")";
                                    if (SBTConfig.huds().skills.actionsLeft) {
                                        return Text.literal(base + " - " + actionsLeft(gained, progress, total) + " Left");
                                    } else {
                                        return Text.literal(base);
                                    }
                                } else {
                                    var base = "+" + gained + " (" + NumberUtils.formatNumber((int) progress, ",") + "/" + (SBTConfig.huds().skills.abridgeDenominator ? NumberUtils.addKOrM((int) total, ",") : NumberUtils.formatNumber((int) total, ",")) + ")";
                                    if (SBTConfig.huds().skills.actionsLeft) {
                                        return Text.literal(base + " - " + actionsLeft(gained, progress, total) + " Left");
                                    } else {
                                        return Text.literal(base);
                                    }
                                }
                            }
                        } else {
                            if (SBTConfig.huds().skills.skillMode == Mode.NUMBER) {
                                var level = tryAndGetSkillLevel(currentSkill);
                                if (level == -1) return Text.literal("+" + gained + " (" + percent + "%)");
                                var table = getSkillTable(currentSkill);
                                var nextLevel = table[level];
                                var progressLevel = (percent / 100) * nextLevel;
                                var base = "+" + gained + " (" + NumberUtils.formatNumber((int) progressLevel, ",") + "/" + (SBTConfig.huds().skills.abridgeDenominator ? NumberUtils.addKOrM(nextLevel, ",") : NumberUtils.formatNumber(nextLevel, ",")) + ")";
                                if (SBTConfig.huds().skills.actionsLeft) {
                                    return Text.literal(base + " - " + actionsLeft(gained, progressLevel, nextLevel) + " Left");
                                } else {
                                    return Text.literal(base);
                                }

                            } else {
                                var base = "+" + gained + " (" + percent + "%)";
                                if (SBTConfig.huds().skills.actionsLeft) {
                                    var level = tryAndGetSkillLevel(currentSkill);
                                    if (level == -1) return Text.literal(base);
                                    var table = getSkillTable(currentSkill);
                                    var nextLevel = table[level];
                                    var progressLevel = (percent / 100) * nextLevel;
                                    return Text.literal(base + " - " + actionsLeft(gained, progressLevel, nextLevel) + " Left");
                                } else {
                                    return Text.literal(base);
                                }
                            }
                        }
                    },
                    () -> {
                        // This sometimes errors for a reason I cannot fathom
                        try {
                            return IconDict.SKILL_ICONS.getOrDefault(currentSkill, IconDict.DEFAULT_ICON);
                        } catch (Exception e) {
                            SkyblockTweaks.LOGGER.error("Error getting skill icon", e);
                            return IconDict.DEFAULT_ICON;
                        }
                    },
                    () -> timeLeft > 0
            );
        }

        private static int tryAndGetSkillLevel(SkyblockConstants.Skills skill) {
            var profile = SkyblockTweaks.PD.profiles.get(SkyblockTweaks.DATA.getCurrentProfileUnique());
            if (profile == null) return -1;
            return profile.skillLevels.getOrDefault(skill, -1);
        }

        private static int[] getSkillTable(SkyblockConstants.Skills skill) {
            return switch (skill) {
                case FARMING, FISHING, FORAGING, MINING, COMBAT, ENCHANTING, ALCHEMY, TAMING, CARPENTRY ->
                        SkyblockConstants.SKILL_LEVELS;
                case RUNECRAFTING -> SkyblockConstants.RUNECRAFTING_LEVELS;
                case SOCIAL -> SkyblockConstants.SOCIAL_LEVELS;
            };
        }

        private static int actionsLeft(float gain, float prog, float tot) {
            var remain = tot - prog;
            return Math.round(remain / gain);
        }

        @Override
        public String getName() {
            return  TextUtils.SECTION + "3Skill HUD";
        }

        @Override
        public boolean shouldRender(boolean fromHudScreen) {
            if (!super.shouldRender(fromHudScreen)) return false;
            if (timeLeft <= 0 && !fromHudScreen) return false;
            return (SkyblockTweaks.DATA.inSB && SBTConfig.huds().skills.enabled) || fromHudScreen;
        }

        public enum Mode implements NameableEnum {
            HYPIXEL,
            NUMBER,
            PERCENT;

            @Override
            public Text getDisplayName() {
                return switch (this) {
                    case HYPIXEL -> Text.literal("Default");
                    case NUMBER -> Text.literal("All Numbers");
                    case PERCENT -> Text.literal("All Percent");
                };
            }
        }

        public static class Config {
            @SerialEntry
            public boolean enabled = false;

            @SerialEntry
            public Mode skillMode = Mode.HYPIXEL;

            @SerialEntry
            public boolean actionsLeft = true;

            @SerialEntry
            public boolean abridgeDenominator = true;

            @SerialEntry
            public HudLine.DrawMode mode = HudLine.DrawMode.SHADOW;

            @SerialEntry // Not handled by YACL Gui
            public float x = 0;

            @SerialEntry // Not handled by YACL Gui
            public float y = 0.80f;

            @SerialEntry
            public float scale = 1.0f;

            @SerialEntry
            public int color = 43690;

            @SerialEntry
            public int outlineColor = 0x000000;

            @SerialEntry
            public AnchorPoint anchor = AnchorPoint.LEFT;

            public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
                var enabled = Option.<Boolean>createBuilder()
                        .name(key("skills.enabled"))
                        .description(keyD("skills.enabled"))
                        .controller(SBTConfig::generateBooleanController)
                        .binding(
                                defaults.huds.skills.enabled,
                                () -> config.huds.skills.enabled,
                                value -> config.huds.skills.enabled = (Boolean) value
                        )
                        .build();

                var skillMode = Option.<Mode>createBuilder()
                        .name(key("skills.skillMode"))
                        .description(keyD("skills.skillMode"))
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(Mode.class))
                        .binding(
                                defaults.huds.skills.skillMode,
                                () -> config.huds.skills.skillMode,
                                value -> config.huds.skills.skillMode = value
                        )
                        .build();

                var actionsLeft = Option.<Boolean>createBuilder()
                        .name(key("skills.actionsLeft"))
                        .description(keyD("skills.actionsLeft"))
                        .controller(SBTConfig::generateBooleanController)
                        .binding(
                                defaults.huds.skills.actionsLeft,
                                () -> config.huds.skills.actionsLeft,
                                value -> config.huds.skills.actionsLeft = (Boolean) value
                        )
                        .build();
                var abridgeDenominator = Option.<Boolean>createBuilder()
                        .name(key("skills.abridgeDenominator"))
                        .description(keyD("skills.abridgeDenominator"))
                        .controller(SBTConfig::generateBooleanController)
                        .binding(
                                defaults.huds.skills.abridgeDenominator,
                                () -> config.huds.skills.abridgeDenominator,
                                value -> config.huds.skills.abridgeDenominator = (Boolean) value
                        )
                        .build();

                var color = Option.<Color>createBuilder()
                        .name(key("skills.color"))
                        .description(keyD("skills.color"))
                        .controller(ColorControllerBuilder::create)
                        .binding(
                                new Color(defaults.huds.skills.color),
                                () -> new Color(config.huds.skills.color),
                                value -> config.huds.skills.color = value.getRGB()

                        )
                        .build();
                var outline = Option.<Color>createBuilder()
                        .name(key("skills.outlineColor"))
                        .description(keyD("skills.outlineColor"))
                        .controller(ColorControllerBuilder::create)
                        .available(config.huds.skills.mode == HudLine.DrawMode.OUTLINE)
                        .binding(
                                new Color(defaults.huds.skills.outlineColor),
                                () -> new Color(config.huds.skills.outlineColor),
                                value -> config.huds.skills.outlineColor = value.getRGB()

                        )
                        .build();
                var mode = Option.<HudLine.DrawMode>createBuilder()
                        .name(key("skills.mode"))
                        .description(keyD("skills.mode"))
                        .controller(SBTConfig::generateDrawModeController)
                        .binding(
                                defaults.huds.skills.mode,
                                () -> config.huds.skills.mode,
                                value -> {
                                    config.huds.skills.mode = value;
                                    outline.setAvailable(value == HudLine.DrawMode.OUTLINE);
                                }
                        )
                        .build();
                var scale = Option.<Float>createBuilder()
                        .name(key("skills.scale"))
                        .description(keyD("skills.scale"))
                        .controller(SBTConfig::generateScaleController)
                        .binding(
                                defaults.huds.skills.scale,
                                () -> config.huds.skills.scale,
                                value -> config.huds.skills.scale = value
                        )
                        .build();

                return OptionGroup.createBuilder()
                        .name(key("skills"))
                        .description(keyD("skills"))
                        .option(enabled)
                        .option(skillMode)
                        .option(actionsLeft)
                        .option(abridgeDenominator)
                        .option(mode)
                        .option(color)
                        .option(outline)
                        .option(scale)
                        .collapsed(true)
                        .build();
            }
        }

    }
    public class SkillBar extends BarHUD {
        public SkillBar() {
            INFO = new HudInformation(
                    () -> SBTConfig.huds().skillBar.x,
                    () -> SBTConfig.huds().skillBar.y,
                    () -> SBTConfig.huds().skillBar.scale,
                    () -> SBTConfig.huds().skillBar.anchor,
                    () -> SBTConfig.huds().skillBar.color,
                    () -> {
                        if (percent != 0) {
                            return percent/100f;
                        } else {
                            return progress / total;
                        }
                    },
                    x -> SBTConfig.huds().skillBar.x = (float) x,
                    y -> SBTConfig.huds().skillBar.y = (float) y,
                    scale -> SBTConfig.huds().skillBar.scale = (float) scale,
                    anchor -> SBTConfig.huds().skillBar.anchor = anchor

            );
        }

        @Override
        public String getName() {
            return TextUtils.SECTION +  "3Skill Progress Bar";
        }

        @Override
        public boolean shouldRender(boolean fromHudScreen) {
            if (!super.shouldRender(fromHudScreen)) return false;
            if (timeLeft <= 0 && !fromHudScreen) return false;
            // Don't display at max level
            if  (percent == 0 && total == 0 ) return false;
            return (SkyblockTweaks.DATA.inSB && SBTConfig.huds().skillBar.enabled) || fromHudScreen;
        }

        public static class Config {
            @SerialEntry
            public boolean enabled = false;

            @SerialEntry // Not handled by YACL Gui
            public float x = 0;

            @SerialEntry // Not handled by YACL Gui
            public float y = 0.85f;

            @SerialEntry
            public float scale = 1.0f;

            @SerialEntry
            public int color = 43690;

            @SerialEntry
            public AnchorPoint anchor = AnchorPoint.LEFT;

            public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
                var enabled = Option.<Boolean>createBuilder()
                        .name(key("skillBar.enabled"))
                        .description(keyD("skillBar.enabled"))
                        .controller(SBTConfig::generateBooleanController)
                        .binding(
                                defaults.huds.skillBar.enabled,
                                () -> config.huds.skillBar.enabled,
                                value -> config.huds.skillBar.enabled = (Boolean) value
                        )
                        .build();

                var color = Option.<Color>createBuilder()
                        .name(key("skillBar.color"))
                        .description(keyD("skillBar.color"))
                        .controller(ColorControllerBuilder::create)
                        .binding(
                                new Color(defaults.huds.skillBar.color),
                                () ->  new Color(config.huds.skillBar.color),
                                value -> config.huds.skillBar.color = value.getRGB()

                        )
                        .build();
                var scale = Option.<Float>createBuilder()
                        .name(key("skillBar.scale"))
                        .description(keyD("skillBar.scale"))
                        .controller(SBTConfig::generateScaleController)
                        .binding(
                                defaults.huds.skillBar.scale,
                                () -> config.huds.skillBar.scale,
                                value -> config.huds.skillBar.scale = value
                        )
                        .build();
                return OptionGroup.createBuilder()
                        .name(key("skillBar"))
                        .description(keyD("skillBar"))
                        .option(enabled)
                        .option(color)
                        .option(scale)
                        .collapsed(true)
                        .build();
            }
        }
    }
}
