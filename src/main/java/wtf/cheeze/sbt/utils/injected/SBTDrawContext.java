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
package wtf.cheeze.sbt.utils.injected;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Text;

public interface SBTDrawContext {
    //? if <=1.21.5 {
    int
     //?} else {
    /*void
    *///?}
    sbt$drawTextWithBackgroundNoShadow(TextRenderer textRenderer, Text text, int x, int y, int width, int color);

    /**
     * You may ask yourself, "Why is this not an accessor?" The answer is that I really did not want to deal with mixins only existing on one version, and this works fine
     */
    //? if <=1.21.5
    VertexConsumerProvider.Immediate sbt$getVertexConsumers();
}
