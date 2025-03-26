/*
 * Copyright (C) 2024 MisterCheezeCake
 *
 * This file is part of SkyblockTweaks.
 *
 * SkyblockTweaks is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * SkyblockTweaks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SkyblockTweaks. If not, see <https://www.gnu.org/licenses/>.
 */
package wtf.cheeze.sbt.features.huds;

import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.config.persistent.PersistentData;
import wtf.cheeze.sbt.hud.bases.TextHud;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.hud.utils.HudName;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.bases.BarHud;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.utils.constants.loader.Constants;
import wtf.cheeze.sbt.utils.enums.Skill;
import wtf.cheeze.sbt.utils.errors.ErrorHandler;
import wtf.cheeze.sbt.utils.errors.ErrorLevel;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.hud.icon.Icons;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;
import wtf.cheeze.sbt.utils.skyblock.SkyblockUtils;

import java.awt.*;

/**
 * Manages the Skill HUD and Skill Bar, storing data and common functions, Singleton
 */
public class SkillHudManager {

    public static final SkillHudManager INSTANCE = new SkillHudManager();

    private SkillHudManager() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (timeLeft > 0) {
                timeLeft--;
            }
        });
    }

    public final SkillHud SKILL_HUD = new SkillHud();
    public final SkillBar SKILL_BAR = new SkillBar();

    private static final int PERSIST_TICKS = 60;

    private int timeLeft = 0;
    private Skill currentSkill = Skill.UNKNOWN;
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

    public class SkillHud extends TextHud {
        public SkillHud() {
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
            line = new SingleHudLine(
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
                            return Icons.SKILL_ICONS.getOrDefault(currentSkill, Icons.DEFAULT_ICON);
                        } catch (Exception e) {
                            //SkyblockTweaks.LOGGER.error("Error getting skill icon", e);
                            ErrorHandler.handleError(e, "Error getting skill icon", ErrorLevel.SILENT);
                            return Icons.DEFAULT_ICON;
                        }
                    },
                    () -> timeLeft > 0
            );
        }

        private static int tryAndGetSkillLevel(Skill skill) {
            var profile = PersistentData.get().profiles.get(SkyblockData.getCurrentProfileUnique());
            if (profile == null) return -1;
            return profile.skillLevels.getOrDefault(skill, -1);
        }

        private static int[] getSkillTable(Skill skill) {
            return switch (skill) {
                case FARMING, FISHING, FORAGING, MINING, COMBAT, ENCHANTING, ALCHEMY, TAMING, CARPENTRY, UNKNOWN ->
                        Constants.skills().mainSkillLevels();
                case RUNECRAFTING ->   Constants.skills().runeLevels();
                case SOCIAL -> Constants.skills().socialLevels();
            };
        }

        private static int actionsLeft(float gain, float prog, float tot) {
            var remain = tot - prog;
            return Math.round(remain / gain);
        }


        public @NotNull HudName getName() {
            return new HudName("Skill Progress HUD", "Skill HUD", Colors.CYAN);
        }

        @Override
        public boolean shouldRender(boolean fromHudScreen) {
            if (!super.shouldRender(fromHudScreen)) return false;
            if (timeLeft <= 0 && !fromHudScreen) return false;
            return (SkyblockData.inSB && SBTConfig.huds().skills.enabled) || fromHudScreen;
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
            public DrawMode mode = DrawMode.SHADOW;

            @SerialEntry // Not handled by YACL Gui
            public float x = 0;

            @SerialEntry // Not handled by YACL Gui
            public float y = 0.80f;

            @SerialEntry
            public float scale = 1.0f;

            @SerialEntry
            public int color = Colors.CYAN;

            @SerialEntry
            public int outlineColor = Colors.BLACK;

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
                                value -> config.huds.skills.enabled = (boolean) value
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
                                value -> config.huds.skills.actionsLeft = (boolean) value
                        )
                        .build();
                var abridgeDenominator = Option.<Boolean>createBuilder()
                        .name(key("skills.abridgeDenominator"))
                        .description(keyD("skills.abridgeDenominator"))
                        .controller(SBTConfig::generateBooleanController)
                        .binding(
                                defaults.huds.skills.abridgeDenominator,
                                () -> config.huds.skills.abridgeDenominator,
                                value -> config.huds.skills.abridgeDenominator = (boolean) value
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
                        .available(config.huds.skills.mode == DrawMode.OUTLINE)
                        .binding(
                                new Color(defaults.huds.skills.outlineColor),
                                () -> new Color(config.huds.skills.outlineColor),
                                value -> config.huds.skills.outlineColor = value.getRGB()

                        )
                        .build();
                var mode = Option.<DrawMode>createBuilder()
                        .name(key("skills.mode"))
                        .description(keyD("skills.mode"))
                        .controller(SBTConfig::generateDrawModeController)
                        .binding(
                                defaults.huds.skills.mode,
                                () -> config.huds.skills.mode,
                                value -> {
                                    config.huds.skills.mode = value;
                                    outline.setAvailable(value == DrawMode.OUTLINE);
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
    public class SkillBar extends BarHud {
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
                    x -> SBTConfig.huds().skillBar.x = x,
                    y -> SBTConfig.huds().skillBar.y = y,
                    scale -> SBTConfig.huds().skillBar.scale = scale,
                    anchor -> SBTConfig.huds().skillBar.anchor = anchor

            );
        }

        @Override
        public @NotNull HudName getName() {
            return new HudName("Skill Progress Bar", "Skill Bar", Colors.CYAN);
        }

        @Override
        public boolean shouldRender(boolean fromHudScreen) {
            if (!super.shouldRender(fromHudScreen)) return false;
            if (fromHudScreen) return true;
            if (timeLeft <= 0) return false;
            // Don't display at max level
            if  (percent == 0 && total == 0 ) return false;
            return (SkyblockData.inSB && SBTConfig.huds().skillBar.enabled);
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
            public int color = Colors.CYAN;

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
                                value -> config.huds.skillBar.enabled = (boolean) value
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
