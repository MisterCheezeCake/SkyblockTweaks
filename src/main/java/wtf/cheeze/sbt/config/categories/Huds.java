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
    public ManaHud.Config mana = new ManaHud.Config();

    @SerialEntry
    public OverflowManaHud.Config overflowMana = new OverflowManaHud.Config();

    @SerialEntry
    public ManaBar.Config manaBar = new ManaBar.Config();

    @SerialEntry
    public HealthHud.Config health = new HealthHud.Config();

    @SerialEntry
    public HealthBar.Config healthBar = new HealthBar.Config();

    @SerialEntry
    public EhpHud.Config ehp = new EhpHud.Config();

    @SerialEntry
    public SpeedHud.Config speed = new SpeedHud.Config();

    @SerialEntry
    public DefenseHud.Config defense = new DefenseHud.Config();

    @SerialEntry
    public DamageReductionHud.Config dr = new DamageReductionHud.Config();

    @SerialEntry
    public DrillFuelHud.Config drillFuel = new DrillFuelHud.Config();

    @SerialEntry
    public DrillFuelBar.Config drillFuelBar = new DrillFuelBar.Config();

    @SerialEntry
    public CoordinatesHud.Config coordinates = new CoordinatesHud.Config();

    @SerialEntry
    public RealTimeHud.Config time = new RealTimeHud.Config();

    @SerialEntry
    public FpsHud.Config fps = new FpsHud.Config();

    @SerialEntry
    public SkillHudManager.SkillHud.Config skills = new SkillHudManager.SkillHud.Config();

    @SerialEntry
    public SkillHudManager.SkillBar.Config skillBar = new SkillHudManager.SkillBar.Config();

    @SerialEntry
    public TickerHud.Config ticker = new TickerHud.Config();

    @SerialEntry
    public QuiverHud.Config quiver = new QuiverHud.Config();

    @SerialEntry
    public ArmorStackHud.Config armorStack = new ArmorStackHud.Config();

    @SerialEntry
    public RiftTimeHud.Config riftTime = new RiftTimeHud.Config();



    public static ConfigCategory getCategory(ConfigImpl defaults, ConfigImpl config) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("sbt.config.huds"))
                .tooltip(Text.translatable("sbt.config.huds.desc"))
                .group(SkillHudManager.SkillHud.Config.getGroup(defaults, config))
                .group(SkillHudManager.SkillBar.Config.getGroup(defaults, config))
                .group(HealthHud.Config.getGroup(defaults, config))
                .group(HealthBar.Config.getGroup(defaults, config))
                .group(ManaHud.Config.getGroup(defaults, config))
                .group(ManaBar.Config.getGroup(defaults, config))
                .group(OverflowManaHud.Config.getGroup(defaults, config))
                .group(EhpHud.Config.getGroup(defaults, config))
                .group(SpeedHud.Config.getGroup(defaults, config))
                .group(DefenseHud.Config.getGroup(defaults, config))
                .group(DamageReductionHud.Config.getGroup(defaults, config))
                .group(DrillFuelHud.Config.getGroup(defaults, config))
                .group(DrillFuelBar.Config.getGroup(defaults, config))
                .group(CoordinatesHud.Config.getGroup(defaults, config))
                .group(RealTimeHud.Config.getGroup(defaults, config))
                .group(FpsHud.Config.getGroup(defaults, config))
                .group(TickerHud.Config.getGroup(defaults, config))
                .group(QuiverHud.Config.getGroup(defaults, config))
                .group(ArmorStackHud.Config.getGroup(defaults, config))
                .group(RiftTimeHud.Config.getGroup(defaults, config))
                .build();
    }
}
