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
package wtf.cheeze.sbt.hud.screen;

import dev.isxander.yacl3.api.Binding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.hud.utils.CompositionEntry;
import wtf.cheeze.sbt.utils.errors.ErrorHandler;
import wtf.cheeze.sbt.utils.errors.ErrorLevel;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.render.RenderUtils;
import wtf.cheeze.sbt.utils.text.Symbols;
import wtf.cheeze.sbt.utils.text.TextUtils;

import java.util.List;

/**
 * A popup screen for changing HUD composition
 * TODO: MoveUp and down buttons are still a little jank
 * TODO: Reset only works the first time its used
 */
public class CompositionPopupScreen<T extends CompositionEntry> extends Screen {

    private static final Identifier BACKGROUND = Identifier.of("skyblocktweaks", "gui/panel_super_wide.png");
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final int WIDTH = 300;
    private static final int HEIGHT = 150;
    private static final int CLICK_CLOSE_BUFFER = 5;
    private final Screen parent;
    private final Binding<List<T>> binding;
    private final T[] values;

    private NewItemList newItemList;
    private ModifyItemList modifyItemList;
    private ButtonWidget previewButton;




    public CompositionPopupScreen(Text title, Screen parent, Binding<List<T>> binding, T[] values) {
        super(title);
        this.parent = parent;
        this.binding = binding;
        this.values = values;
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX < popupX - CLICK_CLOSE_BUFFER || mouseX > rightEdge + CLICK_CLOSE_BUFFER || mouseY < popupY - CLICK_CLOSE_BUFFER || mouseY > popupY + HEIGHT + CLICK_CLOSE_BUFFER) {
            close();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private int popupX;
    private int rightEdge;
    private int popupY;
    private int centerX;

    private static final Text ADD_ITEMS_TITLE = Text.translatable("sbt.gui.config.composition.add_items");
    private static final Text ADD_ITEM_BUTTON = Text.translatable("sbt.gui.config.composition.add_item");
    private static final Text MODIFY_ITEMS_TITLE = Text.translatable("sbt.gui.config.composition.modify_items");
    private static final Text DELETE_ITEM_BUTTON = Text.translatable("sbt.gui.config.composition.delete_item");

    private static final Tooltip PREVIEW_INACTIVE = Tooltip.of(Text.translatable("sbt.gui.config.composition.preview.disabled"));


    @Override
    protected void init() {
        centerX = client.getWindow().getScaledWidth() / 2;
        popupX = centerX - WIDTH /2;
        popupY = (client.getWindow().getScaledHeight() / 2) - HEIGHT /2;
        rightEdge = popupX + WIDTH;
        newItemList = new NewItemList(client);
        newItemList.reload();
        this.addDrawableChild(newItemList);
        var title = new TextWidget(centerX - RenderUtils.getStringWidth(getTitle()) / 2, popupY + 6, RenderUtils.getStringWidth(getTitle()), textRenderer.fontHeight, getTitle(), textRenderer);
        this.addDrawableChild(title);
        var addItemsTitle = new TextWidget(popupX + 6 + newItemList.getWidthForBorder() / 2 - RenderUtils.getStringWidth(ADD_ITEMS_TITLE) / 2, popupY + 19, RenderUtils.getStringWidth(ADD_ITEMS_TITLE), textRenderer.fontHeight, ADD_ITEMS_TITLE, textRenderer);
        this.addDrawableChild(addItemsTitle);
        var addItemButton = ButtonWidget.builder(ADD_ITEM_BUTTON, button -> {
            var selected = newItemList.getSelectedOrNull();
            if (selected == null) return;
            var selectedItem = selected.item;
            var list = binding.getValue();
            list.add(selectedItem);
            binding.setValue(list);
            newItemList.reload();
            modifyItemList.reload();
        }).dimensions(popupX + 6 + newItemList.getWidthForBorder() / 2 - 45, popupY + TOP_OFFSET + NewItemList.HEIGHT + 5, 90, 20).build();
        this.addDrawableChild(addItemButton);

        var modifyItemsTitle = new TextWidget(rightEdge - 6 - ModifyItemList.WIDTH / 2 - ModifyItemListEntry.BUTTON_DIMENSION - RenderUtils.getStringWidth(MODIFY_ITEMS_TITLE) / 2, popupY + 19, RenderUtils.getStringWidth(MODIFY_ITEMS_TITLE), textRenderer.fontHeight, MODIFY_ITEMS_TITLE, textRenderer);
        this.addDrawableChild(modifyItemsTitle);
        var deleteItemButton = ButtonWidget.builder(DELETE_ITEM_BUTTON, button -> {
            var selected = modifyItemList.getSelectedOrNull();
            if (selected == null) return;
            var selectedItem = selected.item;
            var list = binding.getValue();
            list.remove(selectedItem);
            binding.setValue(list);
            newItemList.reload();
            modifyItemList.reload();
        }).dimensions(rightEdge - 6 - ModifyItemList.WIDTH / 2 - ModifyItemListEntry.BUTTON_DIMENSION - 45, popupY + TOP_OFFSET + ModifyItemList.HEIGHT + 5, 90, 20).build();
        this.addDrawableChild(deleteItemButton);
        modifyItemList = new ModifyItemList(client);
        modifyItemList.reload();
        this.addDrawableChild(modifyItemList);

        int centralButtonOffset = popupX + 6 + NewItemList.WIDTH + 8 ;

        var resetCompositionButton = ButtonWidget.builder(Text.translatable("sbt.gui.config.composition.reset"), button -> {
            binding.setValue(binding.defaultValue());
            newItemList.reload();
            modifyItemList.reload();
        }).dimensions(centralButtonOffset, popupY + TOP_OFFSET + 6, 70, 20).build();

        this.previewButton = ButtonWidget.builder(Text.translatable("sbt.gui.config.composition.preview"), button -> {

        }) .dimensions(centralButtonOffset, popupY + TOP_OFFSET + 6 + 40, 70, 20).build();

        previewButton.active = false;
        previewButton.setTooltip(PREVIEW_INACTIVE);

        this.addDrawableChild(previewButton);

        var doneButton = ButtonWidget.builder(Text.translatable("gui.done"), button -> {
            close();
        }).dimensions(centralButtonOffset, popupY + TOP_OFFSET + ModifyItemList.HEIGHT + 5, 70, 20).build();

        this.addDrawableChild(doneButton);


        this.addDrawableChild(resetCompositionButton);
    }


    @Override
    public void close() {
        //client.setScreen(parent);
        client.currentScreen = parent;
        SBTConfig.save();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        parent.render(context, -1, -1, delta);

        RenderUtils.drawTranslated(context, 10, 1, () -> {
            RenderUtils.drawTexture(context, BACKGROUND, popupX, popupY, WIDTH, HEIGHT, WIDTH, HEIGHT);
            for (Drawable drawable : this.drawables) {
                drawable.render(context, mouseX, mouseY, delta);
            }
        });
        if (hasShiftDown()) {

            var base = Text.empty();
            boolean first = true;

            for (var item: binding.getValue()) {
                if (!first) base.append(TextUtils.NEW_LINE);
                base.append(item.getPreviewText());
                first = false;
            }

            previewButton.setTooltip(Tooltip.of(base));



        } else {
            previewButton.setTooltip(PREVIEW_INACTIVE);

        }


    }

    private class NewItemList extends AbstractList<NewItemListEntry> {
        private static final int WIDTH = 90;
        private static final int HEIGHT = 90;
        public NewItemList(MinecraftClient client) {
            super(client,
                   WIDTH,
                   HEIGHT,
                   popupX + 6,
                    popupY + TOP_OFFSET,
                    ENTRY_HEIGHT


            );
        }

        protected void setupEntries() {
            for (var item: values) {
                if (item.isRepeatable() || !binding.getValue().contains(item)) {
                    addEntry(new NewItemListEntry(item));
                }
            }

        }

        @Override
        protected void drawHeaderAndFooterSeparators(DrawContext context) {
            RenderUtils.drawBorder(context, 1, Colors.GRAY, x, getY(), getWidthForBorder() , getHeight());
        }

    }

    private static final int HIGHLIGHT_1 = -2131561742;
    private static final int HIGHLIGHT_2 = -2136956768;
    private static final int ENTRY_HEIGHT = 15;
    private static final int TOP_OFFSET = 30;


    private class NewItemListEntry extends AbstractEntry<NewItemListEntry> {
        public NewItemListEntry(T item) {
            super(item);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.fill(x -1  , y - 2, x + entryWidth , y + entryHeight + 2, index % 2 == 0 ? HIGHLIGHT_1: HIGHLIGHT_2);
            context.drawCenteredTextWithShadow(client.textRenderer, item.getDisplayName(), x + entryWidth / 2, y + 2, Colors.WHITE);
        }
    }

    private static final Text MOVE_UP = Text.translatable("sbt.gui.config.move_up");
    private static final Text MOVE_DOWN = Text.translatable("sbt.gui.config.move_down");
    private static final Text UP_SYMBOL = Text.literal(Symbols.BUTTON_UP);
    private static final Text DOWN_SYMBOL = Text.literal(Symbols.BUTTON_DOWN);

    private class ModifyItemList extends AbstractList<ModifyItemListEntry> {

        private static final int HEIGHT = 90;
        private static final int WIDTH = 90;


        public ModifyItemList(MinecraftClient minecraftClient) {
            super(minecraftClient, WIDTH, HEIGHT, rightEdge - 6 - WIDTH, popupY + TOP_OFFSET, ENTRY_HEIGHT);

        }

        @Override
        protected void setupEntries() {
            for (var item: binding.getValue()) {
                addEntry(new ModifyItemListEntry(item));

            }
            if (sbtCheckOverflow()) {
                this.x = rightEdge - 6 - WIDTH - SCROLLBAR_WIDTH;
            } else {
                this.x = rightEdge - 6 - WIDTH;
            }


        }

        //TODO: This may not properly work with repeatable items
        public boolean moveUp(ModifyItemListEntry entry) {
            try {
                var list = binding.getValue();
                var listI = list.indexOf(entry.item);
                if (listI == 0) return false;
                var before = getEntry(listI - 1);
                list.set(listI - 1, entry.item);
                list.set(listI, before.item);
                this.children().set(listI - 1, entry);
                this.children().set(listI, before);
                binding.setValue(list);
                return true;
            } catch (Exception e) {
                ErrorHandler.handleError(e, "Failed to move item up", ErrorLevel.WARNING);
                return false;
            }
        }

        public boolean moveDown(ModifyItemListEntry entry) {
            try {
                var list = binding.getValue();
                var listI = list.indexOf(entry.item);
                if (listI == list.size() - 1) return false;
                var after = getEntry(listI + 1);
                list.set(listI + 1, entry.item);
                list.set(listI, after.item);
                this.children().set(listI + 1, entry);
                this.children().set(listI, after);
                binding.setValue(list);
                return true;
            } catch (Exception e) {
                    ErrorHandler.handleError(e, "Failed to move item up", ErrorLevel.WARNING);
                    return false;
            }
        }

        @Override
        protected void clearEntries() {
            for (var entry: this.children()) {
                entry.cleanup();
            }
            super.clearEntries();
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            try {
                super.renderWidget(context, mouseX, mouseY, delta);
                context.enableScissor(popupX, this.getY(), rightEdge, this.getBottom());
                for (var entry : this.children()) {
                    int i = this.children().indexOf(entry);
                    boolean isFirst = i == 0;
                    boolean isLast = i == this.children().size() - 1;
                    entry.moveUp.render(context, mouseX, mouseY, delta);
                    entry.moveDown.render(context, mouseX, mouseY, delta);
                    if (entry.moveUp.getY() + ModifyItemListEntry.BUTTON_DIMENSION < this.getY() || entry.moveUp.getY() > this.getBottom()) {
                        if (!isFirst) entry.moveUp.active = false;
                        if (!isLast) entry.moveDown.active = false;
                        entry.moveUp.visible = false;
                        entry.moveDown.visible = false;
                    } else {
                        if (!isFirst) entry.moveUp.active = true;
                        if (!isLast) entry.moveDown.active = true;
                        entry.moveUp.visible = true;
                        entry.moveDown.visible = true;
                    }


                }
                context.disableScissor();
            } catch (Exception e) {
                ErrorHandler.handleError(e, "Failed to render modify item list", ErrorLevel.WARNING);
                context.disableScissor();
            }
        }

        @Override
        protected void drawHeaderAndFooterSeparators(DrawContext context) {
            RenderUtils.drawBorder(context, 1, Colors.GRAY, x - ModifyItemListEntry.BUTTON_DIMENSION*2, getY(), getWidthForBorder() + ModifyItemListEntry.BUTTON_DIMENSION*2 , getHeight());
        }
    }

    private class ModifyItemListEntry extends AbstractEntry<ModifyItemListEntry> {

        public final TooltipButton moveUp, moveDown;
        private static final int BUTTON_DIMENSION = 15;


        public ModifyItemListEntry(T item) {
            super(item);
            moveUp = new TooltipButton(CompositionPopupScreen.this, 0, 0, BUTTON_DIMENSION, BUTTON_DIMENSION, UP_SYMBOL, MOVE_UP, button -> {
                modifyItemList.moveUp(this);
            });
            moveDown = new TooltipButton(CompositionPopupScreen.this, 0, 0, BUTTON_DIMENSION, BUTTON_DIMENSION, DOWN_SYMBOL, MOVE_DOWN, button -> {
                modifyItemList.moveDown(this);
            });
            CompositionPopupScreen.this.addSelectableChild(moveUp);
            CompositionPopupScreen.this.addSelectableChild(moveDown);
        }

        public void cleanup() {
            CompositionPopupScreen.this.remove(moveUp);
            CompositionPopupScreen.this.remove(moveDown);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            moveUp.setPosition(x - 1 - 2*BUTTON_DIMENSION, y - 2);
            moveDown.setPosition(x - 1 - BUTTON_DIMENSION, y - 2);
            context.fill(x, y - 2, x + entryWidth , y + entryHeight + 2, index % 2 == 0 ? HIGHLIGHT_1: HIGHLIGHT_2);
            context.drawCenteredTextWithShadow(client.textRenderer, item.getDisplayName(), x + entryWidth / 2, y + 2, Colors.WHITE);
        }

    }

    private abstract static class AbstractList<E extends AlwaysSelectedEntryListWidget.Entry<E>> extends AlwaysSelectedEntryListWidget<E> {

        public static final int SCROLLBAR_WIDTH = 6;
        protected int x;

        /**
         * This existed for multiversion support, and is retained
         * in case it changes again in the future.
         */
        protected boolean sbtCheckOverflow() {
            return overflows();
        }

        /**
         * This existed for multiversion support, and is retained
         * in case it changes again in the future.
         */
        protected void sbtSetScroll(double scroll) {
            setScrollY(scroll);
        }

        public AbstractList(MinecraftClient minecraftClient, int width, int height, int x, int y, int entryHeight) {
            super(minecraftClient, width, height, y, entryHeight, 0);
            this.x = x;
        }

        @Override
        public int getX() {
            return this.x;
        }

        @Override
        public int getRowWidth() {
            return this.width;
        }

        @Override
        protected int getScrollbarX() {
            return getRight();
        }

        @Override
        protected void drawMenuListBackground(DrawContext context) {
            // Intentionally empty
        }

        protected int getWidthForBorder() {
            if (!sbtCheckOverflow()) return getWidth();

            return getWidth() + SCROLLBAR_WIDTH;
        }

        public void reload() {
            try {
                this.clearEntries();
                this.setupEntries();
                this.sbtSetScroll(0);
            } catch (Exception e) {
                ErrorHandler.handleError(e, "Failed to reload entry list", ErrorLevel.WARNING);
            }
        }

        protected abstract void setupEntries();

    }

    private abstract class AbstractEntry<E extends AlwaysSelectedEntryListWidget.Entry<E>> extends AlwaysSelectedEntryListWidget.Entry<E> {
        public final T item;

        private AbstractEntry(T item) {
            this.item = item;
        }

        @Override
        public Text getNarration() {
            return item.getDisplayName();
        }
    }

}
