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
package wtf.cheeze.sbt.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import wtf.cheeze.sbt.SkyBlockTweaks;
import wtf.cheeze.sbt.utils.actionbar.ActionBarData;

public class SkyBlockData {
    public boolean inSB = false;

    public int defense = 0;
    public float maxHealth = 0;
    public float health = 0;
    public float maxMana = 0;
    public float mana = 0;
    public float overflowMana = 0;

    public float drillFuel = 0;
    public float maxDrillFuel = 0;


    public float getSpeed() {
        MinecraftClient mc = MinecraftClient.getInstance();
        // sprint = 1.3 x base speed
        return mc.player.isSprinting() ? (mc.player.getMovementSpeed() / 1.3f) * 1000 : mc.player.getMovementSpeed() * 1000;
    }
    public float effectiveHealth() {
        return Math.round( health * (1 + (defense / 100f)));
    }
    public float damageReduction() {
        return (defense / (defense + 100f)) * 100;
    }

    public boolean isThePlayerHoldingADrill() {
        return MinecraftClient.getInstance().player.getMainHandStack().getName().getString().contains("Drill");
    }

    public void update(ActionBarData data) {
        if (data == null) return;
        if (data.defense != null) this.defense = data.defense;
        if (data.maxHealth != null) this.maxHealth = data.maxHealth;
        if (data.currentHealth != null) this.health = data.currentHealth;
        if (data.maxMana != null) this.maxMana = data.maxMana;
        if (data.currentMana != null) this.mana = data.currentMana;
        if (data.overflowMana != null) this.overflowMana = data.overflowMana;
        if (data.drillFuel != null) this.drillFuel = data.drillFuel;
        if (data.maxDrillFuel != null) this.maxDrillFuel = data.maxDrillFuel;


    }
}
