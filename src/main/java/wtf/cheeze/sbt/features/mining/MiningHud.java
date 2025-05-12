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
package wtf.cheeze.sbt.features.mining;

import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.config.categories.Mining;
import wtf.cheeze.sbt.hud.bases.MultilineTextHud;
import wtf.cheeze.sbt.hud.icon.HudIcon;
import wtf.cheeze.sbt.hud.cache.Cache;
import wtf.cheeze.sbt.hud.cache.UpdateTiming;
import wtf.cheeze.sbt.hud.components.FlexibleHudLine;
import wtf.cheeze.sbt.hud.components.HudComponent;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.hud.utils.HudName;
import wtf.cheeze.sbt.utils.CheezePair;
import wtf.cheeze.sbt.utils.DataUtils;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.text.TextUtils;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.hud.icon.Icons;
import wtf.cheeze.sbt.utils.skyblock.MiningData;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;
import wtf.cheeze.sbt.utils.skyblock.SkyblockUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;


public class MiningHud extends MultilineTextHud {

    public static final MiningHud INSTANCE = new MiningHud();

    private MiningHud() {
        refreshComposition();
        INFO = new HudInformation(
                () -> SBTConfig.mining().hud.x,
                () -> SBTConfig.mining().hud.y,
                () -> SBTConfig.mining().hud.scale,
                x -> SBTConfig.mining().hud.x = x,
                y -> SBTConfig.mining().hud.y = y,
                scale -> SBTConfig.mining().hud.scale = scale
        );
        SBTConfig.CONFIG_SAVE.register(this::refreshComposition);
    }

    @Override
    public @NotNull HudName getName() {

        return new HudName("Mining HUD", Colors.CYAN);
    }

    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        return (SkyblockUtils.inMiningIsland() && this.lines.length > 0) && (SBTConfig.mining().hud.enabled || fromHudScreen);
    }

    private final Supplier<Boolean> useIconSupplier = () -> SBTConfig.mining().hud.icons;

    public void refreshComposition() {
        this.lines = genLines();
    }


    private HudComponent[] genLines() {
        List<HudComponent> lines = new ArrayList<>();
        for (Entry entry : SBTConfig.mining().hud.composition) {
            switch (entry) {
                // For the commissions widget, we cache the parts for a second, since there's no need to update it more frequently
                case COMMISSIONS -> lines.add(new FlexibleHudLine(getComParts(), UpdateTiming.SECOND));
                case MITHRIL_POWDER -> lines.add(new SingleHudLine(
                        DataUtils.alwaysZero,
                        () -> SBTConfig.mining().hud.outlineColor,
                        () -> SBTConfig.mining().hud.mode,
                        () -> TextUtils.join(
                                TextUtils.withColor("Mithril Powder:", SBTConfig.mining().hud.color),
                                TextUtils.SPACE,
                                TextUtils.withColor(SBTConfig.mining().hud.abbreviatePowder ? NumberUtils.addKOrM(SkyblockData.miningData.mithPowder, ",") : NumberUtils.formatNumber(SkyblockData.miningData.mithPowder, ","), Colors.GREEN)
                        ),
                        () -> Icons.MINING_ICONS.get("MITHRIL_POWDER"),
                        useIconSupplier
                ));
                case GEMSTONE_POWDER -> lines.add(new SingleHudLine(
                        DataUtils.alwaysZero,
                        () -> SBTConfig.mining().hud.outlineColor,
                        () -> SBTConfig.mining().hud.mode,
                        () -> TextUtils.join(
                                TextUtils.withColor("Gemstone Powder:", SBTConfig.mining().hud.color),
                                TextUtils.SPACE,
                                TextUtils.withColor(SBTConfig.mining().hud.abbreviatePowder ? NumberUtils.addKOrM(SkyblockData.miningData.gemPowder, ",") : NumberUtils.formatNumber(SkyblockData.miningData.gemPowder, ","), Colors.PINK)
                        ),
                        () -> Icons.MINING_ICONS.get("GEMSTONE_POWDER"),
                        useIconSupplier
                ));
                case GLACITE_POWER -> lines.add(new SingleHudLine(
                        DataUtils.alwaysZero,
                        () -> SBTConfig.mining().hud.outlineColor,
                        () -> SBTConfig.mining().hud.mode,
                        () -> TextUtils.join(
                                TextUtils.withColor("Glacite Power:", SBTConfig.mining().hud.color),
                                TextUtils.SPACE,
                                TextUtils.withColor(SBTConfig.mining().hud.abbreviatePowder ? NumberUtils.addKOrM(SkyblockData.miningData.glacPowder, ",") : NumberUtils.formatNumber(SkyblockData.miningData.glacPowder, ","), Colors.LIGHT_BLUE)
                        ),
                        () -> Icons.MINING_ICONS.get("GLACITE_POWDER"),
                        useIconSupplier

                ));

            }
        }

        return lines.toArray(new HudComponent[0]);
    }


    private final ArrayList<Cache<Text>> comCache = new ArrayList<>();

    private Supplier<FlexibleHudLine.Part[]> getComParts() {
        return () -> {
            var arr = new FlexibleHudLine.Part[SkyblockData.miningData.comNo];
            for (int i = 0; i < SkyblockData.miningData.comNo; i++) {

                var suppliers = genComSuppliers(i);
                if (comCache.toArray().length <= i || comCache.get(i) == null) {
                    comCache.add(new Cache<>(UpdateTiming.TICK, suppliers.key(), HudComponent.ERROR));
                }
                arr[i] = new FlexibleHudLine.Part(
                        suppliers.key(),
                        () -> SBTConfig.mining().hud.mode,
                        DataUtils.alwaysZero,
                        () -> SBTConfig.mining().hud.outlineColor,
                        suppliers.val(),
                        () -> suppliers.val().get() != null && useIconSupplier.get(),
                        comCache.get(i)
                );
            }
            return arr;
        };
    }

    private CheezePair<Supplier<Text>, Supplier<HudIcon>> genComSuppliers(int i) {

        return new CheezePair<>(
                () -> {
                    var com = SkyblockData.miningData.coms[i];
                    var max = MiningData.getComMax(com.key());
                    var num = com.val();
                    return TextUtils.join(
                            TextUtils.withColor(com.key() + ":", SBTConfig.mining().hud.color),
                            TextUtils.SPACE,
                            num == 1 ? TextUtils.withColor("DONE", Colors.LIME) : TextUtils.withColor(((max == -1 || !SBTConfig.mining().hud.useNumbers) ? NumberUtils.formatPercent(num) : Math.round(num * max) + "/" + max), Colors.fromFloatValue(num))
                    );
                },
                () -> MiningData.getComIcon(SkyblockData.miningData.coms[i].key()));

    }


    public enum Entry implements NameableEnum {
        COMMISSIONS(TextUtils.withColor("Commissions", Colors.CYAN)),
        MITHRIL_POWDER(TextUtils.withColor("Mithril Powder", Colors.GREEN)),
        GEMSTONE_POWDER(TextUtils.withColor("Gemstone Powder", Colors.PINK)),
        GLACITE_POWER(TextUtils.withColor("Glacite Power", Colors.LIGHT_BLUE)),
        COOLDOWN(TextUtils.withColor("Pickaxe Cooldown", Colors.LIME)),
        // FORGES(TextUtils.withColor("Forges", Colors.WHITE)),

        ;

        private final Text name;

        Entry(Text name) {
            this.name = name;
        }

        @Override
        public Text getDisplayName() {
            return name;
        }
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public boolean useNumbers = true;

        @SerialEntry
        public boolean abbreviatePowder = false;

        @SerialEntry
        public boolean icons = true;

        @SerialEntry
        public List<Entry> composition = List.of(Entry.COMMISSIONS, Entry.MITHRIL_POWDER, Entry.GEMSTONE_POWDER, Entry.GLACITE_POWER);

        @SerialEntry
        public float x = 0.25f;

        @SerialEntry
        public float y = 0f;

        @SerialEntry
        public DrawMode mode = DrawMode.SHADOW;

        @SerialEntry
        public int color = Colors.CYAN;

        @SerialEntry
        public int outlineColor = Colors.BLACK;

        @SerialEntry
        public float scale = 1.0f;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Mining.key("hud.enabled"))
                    .description(Mining.keyD("hud.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.mining.hud.enabled,
                            () -> config.mining.hud.enabled,
                            value -> config.mining.hud.enabled = value
                    )
                    .build();

            var useNumbers = Option.<Boolean>createBuilder()
                    .name(Mining.key("hud.useNumbers"))
                    .description(Mining.keyD("hud.useNumbers"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.mining.hud.useNumbers,
                            () -> config.mining.hud.useNumbers,
                            value -> config.mining.hud.useNumbers = value
                    )
                    .build();


            var abbreviatePowder = Option.<Boolean>createBuilder()
                    .name(Mining.key("hud.abbreviatePowder"))
                    .description(Mining.keyD("hud.abbreviatePowder"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.mining.hud.abbreviatePowder,
                            () -> config.mining.hud.abbreviatePowder,
                            value -> config.mining.hud.abbreviatePowder = value
                    )
                    .build();

            var icons = Option.<Boolean>createBuilder()
                    .name(Mining.key("hud.icons"))
                    .description(Mining.keyD("hud.icons"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.mining.hud.icons,
                            () -> config.mining.hud.icons,
                            value -> config.mining.hud.icons = value
                    )
                    .build();
            var color = Option.<Color>createBuilder()
                    .name(Mining.key("hud.color"))
                    .description(Mining.keyD("hud.color"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.mining.hud.color),
                            () -> new Color(config.mining.hud.color),
                            value -> config.mining.hud.color = value.getRGB()
                    )
                    .build();
            var outlineColor = Option.<Color>createBuilder()
                    .name(Mining.key("hud.outlineColor"))
                    .description(Mining.keyD("hud.outlineColor"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.mining.hud.outlineColor),
                            () -> new Color(config.mining.hud.outlineColor),
                            value -> config.mining.hud.outlineColor = value.getRGB()
                    )
                    .build();


            var drawMode = Option.<DrawMode>createBuilder()
                    .name(Mining.key("hud.mode"))
                    .description(Mining.keyD("hud.mode"))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.mining.hud.mode,
                            () -> config.mining.hud.mode,
                            value -> {
                                config.mining.hud.mode = value;
                                outlineColor.setAvailable(value == DrawMode.OUTLINE);
                            }
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Mining.key("hud.scale"))
                    .description(Mining.keyD("hud.scale"))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.mining.hud.scale,
                            () -> config.mining.hud.scale,
                            value -> config.mining.hud.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(Mining.key("hud"))
                    .description(Mining.keyD("hud"))
                    .option(enabled)
                    .option(useNumbers)
                    .option(abbreviatePowder)
                    .option(icons)
                    .option(drawMode)
                    .option(color)
                    .option(outlineColor)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }

        public static ListOption<Entry> getCompositionOption(ConfigImpl defaults, ConfigImpl config) {
            return ListOption.<Entry>createBuilder()
                    .name(Mining.key("hud.composition"))
                    .description(Mining.keyD("hud.composition"))
                    .insertEntriesAtEnd(true)
                    .controller(entryOption -> EnumControllerBuilder.create(entryOption).enumClass(Entry.class))
                    .initial(Arrays.stream(Entry.values()).filter(entry -> !config.mining.hud.composition.contains(entry)).findFirst().orElseGet(() -> Entry.MITHRIL_POWDER))
                    .binding(
                            defaults.mining.hud.composition,
                            () -> config.mining.hud.composition,
                            value -> config.mining.hud.composition = value
                    )
                    .build();
        }
    }
}



