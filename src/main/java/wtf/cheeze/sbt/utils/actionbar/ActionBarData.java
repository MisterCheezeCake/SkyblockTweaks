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

import org.jetbrains.annotations.Nullable;
import wtf.cheeze.sbt.SkyblockTweaks;

/**
 * Represents the data that can be extracted from the action bar, and also contains the transformed text.
 * Not all fields are always present, and the mod does not use or parse the following fields:
 *      - Race Fields
 *      - Location Alert Fields
 */
public class ActionBarData {
    @Nullable
    public Float maxHealth;
    @Nullable
    public Float currentHealth;
    @Nullable
    public Integer defense;
    @Nullable
    public Float maxMana;
    @Nullable
    public Float currentMana;
    @Nullable
    public Float overflowMana;
    @Nullable
    public String skillType;
    @Nullable
    public Float gainedXP;
    @Nullable
    public Float totalXP;
    @Nullable
    public Float nextLevelXP;
    @Nullable
    public Float skillPercentage;
    @Nullable
    public Integer drillFuel;
    @Nullable
    public Integer maxDrillFuel;
    @Nullable
    public String abilityName;
    @Nullable
    public Integer abilityManaCost;
    @Nullable
    public Integer secretsFound;
    @Nullable
    public Integer secretsTotal;
    @Nullable
    public Integer maxTickers;
    @Nullable
    public Integer currentTickers;
    @Nullable
    public String stackSymbol;
    @Nullable
    public Integer stackAmount;
    @Nullable
    public String riftTime;
    @Nullable
    public Boolean riftTicking;
    @Nullable
    public Integer pressure;

    public String toJson() {
        return SkyblockTweaks.GSON.toJson(this);
    }
}

