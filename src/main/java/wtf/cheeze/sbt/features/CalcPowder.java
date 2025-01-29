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
package wtf.cheeze.sbt.features;

import java.util.Map;

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

    public static class Perk {
        public int max;
        public CostFunction cost;
        public Powder powder;

        public Perk(int max, CostFunction cost, Powder powder) {
            this.max = max;
            this.cost = cost;
            this.powder = powder;
        }
        private int calculateCost(int target) {
            var currentCost = 0;
            for (var i = 1; i <= target; i++) {
                currentCost += cost.calculate(i);
            }
            return currentCost;
        }
        public int costBetween(int start, int end) {
            return this.calculateCost(end) - this.calculateCost(start);
        }
    }

    public static final Map<String, Perk> PERKS = Map.ofEntries(
            // t1
            Map.entry("mining_speed", new Perk(50, (var n) -> Math.pow((n+1), 3), Powder.MITHRIL)),
            // t2
            Map.entry("mining_fortune", new Perk(50, (var n) -> Math.pow((n+1), 3.05), Powder.MITHRIL)),
            Map.entry("quick_forge", new Perk(20, (var n) -> Math.pow((n+1), 4), Powder.MITHRIL)),
            Map.entry("titanium_insanium", new Perk(50, (var n) -> Math.pow((n+1), 3.1), Powder.MITHRIL)),
            // t3
            Map.entry("daily_powder", new Perk(100, (var n) -> 200 + ((n-1)*18), Powder.MITHRIL)),
            Map.entry("luck_of_the_cave", new Perk(45, (var n) -> Math.pow((n+1), 3.07), Powder.MITHRIL)),
            Map.entry("crystalized", new Perk(30, (var n) -> Math.pow((n+1), 3.4), Powder.MITHRIL)),
            // t4
            Map.entry("efficient_miner", new Perk(100, (var n) -> Math.pow((n+1), 2.6), Powder.MITHRIL)),
            Map.entry("orbiter", new Perk(80, (var n) -> n*70, Powder.MITHRIL)),
            Map.entry("seasoned_mineman", new Perk(100, (var n) -> Math.pow((n+1), 2.3), Powder.MITHRIL)),
            // t6
            Map.entry("mole", new Perk(180, (var n) -> Math.pow((n+1), 2.2), Powder.GEMSTONE)),
            Map.entry("professional", new Perk(140, (var n) -> Math.pow((n+1), 2.3), Powder.GEMSTONE)),
            Map.entry("lonesome_miner", new Perk(45, (var n) -> Math.pow((n+1), 3.07), Powder.GEMSTONE)),
            Map.entry("great_explorer", new Perk(20, (var n) -> Math.pow((n+1), 4), Powder.GEMSTONE)),
            Map.entry("fortunate", new Perk(20, (var n) -> Math.pow((n+1), 3.05), Powder.GEMSTONE)),
            // t7
            Map.entry("powder_buff", new Perk(50, (var n) -> Math.pow((n+1), 3.2), Powder.GEMSTONE)),
            Map.entry("mining_speed_2", new Perk(50, (var n) -> Math.pow((n+1), 3.2), Powder.GEMSTONE)),
            Map.entry("mining_fortune_2", new Perk(50, (var n) -> Math.pow((n+1), 3.2), Powder.GEMSTONE)),
            // t8
            Map.entry("daily_grind", new Perk(100, (var n) -> 200 + ((n-1)*18), Powder.MITHRIL)),
            Map.entry("warm_hearted", new Perk(50, (var n) -> Math.pow((n+1), 3.1), Powder.GLACITE)),
            Map.entry("dust_collector", new Perk(20, (var n) -> Math.pow((n+1), 4), Powder.GLACITE)),
            Map.entry("strong_arm", new Perk(100, (var n) -> Math.pow((n+1), 2.3), Powder.GLACITE)),
            Map.entry("no_stone_unturned", new Perk(50, (var n) -> Math.pow((n+1), 3.05), Powder.GLACITE)),
            // t9
            Map.entry("surveyor", new Perk(20, (var n) -> Math.pow((n+1), 4), Powder.GLACITE)),
            Map.entry("subzero_mining", new Perk(100, (var n) -> Math.pow((n+1), 2.3), Powder.GLACITE)),
            Map.entry("eager_adventurer", new Perk(100, (var n) -> Math.pow((n+1), 2.3), Powder.GLACITE)),
            // t10
            Map.entry("gifts_from_the_departed", new Perk(100, (var n) -> Math.pow((n+1), 2.45), Powder.GLACITE)),
            Map.entry("dead_mans_chest", new Perk(50, (var n) -> Math.pow((n+1), 3.2), Powder.GLACITE)),
            Map.entry("excavator", new Perk(50, (var n) -> Math.pow((n+1), 3), Powder.GLACITE)),
            Map.entry("rags_to_riches", new Perk(50, (var n) -> Math.pow((n+1), 3.05), Powder.GLACITE))
    );


}

