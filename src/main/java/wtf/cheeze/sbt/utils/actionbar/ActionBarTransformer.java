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
import wtf.cheeze.sbt.config.SkyblockTweaksConfig;
import wtf.cheeze.sbt.features.huds.SkillHUDManager;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.TextUtils;

import java.util.Arrays;
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
 */
public class ActionBarTransformer {
    public static final String SEPERATOR3 = "   ";
    public static final String SEPERATOR4 = "     ";
    public static final String SEPERATOR5 = "     ";
    public static final String SEPERATOR12 = "            ";
    private static Pattern manaAbilityPattern = Pattern.compile("-(\\d+) Mana \\((.+)\\)");
    private static Pattern skillLevelPatern = Pattern.compile("\\+(\\d+\\.?\\d*) (.+) \\((.+)\\)");
    private static Pattern secretsPattern = Pattern.compile("(\\d+)/(\\d+) Secrets");

    public static ActionBarData extractDataAndRunTransformation(String actionBarText) {
      try {
          ActionBarData data = new ActionBarData();
          String[] parts = actionBarText.split(SEPERATOR3);
          String newText = "";
          for (String part : parts) {
              String unpadded = part.trim();
              String segment = TextUtils.removeColorCodes(unpadded);
              if (segment.toLowerCase().contains("race")) {
                  // Races, we do these first because the timer updates an obscene amount
                  newText += unpadded + SEPERATOR12;
              } else if (segment.contains("❤")) {
                  // Health
                  if (segment.contains("+")) {
                      // Health with a healing wand
                      String[] health = segment.replaceAll("❤", "").split("\\+")[0].split("/");
                      data.currentHealth = Float.parseFloat(health[0].replace(",", ""));
                      data.maxHealth = Float.parseFloat(health[1].replace(",", ""));
                      if (!SkyblockTweaks.CONFIG.config.actionBarFilters.hideHealth) {
                          newText += SEPERATOR5 + unpadded;
                      } else {
                          newText += SEPERATOR5 + TextUtils.SECTION + "c" + "+" + segment.split("\\+")[1];
                      }
                      continue;
                  }
                  String[] health = segment.replaceAll("❤", "").split("/");
                  data.currentHealth = Float.parseFloat(health[0].replace(",", ""));
                  data.maxHealth = Float.parseFloat(health[1].replace(",", ""));
                  if (!SkyblockTweaks.CONFIG.config.actionBarFilters.hideHealth) {
                      newText += SEPERATOR5 + unpadded;
                  }

              } else if (segment.contains("✎")) {
                  // Mana
                  // 411/1,221✎ 2ʬ
                  // 289/1,221✎ Mana
                  String[] manaParts = segment.split(" ");
                  manaParts[0] = manaParts[0].replace("✎", "");
                  String[] mana = manaParts[0].split("/");
                  data.currentMana = Float.parseFloat(mana[0].replace(",", ""));
                  data.maxMana = Float.parseFloat(mana[1].replace(",", ""));
                  if (manaParts[1].contains("ʬ")) {
                      data.overflowMana = Float.parseFloat(manaParts[1].replace("ʬ", "").replace(",", ""));
                  } else {
                      data.overflowMana = 0f;
                  }
                  if (!SkyblockTweaks.CONFIG.config.actionBarFilters.hideMana) {
                      newText += SEPERATOR5 + unpadded;
                  }
              } else if (segment.contains("NOT ENOUGH MANA")) {
                  newText += SEPERATOR5 + unpadded;
              } else if (segment.contains("❈")) {
                  // Defense
                  String defense = segment.split("❈")[0].trim();
                  data.defense = Integer.parseInt(defense.replace(",", ""));
                  if (!SkyblockTweaks.CONFIG.config.actionBarFilters.hideDefense) {
                      newText += SEPERATOR5 + unpadded;
                  }

              } else if (segment.contains("Mana")) {
                  Matcher matcher = manaAbilityPattern.matcher(segment);
                  if (matcher.find()) {
                      data.abilityManaCost = Integer.parseInt(matcher.group(1));
                      data.abilityName = matcher.group(2);
                      if (!SkyblockTweaks.CONFIG.config.actionBarFilters.hideAbilityUse) {
                          newText += SEPERATOR5 + unpadded;
                      }
                      continue;
                  }
                  newText += SEPERATOR5 + unpadded;

              } else if (skillLevelPatern.matcher(segment).matches()) {
                  Matcher matcher = skillLevelPatern.matcher(segment);
                  if (matcher.find()) {
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
                  }
                  if (!SkyblockTweaks.CONFIG.config.actionBarFilters.hideSkill) {
                      newText += SEPERATOR5 + unpadded;
                  }
              } else if (segment.contains("Secrets")) {
                  Matcher matcher = secretsPattern.matcher(segment);
                  if (matcher.find()) {
                      data.secretsFound = Integer.parseInt(matcher.group(1));
                      data.secretsTotal = Integer.parseInt(matcher.group(2));
                  }
                  if (!SkyblockTweaks.CONFIG.config.actionBarFilters.hideSecrets) {
                      newText += SEPERATOR5 + unpadded;
                  }
              } else if (segment.contains("Drill Fuel")) {
                  // Drill Fuel
                  String[] drillFuel = segment.split(" ")[0].split("/");
                  data.drillFuel = Integer.parseInt(drillFuel[0].replace(",", ""));
                  data.maxDrillFuel = NumberUtils.parseIntWithKorM(drillFuel[1]);
                  if (!SkyblockTweaks.CONFIG.config.actionBarFilters.hideDrill) {
                      newText += SEPERATOR5 + unpadded;
                  }
              } else if (segment.contains("second") || segment.contains("DPS")) {
                  // Trial of Fire
                  newText += SEPERATOR3 + unpadded;
              } else if (segment.contains("ⓩ") || segment.contains("Ⓞ")){
                  // Ornate/Florid: §e§lⓩⓩⓩ§6§lⓄⓄ
                  // Regular: §a§lⓩ§2§lⓄⓄⓄ
                  // Foil: §e§lⓄⓄ§7§lⓄⓄ
                  data.maxTickers = segment.length();
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

                  if (!SkyblockTweaks.CONFIG.config.actionBarFilters.hideTickers) {
                        newText += SEPERATOR4 + unpadded;
                  }

              } else {
                  newText += SEPERATOR5 + unpadded;
              }
          }
          newText = newText.trim();
          data.transformedText = newText;
          return data;
      } catch (Exception e) {
          SkyblockTweaks.LOGGER.error("Error parsing action bar text: {}", actionBarText, e);
          SkyblockTweaks.LOGGER.warn("Some features may not work correctly. Please report this to MisterCheezeCake immediately.");
          return new ActionBarData();
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
                    .name(Text.literal("Hide Health in Action Bar"))
                    .description(OptionDescription.of(Text.literal("Hides the health in the action bar")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideHealth,
                            () -> config.actionBarFilters.hideHealth,
                            value -> config.actionBarFilters.hideHealth = (Boolean) value
                    )
                    .build();
            var defense = Option.<Boolean>createBuilder()
                    .name(Text.literal("Hide Defense in Action Bar"))
                    .description(OptionDescription.of(Text.literal("Hides the defense in the action bar")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideDefense,
                            () -> config.actionBarFilters.hideDefense,
                            value -> config.actionBarFilters.hideDefense = (Boolean) value
                    )
                    .build();
            var mana = Option.<Boolean>createBuilder()
                    .name(Text.literal("Hide Mana in Action Bar"))
                    .description(OptionDescription.of(Text.literal("Hides the mana in the action bar")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideMana,
                            () -> config.actionBarFilters.hideMana,
                            value -> config.actionBarFilters.hideMana = (Boolean) value
                    )
                    .build();
            var ability = Option.<Boolean>createBuilder()
                    .name(Text.literal("Hide Ability Use in Action Bar"))
                    .description(OptionDescription.of(Text.literal("Hides the ability use in the action bar")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideAbilityUse,
                            () -> config.actionBarFilters.hideAbilityUse,
                            value -> config.actionBarFilters.hideAbilityUse = (Boolean) value
                    )
                    .build();
            var skill = Option.<Boolean>createBuilder()
                    .name(Text.literal("Hide Skill in Action Bar"))
                    .description(OptionDescription.of(Text.literal("Hides the skill in the action bar")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideSkill,
                            () -> config.actionBarFilters.hideSkill,
                            value -> config.actionBarFilters.hideSkill = (Boolean) value
                    )
                    .build();
            var drill = Option.<Boolean>createBuilder()
                    .name(Text.literal("Hide Drill Fuel in Action Bar"))
                    .description(OptionDescription.of(Text.literal("Hides the drill fuel in the action bar")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideDrill,
                            () -> config.actionBarFilters.hideDrill,
                            value -> config.actionBarFilters.hideDrill = (Boolean) value
                    )
                    .build();
            var secrets = Option.<Boolean>createBuilder()
                    .name(Text.literal("Hide Secrets in Action Bar"))
                    .description(OptionDescription.of(Text.literal("Hides the secrets display in the action bar")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideSecrets,
                            () -> config.actionBarFilters.hideSecrets,
                            value -> config.actionBarFilters.hideSecrets = (Boolean) value
                    )
                    .build();
            var tickers = Option.<Boolean>createBuilder()
                    .name(Text.literal("Hide Tickers/Charges in Action Bar"))
                    .description(OptionDescription.of(Text.literal("Hides the tickers/charges in the action bar")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.actionBarFilters.hideTickers,
                            () -> config.actionBarFilters.hideTickers,
                            value -> config.actionBarFilters.hideTickers = (Boolean) value
                    )
                    .build();
            return OptionGroup.createBuilder()
                    .name(Text.literal("Action Bar Filters"))
                    .description(OptionDescription.of(Text.literal("Filters out certain information from the action bar")))
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


