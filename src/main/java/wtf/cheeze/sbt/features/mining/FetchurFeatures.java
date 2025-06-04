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

import com.google.common.collect.ImmutableMap;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.config.categories.Mining;
import wtf.cheeze.sbt.config.persistent.PersistentData;
import wtf.cheeze.sbt.events.ChatEvents;
import wtf.cheeze.sbt.hud.bases.SingleLineHybridHud;
import wtf.cheeze.sbt.hud.cache.UpdateTiming;
import wtf.cheeze.sbt.hud.components.GapComponent;
import wtf.cheeze.sbt.hud.components.HudComponent;
import wtf.cheeze.sbt.hud.components.ItemStackComponent;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.hud.utils.HudName;
import wtf.cheeze.sbt.utils.CheezePair;
import wtf.cheeze.sbt.utils.DataUtils;
import wtf.cheeze.sbt.utils.enums.Location;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.ItemStackUtils;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;
import wtf.cheeze.sbt.utils.text.MessageManager;
import wtf.cheeze.sbt.utils.text.TextUtils;
import wtf.cheeze.sbt.utils.timing.TimeUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class FetchurFeatures {

    private static final Pattern FETCHUR_PATTERN =  Pattern.compile("\\[NPC\\] Fetchur: (.*)");
    private static final Pattern NEEDS_PATTERN = Pattern.compile("i need .* pls");
    private static final String SUCCESS_MESSAGE = "thanks thats probably what i needed";
    private static final String ALREADY_DONE_MESSAGE = "come back another time, maybe tmrw";

    private static Text waitingToSend;
    private static boolean isWaitingToSend = false;

    public static void registerEvents() {
        ChatEvents.ON_GAME.register(message -> {;
            var str = TextUtils.removeFormatting(message.getString());
            var matcher = FETCHUR_PATTERN.matcher(str);
            if (!matcher.matches()) return;
            var content = matcher.group(1);
            if (content.equals(SUCCESS_MESSAGE) || content.equals(ALREADY_DONE_MESSAGE)) {
                PersistentData.get().currentProfile().lastGaveFetchurItem = System.currentTimeMillis();
                PersistentData.get().requestSave();
            } else if (NEEDS_PATTERN.matcher(content).matches())  {
                if (!SBTConfig.mining().fetchur.chatSolver) return;
                if (isWaitingToSend) {
                    isWaitingToSend = false;
                    MinecraftClient.getInstance().send(() -> {
                        MessageManager.send(waitingToSend);
                        waitingToSend = null;
                    });
                }

            } else {
                if (!SBTConfig.mining().fetchur.chatSolver) return;
                var item = FetchurItem.fromDescription(content);
                // Nothing to worry about if it is null, it's just another of his messages
                if (item != null) {
                    waitingToSend = TextUtils.join(
                            TextUtils.withColor("Fetchur wants ", Colors.LIME),
                            item.display
                    );
                    isWaitingToSend = true;
                }
            }


        });

    }

    public static class FetchurHud extends SingleLineHybridHud {

        public static final FetchurHud INSTANCE = new FetchurHud();



        private FetchurHud() {
            INFO = new HudInformation(
                    () -> SBTConfig.mining().fetchur.x,
                    () -> SBTConfig.mining().fetchur.y,
                    () -> SBTConfig.mining().fetchur.scale,
                    () -> SBTConfig.mining().fetchur.anchor,
                    x -> SBTConfig.mining().fetchur.x = x,
                    y -> SBTConfig.mining().fetchur.y = y,
                    scale -> SBTConfig.mining().fetchur.scale = scale,
                    anchorPoint -> SBTConfig.mining().fetchur.anchor = anchorPoint
            );
            refreshComposition();
            SBTConfig.CONFIG_SAVE.register(this::refreshComposition);

        }

        private void refreshComposition() {
            this.components = getComponents();
        }

        @Override
        public boolean shouldRender(boolean fromHudScreen) {
            if (!super.shouldRender(fromHudScreen)) return false;
            return SkyblockData.inSB && (fromHudScreen ?
                    (SkyblockData.location == Location.DWARVEN_MINES || SBTConfig.mining().fetchur.showHudOutsideMines)
                    : SBTConfig.mining().fetchur.fetchurHud && (SkyblockData.location == Location.DWARVEN_MINES || SBTConfig.mining().fetchur.showHudOutsideMines) && (SBTConfig.mining().fetchur.showHudWhenAlreadyDone || !hasFetchuredToday()));
        }

        private static final int TEXT_DOWNWARD_OFFSET = 4;
        @Override
        public @NotNull HudName getName() {
            return new HudName("Fetchur HUD", "Fetchur", Colors.LIME);
        }

        private CheezePair<HudComponent, Integer>[] getComponents() {
            var list = new ArrayList<CheezePair<HudComponent, Integer>>();
            var offsetToUse = SBTConfig.mining().fetchur.showIcon ? TEXT_DOWNWARD_OFFSET : 0;
            if (SBTConfig.mining().fetchur.showPrefix) {
                list.add(CheezePair.of(new SingleHudLine(UpdateTiming.MEMOIZED, DataUtils.alwaysZero, () -> SBTConfig.mining().fetchur.outlineColor, () -> SBTConfig.mining().fetchur.mode, () -> TextUtils.withColor("Fetchur:" + (SBTConfig.mining().fetchur.showIcon ? "" : " "), SBTConfig.mining().fetchur.color), null, DataUtils.alwaysFalse), offsetToUse));
            }
            if (SBTConfig.mining().fetchur.showIcon) {
                list.add(CheezePair.of(new ItemStackComponent(UpdateTiming.SECOND, () -> FetchurItem.forToday().stack), 0));
                if (SBTConfig.mining().fetchur.showName) {
                    list.add(CheezePair.of(new GapComponent(4), 0));
                }
            }
            if (SBTConfig.mining().fetchur.showName) {
                list.add(CheezePair.of(new SingleHudLine(UpdateTiming.SECOND, DataUtils.alwaysZero, () -> SBTConfig.mining().fetchur.outlineColor, () -> SBTConfig.mining().fetchur.mode, () -> FetchurItem.forToday().display), offsetToUse));
            }

            return list.toArray(CheezePair[]::new);

        }
    }



    private static boolean hasFetchuredToday() {
        return TimeUtils.isInSameDayET(System.currentTimeMillis() - 1, PersistentData.get().currentProfile().lastGaveFetchurItem);
    }

    /**
     * Fetchur's requests are not random, they are based on the day of the month and run in a predictable order, which is reflected in
     * this enum's ordering. The index of the item he wants will correspond to the modulo of the zero-indexed day of the month in the US-East timezone.
     * Credit to SBA for inspiration and information on how this works.
     */
    public enum FetchurItem {
        YELLOW_STAINED_GLASS(Items.YELLOW_STAINED_GLASS, 20, TextUtils.withColor("20 Yellow Stained Glass", Colors.WHITE), "theyre yellow and see-through"),
        COMPASS(Items.COMPASS, TextUtils.withColor("Compass", Colors.WHITE), "its circular and sometimes moves"),
        MITHRIL(ItemStackUtils.getSkyblock("prismarine_crystals", "MITHRIL_ORE", 20), TextUtils.withColor("20 Mithril", Colors.WHITE), "theyre expensive minerals"),
        FIREWORK(Items.FIREWORK_ROCKET, TextUtils.withColor("Firework Rocket", Colors.WHITE), "its useful during celebrations"),
        COFFEE(ItemStackUtils.getHead("CHEAP_COFFEE", "COFFEE"), TextUtils.withColor("Coffee (any type)", Colors.WHITE),"its hot and gives energy"),
        DOOR(Items.OAK_DOOR, TextUtils.withColor("Wooden Door (any type)", Colors.WHITE), "its tall and can be opened"),
        RABBIT_FOOT(Items.RABBIT_FOOT, 3,  TextUtils.withColor("3 Rabbits' Feet", Colors.WHITE), "theyre brown and fluffy"),
        TNT(ItemStackUtils.getSkyblock("tnt", "SUPERBOOM_TNT"), TextUtils.withColor("Superboom TNT", Colors.BLUE), "its explosive, more than usual"),
        PUMPKIN(Items.PUMPKIN, TextUtils.withColor("Pumpkin", Colors.WHITE), "its wearable and grows"),
        FLINT_AND_STEEL(Items.FLINT_AND_STEEL, TextUtils.withColor("Flint and Steel", Colors.WHITE), "its shiny and makes sparks"),
        EMERALD(Items.EMERALD, 50, TextUtils.withColor("50 Emeralds", Colors.WHITE), "theyre green and some dudes trade stuff for it"),
        RDD_WOOL(Items.RED_WOOL, 50, TextUtils.withColor("50 Red Wool", Colors.WHITE), "theyre red and soft"),;
        public final ItemStack stack;
        public final Text display;
        public final String hisDescription;



        FetchurItem(Item item, Text display, String hisDescription) {
           this.stack = new ItemStack(item);
           this.display = display;
            this.hisDescription = hisDescription;
        }
        FetchurItem(Item item, int count, Text display,  String hisDescription) {
            this.stack = new ItemStack(item, count);
            this.display = display;
            this.hisDescription = hisDescription;
        }
        FetchurItem(ItemStack stack, Text display,  String hisDescription) {
            this.stack = stack;
            this.display = display;
            this.hisDescription = hisDescription;
        }

        private static final Map<String, FetchurItem> BY_DESCRIPTION;

        public static FetchurItem fromDescription(String description) {
            return BY_DESCRIPTION.getOrDefault(description, null);
        }

        public static FetchurItem forToday() {
            return values()[(TimeUtils.dayOfMonthET(System.currentTimeMillis()) - 1) % values().length];
        }

        static {
            var map = new HashMap<String, FetchurItem>();
            for (var value : FetchurItem.values()) {
                map.put(value.hisDescription, value);
            }
            BY_DESCRIPTION = ImmutableMap.copyOf(map);
        }

    }

    public static final class Config {
        @SerialEntry
        public boolean chatSolver = true;

        @SerialEntry
        public boolean fetchurHud = false;

        @SerialEntry
        public boolean showHudOutsideMines = false;

        @SerialEntry
        public boolean showHudWhenAlreadyDone = false;

        @SerialEntry
        public boolean showPrefix = true;

        @SerialEntry
        public boolean showIcon = true;

        @SerialEntry
        public boolean showName = false;

        @SerialEntry
        public int color = Colors.LIME;

        @SerialEntry
        public int outlineColor = Colors.BLACK;

        @SerialEntry
        public DrawMode mode = DrawMode.SHADOW;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry // Not handled by YACL gui
        public float x = 0.3f;

        @SerialEntry // Not handled by YACL gui
        public float y = 0.3f;

        @SerialEntry // Not handled by YACL gui
        public AnchorPoint anchor = AnchorPoint.LEFT;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var chatSolver = Option.<Boolean>createBuilder()
                    .name(Mining.key("fetchur.chatSolver"))
                    .description(Mining.keyD("fetchur.chatSolver"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.mining.fetchur.chatSolver,
                            () -> config.mining.fetchur.chatSolver,
                            value -> config.mining.fetchur.chatSolver = value
                    )
                    .build();

            var fetchurHud = Option.<Boolean>createBuilder()
                    .name(Mining.key("fetchur.fetchurHud"))
                    .description(Mining.keyD("fetchur.fetchurHud"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.mining.fetchur.fetchurHud,
                            () -> config.mining.fetchur.fetchurHud,
                            value -> config.mining.fetchur.fetchurHud = value
                    )
                    .build();

            var showHudOutsideMines = Option.<Boolean>createBuilder()
                    .name(Mining.key("fetchur.showHudOutsideMines"))
                    .description(Mining.keyD("fetchur.showHudOutsideMines"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.mining.fetchur.showHudOutsideMines,
                            () -> config.mining.fetchur.showHudOutsideMines,
                            value -> config.mining.fetchur.showHudOutsideMines = value
                    )
                    .build();

            var showHudWhenFetchuredToday = Option.<Boolean>createBuilder()
                    .name(Mining.key("fetchur.showHudWhenAlreadyDone"))
                    .description(Mining.keyD("fetchur.showHudWhenAlreadyDone"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.mining.fetchur.showHudWhenAlreadyDone,
                            () -> config.mining.fetchur.showHudWhenAlreadyDone,
                            value -> config.mining.fetchur.showHudWhenAlreadyDone = value
                    )
                    .build();

            var showPrefix = Option.<Boolean>createBuilder()
                    .name(Mining.key("fetchur.showPrefix"))
                    .description(Mining.keyD("fetchur.showPrefix"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.mining.fetchur.showPrefix,
                            () -> config.mining.fetchur.showPrefix,
                            value -> config.mining.fetchur.showPrefix = value
                    )
                    .build();

            var showIcon = Option.<Boolean>createBuilder()
                    .name(Mining.key("fetchur.showIcon"))
                    .description(Mining.keyD("fetchur.showIcon"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.mining.fetchur.showIcon,
                            () -> config.mining.fetchur.showIcon,
                            value -> config.mining.fetchur.showIcon = value
                    )
                    .build();

            var showName = Option.<Boolean>createBuilder()
                    .name(Mining.key("fetchur.showName"))
                    .description(Mining.keyD("fetchur.showName"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.mining.fetchur.showName,
                            () -> config.mining.fetchur.showName,
                            value -> config.mining.fetchur.showName = value
                    )
                    .build();

            var color = Option.<Color>createBuilder()
                    .name(Mining.key("fetchur.color"))
                    .description(Mining.keyD("fetchur.color"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.mining.fetchur.color),
                            () -> new Color(config.mining.fetchur.color),
                            value -> config.mining.fetchur.color = value.getRGB()
                    )
                    .build();

            var outlineColor = Option.<Color>createBuilder()
                    .name(Mining.key("fetchur.outlineColor"))
                    .description(Mining.keyD("fetchur.outlineColor"))
                    .controller(ColorControllerBuilder::create)
                    .available(SBTConfig.mining().fetchur.mode == DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.mining.fetchur.outlineColor),
                            () -> new Color(config.mining.fetchur.outlineColor),
                            value -> config.mining.fetchur.outlineColor = value.getRGB()
                    )
                    .build();

            var drawMode = Option.<DrawMode>createBuilder()
                    .name(Mining.key("fetchur.mode"))
                    .description(Mining.keyD("fetchur.mode"))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.mining.fetchur.mode,
                            () -> config.mining.fetchur.mode,
                            value -> {
                                config.mining.fetchur.mode = value;
                                outlineColor.setAvailable(value == DrawMode.OUTLINE);
                            }
                    )
                    .build();

            var scale = Option.<Float>createBuilder()
                    .name(Mining.key("fetchur.scale"))
                    .description(Mining.keyD("fetchur.scale"))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.mining.fetchur.scale,
                            () -> config.mining.fetchur.scale,
                            value -> config.mining.fetchur.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(Mining.key("fetchur"))
                    .description(Mining.keyD("fetchur"))
                    .option(chatSolver)
                    .option(fetchurHud)
                    .option(showHudOutsideMines)
                    .option(showHudWhenFetchuredToday)
                    .option(showPrefix)
                    .option(showIcon)
                    .option(showName)
                    .option(drawMode)
                    .option(color)
                    .option(outlineColor)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }

}
