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
package wtf.cheeze.sbt.features.misc;

import wtf.cheeze.sbt.utils.enums.Powder;

/**
 * This lives in its own class instead of SkyblockConstants because it has its own complex logic
 * Java version of <a href="https://github.com/MisterCheezeCake/ChatTriggers-Modules/tree/main/CalcPowder">CalcPowder</a>, by Yours Truly
 */
public class CalcPowder {
    @FunctionalInterface
    public interface CostFunction {
        double calculate(double nextLevel);
    }

    public record Perk(
            Powder powder,
            int max,
            int add,
            float pow
    ){
        public CostFunction getCostFunction() {
            return (double n) -> Math.pow((n+add), pow);
        }
        public int calculateCost(int target) {
            var currentCost = 0;
            for (var i = 1; i <= target; i++) {
                currentCost += (int) getCostFunction().calculate(i);
            }
            return currentCost;
        }
        public int costBetween(int start, int end) {
            return this.calculateCost(end) - this.calculateCost(start);
        }
    }

}

