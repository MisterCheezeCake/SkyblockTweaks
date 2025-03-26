package wtf.cheeze.sbt.features.huds;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.events.DrawSlotEvents;
import wtf.cheeze.sbt.hud.bases.TextHud;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.hud.icon.Icons;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.hud.utils.HudName;
import wtf.cheeze.sbt.utils.TimeUtils;
import wtf.cheeze.sbt.utils.enums.Location;
import wtf.cheeze.sbt.utils.enums.Skill;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RainmakerHud extends TextHud {

    public static final RainmakerHud INSTANCE = new RainmakerHud();
    private static final Pattern RAINMAKER_PATTERN = Pattern.compile("Remaining rain: (\\d\\d):(\\d\\d)");

    private RainmakerHud() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().rainmaker.x,
                () -> SBTConfig.huds().rainmaker.y,
                () -> SBTConfig.huds().rainmaker.scale,
                () -> SBTConfig.huds().rainmaker.anchor,
                x -> SBTConfig.huds().rainmaker.x = x,
                y -> SBTConfig.huds().rainmaker.y = y,
                scale -> SBTConfig.huds().rainmaker.scale = scale,
                anchor -> SBTConfig.huds().rainmaker.anchor = anchor
        );
        line = new SingleHudLine(
                () -> SBTConfig.huds().rainmaker.color,
                () -> SBTConfig.huds().rainmaker.outlineColor,
                () -> SBTConfig.huds().rainmaker.mode,
                () -> {
                    return Text.literal(TimeUtils.formatTime(calcTime(), false));
                },
                () -> Icons.WATER_BUCKET,
                () -> SBTConfig.huds().rainmaker.icon
        );
    }


    public void registerEvents() {
        DrawSlotEvents.BEFORE_ITEM.register((screenTitle, context, slot) -> {
            if (SkyblockData.location != Location.PARK) return;
            if (!screenTitle.getString().equals("Vanessa")) {
                return;
            }
            if (slot.id != 13) {
                return;
            }
         //   SkyblockTweaks.LOGGER.info("Rainmaker HUD: Slot ID: " + slot.id);
            if (slot.getStack() != null && !slot.getStack().getName().getString().equals("The Rainmaker")) return;
            var lines = slot.getStack().getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).lines();
            for (var line: lines) {
               // SkyblockTweaks.LOGGER.info("Rainmaker HUD: " + line.getString());
                var matcher = RAINMAKER_PATTERN.matcher(line.getString());
                if (matcher.find()) {
                    var minutes = Integer.parseInt(matcher.group(1));
                    var seconds = Integer.parseInt(matcher.group(2));
                    var entry = new TimeEntry((minutes * 60L) + seconds, System.currentTimeMillis() / 1000);
                    timeEntries.put(SkyblockData.currentServer, entry);
                    break;
                }
            }
        });

    }

    private final Map<String, TimeEntry> timeEntries = new HashMap<>();



    private int calcTime() {
        var entry = timeEntries.get(SkyblockData.currentServer);
        if (entry == null) {
            return 0;
        }
        return (int) (entry.timeAtLastUpdate - (System.currentTimeMillis() / 1000 - entry.lastUpdateS));
    }

    @Override
    public @NotNull HudName getName() {
        return new HudName("Rainmaker Timer HUD", "Rainmaker HUD", Colors.LIGHT_BLUE);
    }

    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        return SkyblockData.location == Location.PARK && ((SBTConfig.huds().rainmaker.enabled && calcTime() > 0) || fromHudScreen);
    }

    private record TimeEntry(long lastUpdateS, long timeAtLastUpdate) {}

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public DrawMode mode = DrawMode.SHADOW;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0.15f;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.25f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public int color = Colors.LIGHT_BLUE;

        @SerialEntry
        public int outlineColor = Colors.BLACK;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        @SerialEntry
        public boolean icon = true;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(key("rainmaker.enabled"))
                    .description(keyD("rainmaker.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.rainmaker.enabled,
                            () -> config.huds.rainmaker.enabled,
                            value -> config.huds.rainmaker.enabled = (boolean) value
                    )
                    .build();
            var icon = Option.<Boolean>createBuilder()
                    .name(key("rainmaker.icon"))
                    .description(keyD("rainmaker.icon"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.rainmaker.icon,
                            () -> config.huds.rainmaker.icon,
                            value -> config.huds.rainmaker.icon = (boolean) value
                    )
                    .build();


            var color = Option.<Color>createBuilder()
                    .name(key("rainmaker.color"))
                    .description(keyD("rainmaker.color"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.rainmaker.color),
                            () -> new Color(config.huds.rainmaker.color),
                            value -> config.huds.rainmaker.color = value.getRGB()
                    )
                    .build();

            var outline = Option.<Color>createBuilder()
                    .name(key("rainmaker.outlineColor"))
                    .description(keyD("rainmaker.outlineColor"))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.rainmaker.mode == DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.rainmaker.outlineColor),
                            () ->  new Color(config.huds.rainmaker.outlineColor),
                            value -> config.huds.rainmaker.outlineColor = value.getRGB()

                    )
                    .build();

            var mode = Option.<DrawMode>createBuilder()
                    .name(key("rainmaker.mode"))
                    .description(keyD("rainmaker.mode"))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.rainmaker.mode,
                            () -> config.huds.rainmaker.mode,
                            value -> {
                                config.huds.rainmaker.mode = value;
                                outline.setAvailable(value == DrawMode.OUTLINE);
                            }
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(key("rainmaker"))
                    .description(keyD("rainmaker"))
                    .option(enabled)
                    .option(color)
                    .option(outline)
                    .option(icon)
                    .option(mode)
                    .collapsed(true)
                    .build();
        }
    }
}
