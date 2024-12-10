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
package wtf.cheeze.sbt.config;


import com.google.gson.FieldNamingPolicy;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import wtf.cheeze.sbt.config.categories.*;
import wtf.cheeze.sbt.hud.utils.DrawMode;

import java.nio.file.Path;


public class  SBTConfig {

    public static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("skyblocktweaks-config.json");

    public static final ConfigClassHandler<ConfigImpl> HANDLER = ConfigClassHandler.createBuilder(ConfigImpl.class)
            .id(Identifier.of("skyblocktweaks", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config).appendGsonBuilder(builder -> builder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY))
                    .setPath(PATH)
                    .build())
            .build();
    public static Screen getScreen(Screen parent) {
        return YetAnotherConfigLib.create(HANDLER,
                (defaults, configThing, builder) -> {
                    return builder
                            .title(Text.literal("SkyblockTweaks"))
                            .category(General.getCategory(defaults, configThing))
                            .category(Chat.getCategory(defaults, configThing))
                            .category(Huds.getCategory(defaults, configThing));

                }).generateScreen(parent);
    }
    public static Screen getGlobalSearchScreen(Screen parent) {
        return YetAnotherConfigLib.create(HANDLER,
                (defaults, configThing, builder) -> {
                    return builder
                            .title(Text.literal("SkyblockTweaks"))
                            .category(GlobalSearchCategory.getCategory(defaults, configThing));

                }).generateScreen(parent);
    }
    public static Screen getSpecialGlobalSearchScreen(Screen bigParent) {
        var yacl = YetAnotherConfigLib.create(HANDLER,
                (defaults, configThing, builder) -> {
                    return builder
                            .title(Text.literal("SkyblockTweaks"))
                            .category(GlobalSearchCategory.getCategory(defaults, configThing));

                });
        return new GlobalSearchYaclScreen(yacl, () -> getScreen(bigParent));
    }

    public static ConfigImpl get() {
        return HANDLER.instance();
    }
    public static Huds huds() {
        return get().huds;
    }

    public static void override(ConfigImpl config) {

    }



    public static ControllerBuilder generateBooleanController(Option<Boolean> opt) {
        return BooleanControllerBuilder.create(opt).coloured(true);
    }
    public static FloatSliderControllerBuilder generateScaleController(Option<Float> opt) {
        return FloatSliderControllerBuilder.create(opt)
                .range(0.1f, 3.0f)
                .step(0.1f);
    }

    public static EnumControllerBuilder<DrawMode> generateDrawModeController(Option<DrawMode> opt) {
        return EnumControllerBuilder.create(opt).enumClass(DrawMode.class);
    }

}
