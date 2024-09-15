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

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.features.huds.*;

public class Huds {


    @SerialEntry
    public ManaHUD.Config mana = new ManaHUD.Config();

    @SerialEntry
    public OverflowManaHUD.Config overflowMana = new OverflowManaHUD.Config();

    @SerialEntry
    public ManaBar.Config manaBar = new ManaBar.Config();

    @SerialEntry
    public HealthHUD.Config health = new HealthHUD.Config();

    @SerialEntry
    public HealthBar.Config healthBar = new HealthBar.Config();

    @SerialEntry
    public EhpHUD.Config ehp = new EhpHUD.Config();

    @SerialEntry
    public SpeedHUD.Config speed = new SpeedHUD.Config();

    @SerialEntry
    public DefenseHUD.Config defense = new DefenseHUD.Config();

    @SerialEntry
    public DamageReductionHUD.Config dr = new DamageReductionHUD.Config();

    @SerialEntry
    public DrillFuelHUD.Config drillFuel = new DrillFuelHUD.Config();

    @SerialEntry
    public DrillFuelBar.Config drillFuelBar = new DrillFuelBar.Config();

    @SerialEntry
    public CoordinatesHUD.Config coordinates = new CoordinatesHUD.Config();

    @SerialEntry
    public RealTimeHUD.Config time = new RealTimeHUD.Config();

    @SerialEntry
    public FpsHUD.Config fps = new FpsHUD.Config();

    @SerialEntry
    public SkillHUD.Config skills = new SkillHUD.Config();

    @SerialEntry
    public TickerHUD.Config ticker = new TickerHUD.Config();

    public static ConfigCategory getCategory(ConfigImpl defaults, ConfigImpl config) {
        return ConfigCategory.createBuilder()
                .name(Text.literal("HUDs"))
                .tooltip(Text.literal("Settings for various HUDs"))
                .group(SkillHUD.Config.getGroup(defaults, config))
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
                .build();
    }
}
