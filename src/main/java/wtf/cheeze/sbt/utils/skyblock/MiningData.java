/*
 * Copyright (C) 2025 MisterCheezeCake
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
package wtf.cheeze.sbt.utils.skyblock;

import net.minecraft.client.Minecraft;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.hud.icon.HudIcon;
import wtf.cheeze.sbt.hud.icon.Icons;
import wtf.cheeze.sbt.mixin.accessors.BossHealthOverlayAccessor;
import wtf.cheeze.sbt.utils.CheezePair;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.text.TextUtils;
import wtf.cheeze.sbt.utils.enums.Location;
import wtf.cheeze.sbt.utils.errors.ErrorHandler;
import wtf.cheeze.sbt.utils.errors.ErrorLevel;
import wtf.cheeze.sbt.utils.tablist.TabListData;
import wtf.cheeze.sbt.utils.tablist.WidgetType;

import java.util.regex.Pattern;

import static wtf.cheeze.sbt.hud.icon.Icons.MINING_ICONS;

public class MiningData {

    public static final Minecraft client = Minecraft.getInstance();

    private static final Pattern ACTIVE_EVENT = Pattern.compile("EVENT (?<name>.+) ACTIVE IN (?<location>.+) for (?<minutes>\\d\\d):(?<seconds>\\d\\d)");
    private static final Pattern PASSIVE_EVENT = Pattern.compile("PASSIVE EVENT (?<name>.+) RUNNING FOR (?<minutes>\\d\\d):(?<seconds>\\d\\d)");
    public final int comNo;
    public final CheezePair<String, Float>[] coms;
    public int mithPowder = 0;
    public int gemPowder = 0;
    public int glacPowder = 0;

    public boolean event = false;
    public String eventName = "";
    public int eventTimeLeft = 0;

    private static long lastDataDumpTime = 0;
    private static final long INTERVAL_BETWEEN_DUMPS_MS = 90000;



    public static int getComMax(String com) {
        // TODO: Switch to repo
        return switch (com) {
            case "Mithril Miner" -> 350;
            case "Lava Springs Mithril", "Royal Mines Mithril", "Cliffside Veins Mithril", "Rampart's Quarry Mithril",
                 "Upper Mines Mithril" -> 250;
            case "Titanium Miner" -> 15;
            case "Lava Springs Titanium", "Royal Mines Titanium", "Cliffside Veins Titanium",
                 "Rampart's Quarry Titanium", "Upper Mines Titanium", "Treasure Hoarder Puncher", "Star Sentry Puncher",
                 "Maniac Slayer" -> 10;
            case "Goblin Slayer" -> SkyblockData.location.equals(Location.CRYSTAL_HOLLOWS) ? 13 : 100;
            case "Glacite Walker Slayer", "Mines Slayer" -> 50;
            case "Goblin Raid Slayer", "Lucky Raffle" -> 20;
            case "Golden Goblin Slayer", "Boss Corleone Slayer", "Mineshaft Explorer", "Scrap Collector", "Goblin Raid",
                 "Raffle", "Jade Crystal Hunter", "Amber Crystal Hunter", "Topaz Crystal Hunter",
                 "Sapphire Crystal Hunter", "Amethyst Crystal Hunter", "First Event" -> 1;
            case "2x Mithril Powder Collector" -> 500;
            case "Hard Stone Miner", "Jade Gemstone Collector", "Amber Gemstone Collector", "Topaz Gemstone Collector",
                 "Sapphire Gemstone Collector", "Amethyst Gemstone Collector", "Ruby Gemstone Collector" -> 1000;
            case "Chest Looter" -> 3;
            case "Corpse Looter" -> 2;
            case "Team Treasurite Member Slayer", "Yog Slayer", "Automaton Slayer" -> 13;
            case "Sludge Slayer" -> 25;
            case "Thyst Slayer" -> 5;
            case "Glacite Collector", "Umber Collector", "Tungsten Collector", "Citrine Gemstone Collector",
                 "Peridot Gemstone Collector", "Onyx Gemstone Collector", "Aquamarine Gemstone Collector" -> 1500;
            default -> -1;
        };
    }

    public static HudIcon getComIcon(String com) {
        return switch (com) {
            case "Mithril Miner", "Lava Springs Mithril", "Royal Mines Mithril", "Cliffside Veins Mithril",
                 "Rampart's Quarry Mithril", "Upper Mines Mithril" -> MINING_ICONS.get("MITHRIL");
            case "2x Mithril Powder Collector" -> MINING_ICONS.get("MITHRIL_POWDER");
            case "Lava Springs Titanium", "Royal Mines Titanium", "Cliffside Veins Titanium",
                 "Rampart's Quarry Titanium", "Upper Mines Titanium", "Titanium Miner" -> MINING_ICONS.get("TITANIUM");
            case "Jade Gemstone Collector" -> MINING_ICONS.get("JADE");
            case "Amber Gemstone Collector" -> MINING_ICONS.get("AMBER");
            case "Topaz Gemstone Collector" -> MINING_ICONS.get("TOPAZ");
            case "Sapphire Gemstone Collector" -> MINING_ICONS.get("SAPPHIRE");
            case "Amethyst Gemstone Collector" -> MINING_ICONS.get("AMETHYST");
            case "Ruby Gemstone Collector" -> MINING_ICONS.get("RUBY");
            case "Jasper Gemstone Collector" -> MINING_ICONS.get("JASPER");
            case "Aquamarine Gemstone Collector" -> MINING_ICONS.get("AQUAMARINE");
            case "Onyx Gemstone Collector" -> MINING_ICONS.get("ONYX");
            case "Peridot Gemstone Collector" -> MINING_ICONS.get("PERIDOT");
            case "Citrine Gemstone Collector" -> MINING_ICONS.get("CITRINE");
            case "Amethyst Crystal Hunter" -> MINING_ICONS.get("AMETHYST_CRYSTAL");
            case "Jade Crystal Hunter" -> MINING_ICONS.get("JADE_CRYSTAL");
            case "Sapphire Crystal Hunter" -> MINING_ICONS.get("SAPPHIRE_CRYSTAL");
            case "Topaz Crystal Hunter" -> MINING_ICONS.get("TOPAZ_CRYSTAL");
            case "Amber Crystal Hunter" -> MINING_ICONS.get("AMBER_CRYSTAL");
            case "Tungsten Collector" -> MINING_ICONS.get("TUNGSTEN");
            case "Umber Collector" -> MINING_ICONS.get("UMBER");
            case "Scrap Collector" -> MINING_ICONS.get("SUSPICIOUS_SCRAP");
            case "Glacite Collector", "Glacite Walker Slayer" -> MINING_ICONS.get("GLACITE");
            case "Mineshaft Explorer" -> Icons.CHEST_MINECART;
            case "Chest Looter" -> Icons.CHEST;
            case "First Event" -> Icons.FIREWORK;
            case "Mines Slayer" -> Icons.IRON_SWORD;
            case "Goblin Slayer", "Goblin Raid Slayer", "Goblin Raid" -> MINING_ICONS.get("GOBLIN");
            case "Team Treasurite Member Slayer" -> MINING_ICONS.get("TEAM_TREASURITE");
            case "Boss Corleone Slayer" -> MINING_ICONS.get("CORLEONE");
            case "Treasure Hoarder Puncher" -> MINING_ICONS.get("TREASURE_HOARDER");
            case "Raffle", "Lucky Raffle" -> MINING_ICONS.get("RAFFLE");
            case "Star Sentry Puncher" -> Icons.NETHER_STAR;
            case "Corpse Looter" -> MINING_ICONS.get("LOOTER");
            case "Thyst Slayer" -> MINING_ICONS.get("THYST");
            case "Automaton Slayer" -> MINING_ICONS.get("AUTOMATON");
            case "Yog Slayer" -> MINING_ICONS.get("YOG");
            case "Sludge Slayer" -> MINING_ICONS.get("SLUDGE");
            case "Maniac Slayer" -> MINING_ICONS.get("MUTT");
            case "Golden Goblin Slayer" -> Icons.GOLDEN_HELMET;
            default -> null;
        };
    }

    public static HudIcon getEventIcon(String event) {
        return switch (event) {
            case "Goblin Raid" -> MINING_ICONS.get("GOBLIN");
            case "Raffle" -> MINING_ICONS.get("RAFFLE");
            case "Mithril Gourmand" -> MINING_ICONS.get("GOURMAND");
            case "Better Together" -> Icons.DEFAULT_HEAD;
            case "Gone With The Wind" -> Icons.COMPASS;
            case "2x Powder" -> MINING_ICONS.get("MITHRIL_POWDER");
            case "Fortunate Freezing" -> MINING_ICONS.get("GLACITE");
            default -> null;
        };

}


    @SuppressWarnings("unchecked")
    public MiningData(TabListData data) {

        if (data.widgetLines.get(WidgetType.COMMISSIONS) != null) {
            coms = data.widgetLines.get(WidgetType.COMMISSIONS).stream().filter(it -> !it.equals("Commissions:"))
                    .map(it -> {
                        //TODO: Could a regex work here
                        String[] split = it.trim().split(": ");
                        float com = NumberUtils.parsePercentage(split[1]);
                        return new CheezePair<>(split[0], com);
                    }).toArray(CheezePair[]::new);
            comNo = coms.length;
        } else {
            comNo = 0;
            coms = new CheezePair[0];
        }

        if (data.widgetLines.get(WidgetType.POWDER) != null) {
            for (var line : data.widgetLines.get(WidgetType.POWDER)) {
                if (line.startsWith(" Mithril")) {
                    mithPowder = parsePowder(line);
                } else if (line.startsWith(" Gemstone")) {
                    gemPowder = parsePowder(line);
                } else if (line.startsWith(" Glacite")) {
                    glacPowder = parsePowder(line);
                }

            }
        }

        var bossBars = ((BossHealthOverlayAccessor) client.gui.getBossOverlay()).getEvents();
        for (var bar: bossBars.values()) {
            var name = TextUtils.removeFormatting(bar.getName().getString());
            var passive = PASSIVE_EVENT.matcher(name);
            if (passive.matches()) {
                event = true;
                eventName = TextUtils.pascalCase(passive.group("name"));
                eventTimeLeft = Integer.parseInt(passive.group("minutes")) * 60 + Integer.parseInt(passive.group("seconds"));
                //ticksSinceEventNotFound = 0;
                break;
            }
            var active = ACTIVE_EVENT.matcher(name);
            if (active.matches()) {
                event = true;
                eventName = TextUtils.pascalCase(active.group("name"));
                eventTimeLeft = Integer.parseInt(active.group("minutes")) * 60 + Integer.parseInt(active.group("seconds"));
                //ticksSinceEventNotFound = 0;
                break;
            }
           //if (ticksSinceEventNotFound <= 30) ticksSinceEventNotFound++;
        }
    }

    public static MiningData of(TabListData data) {
        try {
            return new MiningData(data);
        } catch (Exception e) {
            ErrorHandler.handle(e, "Failed to parse mining data", ErrorLevel.WARNING);
            if (System.currentTimeMillis() - lastDataDumpTime > INTERVAL_BETWEEN_DUMPS_MS) {
                lastDataDumpTime = System.currentTimeMillis();
                SkyblockTweaks.LOGGER.info("Dumping tab list data: {}", data.serialize());
            }
            return EMPTY;
        }
    }

    private static int parsePowder(String line) {
        return Integer.parseInt(line.split(": ")[1].replaceAll(",", ""));
    }
    private MiningData() {
        comNo = 0;
        coms = new CheezePair[0];
    }

    public static final MiningData EMPTY = new MiningData();
}
