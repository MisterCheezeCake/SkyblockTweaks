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
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import wtf.cheeze.sbt.config.categories.*;
import wtf.cheeze.sbt.config.migration.BarColorTransformation;
import wtf.cheeze.sbt.config.migration.MigrationManager;
import wtf.cheeze.sbt.config.migration.TextColorTransformation;
import wtf.cheeze.sbt.hud.HUD;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.events.EventUtils;
import wtf.cheeze.sbt.utils.enums.Side;

import java.nio.file.Path;


public class  SBTConfig {

    public static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("skyblocktweaks-config.json");

    private static final ConfigClassHandler<ConfigImpl> HANDLER = ConfigClassHandler.createBuilder(ConfigImpl.class)
            .id(Identifier.of("skyblocktweaks", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config).appendGsonBuilder(builder -> builder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY))
                    .setPath(PATH)
                    .build())
            .build();

    public static Event<Runnable> CONFIG_SAVE = EventUtils.getRunnableBackedEvent();
    public static Screen getScreen(Screen parent) {
        var defaults = HANDLER.defaults();
        var config = HANDLER.instance();
        return baseBuilder()
                .category(General.getCategory(defaults, config))
                .category(Chat.getCategory(defaults, config))
                .category(Huds.getCategory(defaults, config))
                .category(Mining.getCategory(defaults, config))
                .build().generateScreen(parent);

    }

    public static void load() {
        //MigrationManager.handleMigrations();
        HANDLER.load();
		boolean ranTransformation =
                MigrationManager.runTransformation(BarColorTransformation.INSTANCE)
                        ||
                MigrationManager.runTransformation(TextColorTransformation.INSTANCE);
        if (ranTransformation) {
            HANDLER.save();
        }
    }

    public static void save() {

        HANDLER.save();
        CONFIG_SAVE.invoker().run();

    }


    public static Screen getGlobalSearchScreen(Screen parent) {
        return baseBuilder().category(GlobalSearchCategory.getCategory(HANDLER.defaults(), HANDLER.instance())).build().generateScreen(parent);
    }
    public static Screen getSpecialGlobalSearchScreen(Screen bigParent) {
        return new GlobalSearchYaclScreen(baseBuilder().category(GlobalSearchCategory.getCategory(HANDLER.defaults(), HANDLER.instance())).build(), () -> getScreen(bigParent));
    }

    public static ConfigImpl get() {
        return HANDLER.instance();
    }
    public static Huds huds() {
        return get().huds;
    }
    public static Mining mining() {
        return get().mining;
    }

    private static YetAnotherConfigLib.Builder baseBuilder() {
        return YetAnotherConfigLib.createBuilder().title(Text.literal("SkyblockTweaks")).save(
                SBTConfig::save
        );
    }

    public static BooleanControllerBuilder generateBooleanController(Option<Boolean> opt) {
        return BooleanControllerBuilder.create(opt).coloured(true);
    }
    public static FloatSliderControllerBuilder generateScaleController(Option<Float> opt) {
        return FloatSliderControllerBuilder.create(opt)
                .range(HUD.MIN_SCALE, HUD.MAX_SCALE)
                .step(HUD.SCALE_STEP);
    }

    public static EnumControllerBuilder<DrawMode> generateDrawModeController(Option<DrawMode> opt) {
        return EnumControllerBuilder.create(opt).enumClass(DrawMode.class);
    }

    public static EnumControllerBuilder<Side> generateSideController(Option<Side> opt) {
        return EnumControllerBuilder.create(opt).enumClass(Side.class);
    }

}
