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
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.features.huds.SkillHUDManager;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.TextUtils;
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
 */

public class ActionBarTransformer {
    public static final String SEPERATOR3 = "   ";
    public static final String SEPERATOR4 = "     ";
    public static final String SEPERATOR5 = "     ";
    public static final String SEPERATOR12 = "            ";

    private static final Pattern manaAbilityPattern = Pattern.compile("-(\\d+) Mana \\((.+)\\)");
    //private static Pattern skillLevelPatern = Pattern.compile("\\+(\\d+\\.?\\d*) (.+) \\((.+)\\)");
    private static final Pattern skillLevelPatern = Pattern.compile("\\+([\\d,]+\\.?\\d*) (.+) \\((.+)\\)");
    private static final Pattern secretsPattern = Pattern.compile("(\\d+)/(\\d+) Secrets");
    private static final Pattern healthPattern = Pattern.compile("(?<current>[\\d,]+)/(?<max>[\\d,]+)❤(?:\\+[\\d,]+.)?(?: {2})?(?<stacks>\\d+)?(?<symbol>.)?");

    public static ActionBarData extractDataAndRunTransformation(String actionBarText) {
      try {
          ActionBarData data = new ActionBarData();
          String[] unmodifiedParts = actionBarText.split(SEPERATOR3);
          StringBuilder newText = new StringBuilder();
          for (String unmodifiedPart : unmodifiedParts) {
              String unpadded = unmodifiedPart.trim();
              String rawContent = TextUtils.removeColorCodes(unpadded);
              if (rawContent.toLowerCase().contains("race")) {
                  // Races, we do these first because the timer updates an obscene amount
                  newText.append(unpadded).append(SEPERATOR12);
              } else if (rawContent.contains("❤")) {
                  var matcher = healthPattern.matcher(rawContent);
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
                  if (!SBTConfig.get().actionBarFilters.hideHealth) {
                      newText.append(SEPERATOR5).append(unpadded);
                  }

              } else if (rawContent.contains("✎")) {
                  // Mana
                  // 411/1,221✎ 2ʬ
                  // 289/1,221✎ Mana
                  String[] manaParts = rawContent.split(" ");
                  manaParts[0] = manaParts[0].replace("✎", "");
                  String[] mana = manaParts[0].split("/");
                  data.currentMana = Float.parseFloat(mana[0].replace(",", ""));
                  data.maxMana = Float.parseFloat(mana[1].replace(",", ""));
                  if (manaParts[1].contains("ʬ")) {
                      data.overflowMana = Float.parseFloat(manaParts[1].replace("ʬ", "").replace(",", ""));
                  } else {
                      data.overflowMana = 0f;
                  }
                  if (!SBTConfig.get().actionBarFilters.hideMana) {
                      newText.append(SEPERATOR5).append(unpadded);
                  }
              } else if (rawContent.contains("NOT ENOUGH MANA")) {
                  newText.append(SEPERATOR5).append(unpadded);
              } else if (rawContent.contains("❈")) {
                  // Defense
                  String defense = rawContent.split("❈")[0].trim();
                  data.defense = Integer.parseInt(defense.replace(",", ""));
                  if (!SBTConfig.get().actionBarFilters.hideDefense) {
                      newText.append(SEPERATOR5).append(unpadded);
                  }

              } else if (rawContent.contains("Mana")) {
                  Matcher matcher = manaAbilityPattern.matcher(rawContent);
                  if (matcher.find()) {
                      data.abilityManaCost = Integer.parseInt(matcher.group(1));
                      data.abilityName = matcher.group(2);
                      if (!SBTConfig.get().actionBarFilters.hideAbilityUse) {
                          newText.append(SEPERATOR5).append(unpadded);
                      }
                      continue;
                  }
                  newText.append(SEPERATOR5).append(unpadded);

              } else if (skillLevelPatern.matcher(rawContent).matches()) {
                  Matcher matcher = skillLevelPatern.matcher(rawContent);
                  if (matcher.find() && !matcher.group(2).contains("SkyBlock XP")) {
                      data.gainedXP = NumberUtils.parseFloatWithKorM(matcher.group(1));
                      data.skillType = matcher.group(2);
                      if (matcher.group(3).contains("/")) {
                          String[] xp = matcher.group(3).split("/");
                          data.totalXP = NumberUtils.parseFloatWithKorM(xp[1]);
                          data.nextLevelXP = NumberUtils.parseFloatWithKorM(xp[0]);
                          SkillHUDManager.INSTANCE.update(data.skillType, data.gainedXP, data.totalXP, data.nextLevelXP);
                      } else {
                          data.skillPercentage = Float.parseFloat(matcher.group(3).replace("%", ""));
                          SkillHUDManager.INSTANCE.update(data.skillType, data.gainedXP, data.skillPercentage);
                      }
                      if (!SBTConfig.get().actionBarFilters.hideSkill) {
                          newText.append(SEPERATOR5).append(unpadded);
                      }
                  } else {
                      newText.append(SEPERATOR5).append(unpadded);
                  }
              } else if (rawContent.contains("Secrets")) {
                  Matcher matcher = secretsPattern.matcher(rawContent);
                  if (matcher.find()) {
                      data.secretsFound = Integer.parseInt(matcher.group(1));
                      data.secretsTotal = Integer.parseInt(matcher.group(2));
                  }
                  if (!SBTConfig.get().actionBarFilters.hideSecrets) {
                      newText.append(SEPERATOR5).append(unpadded);
                  }
              } else if (rawContent.contains("Drill Fuel")) {
                  // Drill Fuel
                  String[] drillFuel = rawContent.split(" ")[0].split("/");
                  data.drillFuel = Integer.parseInt(drillFuel[0].replace(",", ""));
                  data.maxDrillFuel = NumberUtils.parseIntWithKorM(drillFuel[1]);
                  if (!SBTConfig.get().actionBarFilters.hideDrill) {
                      newText.append(SEPERATOR5).append(unpadded);
                  }
              } else if (rawContent.contains("second") || rawContent.contains("DPS")) {
                  // Trial of Fire
                  newText.append(SEPERATOR3).append(unpadded);
              } else if (rawContent.contains("ⓩ") || rawContent.contains("Ⓞ")){
                  // Ornate/Florid: §e§lⓩⓩⓩ§6§lⓄⓄ
                  // Regular: §a§lⓩ§2§lⓄⓄⓄ
                  // Foil: §e§lⓄⓄ§7§lⓄⓄ
                  data.maxTickers = rawContent.length();
                  if (unpadded.contains("§6§l")) {
                        var split = unpadded.split("§6§l") ;
                      data.currentTickers = TextUtils.removeColorCodes(split[0]).length();
                  } else if (unpadded.contains("§2§l")) {
                      var split = unpadded.split("§2§l") ;
                      data.currentTickers = TextUtils.removeColorCodes(split[0]).length();
                  }
                  else if (unpadded.contains("§7§l")) {
                      var split = unpadded.split("§7§l") ;
                      data.currentTickers = TextUtils.removeColorCodes(split[0]).length();
                  }

                  if (!SBTConfig.get().actionBarFilters.hideTickers) {
                        newText.append(SEPERATOR4).append(unpadded);
                  }

              } else {
                  newText.append(SEPERATOR5).append(unpadded);
              }
          }

          data.transformedText = newText.toString().trim();
          return data;
      } catch (Exception e) {
          SkyblockTweaks.LOGGER.error("Error parsing action bar text: {}", actionBarText, e);
          SkyblockTweaks.LOGGER.warn("Some features may not work correctly. Please report this to MisterCheezeCake immediately.");
          var data = new ActionBarData();
          // data.transformedText = actionBarText;
          return data;
      }
    }

    public static void registerEvents() {
        ClientReceiveMessageEvents.MODIFY_GAME.register((message, overlay) -> {
            if (!overlay) return message;
            //SkyblockTweaks.LOGGER.info("Old: " + message.getString());
            var data = ActionBarTransformer.extractDataAndRunTransformation(message.getString());
            //SkyblockTweaks.LOGGER.info("New: " + data.transformedText);
            SkyblockTweaks.DATA.update(data);
            return Text.of(data.transformedText);

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


        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var health = Option.<Boolean>createBuilder()
                    .name(key("actionBarFilters.hideHealth"))
                    .description(keyD("actionBarFilters.hideHealth"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideHealth,
                            () -> config.actionBarFilters.hideHealth,
                            value -> config.actionBarFilters.hideHealth = (Boolean) value
                    )
                    .build();
            var defense = Option.<Boolean>createBuilder()
                    .name(key("actionBarFilters.hideDefense"))
                    .description(keyD("actionBarFilters.hideDefense"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideDefense,
                            () -> config.actionBarFilters.hideDefense,
                            value -> config.actionBarFilters.hideDefense = (Boolean) value
                    )
                    .build();
            var mana = Option.<Boolean>createBuilder()
                    .name(key("actionBarFilters.hideMana"))
                    .description(keyD("actionBarFilters.hideMana"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideMana,
                            () -> config.actionBarFilters.hideMana,
                            value -> config.actionBarFilters.hideMana = (Boolean) value
                    )
                    .build();
            var ability = Option.<Boolean>createBuilder()
                    .name(key("actionBarFilters.hideAbilityUse"))
                    .description(keyD("actionBarFilters.hideAbilityUse"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideAbilityUse,
                            () -> config.actionBarFilters.hideAbilityUse,
                            value -> config.actionBarFilters.hideAbilityUse = (Boolean) value
                    )
                    .build();
            var skill = Option.<Boolean>createBuilder()
                    .name(key("actionBarFilters.hideSkill"))
                    .description(keyD("actionBarFilters.hideSkill"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideSkill,
                            () -> config.actionBarFilters.hideSkill,
                            value -> config.actionBarFilters.hideSkill = (Boolean) value
                    )
                    .build();
            var drill = Option.<Boolean>createBuilder()
                    .name(key("actionBarFilters.hideDrill"))
                    .description(keyD("actionBarFilters.hideDrill"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideDrill,
                            () -> config.actionBarFilters.hideDrill,
                            value -> config.actionBarFilters.hideDrill = (Boolean) value
                    )
                    .build();
            var secrets = Option.<Boolean>createBuilder()
                    .name(key("actionBarFilters.hideSecrets"))
                    .description(keyD("actionBarFilters.hideSecrets"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideSecrets,
                            () -> config.actionBarFilters.hideSecrets,
                            value -> config.actionBarFilters.hideSecrets = (Boolean) value
                    )
                    .build();
            var tickers = Option.<Boolean>createBuilder()
                    .name(key("actionBarFilters.hideTickers"))
                    .description(keyD("actionBarFilters.hideTickers"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideTickers,
                            () -> config.actionBarFilters.hideTickers,
                            value -> config.actionBarFilters.hideTickers = (Boolean) value
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
                    .build();
        }
    }



}


