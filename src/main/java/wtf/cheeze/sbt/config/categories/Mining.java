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
package wtf.cheeze.sbt.config.categories;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.features.mining.EventTimerHud;
import wtf.cheeze.sbt.features.mining.MiningHud;

public class Mining {

    @SerialEntry
    public MiningHud.Config hud = new MiningHud.Config();

    @SerialEntry
    public EventTimerHud.Config eventTimer = new EventTimerHud.Config();


    public static ConfigCategory getCategory(ConfigImpl defaults, ConfigImpl configThing) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("sbt.config.mining"))
                .tooltip(Text.translatable("sbt.config.mining.desc"))
                .group(MiningHud.Config.getGroup(defaults, configThing))
                .group(EventTimerHud.Config.getGroup(defaults, configThing))
                .build();
    }


    private static final String BASE_KEY = "sbt.config.mining.";
    public static Text key(String key) {
        return Text.translatable(BASE_KEY + key);
    }
    public static OptionDescription keyD(String key) {
        return OptionDescription.of(Text.translatable(BASE_KEY + key + ".desc"));
    }
}
