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
package wtf.cheeze.sbt.compat.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZonesProvider;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import wtf.cheeze.sbt.hud.bounds.Bounds;
import wtf.cheeze.sbt.utils.injected.SBTHandledScreen;
import wtf.cheeze.sbt.utils.render.Popup;

import java.util.Collection;
import java.util.List;

public class ExclusionZoneProvider implements ExclusionZonesProvider<ContainerScreen> {
    @Override
    public Collection<Rectangle> provide(ContainerScreen screen) {
        Popup popup = ((SBTHandledScreen) screen).sbt$getPopup();
        if (popup != null) {
            Bounds bounds = popup.getBounds();
            return List.of(new Rectangle(bounds.x, bounds.y , bounds.width, bounds.height));
        }
        return List.of();
    }
}
