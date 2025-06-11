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
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.config.categories.General;
import wtf.cheeze.sbt.events.DrawSlotEvents;
import wtf.cheeze.sbt.utils.KillSwitch;
import wtf.cheeze.sbt.utils.constants.loader.Constants;
import wtf.cheeze.sbt.utils.enums.Side;
import wtf.cheeze.sbt.utils.injected.SBTHandledScreen;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.render.Popup;
import wtf.cheeze.sbt.utils.render.RenderUtils;
import wtf.cheeze.sbt.utils.skyblock.ItemStackUtils;
import wtf.cheeze.sbt.utils.text.TextUtils;

import java.util.List;
import java.util.stream.Stream;

//TODO: make this work in the hex
public class ReforgeOverlay {

    private static final int REFORGE_ITEM_SLOT = 13;
    private static final int REFORGE_BUTTON_SLOT = 22;
    private static final float Z_TRANSLATE = 300;
    private static final int X_LABEL_OFFSET = 20 ;
    private static final String SCREEN_TITLE = "Reforge Item";

    private static final String FILTER_FEATURE_ID = "reforge_filters";

    private static final Text QUESTION_MARK = TextUtils.withUnderlined(TextUtils.withBold("?")).withColor(Colors.GRAY);
    private static final Text MATCHES_TEXT = TextUtils.withColor("Matches ", Colors.CYAN);
    private static final Text EXCLUSIONS_TEXT = TextUtils.withColor("Exclusions ", Colors.CYAN);

    private static final List<Text> MATCH_HELP_LINES = Stream.of(
            "Enter a comma seperated list of",
            "reforges to match. You will be",
            "prevented from accidentally",
            "re-rolling any reforge which",
            "contains one of the substrings."
    ).map(Text::of).toList();

    private static final List<Text> EXLCUSION_HELP_LINES = Stream.of(
            "Enter a comma seperated list of",
            "reforges to exclude. Even if",
            "it matches on of the above strings",
            "a reforge will not be considered a",
            "match if it contains any of the",
            "exclusion substrings below"
    ).map(Text::of).toList();


    public static void registerEvents() {
        DrawSlotEvents.BEFORE_ITEM.register((screenTitle, context, slot) -> {
            if (!SBTConfig.get().reforgeOverlay.nameTooltip) return;
            if (!screenTitle.getString().equals(SCREEN_TITLE)) return;
            if (slot.id != REFORGE_ITEM_SLOT || slot.getStack().isEmpty()) return;
            var reforge = ItemStackUtils.getReforge(slot.getStack());
            if (reforge.isEmpty()) return;
            var text = TextUtils.withColor(Constants.reforges().specialModifiers().getOrDefault(reforge, TextUtils.firstLetterUppercase(reforge.toLowerCase())), Colors.YELLOW);
            var x = slot.x - X_LABEL_OFFSET - RenderUtils.getStringWidth(text);
            context.getMatrices().push();
            context.getMatrices().translate(0, 0, Z_TRANSLATE);
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, text, x, slot.y + 16 );
            context.getMatrices().pop();
        });

        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof HandledScreen<?> handledScreen && handledScreen.getTitle().getString().equals(SCREEN_TITLE) && SBTConfig.get().reforgeOverlay.filterOverlay && !KillSwitch.shouldKill(FILTER_FEATURE_ID)) {
                ((SBTHandledScreen)handledScreen).sbt$setPopup(new FilterOverlayPopup(handledScreen));
            }
        });

    }




    public static class FilterOverlayPopup implements Popup {



        private final HandledScreen<?> screen;
        private final int x;
        private final int y;

        private final Text matchLabel;
        private final Text exclusionLabel;

        private final TextFieldWidget matchWidget;
        private final TextFieldWidget exclusionWidget;

        public FilterOverlayPopup(HandledScreen<?> screen) {
            this.x = SBTConfig.get().reforgeOverlay.filterOverlaySide.positionPopup(screen.x);
            this.y = screen.y;
            this.screen = screen;

            screen.drawables.add(this);


            this.matchLabel =  TextUtils.join(
                    MATCHES_TEXT,
                    QUESTION_MARK
            );
            this.exclusionLabel = TextUtils.join(
                    EXCLUSIONS_TEXT,
                    QUESTION_MARK

            );

            this.matchWidget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, x + 10, y + 30, 60, 15, Text.empty());
            this.matchWidget.setMaxLength(70);
            this.matchWidget.setPlaceholder(TextUtils.withColor("e.g. \"a, b\"", Colors.GRAY));
            this.exclusionWidget   = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, x + 10, y + 60, 60, 15, Text.empty());
            this.exclusionWidget.setMaxLength(70);
            this.exclusionWidget.setPlaceholder(TextUtils.withColor("e.g. \"c, d\"", Colors.GRAY));


            screen.addDrawableChild(this.matchWidget);
            screen.addDrawableChild(this.exclusionWidget);

        }


        @Override
        public int x() { return x; };

        @Override
        public int y() { return y;}

        @Override
        public Screen screen() { return screen;}

        @Override
        public List<? extends ClickableWidget> childrenList() {
            return List.of(this.matchWidget, this.exclusionWidget);
        }

        /**
         * 0: hovering over none
         * 1: hovering over match label
         * 2: hovering over exclusion label
         */
        private int whichHovering(int mouseX, int mouseY) {
            var xPositionMatch = x + WIDTH / 2 - RenderUtils.getStringWidth(matchLabel) / 2;
            var xPositionExclusion = x + WIDTH / 2 - RenderUtils.getStringWidth(exclusionLabel) / 2;
            var xPositionQuestionMarkMatch = xPositionMatch + RenderUtils.getStringWidth(matchLabel) - RenderUtils.getStringWidth(QUESTION_MARK);
            var xPositionQuestionMarkExclusion = xPositionExclusion + RenderUtils.getStringWidth(exclusionLabel) - RenderUtils.getStringWidth(QUESTION_MARK);
            var questionMarkWidth = RenderUtils.getStringWidth(QUESTION_MARK);
            if (mouseX >= xPositionQuestionMarkMatch && mouseX <= xPositionQuestionMarkMatch + questionMarkWidth && mouseY >= y + 20 && mouseY <= y + 29) {
                return 1; // hovering over match label
            } else if (mouseX >= xPositionQuestionMarkExclusion && mouseX <= xPositionQuestionMarkExclusion + questionMarkWidth && mouseY >= y + 50 && mouseY <= y + 59) {
                return 2; // hovering over exclusion label
            }
            return 0; // not hovering over any label
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (matchWidget.isFocused() && !matchWidget.isMouseOver(mouseX, mouseY)) {
                matchWidget.setFocused(false);
            } else {
            }
            if (exclusionWidget.isFocused() && !exclusionWidget.isMouseOver(mouseX, mouseY)) {
                exclusionWidget.setFocused(false);
            }

            var shouldStop = shouldStopReforge();
            var slot = screen.getSlotAt(mouseX, mouseY);
            var id = slot == null ? -1 : slot.id;
            if (id == REFORGE_BUTTON_SLOT && shouldStop) {
                MinecraftClient.getInstance().player.playSound(SoundEvents.BLOCK_BELL_USE);
                return true;
            }

            return false;
        }








        private boolean shouldStopReforge() {
            boolean tentativeBlock = false;
            for (var unformatted: matchWidget.getText().split(",")) {
                var formatted = unformatted.trim().toLowerCase();
                if (formatted.isEmpty()) continue;
                if (ItemStackUtils.getReforge(screen.getScreenHandler().slots.get(REFORGE_ITEM_SLOT).getStack()).toLowerCase().contains(formatted)) {
                    tentativeBlock = true;
                    break;
                }
            }
            if (!tentativeBlock) return false;
            for (var unformatted: exclusionWidget.getText().split(",")) {
                var formatted = unformatted.trim().toLowerCase();
                if (formatted.isEmpty()) continue;
                if (ItemStackUtils.getReforge(screen.getScreenHandler().slots.get(REFORGE_ITEM_SLOT).getStack()).toLowerCase().contains(formatted)) {
                    return false; // if it matches an exclusion, we can reforge
                }
            }
            return true; // if it matches a match, but not an exclusion, we should stop the reforge
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            boolean escape = keyCode == GLFW.GLFW_KEY_ESCAPE;
            boolean inv = keyCode == MinecraftClient.getInstance().options.inventoryKey.boundKey.getCode();
            if (matchWidget.isFocused())  {
                if (escape) {
                    matchWidget.setFocused(false);
                    return true;
                }
                if (!inv) return matchWidget.keyPressed(keyCode, scanCode, modifiers);
                else {
                    matchWidget.keyPressed(keyCode, scanCode, modifiers);
                    return true;
                }
            }
            if (exclusionWidget.isFocused()) {
                if (escape) {
                    exclusionWidget.setFocused(false);
                    return true;
                }
                if (!inv) return exclusionWidget.keyPressed(keyCode, scanCode, modifiers);
                else {
                    exclusionWidget.keyPressed(keyCode, scanCode, modifiers);
                    return true;
                }
            }
            return Popup.super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            boolean shadow = SBTConfig.get().reforgeOverlay.filterOverlayShadow;
            Popup.super.renderBackground(context);
            Popup.super.drawSBTFooter(context, shadow);
            RenderUtils.drawCenteredText(context, TextUtils.withBold("Filters"), x + WIDTH / 2, y + 5, Colors.WHITE, shadow);
            RenderUtils.drawCenteredText(context, matchLabel, x + WIDTH / 2, y + 20, 0, shadow);
            RenderUtils.drawCenteredText(context, exclusionLabel, x + WIDTH / 2, y + 50, 0, shadow);
            switch (whichHovering(mouseX, mouseY)) {
                case 1 -> context.drawTooltip(MinecraftClient.getInstance().textRenderer, MATCH_HELP_LINES, mouseX, mouseY);
                case 2 -> context.drawTooltip(MinecraftClient.getInstance().textRenderer, EXLCUSION_HELP_LINES, mouseX, mouseY);
            }

            var slot = screen.getScreenHandler().slots.get(REFORGE_BUTTON_SLOT);
            context.getMatrices().push();
            context.getMatrices().translate(0, 0, Z_TRANSLATE);
            context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, Colors.SBT_GREEN);
            context.getMatrices().pop();
        }
    }


    public static class Config {

        @SerialEntry
        public boolean filterOverlay = true;

        @SerialEntry
        public Side filterOverlaySide = Side.LEFT;

        @SerialEntry
        public boolean filterOverlayShadow = false;

        @SerialEntry
        public boolean nameTooltip = true;




        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var overlay = Option.<Boolean>createBuilder()
                    .name(General.key("reforgeOverlay.filterOverlay"))
                    .description(General.keyD("reforgeOverlay.filterOverlay"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.reforgeOverlay.filterOverlay,
                            () -> config.reforgeOverlay.filterOverlay,
                            value -> config.reforgeOverlay.filterOverlay = value
                    )
                    .build();


            var side = Option.<Side>createBuilder()
                    .name(General.key("reforgeOverlay.filterOverlaySide"))
                    .description(General.keyD("reforgeOverlay.filterOverlaySide"))
                    .controller(SBTConfig::generateSideController)
                    .binding(
                            defaults.reforgeOverlay.filterOverlaySide,
                            () -> config.reforgeOverlay.filterOverlaySide,
                            value -> config.reforgeOverlay.filterOverlaySide = value
                    )
                    .build();

            var shadow = Option.<Boolean>createBuilder()
                    .name(General.key("reforgeOverlay.filterOverlayShadow"))
                    .description(General.keyD("reforgeOverlay.filterOverlayShadow"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.reforgeOverlay.filterOverlayShadow,
                            () -> config.reforgeOverlay.filterOverlayShadow,
                            value -> config.reforgeOverlay.filterOverlayShadow = value
                    )
                    .build();

            var toolTip = Option.<Boolean>createBuilder()
                    .name(General.key("reforgeOverlay.nameTooltip"))
                    .description(General.keyD("reforgeOverlay.nameTooltip"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.reforgeOverlay.nameTooltip,
                            () -> config.reforgeOverlay.nameTooltip,
                            value -> config.reforgeOverlay.nameTooltip = value
                    )
                    .build();


            return OptionGroup.createBuilder()
                    .name(General.key("reforgeOverlay"))
                    .description(General.keyD("reforgeOverlay"))
                    .option(overlay)
                    .option(side)
                    .option(shadow)
                    .option(toolTip)
                    .build();


        }
    }
}
