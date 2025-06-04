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
package wtf.cheeze.sbt.features.overlay;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.intellij.lang.annotations.Language;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.config.categories.General;
import wtf.cheeze.sbt.config.persistent.PersistentData;
import wtf.cheeze.sbt.utils.KillSwitch;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.text.Predicates;
import wtf.cheeze.sbt.utils.text.TextUtils;
import wtf.cheeze.sbt.utils.constants.loader.Constants;
import wtf.cheeze.sbt.utils.enums.Skill;
import wtf.cheeze.sbt.utils.errors.ErrorHandler;
import wtf.cheeze.sbt.utils.errors.ErrorLevel;
import wtf.cheeze.sbt.utils.injected.SBTHandledScreen;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.render.Popup;
import wtf.cheeze.sbt.utils.render.RenderUtils;
import wtf.cheeze.sbt.utils.skyblock.ItemStackUtils;

import java.util.*;

public class MinionExp {


    @Language("RegExp")
    public static final String MINION_EXP_SCREEN_REGEX = "Minion Chest|.+ Minion [IVX]+";
    private static final int[] MINION_SLOTS = {21, 22, 23, 24, 25, 30, 31, 32, 33, 34, 39, 40, 41, 42, 43};

    private static String contents = "____";
    private static final String FEATURE_ID = "minion_exp_overlay";


    public static void registerEvents() {
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof HandledScreen<?> handledScreen && handledScreen.getTitle().getString().matches(MINION_EXP_SCREEN_REGEX) && SBTConfig.get().minionExp.enabled && !KillSwitch.shouldKill(FEATURE_ID)) {
                ((SBTHandledScreen)handledScreen).sbt$setPopup(new MinionExpPopup(handledScreen));
            }
        });
    }

    public static class MinionExpPopup implements Popup {

        private static final int CHEST_LAST_SLOT = 27;
        private final int x;
        private final int y;
        private final HandledScreen<?> screen;
        private final boolean isChest;
        private final List<TextFieldWidget> children;



        public MinionExpPopup(HandledScreen<?> screen) {
            this.x = screen.x + 256 - 64;
            this.y = screen.y;
            this.screen = screen;
            this.isChest = screen.getTitle().getString().equals("Minion Chest");
            var textWidget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, x + 10, y + 95, 60, 15, Text.empty());
            textWidget.setTextPredicate(Predicates.INT);
            textWidget.setMaxLength(4);
            textWidget.setText(contents);
            this.children = List.of(textWidget);
            screen.drawables.add(this);
            screen.addDrawableChild(textWidget);
        }



        @Override
        public int x() {
            return x;
        }

        @Override
        public int y() {
            return y;
        }

        @Override
        public List<? extends ClickableWidget> childrenList() {
            return children;
        }

        @Override
        public Screen screen() {
            return screen;
        }


        //TODO: Switch to text widgets
        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            boolean shadow = SBTConfig.get().minionExp.shadowText;
            try {
                Popup.super.renderBackground(context);
                Popup.super.drawSBTFooter(context, shadow);
                RenderUtils.drawCenteredText(context, TextUtils.withBold("Skill EXP"), x + WIDTH / 2, y + 5, Colors.WHITE, shadow);
                RenderUtils.drawCenteredText(context, Text.literal("☯ Wisdom"), x + WIDTH / 2, y + 85, Colors.CYAN, shadow);

                var renderY = y + 20;
                var minionExp = getMinionExp();
                if (this.children.getFirst().getText().equals("____")) {
                    var primarySkill = getPrimarySkill(minionExp.keySet());
                    this.children.getFirst().setText(PersistentData.get().currentProfile().skillWisdom.getOrDefault(primarySkill, 0) + "");
                }
                for (var entry : minionExp.entrySet()) {
                    RenderUtils.drawCenteredText(context, Text.literal(entry.getKey().getName() + ":"), x + WIDTH / 2, renderY, Colors.CYAN, shadow);
                    var exp = entry.getValue();
                    RenderUtils.drawCenteredText(context, Text.literal(NumberUtils.addKOrM((int) (exp * getMult()), ",") + " XP"), x + WIDTH / 2, renderY + 10, Colors.WHITE, shadow);
                    renderY += 30;
                }
            } catch (Exception e) {
                ErrorHandler.handleError(e, "Error rendering Minion EXP popup", ErrorLevel.WARNING);
            }
        }

        private List<Slot> getRelevantSlots() {
            return isChest ? screen.getScreenHandler().slots.stream().filter(slot -> slot.id < CHEST_LAST_SLOT).toList() : Arrays.stream(MINION_SLOTS).mapToObj(slotId -> screen.getScreenHandler().slots.get(slotId)).toList();
        }

        private Map<Skill, Double> getMinionExp() {
            var exp = Constants.minions().minionExp();
            var map = new EnumMap<Skill, Double>(Skill.class);
            for (var slot: getRelevantSlots()) {
                var id = ItemStackUtils.getSkyblockId(slot.getStack());
                if (id.isEmpty()) continue;
                var entry = exp.get(id);
                if (entry == null) continue;
                map.putIfAbsent(entry.skill(), 0d);
                var xpFromThisStack = entry.exp() * slot.getStack().getCount() * getMult();
                map.put(entry.skill(), map.get(entry.skill()) + xpFromThisStack);
                SkyblockTweaks.LOGGER.debug("Adding {} XP to {} from {}", xpFromThisStack, entry.skill(), id);
            }
            return map;
        }

        private float getMult() {
            var text = this.children.getFirst().getText();
            contents = text;
            return  text.isEmpty() || text.equals("____") ? 1f : 1f + Float.parseFloat(text) / 100f;
        }


    }


    private static Skill getPrimarySkill(Collection<Skill> skills) {
        if (skills.contains(Skill.MINING)){
            if (skills.size() > 1) return skills.stream().filter(s -> s != Skill.MINING).findFirst().orElse(Skill.MINING);
            return Skill.MINING;
        }
        if (skills.contains(Skill.FARMING)) {
            if (skills.size() > 1) return skills.stream().filter(s -> s != Skill.FARMING).findFirst().orElse(Skill.FARMING);
            return Skill.FARMING;
        }
        return skills.stream().findFirst().orElse(Skill.UNKNOWN);
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = true;

        @SerialEntry
        public boolean shadowText = false;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(General.key("minionExp.enabled"))
                    .description(General.keyD("minionExp.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.minionExp.enabled,
                            () -> config.minionExp.enabled,
                            value -> config.minionExp.enabled = value
                    )
                    .build();

            var shadowText = Option.<Boolean>createBuilder()
                    .name(General.key("minionExp.shadowText"))
                    .description(General.keyD("minionExp.shadowText"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.minionExp.shadowText,
                            () -> config.minionExp.shadowText,
                            value -> config.minionExp.shadowText = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(General.key("minionExp"))
                    .description(General.keyD("minionExp"))
                    .option(enabled)
                    .option(shadowText)
                    .build();


        }
    }

}
