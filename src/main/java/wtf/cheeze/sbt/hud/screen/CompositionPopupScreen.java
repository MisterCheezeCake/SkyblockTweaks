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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.hud.utils.CompositionEntry;
import wtf.cheeze.sbt.utils.MultiUtils;
import wtf.cheeze.sbt.utils.errors.ErrorHandler;
import wtf.cheeze.sbt.utils.errors.ErrorLevel;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.render.RenderUtils;
import wtf.cheeze.sbt.utils.text.Symbols;
import wtf.cheeze.sbt.utils.text.TextUtils;

import java.util.List;

//?if >1.21.8
import net.minecraft.client.input.MouseButtonEvent;

/**
 * A popup screen for changing HUD composition
 * TODO: MoveUp and down buttons are still a little jank
 * TODO: Reset only works the first time its used
 */
public class CompositionPopupScreen<T extends CompositionEntry> extends Screen {
    private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath("skyblocktweaks", "gui/panel_super_wide.png");
    private static final Minecraft client = Minecraft.getInstance();
    private static final int WIDTH = 300;
    private static final int HEIGHT = 150;
    private static final int CLICK_CLOSE_BUFFER = 5;
    private final Screen parent;
    private final Binding<List<T>> binding;
    private final T[] values;

    private NewItemList newItemList;
    private ModifyItemList modifyItemList;
    private Button previewButton;

    public CompositionPopupScreen(Component title, Screen parent, Binding<List<T>> binding, T[] values) {
        super(title);
        this.parent = parent;
        this.binding = binding;
        this.values = values;
    }



    private int popupX;
    private int rightEdge;
    private int popupY;
    private int centerX;

    private static final Component ADD_ITEMS_TITLE = Component.translatable("sbt.gui.config.composition.add_items");
    private static final Component ADD_ITEM_BUTTON = Component.translatable("sbt.gui.config.composition.add_item");
    private static final Component MODIFY_ITEMS_TITLE = Component.translatable("sbt.gui.config.composition.modify_items");
    private static final Component DELETE_ITEM_BUTTON = Component.translatable("sbt.gui.config.composition.delete_item");

    private static final Tooltip PREVIEW_INACTIVE = Tooltip.create(Component.translatable("sbt.gui.config.composition.preview.disabled"));

    @Override
    protected void init() {
        centerX = client.getWindow().getGuiScaledWidth() / 2;
        popupX = centerX - WIDTH /2;
        popupY = (client.getWindow().getGuiScaledHeight() / 2) - HEIGHT /2;
        rightEdge = popupX + WIDTH;
        newItemList = new NewItemList(client);
        newItemList.reload();
        this.addRenderableWidget(newItemList);
        var title = new StringWidget(centerX - RenderUtils.getStringWidth(getTitle()) / 2, popupY + 6, RenderUtils.getStringWidth(getTitle()), font.lineHeight, getTitle(), font);
        this.addRenderableWidget(title);
        var addItemsTitle = new StringWidget(popupX + 6 + newItemList.getWidthForBorder() / 2 - RenderUtils.getStringWidth(ADD_ITEMS_TITLE) / 2, popupY + 19, RenderUtils.getStringWidth(ADD_ITEMS_TITLE), font.lineHeight, ADD_ITEMS_TITLE, font);
        this.addRenderableWidget(addItemsTitle);
        var addItemButton = Button.builder(ADD_ITEM_BUTTON, button -> {
            var selected = newItemList.getSelected();
            if (selected == null) return;
            var selectedItem = selected.item;
            var list = binding.getValue();
            list.add(selectedItem);
            binding.setValue(list);
            newItemList.reload();
            modifyItemList.reload();
        }).bounds(popupX + 6 + newItemList.getWidthForBorder() / 2 - 45, popupY + TOP_OFFSET + NewItemList.HEIGHT + 5, 90, 20).build();
        this.addRenderableWidget(addItemButton);

        var modifyItemsTitle = new StringWidget(rightEdge - 6 - ModifyItemList.WIDTH / 2 - ModifyItemListEntry.BUTTON_DIMENSION - RenderUtils.getStringWidth(MODIFY_ITEMS_TITLE) / 2, popupY + 19, RenderUtils.getStringWidth(MODIFY_ITEMS_TITLE), font.lineHeight, MODIFY_ITEMS_TITLE, font);
        this.addRenderableWidget(modifyItemsTitle);
        var deleteItemButton = Button.builder(DELETE_ITEM_BUTTON, button -> {
            var selected = modifyItemList.getSelected();
            if (selected == null) return;
            var selectedItem = selected.item;
            var list = binding.getValue();
            list.remove(selectedItem);
            binding.setValue(list);
            newItemList.reload();
            modifyItemList.reload();
        }).bounds(rightEdge - 6 - ModifyItemList.WIDTH / 2 - ModifyItemListEntry.BUTTON_DIMENSION - 45, popupY + TOP_OFFSET + ModifyItemList.HEIGHT + 5, 90, 20).build();
        this.addRenderableWidget(deleteItemButton);
        modifyItemList = new ModifyItemList(client);
        modifyItemList.reload();
        this.addRenderableWidget(modifyItemList);

        int centralButtonOffset = popupX + 6 + NewItemList.WIDTH + 8 ;

        var resetCompositionButton = Button.builder(Component.translatable("sbt.gui.config.composition.reset"), button -> {
            binding.setValue(binding.defaultValue());
            newItemList.reload();
            modifyItemList.reload();
        }).bounds(centralButtonOffset, popupY + TOP_OFFSET + 6, 70, 20).build();

        this.previewButton = Button.builder(Component.translatable("sbt.gui.config.composition.preview"), button -> {

        }) .bounds(centralButtonOffset, popupY + TOP_OFFSET + 6 + 40, 70, 20).build();

        previewButton.active = false;
        previewButton.setTooltip(PREVIEW_INACTIVE);

        this.addRenderableWidget(previewButton);

        var doneButton = Button.builder(Component.translatable("gui.done"), button -> onClose()).bounds(centralButtonOffset, popupY + TOP_OFFSET + ModifyItemList.HEIGHT + 5, 70, 20).build();

        this.addRenderableWidget(doneButton);
        this.addRenderableWidget(resetCompositionButton);

      //  this.addWidget(new EventListener());
    }


    @Override
    public void onClose() {
        //client.setScreen(parent);
        client.screen = parent;
        SBTConfig.save();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        parent.render(guiGraphics, -1, -1, delta);

        RenderUtils.drawTexture(guiGraphics, BACKGROUND, popupX, popupY, WIDTH, HEIGHT, WIDTH, HEIGHT);
        for (Renderable drawable : this.renderables) {
            drawable.render(guiGraphics, mouseX, mouseY, delta);
        }

        if (MultiUtils.shiftDown()) {
            var base = Component.empty();
            boolean first = true;

            for (var item: binding.getValue()) {
                if (!first) base.append(TextUtils.NEW_LINE);
                base.append(item.getPreviewText());
                first = false;
            }

            previewButton.setTooltip(Tooltip.create(base));
        } else {
            previewButton.setTooltip(PREVIEW_INACTIVE);
        }
        //?if >1.21.8 {
        
        for (var entry: newItemList.children()) guiGraphics.drawCenteredString(client.font, entry.item.getDisplayName(), entry.getX() + 90 / 2, entry.getY() + 2, Colors.WHITE);
        for (var entry: modifyItemList.children()) guiGraphics.drawCenteredString(client.font, entry.item.getDisplayName(), entry.getX() + 90 / 2, entry.getY() + 2, Colors.WHITE);
       
        //?}
    }

    private class NewItemList extends AbstractList<NewItemListEntry> {
        private static final int WIDTH = 90;
        private static final int HEIGHT = 90;
        public NewItemList(Minecraft client) {
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
        protected void renderListSeparators(GuiGraphics guiGraphics) {
            RenderUtils.drawBorder(guiGraphics, 1, Colors.GRAY, x, getY(), getWidthForBorder() , getHeight());
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
        //?if >1.21.8 {
        
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            guiGraphics.fill(CompositionPopupScreen.this.popupX -1  , getY() - 2, getX() + WIDTH , getY() + ENTRY_HEIGHT + 2, CompositionPopupScreen.this.children().indexOf(this) % 2 == 0 ? HIGHLIGHT_1: HIGHLIGHT_2);
            // TODO: Why is it not rendering here and I had to move it up?
            // guiGraphics.drawCenteredString(client.font, item.getDisplayName(), getX() + WIDTH / 2, getY() + 2, Colors.WHITE);
            //guiGraphics.drawString(client.font, TextUtils.withBold("Hi"), 1, 1, Colors.WHITE);
        } 
        //?} else {
        /*public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            guiGraphics.fill(x -1  , y - 2, x + entryWidth , y + entryHeight + 2, index % 2 == 0 ? HIGHLIGHT_1: HIGHLIGHT_2);
            guiGraphics.drawCenteredString(client.font, item.getDisplayName(), x + entryWidth / 2, y + 2, Colors.WHITE);
        }
        *///?}
    }

    private static final Component MOVE_UP = Component.translatable("sbt.gui.config.move_up");
    private static final Component MOVE_DOWN = Component.translatable("sbt.gui.config.move_down");
    private static final Component UP_SYMBOL = Component.literal(Symbols.BUTTON_UP);
    private static final Component DOWN_SYMBOL = Component.literal(Symbols.BUTTON_DOWN);

    private class ModifyItemList extends AbstractList<ModifyItemListEntry> {

        private static final int HEIGHT = 90;
        private static final int WIDTH = 90;

        public ModifyItemList(Minecraft minecraftClient) {
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
                this.children.set(listI - 1, entry);
                this.children.set(listI, before);
                binding.setValue(list);
                CompositionPopupScreen.this.modifyItemList.reload();
                return true;
            } catch (Exception e) {
                ErrorHandler.handle(e, "Failed to move item up", ErrorLevel.WARNING);
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
                this.children.set(listI + 1, entry);
                this.children.set(listI, after);
                binding.setValue(list);
                CompositionPopupScreen.this.modifyItemList.reload();
                return true;
            } catch (Exception e) {
                    ErrorHandler.handle(e, "Failed to move item down", ErrorLevel.WARNING);
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
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
            try {
                super.renderWidget(guiGraphics, mouseX, mouseY, delta);
                guiGraphics.enableScissor(popupX, this.getY(), rightEdge, this.getBottom());
                for (var entry : this.children()) {
                    int i = this.children().indexOf(entry);
                    boolean isFirst = i == 0;
                    boolean isLast = i == this.children().size() - 1;
                    entry.moveUp.render(guiGraphics, mouseX, mouseY, delta);
                    entry.moveDown.render(guiGraphics, mouseX, mouseY, delta);
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
                guiGraphics.disableScissor();
            } catch (Exception e) {
                ErrorHandler.handle(e, "Failed to render modify item list", ErrorLevel.WARNING);
                guiGraphics.disableScissor();
            }
        }

        @Override
        protected void renderListSeparators(GuiGraphics guiGraphics) {
            RenderUtils.drawBorder(guiGraphics, 1, Colors.GRAY, x - ModifyItemListEntry.BUTTON_DIMENSION*2, getY(), getWidthForBorder() + ModifyItemListEntry.BUTTON_DIMENSION*2 , getHeight());
        }
    }

    private class ModifyItemListEntry extends AbstractEntry<ModifyItemListEntry> {
        public final TooltipButton moveUp, moveDown;
        private static final int BUTTON_DIMENSION = 15;

        public ModifyItemListEntry(T item) {
            super(item);
            moveUp = new TooltipButton(CompositionPopupScreen.this, 0, 0, BUTTON_DIMENSION, BUTTON_DIMENSION, UP_SYMBOL, MOVE_UP, button -> modifyItemList.moveUp(this));
            moveDown = new TooltipButton(CompositionPopupScreen.this, 0, 0, BUTTON_DIMENSION, BUTTON_DIMENSION, DOWN_SYMBOL, MOVE_DOWN, button -> modifyItemList.moveDown(this));
            CompositionPopupScreen.this.addWidget(moveUp);
            CompositionPopupScreen.this.addWidget(moveDown);
        }

        public void cleanup() {
            CompositionPopupScreen.this.removeWidget(moveUp);
            CompositionPopupScreen.this.removeWidget(moveDown);
        }

        @Override
        //? if >1.21.8 {
        
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            moveUp.setPosition(getX() - 1 - 2*BUTTON_DIMENSION, getY() - 2);
            moveDown.setPosition(getX() - 1 - BUTTON_DIMENSION, getY() - 2);

            guiGraphics.fill(getX(), getY() - 2, getX() + width , getY() + ENTRY_HEIGHT + 2, CompositionPopupScreen.this.children().indexOf(this) % 2 == 0 ? HIGHLIGHT_1: HIGHLIGHT_2);
                   // TODO: Why is it not rendering here and I had to move it up?
         //   guiGraphics.drawCenteredString(client.font, item.getDisplayName(), getX() + WIDTH / 2, getY() + 2, Colors.WHITE);
           // RenderUtils.drawText(guiGraphics, Component.literal("Test"), 0, 0, Colors.WHITE, true);
        }
        
        //?} else {
        /*public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            moveUp.setPosition(x - 1 - 2*BUTTON_DIMENSION, y - 2);
            moveDown.setPosition(x - 1 - BUTTON_DIMENSION, y - 2);
            guiGraphics.fill(x, y - 2, x + entryWidth , y + entryHeight + 2, index % 2 == 0 ? HIGHLIGHT_1: HIGHLIGHT_2);
            guiGraphics.drawCenteredString(client.font, item.getDisplayName(), x + entryWidth / 2, y + 2, Colors.WHITE);
        }
        *///?}
    }

    private abstract static class AbstractList<E extends ObjectSelectionList.Entry<E>> extends ObjectSelectionList<E> {
        public static final int SCROLLBAR_WIDTH = 6;
        protected int x;

        /**
         * This existed for multiversion support, and is retained
         * in case it changes again in the future.
         */
        protected boolean sbtCheckOverflow() {
            return scrollbarVisible();
        }

        /**f
         * This existed for multiversion support, and is retained
         * in case it changes again in the future.
         */
        protected void sbtSetScroll(double scroll) {
            setScrollAmount(scroll);
        }

        protected E getEntry(int index) {
            return this.children().get(index);
        }

        public AbstractList(Minecraft minecraftClient, int width, int height, int x, int y, int entryHeight) {
            super(minecraftClient, width, height, y, entryHeight);
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
        protected int scrollBarX() {
            return getRight();
        }

        @Override
        protected void renderListBackground(GuiGraphics guiGraphics) {
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
                ErrorHandler.handle(e, "Failed to reload entry list", ErrorLevel.WARNING);
            }
        }

        protected abstract void setupEntries();

    }

    private abstract class AbstractEntry<E extends ObjectSelectionList.Entry<E>> extends ObjectSelectionList.Entry<E> {
        public final T item;

        private AbstractEntry(T item) {
            this.item = item;
        }

        @Override
        public Component getNarration() {
            return item.getDisplayName();
        }
    }


    @Override
    //?if >1.21.8 {
    
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClicked) {
        if (event.x() < this.popupX - CLICK_CLOSE_BUFFER || event.x() > this.rightEdge + CLICK_CLOSE_BUFFER || event.y() < CompositionPopupScreen.this.popupY - CLICK_CLOSE_BUFFER || event.y() > popupY + HEIGHT + CLICK_CLOSE_BUFFER) {
            onClose();
            return true;
        }
        return super.mouseClicked(event, isDoubleClicked);
    }
     
    //? } else {
    /*public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX < popupX - CLICK_CLOSE_BUFFER || mouseX > rightEdge + CLICK_CLOSE_BUFFER || mouseY < popupY - CLICK_CLOSE_BUFFER || mouseY > popupY + HEIGHT + CLICK_CLOSE_BUFFER) {
            onClose();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    *///?}

}
