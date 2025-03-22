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
package wtf.cheeze.sbt.features.huds;

import net.minecraft.text.Text;
import wtf.cheeze.sbt.hud.bases.MultilineTextHud;
import wtf.cheeze.sbt.hud.icon.HudIcon;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.hud.utils.HudName;
import wtf.cheeze.sbt.utils.DataUtils;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.hud.icon.Icons;

import java.util.ArrayList;
import java.util.List;

/**
 * A HUD for testing icons.
 * Not meant to be used in production.
 */
public class IconTestHud extends MultilineTextHud {
    @Override
    public HudName getName() {
        return new HudName("Icon Test HUD", "Icon HUD", Colors.WHITE);
    }

    private float x = 0.5f;
    private float y = 0.5f;
    private float scale = 1.0f;

    public IconTestHud() {
        super();
        lines = getLines().toArray(new SingleHudLine[0]);
        INFO = new HudInformation(
                () -> x,
                () -> y,
                () -> scale,
                x -> this.x = x,
                y -> this.y = y,
                scale -> this.scale = scale
        );


    }

    private List<SingleHudLine> getLines() {
        var list = new ArrayList<SingleHudLine>();
        list.add(genLine(Icons.ARROW));
        list.add(genLine(Icons.CHEST_MINECART));
        list.add(genLine(Icons.CHEST));
        list.add(genLine(Icons.FIREWORK));
        list.add(genLine(Icons.IRON_SWORD));
        list.add(genLine(Icons.DIAMOND_PICKAXE));
        list.add(genLine(Icons.NETHER_STAR));
        for (var entry : Icons.SKILL_ICONS.entrySet()) {
            list.add(genLine(entry.getValue()));
        }
        for (var entry : Icons.MINING_ICONS.entrySet()) {
            list.add(genLine(entry.getValue()));
        }
        return list;
    }

    private SingleHudLine genLine(HudIcon icon) {
        return new SingleHudLine(
                () -> Colors.WHITE,
                DataUtils.alwaysZero,
                () -> DrawMode.PURE,
                () -> Text.literal("Test"),
                () -> icon,
                DataUtils.alwaysTrue
        );
    }
}
