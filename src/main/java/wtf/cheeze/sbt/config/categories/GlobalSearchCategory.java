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
package wtf.cheeze.sbt.config.categories;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.OptionDescription;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.features.BrewingStandOverlay;
import wtf.cheeze.sbt.features.MenuHighlights;
import wtf.cheeze.sbt.features.chat.ChatProtections;
import wtf.cheeze.sbt.features.chat.PartyFeatures;
import wtf.cheeze.sbt.features.huds.*;
import wtf.cheeze.sbt.features.mining.EventTimerHUD;
import wtf.cheeze.sbt.features.mining.MiningHUD;
import wtf.cheeze.sbt.mixin.YACLScreenAccessor;
import wtf.cheeze.sbt.utils.Version;
import wtf.cheeze.sbt.utils.actionbar.ActionBarTransformer;
import wtf.cheeze.sbt.utils.errors.ErrorHandler;


public class GlobalSearchCategory {

    public static ConfigCategory getCategory(ConfigImpl defaults, ConfigImpl config) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("sbt.config.globalSearch"))
                .tooltip(Text.translatable("sbt.config.globalSearch.desc"))
                .option(Version.getStreamOption(defaults, config))
                .option(ErrorHandler.getChatAll(defaults, config))
                .group(General.InventoryTweaks.getGroup(defaults, config))
                .group(MenuHighlights.Config.getGroup(defaults, config))
                .group(BrewingStandOverlay.Config.getGroup(defaults, config))
                .group(General.HudTweaks.getGroup(defaults, config))
                .group(ActionBarTransformer.Config.getGroup(defaults, config))
                .group(PartyFeatures.Config.getGroup(defaults, config))
                .group(PartyFeatures.Config.getBlackList(defaults, config))
                .group(ChatProtections.Config.getGroup(defaults, config))
                .group(SkillHUDManager.SkillHUD.Config.getGroup(defaults, config))
                .group(SkillHUDManager.SkillBar.Config.getGroup(defaults, config))
                .group(HealthHUD.Config.getGroup(defaults, config))
                .group(HealthBar.Config.getGroup(defaults, config))
                .group(ManaHUD.Config.getGroup(defaults, config))
                .group(ManaBar.Config.getGroup(defaults, config))
                .group(OverflowManaHUD.Config.getGroup(defaults, config))
                .group(EhpHUD.Config.getGroup(defaults, config))
                .group(SpeedHUD.Config.getGroup(defaults, config))
                .group(DefenseHUD.Config.getGroup(defaults, config))
                .group(DamageReductionHUD.Config.getGroup(defaults, config))
                .group(DrillFuelHUD.Config.getGroup(defaults, config))
                .group(DrillFuelBar.Config.getGroup(defaults, config))
                .group(CoordinatesHUD.Config.getGroup(defaults, config))
                .group(RealTimeHUD.Config.getGroup(defaults, config))
                .group(FpsHUD.Config.getGroup(defaults, config))
                .group(TickerHUD.Config.getGroup(defaults, config))
                .group(QuiverHUD.Config.getGroup(defaults, config))
                .group(ArmorStackHUD.Config.getGroup(defaults, config))
                .group(RiftTimeHUD.Config.getGroup(defaults, config))
                .group(MiningHUD.Config.getGroup(defaults, config))
                .group(MiningHUD.Config.getCompositionOption(defaults, config))
                .group(EventTimerHUD.Config.getGroup(defaults, config))
                .build();
    }

    public static ButtonOption getOpenGlobalSearchButton(ConfigImpl defaults, ConfigImpl config) {
        return ButtonOption.createBuilder()
                .name(Text.translatable("sbt.config.globalSearch.open"))
                .description(OptionDescription.of(Text.translatable("sbt.config.globalSearch.open.desc")))
                .text(Text.translatable("sbt.config.globalSearch.open.text"))
                .action((y, o) -> {

                    if (y.pendingChanges()) {
                        y.finishOrSave();
                    }
                   MinecraftClient.getInstance().setScreen(SBTConfig.getSpecialGlobalSearchScreen(((YACLScreenAccessor) y).sbt$getParent()));


                })
                .build();
    }


}
