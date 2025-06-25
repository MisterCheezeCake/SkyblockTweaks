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
package wtf.cheeze.sbt.utils.actionbar;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.events.ChatEvents;
import wtf.cheeze.sbt.features.huds.SkillHudManager;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.text.Symbols;
import wtf.cheeze.sbt.utils.text.TextUtils;
import wtf.cheeze.sbt.utils.errors.ErrorHandler;
import wtf.cheeze.sbt.utils.errors.ErrorLevel;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;

import static wtf.cheeze.sbt.config.categories.General.key;
import static wtf.cheeze.sbt.config.categories.General.keyD;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 2,902/2,527❤     188❈ Defense     144/1,227✎ Mana
// 2,902/2,527❤     -24 Mana (Instant Transmission)     45/1,227✎ Mana
// 2,902/2,527❤     189❈ Defense     NOT ENOUGH MANA
// 2,902/2,527❤     ⏣ Graveyard     15/1,227✎ Mana
// 2,902/2,527❤     +10.8 Combat (20,056,461/0)     222/1,227✎ Mana
// +7.3 Foraging (58.09%)
// +6 Foraging (58/125)
//  6,434/5,987❤     +49.6 Combat (20,059,730/0)     1,945/1,945✎ Mana     0/7 Secrets
// 3,665/3,665❤     827❈ Defense     1,650/3k Drill Fuel

/**
 * Parses and modifies the action bar text
 * Inspired by the SkyBlockAddons Action Bar Parser
 * TODO: Switch more things in here to regex
 * TODO: Split the main method into smaller methods
 */


public class ActionBarTransformer {
    private static final String SEPERATOR3 = "   ";
    private static final String SEPERATOR4 = "     ";
    private static final String SEPERATOR5 = "     ";
    private static final String SEPERATOR12 = "            ";


    private static final Pattern manaAbilityPattern = Pattern.compile("-(\\d+) Mana \\((.+)\\)");
    private static final Pattern skillLevelPatern = Pattern.compile("\\+([\\d,]+\\.?\\d*) (.+) \\((.+)\\)");
    private static final Pattern secretsPattern = Pattern.compile("(\\d+)/(\\d+) Secrets");
    private static final Pattern healthPattern = Pattern.compile("(?<current>[\\d,]+)/(?<max>[\\d,]+)❤(?:\\+[\\d,]+.)?(?: {2})?(?<stacks>\\d+)?(?<symbol>.)?");
    private static final Pattern riftTimePattern = Pattern.compile("(?<time>.+)ф Left(?: [+-]\\d+[ms]!)?");
    private static final Pattern pressurePattern = Pattern.compile("Pressure: ❍(?<pressure>\\d+)%");

    private static String[] getUnmodifiedParts(String actionBarText) {
        String[] unmodifiedParts = actionBarText.split(SEPERATOR3 + "|(?=§e§l)");
        for (int i = 0; i < unmodifiedParts.length; i++) {
            if (unmodifiedParts[i].contains(Symbols.TICKER_Z) || unmodifiedParts[i].contains(Symbols.TICKER_O)) {
                unmodifiedParts[i] = "§e§l" + unmodifiedParts[i];
                break;
            }
        }
        return unmodifiedParts;
    }

    public static ActionBarData extractData(String actionBarText) {
            try {
                ActionBarData data = new ActionBarData();
                String[] unmodifiedParts = getUnmodifiedParts(actionBarText);
                for (String unmodifiedPart : unmodifiedParts) {
                    try {
                        String trimmed = unmodifiedPart.trim();
                        String unformatted = TextUtils.removeFormatting(trimmed);
                        if (unformatted.contains(Symbols.HEALTH)) {
                            var matcher = healthPattern.matcher(unformatted);
                            if (matcher.find()) {
                                data.currentHealth = Float.parseFloat(matcher.group("current").replace(",", ""));
                                data.maxHealth = Float.parseFloat(matcher.group("max").replace(",", ""));
                                if (matcher.group("stacks") != null) {
                                    data.stackAmount = Integer.parseInt(matcher.group("stacks"));
                                }
                                if (matcher.group("symbol") != null) {
                                    data.stackSymbol = matcher.group("symbol");
                                }
                            }
                        } else if (unformatted.contains(Symbols.MANA)) {
                            //TODO: Still uses string manipulation

                            // Mana
                            // 411/1,221✎ 2ʬ
                            // 289/1,221✎ Mana
                            String[] manaParts = unformatted.split(" ");
                            manaParts[0] = manaParts[0].replace(Symbols.MANA, "");
                            String[] mana = manaParts[0].split("/");
                            data.currentMana = Float.parseFloat(mana[0].replace(",", ""));
                            data.maxMana = Float.parseFloat(mana[1].replace(",", ""));
                            if (manaParts[1].contains(Symbols.OVERFLOW_MANA)) {
                                data.overflowMana = Float.parseFloat(manaParts[1].replace(Symbols.OVERFLOW_MANA, "").replace(",", ""));
                            } else {
                                data.overflowMana = 0f;
                            }

                        } else if (unformatted.contains(Symbols.DEFENSE)) {
                            //TODO: Still uses string manipulation

                            // Defense
                            String defense = unformatted.split(Symbols.DEFENSE)[0].trim();
                            data.defense = Integer.parseInt(defense.replace(",", ""));

                        } else if (unformatted.contains("Mana")) {
                            Matcher matcher = manaAbilityPattern.matcher(unformatted);
                            if (matcher.find()) {
                                data.abilityManaCost = Integer.parseInt(matcher.group(1));
                                data.abilityName = matcher.group(2);
                            }
                        } else if (skillLevelPatern.matcher(unformatted).matches()) {
                            Matcher matcher = skillLevelPatern.matcher(unformatted);
                            if (matcher.find() && !matcher.group(2).contains("SkyBlock XP")) {
                                data.gainedXP = NumberUtils.parseFloatWithKorM(matcher.group(1));
                                data.skillType = matcher.group(2);
                                if (matcher.group(3).contains("/")) {
                                    String[] xp = matcher.group(3).split("/");
                                    data.totalXP = NumberUtils.parseFloatWithKorM(xp[1]);
                                    data.nextLevelXP = NumberUtils.parseFloatWithKorM(xp[0]);
                                    // TODO: Transition uses of this to an event which SkillHud can subscribe to
                                    SkillHudManager.INSTANCE.update(data.skillType, data.gainedXP, data.totalXP, data.nextLevelXP);
                                } else {
                                    data.skillPercentage = Float.parseFloat(matcher.group(3).replace("%", ""));
                                    SkillHudManager.INSTANCE.update(data.skillType, data.gainedXP, data.skillPercentage);
                                }
                            }
                        } else if (unformatted.contains("Secrets")) {
                            Matcher matcher = secretsPattern.matcher(unformatted);
                            if (matcher.find()) {
                                data.secretsFound = Integer.parseInt(matcher.group(1));
                                data.secretsTotal = Integer.parseInt(matcher.group(2));
                            }
                        } else if (unformatted.contains("Drill Fuel")) {
                            // Drill Fuel
                            String[] drillFuel = unformatted.split(" ")[0].split("/");
                            data.drillFuel = Integer.parseInt(drillFuel[0].replace(",", ""));
                            data.maxDrillFuel = NumberUtils.parseIntWithKorM(drillFuel[1]);
                        } else if (unformatted.contains("ф Left")) {
                            // Rift Timer
                            SkyblockTweaks.LOGGER.info("Rift Timer: {}", unformatted);
                            Matcher matcher = riftTimePattern.matcher(unformatted);
                            if (matcher.matches()) {
                                data.riftTime = matcher.group("time");
                                data.riftTicking = trimmed.contains("§a");
                            }
                        } else if (unformatted.contains(Symbols.TICKER_Z) || unformatted.contains(Symbols.TICKER_O)) {
                            //TODO: Still uses string manipulation

                            // Ornate/Florid: §e§lⓩⓩⓩ§6§lⓄⓄ
                            // Regular: §a§lⓩ§2§lⓄⓄⓄ
                            // Foil: §e§lⓄⓄ§7§lⓄⓄ
                            data.maxTickers = unformatted.length();
                            if (trimmed.contains("§6§l")) {
                                var split = trimmed.split("§6§l");
                                data.currentTickers = TextUtils.removeFormatting(split[0]).length();
                            } else if (trimmed.contains("§2§l")) {
                                var split = trimmed.split("§2§l");
                                data.currentTickers = TextUtils.removeFormatting(split[0]).length();
                            } else if (trimmed.contains("§7§l")) {
                                var split = trimmed.split("§7§l");
                                data.currentTickers = TextUtils.removeFormatting(split[0]).length();
                            }
                        } else if (unformatted.contains(Symbols.PRESSURE)) {
                            Matcher matcher = pressurePattern.matcher(unformatted);
                            if (matcher.matches()) {
                                data.pressure = Integer.parseInt(matcher.group("pressure"));
                            }


                        }
                    } catch (Exception e) {
                        ErrorHandler.handleError(e, "Error Parsing action bar segment/*LOGONLY: {}*/", ErrorLevel.WARNING, false, unmodifiedPart);
                    }
                }
                return data;
            } catch (Exception e) {
                ErrorHandler.handleError(e, "Error Parsing action bar text/*LOGONLY: {}*/", ErrorLevel.WARNING, false, actionBarText);
                return new ActionBarData();
            }


    }

    public static Text runTransformations(Text actionBarText) {
        try {
            String[] unmodifiedParts = getUnmodifiedParts(actionBarText.getString());
            StringBuilder newText = new StringBuilder();
            for (String unmodifiedPart : unmodifiedParts) {
                try {
                    String trimmed = unmodifiedPart.trim();
                    String unformatted = TextUtils.removeFormatting(trimmed);
                    if (unformatted.toLowerCase().contains("race")) {
                        // Races, we do these first because the timer updates an obscene amount
                        newText.append(trimmed).append(SEPERATOR12);
                    } else if (unformatted.contains(Symbols.HEALTH)) {
                        if (!SBTConfig.get().actionBarFilters.hideHealth) {
                            newText.append(SEPERATOR5).append(trimmed);
                        }
                    } else if (unformatted.contains(Symbols.MANA)) {
                        if (!SBTConfig.get().actionBarFilters.hideMana) {
                            newText.append(SEPERATOR5).append(trimmed);
                        }
                    } else if (unformatted.contains("NOT ENOUGH MANA")) {
                        newText.append(SEPERATOR5).append(trimmed);
                    } else if (unformatted.contains(Symbols.DEFENSE)) {
                        if (!SBTConfig.get().actionBarFilters.hideDefense) {
                            newText.append(SEPERATOR5).append(trimmed);
                        }

                    } else if (unformatted.contains("Mana")) {
                        newText.append(SEPERATOR5).append(trimmed);
                    } else if (skillLevelPatern.matcher(unformatted).matches() && !unformatted.contains("SkyBlock XP")) {
                        if (!SBTConfig.get().actionBarFilters.hideSkill) {
                            newText.append(SEPERATOR5).append(trimmed);
                        }
                    } else if (unformatted.contains("Secrets")) {
                        if (!SBTConfig.get().actionBarFilters.hideSecrets) {
                            newText.append(SEPERATOR5).append(trimmed);
                        }
                    } else if (unformatted.contains("Drill Fuel")) {
                        if (!SBTConfig.get().actionBarFilters.hideDrill) {
                            newText.append(SEPERATOR5).append(trimmed);
                        }
                    } else if (unformatted.contains("ф Left")) {
                        if (!SBTConfig.get().actionBarFilters.hideRiftTime) {
                            newText.append(SEPERATOR5).append(trimmed);
                        }
                    } else if (unformatted.contains("second") || unformatted.contains("DPS")) {
                        // Trial of Fire
                        newText.append(SEPERATOR3).append(trimmed);
                    } else if (unformatted.contains("ⓩ") || unformatted.contains("Ⓞ")) {
                        if (!SBTConfig.get().actionBarFilters.hideTickers) {
                            newText.append(SEPERATOR4).append(trimmed);
                        }
                    } else if (unformatted.contains("⏣")) {
                        if (!SBTConfig.get().actionBarFilters.hideLocation) {
                            newText.append(SEPERATOR5).append(trimmed);
                        }
                    } else if (unformatted.contains(Symbols.PRESSURE)) {
                        if (!SBTConfig.get().actionBarFilters.hidePressure) {
                            newText.append(SEPERATOR5).append(trimmed);
                        }
                    } else {
                        newText.append(SEPERATOR5).append(trimmed);
                    }
                } catch (Exception e) {
                    ErrorHandler.handleError(e, "Error Parsing action bar segment/*LOGONLY: {}*/", ErrorLevel.WARNING, false, unmodifiedPart);
                    newText.append(SEPERATOR5).append(unmodifiedPart.trim());

                }
            }
            return Text.of(newText.toString());
        } catch (Exception e) {
            ErrorHandler.handleError(e, "Error Parsing transforming bar text/*LOGONLY: {}*/", ErrorLevel.WARNING, false, actionBarText.getString());
            return actionBarText;
        }
    }

    public static void registerEvents() {
        ChatEvents.ON_ACTION_BAR.register(message -> {
           // SkyblockTweaks.LOGGER.info(message.getString());
            SkyblockData.update(ActionBarTransformer.extractData(message.getString()));
        });
        ClientReceiveMessageEvents.MODIFY_GAME.register((message, overlay) -> {
            if (!overlay) return message;
            return ActionBarTransformer.runTransformations(message);
        });
    }




    public static class Config {
        @SerialEntry
        public boolean hideHealth = false;

        @SerialEntry
        public boolean hideDefense = false;

        @SerialEntry
        public boolean hideMana = false;

        @SerialEntry
        public boolean hideAbilityUse = false;

        @SerialEntry
        public boolean hideSkill = false;

        @SerialEntry
        public boolean hideDrill = false;

        @SerialEntry
        public boolean hideSecrets = false;

        @SerialEntry
        public boolean hideTickers = false;

        @SerialEntry
        public boolean hideRiftTime = false;

        @SerialEntry
        public boolean hideLocation = false;

        @SerialEntry
        public boolean hidePressure = false;


        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var health = Option.<Boolean>createBuilder()
                    .name(key("actionBarFilters.hideHealth"))
                    .description(keyD("actionBarFilters.hideHealth"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideHealth,
                            () -> config.actionBarFilters.hideHealth,
                            value -> config.actionBarFilters.hideHealth = value
                    )
                    .build();
            var defense = Option.<Boolean>createBuilder()
                    .name(key("actionBarFilters.hideDefense"))
                    .description(keyD("actionBarFilters.hideDefense"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideDefense,
                            () -> config.actionBarFilters.hideDefense,
                            value -> config.actionBarFilters.hideDefense = value
                    )
                    .build();
            var mana = Option.<Boolean>createBuilder()
                    .name(key("actionBarFilters.hideMana"))
                    .description(keyD("actionBarFilters.hideMana"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideMana,
                            () -> config.actionBarFilters.hideMana,
                            value -> config.actionBarFilters.hideMana = value
                    )
                    .build();
            var ability = Option.<Boolean>createBuilder()
                    .name(key("actionBarFilters.hideAbilityUse"))
                    .description(keyD("actionBarFilters.hideAbilityUse"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideAbilityUse,
                            () -> config.actionBarFilters.hideAbilityUse,
                            value -> config.actionBarFilters.hideAbilityUse = value
                    )
                    .build();
            var skill = Option.<Boolean>createBuilder()
                    .name(key("actionBarFilters.hideSkill"))
                    .description(keyD("actionBarFilters.hideSkill"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideSkill,
                            () -> config.actionBarFilters.hideSkill,
                            value -> config.actionBarFilters.hideSkill = value
                    )
                    .build();
            var drill = Option.<Boolean>createBuilder()
                    .name(key("actionBarFilters.hideDrill"))
                    .description(keyD("actionBarFilters.hideDrill"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideDrill,
                            () -> config.actionBarFilters.hideDrill,
                            value -> config.actionBarFilters.hideDrill = value
                    )
                    .build();
            var secrets = Option.<Boolean>createBuilder()
                    .name(key("actionBarFilters.hideSecrets"))
                    .description(keyD("actionBarFilters.hideSecrets"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideSecrets,
                            () -> config.actionBarFilters.hideSecrets,
                            value -> config.actionBarFilters.hideSecrets = value
                    )
                    .build();
            var tickers = Option.<Boolean>createBuilder()
                    .name(key("actionBarFilters.hideTickers"))
                    .description(keyD("actionBarFilters.hideTickers"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideTickers,
                            () -> config.actionBarFilters.hideTickers,
                            value -> config.actionBarFilters.hideTickers = value
                    )
                    .build();

            var riftTime = Option.<Boolean>createBuilder()
                    .name(key("actionBarFilters.hideRiftTime"))
                    .description(keyD("actionBarFilters.hideRiftTime"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideRiftTime,
                            () -> config.actionBarFilters.hideRiftTime,
                            value -> config.actionBarFilters.hideRiftTime = value
                    )
                    .build();

            var location = Option.<Boolean>createBuilder()
                    .name(key("actionBarFilters.hideLocation"))
                    .description(keyD("actionBarFilters.hideLocation"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideLocation,
                            () -> config.actionBarFilters.hideLocation,
                            value -> config.actionBarFilters.hideLocation = value
                    )
                    .build();
            var pressure = Option.<Boolean>createBuilder()
                    .name(key("actionBarFilters.hidePressure"))
                    .description(keyD("actionBarFilters.hidePressure"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hidePressure,
                            () -> config.actionBarFilters.hidePressure,
                            value -> config.actionBarFilters.hidePressure = value
                    )
                    .build();


            return OptionGroup.createBuilder()
                    .name(key("actionBarFilters"))
                    .description(keyD("actionBarFilters"))
                    .option(health)
                    .option(defense)
                    .option(mana)
                    .option(ability)
                    .option(skill)
                    .option(drill)
                    .option(secrets)
                    .option(tickers)
                    .option(riftTime)
                    .option(location)
                    .option(pressure)
                    .build();
        }
    }



}


