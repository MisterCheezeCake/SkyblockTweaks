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
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SkyblockTweaksConfig;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.hud.HudInformation;
import wtf.cheeze.sbt.utils.hud.HudLine;
import wtf.cheeze.sbt.utils.hud.TextHUD;
import wtf.cheeze.sbt.utils.skyblock.IconDict;
import wtf.cheeze.sbt.utils.skyblock.SkyblockConstants;
import wtf.cheeze.sbt.utils.skyblock.SkyblockUtils;

import java.awt.*;

public class SkillHUD extends TextHUD {

    private static final int PERSIST_TICKS = 60;

    private int timeLeft = 0;
    private SkyblockConstants.Skills currentSkill;
    private float gained = 0;
    private float total = 0;
    private float progress = 0;
    private float percent = 0;


    public SkillHUD() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (timeLeft > 0) {
                timeLeft--;
            }
        });
        INFO = new HudInformation(
                () -> SkyblockTweaks.CONFIG.config.huds.skills.x,
                () -> SkyblockTweaks.CONFIG.config.huds.skills.y,
                () -> SkyblockTweaks.CONFIG.config.huds.skills.scale,
                () -> SkyblockTweaks.CONFIG.config.huds.skills.anchor,
                x -> SkyblockTweaks.CONFIG.config.huds.skills.x = x,
                y -> SkyblockTweaks.CONFIG.config.huds.skills.y = y,
                scale -> SkyblockTweaks.CONFIG.config.huds.skills.scale = scale,
                anchor -> SkyblockTweaks.CONFIG.config.huds.skills.anchor = anchor
        );
        line = new HudLine(
                () -> SkyblockTweaks.CONFIG.config.huds.skills.color,
                () -> SkyblockTweaks.CONFIG.config.huds.skills.outlineColor,
                () -> SkyblockTweaks.CONFIG.config.huds.skills.mode,
                () -> {
                    if (timeLeft <= 0) return "Skill HUD Placeholder Text";
                    if (this.percent == 0) {
                        if (this.total == 0) {
                            return "+" + gained + " (" + TextUtils.formatNumber((int) progress, ",") + ")";
                        } else {
                            if (SkyblockTweaks.CONFIG.config.huds.skills.skillMode == Mode.PERCENT) {
                                var base = "+" + gained + " (" + NumberUtils.formatPercent(progress, total) + "%)";
                                if (SkyblockTweaks.CONFIG.config.huds.skills.actionsLeft) {
                                    return base + " - " + actionsLeft(gained, progress, total) + " Left";
                                } else {
                                    return base;
                                }
                            } else {
                                var base = "+" + gained + " (" + TextUtils.formatNumber((int) progress, ",") + "/" + (SkyblockTweaks.CONFIG.config.huds.skills.abridgeDenominator ? TextUtils.addKOrM((int) total, ",") : TextUtils.formatNumber((int) total, ",")) + ")";
                                if (SkyblockTweaks.CONFIG.config.huds.skills.actionsLeft) {
                                    return base + " - " + actionsLeft(gained, progress, total) + " Left";
                                } else {
                                    return base;
                                }
                            }
                        }
                    } else {
                        if (SkyblockTweaks.CONFIG.config.huds.skills.skillMode == Mode.NUMBER) {
                            var level = tryAndGetSkillLevel(currentSkill);
                            if (level == -1) return "+" + gained + " (" + percent + ")%";
                            var table = getSkillTable(currentSkill);
                            var nextLevel = table[level];
                            var progressLevel = (percent / 100) * nextLevel;
                            var base = "+" + gained + " (" + TextUtils.formatNumber((int) progressLevel, ",") + "/" + (SkyblockTweaks.CONFIG.config.huds.skills.abridgeDenominator ? TextUtils.addKOrM(nextLevel, ",") : TextUtils.formatNumber(nextLevel, ",")) + ")";
                            if (SkyblockTweaks.CONFIG.config.huds.skills.actionsLeft) {
                                return base + " - " + actionsLeft(gained, progressLevel, nextLevel) + " Left";
                            } else {
                                return base;
                            }

                        } else {
                            var base = "+" + gained + " (" + percent + "%)";
                            if (SkyblockTweaks.CONFIG.config.huds.skills.actionsLeft) {
                                var level = tryAndGetSkillLevel(currentSkill);
                                if (level == -1) return base;
                                var table = getSkillTable(currentSkill);
                                var nextLevel = table[level];
                                var progressLevel = (percent / 100) * nextLevel;
                                return base + " - " + actionsLeft(gained, progressLevel, nextLevel) + " Left";
                            } else {
                                return base;
                            }
                        }
                    }
                },
                () -> IconDict.SKILL_ICONS.getOrDefault(currentSkill, IconDict.DEFAULT_ICON),
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
        return "Skill HUD";
    }

    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if (timeLeft <= 0 && !fromHudScreen) return false;
        return (SkyblockTweaks.DATA.inSB && SkyblockTweaks.CONFIG.config.huds.skills.enabled) || fromHudScreen;
    }

    public void update(String skill, float gained, float percent) {
        commonUpdate(skill, gained);
        this.percent = percent;
        this.total = 0;
        this.progress = 0;
    }

    public void update(String skill, float gained, float total, float progress) {
        commonUpdate(skill, gained);
        this.total = total;
        this.progress = progress;
        this.percent = 0;
    }

    private void commonUpdate(String skill, float gained) {
        timeLeft = PERSIST_TICKS;
        currentSkill = SkyblockUtils.strictCastStringToSkill(skill);
        this.gained = gained;
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
                    .name(Text.literal("Enable Skill Progress HUD"))
                    .description(OptionDescription.of(Text.literal("Enables the Skill Progress HUD")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.skills.enabled,
                            () -> config.huds.skills.enabled,
                            value -> config.huds.skills.enabled = (Boolean) value
                    )
                    .build();

            var skillMode = Option.<Mode>createBuilder()
                    .name(Text.literal("Skill Progress HUD Mode"))
                    .description(OptionDescription.of(Text.literal("The mode of the Skill Progress HUD.\nDefault will show the progress based on how Hypixel does by default\nNumber always will show the progress as a number\nPercent will always show the progress as a percentage")))
                    .controller(opt -> EnumControllerBuilder.create(opt).enumClass(Mode.class))
                    .binding(
                            defaults.huds.skills.skillMode,
                            () -> config.huds.skills.skillMode,
                            value -> config.huds.skills.skillMode = value
                    )
                    .build();

            var actionsLeft = Option.<Boolean>createBuilder()
                    .name(Text.literal("Skill Progress HUD Actions Left"))
                    .description(OptionDescription.of(Text.literal("Shows the amount of actions left to level up in the Skill Progress HUD")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.skills.actionsLeft,
                            () -> config.huds.skills.actionsLeft,
                            value -> config.huds.skills.actionsLeft = (Boolean) value
                    )
                    .build();
            var abridgeDenominator = Option.<Boolean>createBuilder()
                    .name(Text.literal("Skill Progress HUD Abridge Denominator"))
                    .description(OptionDescription.of(Text.literal("Abridges the denominator in the Skill Progress HUD")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.skills.abridgeDenominator,
                            () -> config.huds.skills.abridgeDenominator,
                            value -> config.huds.skills.abridgeDenominator = (Boolean) value
                    )
                    .build();

            var color = Option.<Color>createBuilder()
                    .name(Text.literal("Skill Progress HUD Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Skill Progress HUD")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.skills.color),
                            () -> new Color(config.huds.skills.color),
                            value -> config.huds.skills.color = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(Text.literal("Skill Progress HUD Outline Color"))
                    .description(OptionDescription.of(Text.literal("The outline color of the Skill Progress HUD")))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.skills.mode == HudLine.DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.skills.outlineColor),
                            () -> new Color(config.huds.skills.outlineColor),
                            value -> config.huds.skills.outlineColor = value.getRGB()

                    )
                    .build();
            var mode = Option.<HudLine.DrawMode>createBuilder()
                    .name(Text.literal("Skill Progress HUD Mode"))
                    .description(OptionDescription.of(Text.literal("The draw mode of the Skill Progress HUD. Pure will render without shadow, Shadow will render with a shadow, and Outline will render with an outline\n§4Warning: §cOutline mode is still a work in progress and can cause annoying visual bugs in menus.")))
                    .controller(SkyblockTweaksConfig::generateDrawModeController)
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
                    .name(Text.literal("Skill Progress HUD Scale"))
                    .description(OptionDescription.of(Text.literal("The scale of the Skill Progress HUD")))
                    .controller(SkyblockTweaksConfig::generateScaleController)
                    .binding(
                            defaults.huds.skills.scale,
                            () -> config.huds.skills.scale,
                            value -> config.huds.skills.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(Text.literal("Skill Progress HUD"))
                    .description(OptionDescription.of(Text.literal("Settings for the Skill Progress HUD")))
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

